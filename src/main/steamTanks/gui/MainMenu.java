package gui;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import bus.Message;
import bus.MessageBus;
import context.ContextInformation;
import input.InputMap;
import main.BuntePanzer;
import main.Settings;
import stateMachine.State;

public class MainMenu extends State {

	MessageBus bus;
	private boolean showMessagePopup = false;
	private String invalidMessage = "Set all Controls for all Tanks";

	public MainMenu(MessageBus bus) {
		super("MainMenu");
		playerTankWidget.add(new NkTankWidget());
		aiTankWidget = new NkTankWidget();
		aiTankWidget.name = "AI";
		aiTankWidget.isAI = true;
		this.bus = bus;
	}

	private IntBuffer players = BufferUtils.createIntBuffer(1).put(0, 1);
	private IntBuffer aiTank = BufferUtils.createIntBuffer(1).put(0, 1);
	private IntBuffer winningScore = BufferUtils.createIntBuffer(1).put(0, 10);

	private List<NkTankWidget> playerTankWidget = new ArrayList<>();
	private NkTankWidget aiTankWidget;

	@Override
	public void process(Object... params) {
		NkContext ctx = (NkContext) params[0];
		ContextInformation ci = (ContextInformation) params[1];
		int width = 640;
		int height = 480;
		int xPos = ci.renderBufferWidth / 2 - width / 2;
		int yPos = ci.renderBufferHeight / 2 - height / 2;
		try (MemoryStack stack = stackPush()) {
			NkRect rect = NkRect.mallocStack(stack);
			if (nk_begin(ctx, "Demo", nk_rect(xPos, yPos, width, height, rect),
					NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				nk_layout_row_dynamic(ctx, 30, 3);
				nk_spacing(ctx, 1);
				nk_label(ctx, "Bunte Panzer", NK_TEXT_CENTERED);
				nk_layout_row_dynamic(ctx, 30, 5);

				nk_property_int(ctx, "Players:", 0, players, 5, 1, 1);
				nk_spacing(ctx, 1);
				nk_property_int(ctx, "AI-Tanks:", 0, aiTank, 10, 1, 1);
				nk_spacing(ctx, 1);
				nk_property_int(ctx, "Score:", 1, winningScore, 100, 1, 1);
				nk_spacing(ctx, 1);
				int h = height / 2;
				nk_layout_row_dynamic(ctx, h, 1);
				Settings.unusedControlIndex = 0;
				if (nk_group_begin(ctx, "Panzer Farbwahl", NK_WINDOW_MINIMIZABLE)) {
					int count = playerTankWidget.size() + (aiTank.get(0) > 0 ? 1 : 0);
					nk_layout_row_static(ctx, h - 20, h / 2, count);
					playerTankWidget.stream().forEach(w -> w.draw(ctx));
					if (aiTank.get(0) > 0)
						aiTankWidget.draw(ctx);
					nk_group_end(ctx);
				}
				controlTankWidgets();
				nk_layout_row_dynamic(ctx, 30, 5);
				nk_spacing(ctx, 1);
				if (nk_button_label(ctx, "Controls")) {
					sendCommand("Controls");
				}
				nk_spacing(ctx, 1);
				if (nk_button_label(ctx, "Start Game")) {
					boolean readyToStart = true;
					Settings.playerCount = players.get(0);
					Settings.aiCount = aiTank.get(0);
					if (Settings.playerCount + Settings.aiCount <= 0) {
						readyToStart = false;
						showMessagePopup = true;
						invalidMessage = "Let at least one tank play";
					}
					Settings.winningScore = winningScore.get(0);
					Float[] hues = playerTankWidget.stream().map(t -> t.getHue()).toArray(Float[]::new);
					Settings.names = playerTankWidget.stream().map(w -> w.name).toArray(String[]::new);
					Settings.usedControlScheme = new InputMap[playerTankWidget.size()];
					for (int i = 0; i < Settings.usedControlScheme.length; i++) {
						int index = playerTankWidget.get(i).controlSchemeIndex;
						if (index == -1) {
							readyToStart = false;
							showMessagePopup = true;
							invalidMessage = "Set all Controls for all Tanks";
							break;
						}
						Settings.usedControlScheme[i] = Settings.controlScheme.get(index);
					}
					Settings.hue = new float[hues.length + 1];
					for (int i = 0; i < hues.length; i++) {
						Settings.hue[i] = hues[i];
					}
					Settings.hue[Settings.hue.length - 1] = aiTankWidget.getHue();
					if (readyToStart) {
						bus.sendMessage(new Message(this, "startGame", BuntePanzer.class));
						sendCommand("startGame");
					}
				}
			}
			showInvalidPopup(width / 2, height / 2, ctx, stack);
			nk_end(ctx);
		}
	}

	private void showInvalidPopup(float xPos, float yPos, NkContext ctx, MemoryStack stack) {
		if (showMessagePopup) {
			NkRect rect = NkRect.mallocStack(stack);
			int width = 240;
			int height = 144;
			if (nk_popup_begin(ctx, NK_POPUP_STATIC, "Controls arnt set", NK_WINDOW_BORDER,
					nk_rect(xPos - width / 2f, yPos - height / 2f, width, height, rect))) {
				nk_layout_row_dynamic(ctx, 30, 1);
				nk_spacing(ctx, 2);
				if (nk_button_label(ctx, invalidMessage)) {
					nk_popup_close(ctx);
					showMessagePopup = false;
				}
				nk_popup_end(ctx);
			}
		}
	}

	private void controlTankWidgets() {
		int wantedWidgets = players.get(0);
		while (playerTankWidget.size() > wantedWidgets) {
			playerTankWidget.remove(playerTankWidget.size() - 1);
		}
		while (playerTankWidget.size() < wantedWidgets) {
			playerTankWidget.add(new NkTankWidget());
		}

	}

	@Override
	public String transition(String command) {
		switch (command) {
		case "startGame":
			return "emptyScreen";
		case "Controls":
			return "Settings";
		}
		return "MainMenu";

	}

}
