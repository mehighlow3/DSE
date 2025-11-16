package Task4;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UDPClientWorker implements Runnable {

    private final int iterations;
    private final int dataSize;

    public UDPClientWorker(int iterations, int dataSize) {
        this.iterations = iterations;
        this.dataSize = dataSize;
    }

    @Override
    public void run() {

        System.out.println("UDP THREAD STARTED: " + Thread.currentThread().getName());

        try (DatagramSocket socket = new DatagramSocket()) {

            InetAddress host = InetAddress.getByName("localhost");
            int port = 7000;

            byte[] blob = new byte[dataSize];
            Arrays.fill(blob, (byte) 1);

            byte[] buf = new byte[2048];

            long totalTime = 0;

            for (int i = 0; i < iterations; i++) {

                Message msg = new Message(
                        i,
                        Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        blob
                );

                String serialized = msg.serialize();
                int msgId = (int) (Math.random() * 1_000_000);

                long start = System.nanoTime();

                // send all fragments
                for (DatagramPacket p :
                        UDPFragmenter.fragment(serialized, host, port, msgId)) {
                    socket.send(p);
                }

                UDPDefragmenter defrag = null;
                String full = null;

                // receive reply fragments
                while (full == null) {

                    DatagramPacket inPkt = new DatagramPacket(buf, buf.length);
                    socket.receive(inPkt);

                    String head = new String(inPkt.getData(), 0, inPkt.getLength(), StandardCharsets.UTF_8);
                    String[] parts = head.split("\\|", 4);
                    if (parts.length < 4) continue;

                    int rMsgId = Integer.parseInt(parts[0]);
                    int rTotal = Integer.parseInt(parts[1]);
                    int rIndex = Integer.parseInt(parts[2]);

                    if (rMsgId != msgId) continue;

                    if (defrag == null)
                        defrag = new UDPDefragmenter(rTotal);

                    byte[] chunkBytes = parts[3].getBytes(StandardCharsets.UTF_8);
                    byte[] fullBytes = defrag.addFragment(rIndex, chunkBytes);

                    if (fullBytes != null) {
                        full = new String(fullBytes, StandardCharsets.UTF_8);
                    }
                }

                Message reply = Message.deserialize(full);

                long end = System.nanoTime();
                totalTime += (end - start);
            }

            long avg = totalTime / iterations;
            System.out.println(Thread.currentThread().getName() + " UDP avg RTT = " + avg + " ns");

        } catch (Exception e) {
            System.out.println("UDPClientWorker error: " + e.getMessage());
        }
    }
}
