package Hamming;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class Print implements Runnable {
    private final Q<Long> in;
    private final int limit;
    private final AtomicBoolean running;
    private final CountDownLatch done;

    public Print(Q<Long> in, int limit, AtomicBoolean running, CountDownLatch done) {
        this.in = in;
        this.limit = limit;
        this.running = running;
        this.done = done;
    }

    @Override
    public void run() {
        try {
            int count = 0;
            while (running.get() && count < limit) {
                long x = in.take();
                System.out.print(x);
                count++;
                if (count < limit) System.out.print(", ");
                if (count % 25 == 0) System.out.println();
            }
            System.out.println();
        } catch (InterruptedException ignored) {
        } finally {
            running.set(false);
            done.countDown();
        }
    }
}
