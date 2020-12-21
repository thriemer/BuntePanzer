package bus;

public interface CommunicationNode {	
	public void procesMessage(Message m);
	public Message answer(Message m);

}
