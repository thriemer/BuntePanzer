package engine.stateMachine;

public abstract class State {

	public String name;
	private FiniteStateMachine machine;

	public State(String name) {
		this.name = name;
	}

	public abstract void process(Object... params);

	public abstract String transition(String command);

	public void sendCommand(String cmd) {
		machine.command(cmd);
	}

	protected void setMachine(FiniteStateMachine machine) {
		this.machine = machine;
	}

}
