package Task2;

import java.net.*;
import java.io.*;

public class SimpleUDPClient {
	public static void main(String[] args)
	{
		 DatagramSocket Socket = null;

	        try {
	            Socket = new DatagramSocket();
	            Socket.setSoTimeout(2000); // 2s timeout

	            Message msg = new Message(
	            	5,
	            	"client",
	            	System.currentTimeMillis()	
	            	);
	            
	            String serialized = msg.serialize();
	            byte[] data = serialized.getBytes();
	            
	            InetAddress Host = InetAddress.getByName("localhost");
	            int serverPort = 7000;

	            DatagramPacket request = new DatagramPacket(
	            		data,
	            		data.length,
	            		Host,
	            		serverPort
	            	);
	            Socket.send(request); 

	         // Prepare buffer for the response
	            byte[] buffer = new byte[1000];
	            DatagramPacket reply = new DatagramPacket(
	            		buffer,
	            		buffer.length
	            	);

	         // Waiting for a response from the server
	            Socket.receive(reply);

	            // Printing the response
	            String replyMsg = new String(reply.getData(), 0, reply.getLength());
	            System.out.println("Received from server: " + replyMsg);

	        } catch (SocketException e) {
	            System.out.println("Socket: " + e.getMessage());
	        } catch (IOException e) {
	            System.out.println("IO: " + e.getMessage());
	        } finally {
	            if (Socket != null)
	                Socket.close();
	        }
		
	}

}
