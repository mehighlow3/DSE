package Task1;

import java.net.*;
import java.io.*;

public class SimpleTCPServer {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(1254)) {
            System.out.println("TCP Server started. Waiting for client...");

            Socket socket = ss.accept();
            System.out.println("Client connected.");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            int number = dis.readInt();     // primi broj
            number++;                       // inkrementira
            dos.writeInt(number);           // vrati klijentu

            dis.close();
            dos.close();
            socket.close();

            System.out.println("Server closed.");
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
