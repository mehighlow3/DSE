package Task4;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UDPFragmenter {

    private static final int MAX_CHUNK_BYTES = 1200;

    public static List<DatagramPacket> fragment(
            String payload, InetAddress address, int port, int msgId) {

        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        int totalLen = bytes.length;

        int totalChunks = (int) Math.ceil((double) totalLen / MAX_CHUNK_BYTES);
        if (totalChunks == 0) totalChunks = 1;

        List<DatagramPacket> packets = new ArrayList<>(totalChunks);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * MAX_CHUNK_BYTES;
            int end = Math.min(start + MAX_CHUNK_BYTES, totalLen);

            int chunkLen = end - start;
            byte[] chunk = new byte[chunkLen];
            System.arraycopy(bytes, start, chunk, 0, chunkLen);

            String header = msgId + "|" + totalChunks + "|" + i + "|";
            byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);

            byte[] full = new byte[headerBytes.length + chunk.length];
            System.arraycopy(headerBytes, 0, full, 0, headerBytes.length);
            System.arraycopy(chunk, 0, full, headerBytes.length, chunk.length);

            packets.add(new DatagramPacket(full, full.length, address, port));
        }

        return packets;
    }
}

