package engine.bus;

public class Message {

	public Object sender;
	public Class<?>[] reciever;
	public String header;
	public Object[] params;

	public Message(Object sender, Class<?>[] reciever, String header, Object[] params) {
		this(sender,header);
		this.reciever = reciever;
		this.params = params;
	}
	public Message(Object sender, String header) {
		this.sender = sender;
		this.header = header;
	}
	
	public Message(Object sender, String header,Class<?> reciever) {
		this.sender = sender;
		this.header = header;
		setRecievers(reciever);
	}
	public Message(Object sender, String header,Class<?> reciever,Object... params) {
		this.sender = sender;
		this.header = header;
		this.params=params;
		setRecievers(reciever);
	}
	public Message setRecievers(Class<?>... reciever) {
		this.reciever=reciever;
		return this;
	}
	public Message setParameters(Object... params) {
		this.params=params;
		return this;
	}
	
	public String toString() {
		return sender+" "+reciever+" "+header+" "+params;
	}
}