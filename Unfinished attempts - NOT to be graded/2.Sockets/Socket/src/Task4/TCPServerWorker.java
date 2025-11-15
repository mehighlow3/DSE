package Task4;

import java.io.*;
import java.net.Socket;

public class TCPServerWorker implements Runnable {

    private Socket socket;

    public TCPServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                Message msg = (Message) in.readObject();

                if (msg.getNumber() == -1) break;

                msg.setNumber(msg.getNumber() + 1);
                msg.setSender("server");
                msg.setTimestamp(System.currentTimeMillis());

                out.writeObject(msg);
                out.flush();
            }

        } catch (Exception ignored) {}

        try { socket.close(); } catch (Exception ignored) {}
    }
}
