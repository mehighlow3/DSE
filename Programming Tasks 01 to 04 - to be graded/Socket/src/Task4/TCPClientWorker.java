package Task4;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class TCPClientWorker implements Runnable {

    private final int iterations;
    private final int dataSize;

    public TCPClientWorker(int iterations, int dataSize) {
        this.iterations = iterations;
        this.dataSize = dataSize;
    }

    @Override
    public void run() {

        try (Socket socket = new Socket("localhost", 1254);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            byte[] blob = new byte[dataSize];
            Arrays.fill(blob, (byte) 1);

            long totalTime = 0;

            for (int i = 0; i < iterations; i++) {

                long start = System.nanoTime();

                Message msg = new Message(
                        i,
                        Thread.currentThread().getName(),
                        System.currentTimeMillis(),
                        blob
                );

                out.writeObject(msg);
                out.flush();

                Message reply = (Message) in.readObject();

                long end = System.nanoTime();
                totalTime += (end - start);
            }

            long avg = totalTime / iterations;
            System.out.println(Thread.currentThread().getName() + " TCP avg RTT = " + avg + " ns");

            // termination
            out.writeObject(new Message(-1, "client", System.currentTimeMillis(), new byte[1]));
            out.flush();

        } catch (Exception e) {
            System.out.println("TCPClientWorker error: " + e.getMessage());
        }
    }
}
