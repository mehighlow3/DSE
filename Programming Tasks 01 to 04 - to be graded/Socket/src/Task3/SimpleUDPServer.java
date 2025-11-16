package Task3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class SimpleUDPServer {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(7000);
            System.out.println("UDP Server Task 3 running on port 7000...");

            // buffer can be slightly larger than the expected fragment
            byte[] buf = new byte[2048];

            while (true) {
                DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                socket.receive(pkt);

                // parse header: msgId|total|index|<chunk...>
                String head = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8);
                // split with limit 4 (so the chunk can contain '|')
                String[] parts = head.split("\\|", 4);
                if (parts.length < 4) {
                    // invalid packet – ignore
                    continue;
                }

                int msgId   = Integer.parseInt(parts[0]);
                int total   = Integer.parseInt(parts[1]);
                int index   = Integer.parseInt(parts[2]);
                String chunkStr = parts[3];
                byte[] chunkBytes = chunkStr.getBytes(StandardCharsets.UTF_8);

                String fullPayload = UDPDefragmenter.addFragment(msgId, total, index, chunkBytes);
                if (fullPayload == null) {
                    // full message not received yet
                    continue;
                }

                // full payload received → deserialize Message
                Message m = Message.deserialize(fullPayload);

                // termination?
                if (m.getNumber() == -1) {
                    System.out.println("Termination message received. Server closing...");
                    break;
                }

                // TASK 3 logic: increment + metadata
                m.setNumber(m.getNumber() + 1);
                m.setSender("server");
                m.setTimestamp(System.currentTimeMillis());

                // serialize back and send as fragmented reply
                String replyPayload = m.serialize();
                int replyMsgId = (int) (Math.random() * 1_000_000);

                for (DatagramPacket outPkt : UDPFragmenter.fragment(
                        replyPayload, pkt.getAddress(), pkt.getPort(), replyMsgId)) {
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
