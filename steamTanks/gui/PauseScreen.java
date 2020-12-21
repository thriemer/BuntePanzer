package gui;

import static org.lwjgl.nuklear.Nuklear.NK_TEXT_CENTERED;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BACKGROUND;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_button_label;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_label;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_push;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_spacing;
import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import bus.Message;
import bus.MessageBus;
import context.ContextInformation;
import main.BuntePanzer;
import stateMachine.State;

public class PauseScreen extends State {

	MessageBus bus;

	public PauseScreen(MessageBus bus) {
		super("PauseScreen");
		this.bus = bus;
	}

	@Override
	public void process(Object... params) {
		NkContext ctx = (NkContext) params[0];
		ContextInformation ci = (ContextInformation) params[1];
		int width = 360;
		int height = 240;
		int xPos = ci.renderBufferWidth / 2 - width / 2;
		int yPos = ci.renderBufferHeight / 2 - height / 2;

		try (MemoryStack stack = stackPush()) {
			NkRect rect = NkRect.mallocStack(stack);

			if (nk_begin(ctx, "Pause", nk_rect(xPos, yPos, width, height, rect),
					NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				nk_layout_row_dynamic(ctx, 30, 3);
				nk_spacing(ctx, 4);
				nk_label(ctx, "Abort Game?", NK_TEXT_CENTERED);
				nk_spacing(ctx, 5);
				if (nk_button_label(ctx, "Continue")) {
					bus.sendMessage(new Message(this, "continue", BuntePanzer.class));
					sendCommand("continue");
				}
				nk_spacing(ctx, 2);
				if (nk_button_label(ctx, "Abort")) {
					bus.sendMessage(new Message(this, "abortGame", BuntePanzer.class));
					sendCommand("abortGame");
				}

				nk_layout_row_push(ctx, 100);
			}
			nk_end(ctx);
		}
	}

	@Override
	public String transition(String command) {
		switch (command) {
		case "continue":
			return "emptyScreen";
		case "abortGame":
			return "MainMenu";
		}
		return "PauseScreen";
	}

}
