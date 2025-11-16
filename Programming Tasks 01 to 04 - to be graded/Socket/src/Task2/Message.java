package Task2;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private int number;
    private String sender;
    private long timestamp;

    public Message(int number, String sender, long timestamp) {
        this.number = number;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getters
    public int getNumber() { return number; }
    public String getSender() { return sender; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setNumber(int number) { this.number = number; }
    public void setSender(String sender) { this.sender = sender; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

 
    //manual marshalling
    public String serialize() {
        return number + "|" + sender + "|" + timestamp;
    }

    //unmarshalling
    public static Message deserialize(String s) {
        String[] parts = s.split("\\|");

        int num = Integer.parseInt(parts[0]);
        String snd = parts[1];
        long ts = Long.parseLong(parts[2]);

        return new Message(num, snd, ts);
    }

    @Override
    public String toString() {
        return "[number=" + number
             + ", sender=" + sender
             + ", timestamp=" + timestamp + "]";
    }
}
