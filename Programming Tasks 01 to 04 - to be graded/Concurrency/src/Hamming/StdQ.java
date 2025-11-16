package Hamming;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StdQ<T> implements Q<T>  // wrapper around Java’s built-in LinkedBlockingQueue; implements Q<T> so it must provide methods defined in the interface
{
    private final BlockingQueue<T> q; 
    // BlockingQueue is an interface (a contract for a queue that can block).
    // final = the reference cannot change (q cannot be reassigned), but the object itself can be modified (q.put, q.take)

    public StdQ(int capacity) {
        // LinkedBlockingQueue is a concrete implementation of BlockingQueue.
        // FIFO queue; supports put() and take().
        // if capacity <= 0 → treat it as “unbounded”
        q = new LinkedBlockingQueue<>(capacity <= 0 ? Integer.MAX_VALUE : capacity);
    }

    @Override   // compiler annotation: confirms we are overriding a method from the interface (polymorphism)
    public void put(T x) throws InterruptedException {
        // inserts an element into the queue; if the queue is full, the thread blocks until space becomes available
        q.put(x);
    }

    @Override
    public T take() throws InterruptedException {
        // retrieves and returns an element; if the queue is empty, the thread blocks until an element appears
        return q.take();
    }
}
