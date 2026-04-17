# Advanced Distributed Systems & Concurrent Computing (Java)

This repository contains high-performance engineering implementations focused on **Concurrency Primitives** and **Distributed Network Protocols**. Developed for the MSc Computer Science curriculum at the University of Vienna.

---

## Task 1: Concurrent Hamming Number Generator
**Architecture:** Multi-threaded Dataflow Pipeline.

This project implements an asynchronous generator for Hamming numbers (prime factors 2, 3, 5) using a pipeline of specialized worker threads.

* **Low-Level Synchronization:** Built a `CustomBlockingQueue` using the **Wait/Notify** pattern to handle thread synchronization, ensuring a deep understanding of monitor locks and thread safety.
* **Pipeline Architecture:** Utilized specialized workers (`Copy`, `MultByN`, `InMerge`, `Feedback`) acting as independent processing units.
* **Orchestration:** Managed thread lifecycles using `CountDownLatch` and `AtomicBoolean` for deterministic termination.

---

## Task 2: TCP/UDP Protocol Performance Benchmarking
**Architecture:** Multithreaded Transport Layer Analysis.

A comparative study of RTT and reliability between connection-oriented (TCP) and connectionless (UDP) protocols under heavy load (16 KiB payloads).

* **Reliability Engineering:** Developed a manual **UDP Fragmentation/Defragmentation** layer to handle payloads exceeding MTU limits, implementing sequence numbering and message reassembly.
* **Network Profiling:** Benchmarked TCP stability (~1.58ms RTT) against UDP’s nondeterministic behavior and reconstruction failures under packet loss.
* **Resource Management:** Optimized throughput using `ExecutorService` thread pools for concurrent client-server communication.

## Task 3 and 4: Meeting Scheduler API & Microservice
**Architecture:** Contract-First RESTful Design & Spring Boot Service.

This project demonstrates a full-cycle backend engineering workflow, transitioning from a formal design specification to a functional, production-ready microservice.

* **Phase A: API Design (OpenAPI 3.0):** Adopted a **Design-First approach** to ensure system architecture was standardized before implementation.
* **Endpoint Definition:** Mapped the complete meeting lifecycle—from creation and draft persistence to publishing and voting logic—within a structured \`api.yaml\`.
* **Phase B: Spring Boot Implementation:** Translated the OpenAPI specification into a functional service using **Spring Boot**, maintaining strict parity between the contract and the code.
* **Stateful Logic:** Developed core controllers to manage complex transitions, ensuring meetings move correctly through **Draft**, **Published**, and **Completed** states.
* **Automated Verification:** Engineered a comprehensive test suite in **JUnit** to automate the verification of the end-to-end workflow: \`Create -> Publish -> Verify\`.
* **Developer Experience:** Integrated **Swagger UI** directly into the running application to provide an interactive interface for real-time manual testing.

---
**Technical Stack:** Java, Spring Boot, Maven, OpenAPI 3.0, Swagger UI, JUnit.
---
**Developed by:** Mihajlo Katić  
*University of Vienna - Computer Science*
