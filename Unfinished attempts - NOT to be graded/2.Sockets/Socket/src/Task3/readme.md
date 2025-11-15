For Task 3, I measured the average round-trip time (RTT) for different message sizes (1, 2, 4, 8, 16, and 128 KiB for TCP; 1–32 KiB for UDP), using 1000 iterations per size.
Each message contained a number, sender name, timestamp, and a data blob.

TCP handled all message sizes, including the largest 128 KiB message.
The average RTT decreased after the smallest sizes and then stabilized:

--- Testing data size: 1 KiB ---
Average RTT for 1 KiB = 561633 ns

--- Testing data size: 2 KiB ---
Average RTT for 2 KiB = 220341 ns

--- Testing data size: 4 KiB ---
Average RTT for 4 KiB = 163890 ns

--- Testing data size: 8 KiB ---
Average RTT for 8 KiB = 167296 ns

--- Testing data size: 16 KiB ---
Average RTT for 16 KiB = 137915 ns

--- Testing data size: 128 KiB ---
Average RTT for 128 KiB = 148500 ns

Client terminating...

TCP was stable for all payloads because the protocol automatically takes care of segmentation, ordering, and retransmission. No special handling was required in the implementation.

For UDP, I tested sizes up to 32 KiB.
Larger payloads (64 KiB and 128 KiB) could not be completed because the number of required fragments was too high, causing packet loss and failed reassembly.


--- UDP testing data size: 1 KiB ---
Average UDP RTT for 1 KiB = 511083 ns

--- UDP testing data size: 2 KiB ---
Average UDP RTT for 2 KiB = 278338 ns

--- UDP testing data size: 4 KiB ---
Average UDP RTT for 4 KiB = 280788 ns

--- UDP testing data size: 8 KiB ---
Average UDP RTT for 8 KiB = 309807 ns

--- UDP testing data size: 16 KiB ---
Average UDP RTT for 16 KiB = 559585 ns

--- UDP testing data size: 32 KiB ---
Average UDP RTT for 32 KiB = 980799 ns

UDP client done.

UDP performance became noticeably worse for larger messages due to manual fragmentation, Base64 overhead, and increased probability of fragment loss.
If even one fragment is dropped, the entire message cannot be reconstructed.

Overall, TCP provided consistently better performance and reliability in this experiment, while UDP required additional logic and still could not successfully handle very large payloads.