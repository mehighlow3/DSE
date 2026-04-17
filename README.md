# Advanced Distributed Systems & Concurrent Computing (Java)

This repository contains high-performance engineering implementations focused on **Concurrency Primitives** and **Distributed Network Protocols**. Developed for the MSc Computer Science curriculum at the University of Vienna.

---

## 🏗️ Task 1: Concurrent Hamming Number Generator
**Architecture:** Multi-threaded Dataflow Pipeline.

This project implements an asynchronous generator for Hamming numbers (prime factors 2, 3, 5) using a pipeline of specialized worker threads.

* **Low-Level Synchronization:** Built a `CustomBlockingQueue` using the **Wait/Notify** pattern to handle thread synchronization, ensuring a deep understanding of monitor locks and thread safety.
* **Pipeline Architecture:** Utilized specialized workers (`Copy`, `MultByN`, `InMerge`, `Feedback`) acting as independent processing units.
* **Orchestration:** Managed thread lifecycles using `CountDownLatch` and `AtomicBoolean` for deterministic termination.

---

## 🌐 Task 2: TCP/UDP Protocol Performance Benchmarking
**Architecture:** Multithreaded Transport Layer Analysis.

A comparative study of RTT and reliability between connection-oriented (TCP) and connectionless (UDP) protocols under heavy load (16 KiB payloads).

* **Reliability Engineering:** Developed a manual **UDP Fragmentation/Defragmentation** layer to handle payloads exceeding MTU limits, implementing sequence numbering and message reassembly.
* **Network Profiling:** Benchmarked TCP stability (~1.58ms RTT) against UDP’s nondeterministic behavior and reconstruction failures under packet loss.
* **Resource Management:** Optimized throughput using `ExecutorService` thread pools for concurrent client-server communication.

---
**Developed by:** Mihajlo Katić  
*University of Vienna - Computer Science*
