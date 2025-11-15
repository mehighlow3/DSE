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

            while(true)
            {
            		Message msg = (Message) in.readObject();
            		
            		if(msg.getNumber() == -1)
            		{
            			System.out.println("Termination msg received. Closing Server");
            			break;
            		}
            		
            		 System.out.println("Server received: number=" + msg.getNumber() + 
            				 ", dataSize=" + (msg.getData() == null ? 0 : msg.getData().length));

	            // Modify message
	            msg.setNumber(msg.getNumber() + 1);
	            msg.setSender("server");
	            msg.setTimestamp(System.currentTimeMillis());

            // Send back modified object
	            out.writeObject(msg);
	            out.flush();

            }
            

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

