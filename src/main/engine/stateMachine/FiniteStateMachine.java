package stateMachine;

import java.util.HashMap;
import java.util.Map;

public class FiniteStateMachine implements CommandListener {

	Map<String, State> states = new HashMap<>();
	State currentState;

	public void command(String command) {
		String newStateName = currentState.transition(command);
		if (newStateName == null) {
			System.out.println("Command: " + command + " for state: " + currentState.name + " has no valid transition");
		}
		currentState = states.get(newStateName);
	}

	public void process(Object... params) {
		currentState.process(params);
	}

	public void addNewState(State state) {
		states.put(state.name, state);
		state.setMachine(this);
	}

	public void setState(String string) {
		currentState = states.get(string);
	}

	@Override
	public void handleCommand(String cmd) {
		command(cmd);
	}

}
