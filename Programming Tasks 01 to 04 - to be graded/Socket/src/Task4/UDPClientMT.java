package Task4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPClientMT {

    static int TOTAL = 1000;
    static int THREADS = 8;
    static int SIZE_KIB = 16;

    public static void main(String[] args) {

        int perThread = TOTAL / THREADS;
        int dataSize = SIZE_KIB * 1024;

        System.out.println("Starting " + THREADS + " UDP threads...");

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            pool.submit(new UDPClientWorker(perThread, dataSize));
        }

        pool.shutdown();
        while (!pool.isTerminated()) {}

        // After all workers finished, send 1 termination
        try {
            DatagramSocket s = new DatagramSocket();
            InetAddress host = InetAddress.getByName("localhost");
            int port = 7000;

            Message term = new Message(-1, "main", System.currentTimeMillis(), new byte[1]);
            String payload = term.serialize();
            int msgId = (int) (Math.random() * 1_000_000);

            for (DatagramPacket p : UDPFragmenter.fragment(payload, host, port, msgId)) {
                s.send(p);
            }

            s.close();
            System.out.println("Sent single termination to server.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("UDP Client MT finished.");
    }
}
