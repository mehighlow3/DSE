package Hamming;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Copy implements Runnable // runnable je java interface koji koristi run
{
    private final Q<Long> in;
    private final List<Q<Long>> outs;
    private final AtomicBoolean running;

    public Copy(Q<Long> in, List<Q<Long>> outs, AtomicBoolean running) {
        this.in = in;
        this.outs = outs;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                long x = in.take();
                for (Q<Long> o : outs) {
                    o.put(x);
                }
            }
        } catch (InterruptedException ignored) {}
    }
}