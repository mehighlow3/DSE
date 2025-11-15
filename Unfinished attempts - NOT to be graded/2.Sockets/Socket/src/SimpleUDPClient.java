import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SimpleUDPClient {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(0);

            InetAddress host = InetAddress.getByName("localhost");
            int port = 7000;

            int[] sizesKiB = {1, 2, 4, 8, 16, 128};
            byte[] buf = new byte[2048];

            for (int size : sizesKiB) {
                int iterations = 1000;
                int dataSize = size * 1024;
                byte[] blob = new byte[dataSize];
                Arrays.fill(blob, (byte) 1);

                long totalNanos = 0;

                System.out.println("\n--- UDP testing data size: " + size + " KiB ---");

                for (int i = 0; i < iterations; i++) {

                    Message m = new Message(i, "client", System.currentTimeMillis(), blob);
                    String payload = m.serialize();
                    int msgId = (int)(Math.random() * 1_000_000);

                    long start = System.nanoTime();

                    for (DatagramPacket pkt : UDPFragmenter.fragment(payload, host, port, msgId)) {
                        socket.send(pkt);
                    }

                    String replyPayload = null;
                    while (replyPayload == null) {
                        DatagramPacket inPkt = new DatagramPacket(buf, buf.length);
                        socket.receive(inPkt);

                        byte[] data = inPkt.getData();
                        int len = inPkt.getLength();

                        int sepCount = 0;
                        int headerEnd = -1;
                        for (int j = 0; j < len; j++) {
                            if (data[j] == (byte)'|') {
                                sepCount++;
                                if (sepCount == 3) { headerEnd = j; break; }
                            }
                        }
                        if (headerEnd == -1) continue;

                        String header = new String(data, 0, headerEnd + 1, StandardCharsets.US_ASCII);
                        String[] parts = header.split("\\|");
                        int rMsgId = Integer.parseInt(parts[0]);
                        int rTotal = Integer.parseInt(parts[1]);
                        int rIndex = Integer.parseInt(parts[2]);

                        int chunkLen = len - (headerEnd + 1);
                        byte[] chunk = Arrays.copyOfRange(data, headerEnd + 1, len);

                        replyPayload = UDPDefragmenter.addFragmentAsString(rMsgId, rTotal, rIndex, chunk);
                    }

                    Message reply = Message.deserialize(replyPayload);

                    long end = System.nanoTime();
                    totalNanos += (end - start);
                }

                long avg = totalNanos / iterations;
                System.out.println("Average UDP RTT for " + size + " KiB = " + avg + " ns");
            }

            Message term = new Message(-1, "client", System.currentTimeMillis(), new byte[1]);
            String termPayload = term.serialize();
            int termId = (int)(Math.random() * 1_000_000);

            for (DatagramPacket pkt : UDPFragmenter.fragment(termPayload, host, port, termId)) {
                socket.send(pkt);
            }

            System.out.println("UDP client done.");

        } catch (Exception e) {
            System.out.println("UDP Client error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
