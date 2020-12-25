package steamTanks.mainGame;

import java.awt.Color;
import java.util.Arrays;

import engine.bus.CommunicationNode;
import engine.bus.Message;
import engine.bus.MessageBus;
import graphics.context.ContextInformation;
import graphics.context.Display;
import graphics.context.NuklearGui;
import engine.coreEngine.Zeitgeist;
import engine.entities.EntityHandler;
import steamTanks.gui.Menu;
import engine.input.InputHandler;
import engine.map.MapHandler;
import graphics.particles.ParticleSystem;
import graphics.renderer.Camera;
import graphics.renderer.RenderEngine;
import engine.stateMachine.FiniteStateMachine;
import engine.stateMachine.State;

public class BuntePanzer implements CommunicationNode {

	private FiniteStateMachine gameState;

	protected Display display;
	protected NuklearGui guiHandler;
	protected ParticleSystem particleSystem;
	protected EntityHandler entityHandler;
	protected InputHandler inputHandler;
	protected MapHandler mapHandler;
	protected Camera camera;
	protected RenderEngine renderEngine;
	protected Zeitgeist zeitgeist = new Zeitgeist();
	protected MessageBus bus = new MessageBus();

	protected Menu menu;
	protected Gameplay gameplay;

	public static void main(String[] args) {
		BuntePanzer tanks = new BuntePanzer();

		tanks.gameLoop();
	}

	public BuntePanzer() {
		display = new Display(1920, 1080);
		display.setFrameTitle("Bunte Panzer");
		display.setClearColor(Color.gray);
		AssetHandler.loadSounds();
		AssetHandler.loadTextures();
		renderEngine = new RenderEngine();
		inputHandler = new InputHandler(display.getWindowId());
		guiHandler = new NuklearGui(inputHandler, display);
		menu = new Menu(guiHandler.getContext(), bus, inputHandler);
		guiHandler.add(menu);
		inputHandler.addInputMapping(menu.getInputMap());
		setupGameState();
		bus.add(this);
		bus.add(menu);
		Settings.init();
	}

	private void gameLoop() {
		while (!display.shouldBeClosed()) {
			gameState.process();
//			System.out.println("Count: "+bus.getNodeCount());
			display.flipBuffers();
			zeitgeist.sleep();
		}
		display.destroy();
		guiHandler.cleanUp();
	}

	public void menuLoop() {
		guiHandler.pollInputs();
		inputHandler.updateInputMaps();
		display.clear();
		ContextInformation ci = display.getContextInformation();
		renderEngine.render(ci);
		guiHandler.renderGUI(ci);
	}

	public void inGameLoop() {
		guiHandler.pollInputs();
		inputHandler.updateInputMaps();
		zeitgeist.updateSystems(zeitgeist.getDelta());
		display.clear();
		ContextInformation ci = display.getContextInformation();
		renderEngine.render(ci);
		guiHandler.renderGUI(ci);
	}

	private void startGame() {
		this.mapHandler = new MapHandler(bus);
		this.entityHandler = new CustomEntityHandler(mapHandler, bus);
		this.camera = new Camera();
		particleSystem = new ParticleSystem(mapHandler);
		this.zeitgeist.addTimeDependentSystem(entityHandler);
		this.renderEngine.addRenderAble(camera);
		camera.addRenderAble(entityHandler);
		camera.addRenderAble(mapHandler);
		camera.addRenderAble(particleSystem);
		gameplay = new Gameplay(this);
		gameplay.initTanks();
		gameplay.newgame();
		Arrays.stream(Settings.usedControlScheme).forEach(i -> inputHandler.addInputMapping(i));
	}

	private void stopGame() {
		this.mapHandler = null;
		this.inputHandler.remove(Settings.usedControlScheme);
		this.entityHandler.allEntities.clear();
		this.entityHandler = null;
		this.particleSystem.clear();
		this.particleSystem = null;
		this.zeitgeist.clear();
		this.camera.clear();
		this.camera = null;
		this.renderEngine.clear();
		this.bus.clearAll();
		bus.add(this);
		bus.add(menu);
		gameplay = null;
	}

	private void setupGameState() {
		gameState = new FiniteStateMachine();
		gameState.addNewState(new State("Menu") {

			@Override
			public String transition(String command) {
				switch (command) {
				case "startGame":
					startGame();
					return "inGame";
				case "continue":
					return "inGame";
				case "abortGame":
					stopGame();
					return "Menu";
				}
				return null;
			}

			@Override
			public void process(Object... params) {
				menuLoop();
			}
		});
		gameState.setState("Menu");
		gameState.addNewState(new State("inGame") {
			@Override
			public String transition(String command) {
				switch (command) {
				case "pauseGame":
					return "Menu";
				case "finishedGame":
					return "Menu";
				}
				return null;
			}

			@Override
			public void process(Object... params) {
				inGameLoop();
			}
		});
	}

	@Override
	public void procesMessage(Message m) {
		gameState.command(m.header);
	}

	@Override
	public Message answer(Message m) {
		return null;
	}

}
