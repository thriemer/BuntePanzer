package engine.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBus {

	private Map<Class<?>, List<CommunicationNode>> allNodes = new HashMap<>();

	public void sendMessage(Message m) {
		for (Class<?> c : m.reciever) {
			List<CommunicationNode> recievers = allNodes.get(c);
			if (recievers != null) {
				for (CommunicationNode cn : recievers) {
					cn.procesMessage(m);
				}
			} else {
				System.out.println("Dead message: " + c.getName() + " " + m.toString());
			}
		}
	}

	public Message[] request(Message m) {
		if (m.reciever.length != 1) {
			System.out.println("You have to request from one type at a time");
		} else {
			List<CommunicationNode> recievers = allNodes.get(m.reciever[0]);
			return recievers.stream().map(cn -> cn.answer(m)).toArray(Message[]::new);
		}
		return null;
	}

	public void add(CommunicationNode cn) {
		List<CommunicationNode> nodes = allNodes.get(cn.getClass());
		if (nodes == null) {
			allNodes.put(cn.getClass(), new ArrayList<CommunicationNode>());
		}
		allNodes.get(cn.getClass()).add(cn);
	}

	public void remove(CommunicationNode cn) {
		allNodes.get(cn.getClass()).remove(cn);
	}
	
	public void remove(Class<?> type) {
		allNodes.remove(type);
	}
	
	public int getNodeCount() {
		int count =0;
		for(Class<?> clazz:allNodes.keySet()) {
			System.out.println(clazz.getCanonicalName()+" "+allNodes.get(clazz).size());
			count+=allNodes.get(clazz).size();
		}
		return count;
	}

	public void clearAll() {
		allNodes.clear();
	}

}
