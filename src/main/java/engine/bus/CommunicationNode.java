package engine.bus;

public interface CommunicationNode {	
	void procesMessage(Message m);
	Message answer(Message m);

}
