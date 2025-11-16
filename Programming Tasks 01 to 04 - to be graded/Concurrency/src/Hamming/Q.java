package Hamming;

public interface Q<T> //interface for shared type
{
    void put(T x) throws InterruptedException;
    T take() throws InterruptedException;
}
