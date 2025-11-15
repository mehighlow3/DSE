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

    // Ključ je jedan klijent: addr:port
    private static class PartialMessage {
        InetAddress addr;
        int port;
        UDPDefragmenter defrag;
    }

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(16);
        // key = "ip:port" → jedna aktivna poruka po klijentu
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

                String head = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8);
                String[] parts = head.split("\\|", 4);
                if (parts.length < 4) {
                    continue;
                }

                int msgId = Integer.parseInt(parts[0]);  // koristimo samo za reply header
                int total = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);
                byte[] chunkBytes = parts[3].getBytes(StandardCharsets.UTF_8);

                // Uzmemo ili napravimo PartialMessage za ovog klijenta
                PartialMessage p = partials.computeIfAbsent(clientKey, key -> {
                    PartialMessage pm = new PartialMessage();
                    pm.addr = addr;
                    pm.port = port;
                    pm.defrag = new UDPDefragmenter(total);
                    return pm;
                });

                // Ako se total promenio između poruka, resetuj defragmenter
                if (p.defrag.total != total) {
                    p.defrag = new UDPDefragmenter(total);
                }

                p.addr = addr;
                p.port = port;

                byte[] fullBytes = p.defrag.addFragment(index, chunkBytes);
                if (fullBytes == null) {
                    continue; // poruka još nije kompletna
                }

                // završili smo poruku za ovog klijenta
                partials.remove(clientKey);
                String payload = new String(fullBytes, StandardCharsets.UTF_8);

                pool.submit(() -> {
                    try {
                        Message m = Message.deserialize(payload);

                        // termination ignorišemo (ne gasimo server)
                        if (m.getNumber() == -1) {
                            System.out.println("UDP Server: termination received (ignored) from " + clientKey);
                            return;
                        }

                        // Task 3/4 logika: increment + meta
                        m.setNumber(m.getNumber() + 1);
                        m.setSender("server");
                        m.setTimestamp(System.currentTimeMillis());

                        String replyPayload = m.serialize();

                        // VAŽNO: koristimo ISTI msgId u odgovoru,
                        // da bi klijent mogao da filtrira odgovore po msgId
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
