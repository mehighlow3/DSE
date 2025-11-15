package Task4;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServerMT {

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(16);

        try (ServerSocket server = new ServerSocket(1254)) {

            System.out.println("TCP Server MT running...");

            while (true) {
                Socket client = server.accept();
                pool.submit(new TCPServerWorker(client));
            }

        } catch (Exception e) {
            System.out.println("TCPServerMT error: " + e.getMessage());
        }
    }
}
