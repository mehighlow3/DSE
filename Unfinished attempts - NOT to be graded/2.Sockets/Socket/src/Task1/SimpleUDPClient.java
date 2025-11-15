package Task1;

import java.net.*;
//import java.io.*;

public class SimpleUDPClient {
    public static void main(String[] args) {

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();

            int number = 5;
            byte[] data = String.valueOf(number).getBytes();

            InetAddress host = InetAddress.getByName("localhost");
            int port = 7000;

            DatagramPacket request =
                    new DatagramPacket(data, data.length, host, port);
            socket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            socket.receive(reply);

            String received = new String(reply.getData(), 0, reply.getLength());
            System.out.println("UDP reply: " + received);

        } catch (Exception e) {
            System.out.println("UDP Client error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
