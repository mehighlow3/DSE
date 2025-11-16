package Task4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClientMT {

    static int TOTAL = 1000;
    static int THREADS = 8;
    static int SIZE_KIB = 16;

    public static void main(String[] args) {

        int perThread = TOTAL / THREADS;
        int dataSize = SIZE_KIB * 1024;
        
        System.out.println("Starting " + THREADS + " threads...");

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            pool.submit(new TCPClientWorker(perThread, dataSize));
        }

        pool.shutdown();
    }
}
