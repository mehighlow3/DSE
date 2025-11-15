
package Task4;

public class UDPDefragmenter {

    private final byte[][] chunks;
    public final int total;
    private int received = 0;

    public UDPDefragmenter(int total) {
        this.total = total;
        this.chunks = new byte[total][];
    }

    /**
     * Dodaj fragment i vrati ceo payload kada su svi tu; inače null.
     */
    public synchronized byte[] addFragment(int index, byte[] chunkData) {
        if (index < 0 || index >= total) return null;

        if (chunks[index] == null) {
            chunks[index] = chunkData;
            received++;
        }

        if (received == total) {
            int totalBytes = 0;
            for (int i = 0; i < total; i++) {
                totalBytes += chunks[i].length;
            }

            byte[] all = new byte[totalBytes];
            int pos = 0;
            for (int i = 0; i < total; i++) {
                System.arraycopy(chunks[i], 0, all, pos, chunks[i].length);
                pos += chunks[i].length;
            }
            return all;
        }

        return null;
    }
}
