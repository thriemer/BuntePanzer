package gui;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;

import bus.CommunicationNode;
import bus.Message;
import bus.MessageBus;
import context.ContextInformation;
import context.GUI;
import input.InputHandler;
import input.InputMap;
import input.KeyboardInputMap;
import main.BuntePanzer;
import stateMachine.FiniteStateMachine;
import stateMachine.State;

public class Menu implements GUI,CommunicationNode {

	private FiniteStateMachine menuState;
	private InputMap inputMap;
	private WinningScreen ws;

	public Menu(NkContext ctx,MessageBus bus,InputHandler input) {
		menuState = new FiniteStateMachine();
		menuState.addNewState(new MainMenu(bus));
		menuState.setState("MainMenu");
		menuState.addNewState(new State("emptyScreen") {
			@Override
			public String transition(String command) {
				switch (command) {
				case "escape":
					bus.sendMessage(new Message(this, "pauseGame", BuntePanzer.class));
					return "PauseScreen";
				case "showWinningScreen":
					return "WinningScreen";
				}
				return "emptyScreen";
			}

			@Override
			public void process(Object... params) {
			}
		});
		ws = new WinningScreen(bus);
		menuState.addNewState(new PauseScreen(bus));
		menuState.addNewState(ws);
		menuState.addNewState(new NkInputMapEditor(input));
		inputMap = new KeyboardInputMap().addMapping("Escape", GLFW.GLFW_KEY_ESCAPE);		
	}

	public void show(NkContext ctx,ContextInformation ci) {
		handleInput();
		menuState.process(ctx,ci);
	}

	private void handleInput() {
		if (inputMap.getValueForAction("Escape") > 0) {
			menuState.command("escape");
		}
	}

	public InputMap getInputMap() {
		return inputMap;
	}

	@Override
	public void procesMessage(Message m) {
		if(m.header.equals("finishedGame")) {
			menuState.command("showWinningScreen");
			ws.winnerName=(String)m.params[0];
			ws.hue=(float)m.params[1];
		}
	}

	@Override
	public Message answer(Message m) {
		return null;
	}

}