package Task1;

import java.net.*;
import java.io.*;

public class SimpleTCPClient {
    public static void main(String[] args) {
        try (Socket s1 = new Socket("localhost", 1254)) {

            DataOutputStream dos = new DataOutputStream(s1.getOutputStream());
            DataInputStream dis = new DataInputStream(s1.getInputStream());

            int number = 5;  // primer
            dos.writeInt(number);  
            int reply = dis.readInt();

            System.out.println("Server replied: " + reply);

            dos.close();
            dis.close();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
