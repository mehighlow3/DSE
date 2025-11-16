package Task3;

import java.io.Serializable;
import java.util.Base64;


public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int number;
	private String sender;
	private long timestamp;
	private byte[] data;
	
	public Message(int number, String sender, long timestamp, byte[] data)
	{
		this.number = number;
		this.sender = sender;
		this.timestamp = timestamp;
		this.data = data;
	}
	
	public int getNumber() {return number; }
	public String getSender() {return sender; }
	public long getTimestamp() {return timestamp; }
	public byte[] getData() {return data; }
	
	public void setNumber(int number) {this.number = number; }
	public void setSender(String sender) {this.sender = sender; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public void setData(byte data[]) {this.data = data; }
	
	public String serialize()
	{
		String encoded = Base64.getEncoder().encodeToString(data);
		return number + "|" + sender + "|" + timestamp + '|' + encoded;
	}
	
	public static Message deserialize(String S)
	{
		String[] parts = S.split("\\|");
		int num = Integer.parseInt(parts[0]);
		String snd = parts[1];
		long ts = Long.parseLong(parts[2]);
		byte[] decoded = Base64.getDecoder().decode(parts[3]);
		
		return new Message(num, snd, ts, decoded);
	}
	
	@Override
	public String toString() {
		return "[number=" + number + ", sender=" + sender + ", timestamp=" + timestamp +  ", dataSize=" + data.length +  "]";
	}

}
