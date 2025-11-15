import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UDPDefragmenter {

    private static class Partial {
        int total = -1;
        Map<Integer, byte[]> chunks = new HashMap<>();
    }

    // msgId -> partial
    private static final Map<Integer, Partial> store = new HashMap<>();

    /** Add one fragment's raw bytes (no header here; caller passes only chunk bytes). */
    public static synchronized byte[] addFragment(int msgId, int total, int index, byte[] chunkData) {
        Partial p = store.computeIfAbsent(msgId, k -> new Partial());
        p.total = total;
        p.chunks.put(index, chunkData);

        if (p.total > 0 && p.chunks.size() == p.total) {
            int totalBytes = 0;
            for (int i = 0; i < p.total; i++) totalBytes += p.chunks.get(i).length;

            byte[] all = new byte[totalBytes];
            int pos = 0;
            for (int i = 0; i < p.total; i++) {
                byte[] part = p.chunks.get(i);
                System.arraycopy(part, 0, all, pos, part.length);
                pos += part.length;
            }
            store.remove(msgId);
            return all;
        }
        return null;
    }

    // Convenience if your Message.serialize() is String-based:
    public static String addFragmentAsString(int msgId, int total, int index, byte[] chunkData) {
        byte[] all = addFragment(msgId, total, index, chunkData);
        return all == null ? null : new String(all, StandardCharsets.UTF_8);
    }
}
