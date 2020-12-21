package gui;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import bus.Message;
import bus.MessageBus;
import context.ContextInformation;
import main.BuntePanzer;
import stateMachine.State;
import texture.TextureManager;

public class WinningScreen extends State {

	MessageBus bus;
	public String winnerName = "";
	public float hue;
	private NkColor color;
	NkImage img;
	
	private long lastChange;
	private int textureId;
	private int idOffset =10;
	private int currentId=0;

	public WinningScreen(MessageBus bus) {
		super("WinningScreen");
		this.bus = bus;
		color = NkColor.create();
		img = NkImage.create();
		textureId= TextureManager.loadTexture("panzerIconAtlas").textureID;
		nk_subimage_id(textureId, (short) 128, (short) 128, NkRect.create().set(32, 32, 32, 32), img);
		lastChange=System.currentTimeMillis();
	}
	
	private void animateTankIcon() {
		if(System.currentTimeMillis()-lastChange>150) {
			lastChange=System.currentTimeMillis();
			currentId--;
			currentId=currentId<-5?0:currentId;
			int abseluteId = idOffset+currentId;
			int xPos = abseluteId%4;
			int yPos = abseluteId/4;
			nk_subimage_id(textureId, (short) 128, (short) 128, NkRect.create().set(xPos*32, yPos*32, 32, 32), img);
		}
	}

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

			if (nk_begin(ctx, "Pause", nk_rect(xPos, yPos, width, height, rect),
					NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND)) {
				animateTankIcon();
				nk_layout_row_dynamic(ctx, 30, 1);
				nk_spacing(ctx, 1);
				nk_label(ctx, "The Winner is: " + winnerName, NK_TEXT_CENTERED);
				nk_spacing(ctx, 1);
				nk_layout_row_static(ctx, 213, 213, 3);
				nk_spacing(ctx, 1);
				NKUtils.hueToNKColor(color, hue);
				nk_image_color(ctx, img, color);
				nk_layout_row_dynamic(ctx, 30, 1);
				nk_spacing(ctx, 2);
				if (nk_button_label(ctx, "Back to menu")) {
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
		case "abortGame":
			return "MainMenu";
		}
		return "WinningScreen";
	}
}