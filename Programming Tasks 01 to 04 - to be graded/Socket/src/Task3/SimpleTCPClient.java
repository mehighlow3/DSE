package Task3;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SimpleTCPClient {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            // Try connecting to server
            try {
                socket = new Socket("localhost", 1254);
            } catch (ConnectException e) {
                System.out.println("Client: server not running. Exiting...");
                return;
            }

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            int[] sizesKiB = {1, 2, 4, 8, 16, 128};

            for (int size : sizesKiB) {

                int dataSize = size * 1024;  // convert KiB → bytes
                byte[] blob = new byte[dataSize];
                Arrays.fill(blob, (byte) 1); 

                long totalTime = 0;

                System.out.println("\n--- Testing data size: " + size + " KiB ---");

                for (int i = 0; i < 1000; i++) {

                    long start = System.nanoTime();

                    Message msg = new Message(
                            i,                     // number
                            "client one",         // sender
                            System.currentTimeMillis(),
                            blob                  // data
                    );

                    out.writeObject(msg);
                    out.flush();

                    Message reply = (Message) in.readObject();
                    

                    long end = System.nanoTime();
                    totalTime += (end - start);
                }

                long avg = totalTime / 1000;
                System.out.println("Average RTT for " + size + " KiB = " + avg + " ns");
            }

            // Send termination message
            Message terminate = new Message(
                    -1,               // signal for server to stop
                    "client one",
                    System.currentTimeMillis(),
                    new byte[1]
            );

            out.writeObject(terminate);
            out.flush();

            System.out.println("\nClient terminating...");

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) { }
        }
    }
}
