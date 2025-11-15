package Task1;

import java.net.*;
//import java.io.*;

public class SimpleUDPServer {
    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(7000);
            System.out.println("UDP Server is running...");

            byte[] buffer = new byte[1000];

            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            String received = new String(request.getData(), 0, request.getLength());
            int number = Integer.parseInt(received);
            number++;

            byte[] replyData = String.valueOf(number).getBytes();
            DatagramPacket reply = new DatagramPacket(
                    replyData,
                    replyData.length,
                    request.getAddress(),
                    request.getPort()
            );

            socket.send(reply);
            System.out.println("Reply sent. Server closing...");

        } catch (Exception e) {
            System.out.println("UDP Server error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
