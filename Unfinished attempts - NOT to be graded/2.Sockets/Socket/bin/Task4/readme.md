For Task 4, I used 8 client threads for both TCP and UDP experiments, with 1000 total iterations distributed equally among the threads and a payload size of 16 KiB.

For TCP, I repeated the measurement three times. Per-thread average round-trip times for each run were slightly different due to OS scheduling and background activity, but all results were in the same range (≈ 1.3–1.9 ms). The overall average RTT across all TCP runs and threads was about 1.58 ms.TCP was stable in every run, and all 8 threads always completed all 125 iterations successfully.

For the UDP version, the behavior was not fully deterministic, which is expected.
In some runs, all 8 threads finished their 125 iterations and produced average RTT values of around 5 ms each.
But in other runs, only a part of the threads completed all iterations (e.g., 1–3 threads), while the others got stuck waiting for missing fragments. (packet loss at the UDP layer).

This happened because UDP: does not guarantee delivery, does not retransmit lost packets, provides no ordering or flow control, and large messages must be fragmented manually.
If even one fragment is lost, the entire message cannot be reconstructed, and that thread never receives a response.

Example UDP output where all 8 threads finished: 

Starting 8 UDP threads...
UDP THREAD STARTED: pool-1-thread-6
UDP THREAD STARTED: pool-1-thread-1
UDP THREAD STARTED: pool-1-thread-2
UDP THREAD STARTED: pool-1-thread-5
UDP THREAD STARTED: pool-1-thread-3
UDP THREAD STARTED: pool-1-thread-4
UDP THREAD STARTED: pool-1-thread-7
UDP THREAD STARTED: pool-1-thread-8
pool-1-thread-1 UDP avg RTT = 4595495 ns
pool-1-thread-2 UDP avg RTT = 4941947 ns
pool-1-thread-5 UDP avg RTT = 4899551 ns
pool-1-thread-4 UDP avg RTT = 5001589 ns
pool-1-thread-6 UDP avg RTT = 5126751 ns
pool-1-thread-7 UDP avg RTT = 5120051 ns
pool-1-thread-8 UDP avg RTT = 5170148 ns
pool-1-thread-3 UDP avg RTT = 5178360 ns
Sent single termination to server.
UDP Client MT finished.

Example TCP output : 

Starting 8 threads...
pool-1-thread-6 TCP avg RTT = 1363668 ns
pool-1-thread-8 TCP avg RTT = 1269456 ns
pool-1-thread-3 TCP avg RTT = 1408330 ns
pool-1-thread-4 TCP avg RTT = 1535348 ns
pool-1-thread-2 TCP avg RTT = 1638638 ns
pool-1-thread-1 TCP avg RTT = 1887105 ns
pool-1-thread-7 TCP avg RTT = 1899572 ns
pool-1-thread-5 TCP avg RTT = 1984160 ns