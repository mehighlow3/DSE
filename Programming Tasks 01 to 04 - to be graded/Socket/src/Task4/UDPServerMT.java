package Task4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServerMT {

    // One active message-state per client (ip:port)
    private static class PartialMessage {
        InetAddress addr;
        int port;
        UDPDefragmenter defrag;
    }

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(16);
        // key = "ip:port" → tracks the active message for that client
        Map<String, PartialMessage> partials = new ConcurrentHashMap<>();
        byte[] buf = new byte[2048];

        try (DatagramSocket socket = new DatagramSocket(7000)) {

            System.out.println("UDP Server MT running on port 7000...");

            while (true) {
                DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                socket.receive(pkt);

                InetAddress addr = pkt.getAddress();
                int port = pkt.getPort();
                String clientKey = addr.getHostAddress() + ":" + port;

                // parse header: msgId|total|index|<chunk...>
                String head = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8);
                String[] parts = head.split("\\|", 4);
                if (parts.length < 4) {
                    continue;
                }

                int msgId = Integer.parseInt(parts[0]);   // used for reply
                int total = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);
                byte[] chunkBytes = parts[3].getBytes(StandardCharsets.UTF_8);

                // Get or create the state slot for this client
                PartialMessage p = partials.computeIfAbsent(clientKey, key -> {
                    PartialMessage pm = new PartialMessage();
                    pm.addr = addr;
                    pm.port = port;
                    pm.defrag = new UDPDefragmenter(total);
                    return pm;
                });

                // If total changed, reset the defragmentation state
                if (p.defrag.total != total) {
                    p.defrag = new UDPDefragmenter(total);
                }

                p.addr = addr;
                p.port = port;

                // Add fragment and check whether the full message arrived
                byte[] fullBytes = p.defrag.addFragment(index, chunkBytes);
                if (fullBytes == null) {
                    continue; // still waiting for the rest
                }

                // Message complete → process it
                partials.remove(clientKey);
                String payload = new String(fullBytes, StandardCharsets.UTF_8);

                pool.submit(() -> {
                    try {
                        Message m = Message.deserialize(payload);

                        // Ignore termination messages (server stays alive)
                        if (m.getNumber() == -1) {
                            System.out.println("UDP Server: termination received (ignored) from " + clientKey);
                            return;
                        }

                        // Task 3/4: update fields
                        m.setNumber(m.getNumber() + 1);
                        m.setSender("server");
                        m.setTimestamp(System.currentTimeMillis());

                        String replyPayload = m.serialize();

                        // Send the reply using the same msgId
                        for (DatagramPacket outPkt :
                                UDPFragmenter.fragment(replyPayload, p.addr, p.port, msgId)) {

                            synchronized (socket) {
                                socket.send(outPkt);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
