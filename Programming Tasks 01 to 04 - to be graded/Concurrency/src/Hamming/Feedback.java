package Hamming;

import java.util.concurrent.atomic.AtomicBoolean;

public class Feedback implements Runnable {
    private final Q<Long> in, out;
    private final AtomicBoolean running;

    public Feedback(Q<Long> in, Q<Long> out, AtomicBoolean running) {
        this.in = in;
        this.out = out;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                out.put(in.take());
            }
        } catch (InterruptedException ignored) {}
    }
}
