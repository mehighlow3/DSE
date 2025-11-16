package Task3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

public class SimpleUDPClient {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(); // ephemeral port
            socket.setSoTimeout(0); // blocking receive
            InetAddress host = InetAddress.getByName("localhost");
            int port = 7000;

            int[] sizesKiB = {1, 2, 4, 8, 16, 32}; // 128 KiB is too much, slow to load
            byte[] buf = new byte[2048];

            for (int size : sizesKiB) {
                int dataSize = size * 1024;
                byte[] blob = new byte[dataSize];
                Arrays.fill(blob, (byte) 1);

                long totalNanos = 0L;

                System.out.println("\n--- UDP testing data size: " + size + " KiB ---");

                for (int i = 0; i < 1000; i++) {
                    // create message
                    Message m = new Message(i, "client one", System.currentTimeMillis(), blob);
                    String payload = m.serialize();

                    int msgId = (int) (Math.random() * 1_000_000);

                    long start = System.nanoTime();

                    // send all fragments
                    for (DatagramPacket pkt : UDPFragmenter.fragment(payload, host, port, msgId)) {
                        socket.send(pkt);
                    }

                    // receive and reassemble reply (may arrive in fragments)
                    String replyPayload = null;
                    while (replyPayload == null) {
                        DatagramPacket inPkt = new DatagramPacket(buf, buf.length);
                        socket.receive(inPkt);

                        String head = new String(inPkt.getData(), 0, inPkt.getLength(), StandardCharsets.UTF_8);
                        String[] parts = head.split("\\|", 4);
                        if (parts.length < 4) continue;

                        int rMsgId  = Integer.parseInt(parts[0]);
                        int rTotal  = Integer.parseInt(parts[1]);
                        int rIndex  = Integer.parseInt(parts[2]);
                        String rChunkStr = parts[3];

                        replyPayload = UDPDefragmenter.addFragment(
                                rMsgId, rTotal, rIndex, rChunkStr.getBytes(StandardCharsets.UTF_8));
                    }

                    // optional: parse response (check number)
                    Message reply = Message.deserialize(replyPayload);
                    // sanity check (not required): reply.getNumber() == i + 1

                    long end = System.nanoTime();
                    totalNanos += (end - start);
                }

                long avg = totalNanos / 1000L;
                System.out.println("Average UDP RTT for " + size + " KiB = " + avg + " ns");
            }

            // termination
            Message term = new Message(-1, "client one", System.currentTimeMillis(), new byte[1]);
            String termPayload = term.serialize();
            int termId = (int) (Math.random() * 1_000_000);
            for (DatagramPacket pkt : UDPFragmenter.fragment(termPayload, host, port, termId)) {
                socket.send(pkt);
            }

            System.out.println("\nUDP client done.");

        } catch (Exception e) {
            System.out.println("UDP Client error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
