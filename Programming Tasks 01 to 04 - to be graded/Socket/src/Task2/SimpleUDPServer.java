package Task2;

import java.net.*;
import java.io.*;

public class SimpleUDPServer {
	public static void main(String[] args)
	{
		
		DatagramSocket aSocket = null;

        try {
            aSocket = new DatagramSocket(7000);
            System.out.println("UDP Server is running...");

            byte[] buffer = new byte[1000];
            
                DatagramPacket request = new DatagramPacket(
                		buffer,
                		buffer.length
                	);
                aSocket.receive(request);
                
                String received = new String(request.getData(), 0, request.getLength());
                Message msg = Message.deserialize(received);
                
                System.out.println("Server received: " + msg);
                
                msg.setNumber(msg.getNumber() + 1);
                msg.setSender("server");
                msg.setTimestamp(System.currentTimeMillis());
                
                byte[] replyBytes = msg.serialize().getBytes();

                DatagramPacket reply = new DatagramPacket(
                    replyBytes,
                    replyBytes.length,
                    request.getAddress(),
                    request.getPort()
                );

                aSocket.send(reply);
                
                System.out.println("Server sent back: " + msg);
                System.out.println("Server terminating...");
            

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
		
		
	}

}
