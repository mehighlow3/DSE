package Hamming;

import java.util.concurrent.atomic.AtomicBoolean;

public class MultByN implements Runnable {
    private final long n;
    private final Q<Long> in, out;
    private final AtomicBoolean running;

    public MultByN(long n, Q<Long> in, Q<Long> out, AtomicBoolean running) {
        this.n = n;
        this.in = in;
        this.out = out;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                long x = in.take();
                out.put(x * n);
            }
        } catch (InterruptedException ignored) {}
    }
}
