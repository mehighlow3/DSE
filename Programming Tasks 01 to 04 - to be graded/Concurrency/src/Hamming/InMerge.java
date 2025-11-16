package Hamming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMerge implements Runnable {
    private final Q<Long> in2, in3, in5;
    private final Q<Long> out;
    private final AtomicBoolean running;

    public InMerge(Q<Long> in2, Q<Long> in3, Q<Long> in5, Q<Long> out, AtomicBoolean running) {
        this.in2 = in2;
        this.in3 = in3;
        this.in5 = in5;
        this.out = out;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                long a = in2.take();
                long b = in3.take();
                long c = in5.take();
                
                ArrayList<Long> a1 = new ArrayList<Long>();
                a1.add(a);
                a1.add(b);
                a1.add(c);
                Collections.sort(a1);

                out.put(a1.get(0));
                out.put(a1.get(1));
                out.put(a1.get(2));
            }
        } catch (InterruptedException ignored) {}
    }
}