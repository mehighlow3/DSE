package Task2;

import java.io.*;
import java.net.*;

public class SimpleTCPServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(1254);
            System.out.println("TCP Server started. Waiting for client...");

            socket = serverSocket.accept();
            System.out.println("Client connected.");

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Receive object
            Message msg = (Message) in.readObject();
            System.out.println("Server received: " + msg);

            // Modify message
            msg.setNumber(msg.getNumber() + 1);
            msg.setSender("server");
            msg.setTimestamp(System.currentTimeMillis());

            // Send back modified object
            out.writeObject(msg);
            out.flush();

            System.out.println("Server sent back: " + msg);
            System.out.println("Server terminating..."); 
            
//            in.close();
//            out.close();
//            socket.close();

        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) { }
        }
    }
}

