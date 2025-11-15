package Hamming;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomBlockingQueue<T> implements Q<T>  // version implemented using wait/notify
{
    private final Deque<T> deque = new ArrayDeque<>();  // FIFO queue
    private final int capacity;                         // <= 0 means unbounded
    private final Object Lock = new Object();           // dedicated lock object for synchronization

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(T x) throws InterruptedException
    {
        synchronized (Lock)  // acquire the lock (no other thread can modify the queue while inside this block)
        {
            while (capacity > 0 && deque.size() >= capacity)
            {
                // temporarily releases the Lock monitor and suspends the thread
                Lock.wait();
            }

            deque.addLast(x);

            // wakes up all threads that may be waiting (e.g., those calling take() because the queue was empty)
            Lock.notifyAll();
        }
    }

    @Override
    public T take() throws InterruptedException {
        synchronized (Lock) {
            while (deque.isEmpty()) {
                // waits until an element appears in the queue
                Lock.wait();
            }

            T v = deque.removeFirst();

            // notifies waiting producers that there is now space available
            Lock.notifyAll();
            return v;
        }
    }
}
