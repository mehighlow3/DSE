package Task2;

import java.io.*;
import java.net.*;

public class SimpleTCPClient {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            // Try connecting
            try {
                socket = new Socket("localhost", 1254);
            } catch (ConnectException e) {
                System.out.println("Client: server not running. Exiting...");
                return;
            }

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
 
            // Create message
            Message msg = new Message(
                    5,
                    "client one",
                    System.currentTimeMillis()
            );

            // Send object
            out.writeObject(msg);
            out.flush();
            System.out.println("Client sent: " + msg);

            // Receive updated message
            Message reply = (Message) in.readObject();
            System.out.println("Client received: " + reply);

            System.out.println("Client terminating...");

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) { }
        }
    }
}
