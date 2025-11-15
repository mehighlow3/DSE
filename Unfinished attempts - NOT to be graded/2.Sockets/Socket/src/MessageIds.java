import java.util.concurrent.atomic.AtomicInteger;

public final class MessageIds {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    public static int next() {
        int v = SEQ.getAndIncrement();
        return v == 0 ? 1 : v; // sigurnost da ne vrati 0
    }

    private MessageIds() {}  // utility class
}
