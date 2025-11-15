package Hamming;

import java.util.*; // List, Arrays, Thread, Collections
import java.util.concurrent.CountDownLatch; // allows one thread to wait until other threads finish (here the "print" thread signals when it’s done)
import java.util.concurrent.atomic.AtomicBoolean; // atomic boolean, safe for use across multiple threads (provides synchronization)

public class App {

    static <T> Q<T> mkQueue(boolean useCustom, int capacity) // method that creates a queue, <T> = template (any type)
    {
        return useCustom ? new CustomBlockingQueue<>(capacity) : new StdQ<>(capacity);
    }

    public static void main(String[] args) throws Exception
    {
        final int N = (args.length > 0) ? Integer.parseInt(args[0]) : 50; 
        // final = constant  
        // final int N = 100000;

        final boolean useCustom = true; // true = use your custom queue implementation

        final AtomicBoolean running = new AtomicBoolean(true);

        Q<Long> qCopyIn  = mkQueue(useCustom, 0);
        Q<Long> qToMult2 = mkQueue(useCustom, 0);
        Q<Long> qToMult3 = mkQueue(useCustom, 0);
        Q<Long> qToMult5 = mkQueue(useCustom, 0);
        Q<Long> qFrom2   = mkQueue(useCustom, 0);
        Q<Long> qFrom3   = mkQueue(useCustom, 0);
        Q<Long> qFrom5   = mkQueue(useCustom, 0);
        Q<Long> qMerged  = mkQueue(useCustom, 0);
        Q<Long> qToPrint = mkQueue(useCustom, 0);

        Copy copy = new Copy(qCopyIn, Arrays.asList(qToMult2, qToMult3, qToMult5, qToPrint), running);
        MultByN mult2 = new MultByN(2, qToMult2, qFrom2, running);
        MultByN mult3 = new MultByN(3, qToMult3, qFrom3, running);
        MultByN mult5 = new MultByN(5, qToMult5, qFrom5, running);
        InMerge merge = new InMerge(qFrom2, qFrom3, qFrom5, qMerged, running);
        Feedback feedback = new Feedback(qMerged, qCopyIn, running);
        
        // CountDownLatch is a synchronization mechanism—allows one or more threads 
        // to wait until a specific number of events are completed.
        CountDownLatch done = new CountDownLatch(1);

        Print printer = new Print(qToPrint, N, running, done);

        List<Thread> threads = Arrays.asList(
                new Thread(copy, "copy"),      // (runnableOBJ, name) → calls the run() method of the runnable
                new Thread(mult2, "mult2"),
                new Thread(mult3, "mult3"),
                new Thread(mult5, "mult5"),
                new Thread(merge, "merge"),
                new Thread(feedback, "feedback"),
                new Thread(printer, "print")
        );

        threads.forEach(Thread::start); 
        // List.forEach(lambda) = apply function to every element  
        // same as a for-loop

        // Start by injecting the initial value
        qCopyIn.put(1L); // 1 as long

        done.await(); // wait until printing completes N numbers

        running.set(false); 
        // “Send a signal to all threads that they should stop working.”

        threads.forEach(Thread::interrupt);  
        // “Send an interrupt signal to all threads in the list.”
        // If a thread is inside a loop, it may still continue running until take() / wait() reacts.

        for (Thread t : threads) t.join(); 
        // wait for each thread to finish
    }
}
