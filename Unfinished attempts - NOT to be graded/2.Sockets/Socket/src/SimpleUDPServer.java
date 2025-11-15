import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SimpleUDPServer {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(7000);
            System.out.println("UDP Server Task 3 running on port 7000...");

            byte[] buf = new byte[2048];

            while (true) {
                DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                socket.receive(pkt);

                byte[] data = pkt.getData();
                int len = pkt.getLength();

                // find 3rd '|'
                int sepCount = 0;
                int headerEnd = -1;
                for (int i = 0; i < len; i++) {
                    if (data[i] == (byte)'|') {
                        sepCount++;
                        if (sepCount == 3) { headerEnd = i; break; }
                    }
                }
                if (headerEnd == -1) continue;

                String header = new String(data, 0, headerEnd + 1, StandardCharsets.US_ASCII);
                String[] parts = header.split("\\|");
                if (parts.length < 3) continue;

                int msgId = Integer.parseInt(parts[0]);
                int total = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);

                // chunk is text (base64 part of your Message)
                int chunkLen = len - (headerEnd + 1);
                byte[] chunk = Arrays.copyOfRange(data, headerEnd + 1, len);

                String full = UDPDefragmenter.addFragmentAsString(msgId, total, index, chunk);
                if (full == null) continue;

                Message m = Message.deserialize(full);

                if (m.getNumber() == -1) {
                    System.out.println("Termination message received.");
                    break;
                }

                m.setNumber(m.getNumber() + 1);
                m.setSender("server");
                m.setTimestamp(System.currentTimeMillis());

                String replyPayload = m.serialize();
                int replyId = (int)(Math.random() * 1_000_000);

                for (DatagramPacket outPkt :
                        UDPFragmenter.fragment(replyPayload, pkt.getAddress(), pkt.getPort(), replyId)) {
                    socket.send(outPkt);
                }
            }

        } catch (Exception e) {
            System.out.println("UDP Server error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
