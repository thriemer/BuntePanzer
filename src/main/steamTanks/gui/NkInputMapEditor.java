package gui;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.NkPluginFilterI;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkTextEdit;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import context.ContextInformation;
import input.InputHandler;
import input.InputMap;
import input.KeyboardInputMap;
import main.Settings;
import stateMachine.State;
import texture.TextureManager;

public class NkInputMapEditor extends State {

	public String[] possibleActions;
	int controlSchemeIndex = 0;
	private boolean showPopUp = false;
	private String currentActionEdit = null;
	private NkVec2 popUpPos = NkVec2.create();
	private int options = NK_EDIT_SIMPLE;
	private int maxLength = 20;
	private ByteBuffer content;
	private IntBuffer length;
	private NkPluginFilterI filter;
	private InputHandler input;

	public NkInputMapEditor(InputHandler input) {
		super("Settings");
		this.input = input;
		content = BufferUtils.createByteBuffer(maxLength + 1);
		length = BufferUtils.createIntBuffer(1); // BufferUtils from LWJGL
		filter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
	}

	private boolean initTextField = false;

	@Override
	public void process(Object... params) {
		NkContext ctx = (NkContext) params[0];
		ContextInformation ci = (ContextInformation) params[1];
		int width = 640;
		int height = 480;
		int xPos = ci.renderBufferWidth / 2 - width / 2;
		int yPos = ci.renderBufferHeight / 2 - height / 2;
		if (!initTextField) {
			NKUtils.setValue(Settings.controlScheme.get(controlSchemeIndex).deviceName, content, length);
			initTextField = true;
		}
		try (MemoryStack stack = stackPush()) {
			NkRect rect = NkRect.mallocStack(stack);
			if (nk_begin(ctx, "NkInputMapEditor", nk_rect(xPos, yPos, width, height, rect),
					NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BACKGROUND | NK_WINDOW_BORDER)) {
				nk_layout_row_dynamic(ctx, 30, 3);
				nk_spacing(ctx, 1);
				nk_label(ctx, "Controls:", NK_TEXT_CENTERED);
				nk_layout_row_dynamic(ctx, 30, 5);
				nk_spacing(ctx, 1);
				if (nk_combo_begin_label(ctx,
						Settings.controlScheme.size() > 0 ? Settings.controlScheme.get(controlSchemeIndex).deviceName
								: "Add a new Control Scheme",
						NkVec2.create().set(200, 400))) {
					nk_layout_row_dynamic(ctx, 30, 1);
					for (int i = 0; i < Settings.controlScheme.size(); i++) {
						if (nk_button_label(ctx, Settings.controlScheme.get(i).deviceName)) {
							controlSchemeIndex = i;
							String putThere = Settings.controlScheme.get(controlSchemeIndex).deviceName;
							NKUtils.setValue(putThere, content, length);
							nk_combo_close(ctx);
						}
					}
					nk_combo_end(ctx);
				}
				if (nk_button_label(ctx, "New Input")) {
					Settings.controlScheme.add(new KeyboardInputMap());
					controlSchemeIndex = Settings.controlScheme.size() - 1;
					String putThere = Settings.controlScheme.get(controlSchemeIndex).deviceName;
					NKUtils.setValue(putThere, content, length);

				}
				if (nk_button_label(ctx, "Remove Input") && Settings.controlScheme.size() > 0) {
					Settings.controlScheme.remove(controlSchemeIndex);
					controlSchemeIndex = Math.min(controlSchemeIndex, Settings.controlScheme.size() - 1);
					NKUtils.setValue(Settings.controlScheme.get(controlSchemeIndex).deviceName, content, length);
				}
				nk_spacing(ctx, 1);
				nk_layout_row_dynamic(ctx, 30, 3);
				nk_spacing(ctx, 1);
				int ops = nk_edit_string(ctx, options, content, length, maxLength + 1, filter);
				if ((ops & (NK_EDIT_COMMITED | NK_EDIT_DEACTIVATED)) > 0) {
					Settings.controlScheme.get(controlSchemeIndex).deviceName = NKUtils.getValue(content, length);
				}
				nk_layout_row_dynamic(ctx, 30, 4);
				nk_spacing(ctx, 1);
				if (Settings.controlScheme.size() > 0) {
					for (String action : Settings.actions) {
						nk_label(ctx, action, NK_TEXT_CENTERED);
						InputMap im = Settings.controlScheme.get(controlSchemeIndex);
						String map = "";
						if (im instanceof KeyboardInputMap) {
							KeyboardInputMap kim = (KeyboardInputMap) im;
							Integer key = kim.getKeyForAction(action);
							String name;
							if (key != null) {
								name = GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key));
								if (name == null) {
									name = "" + key;
								}
							} else {
								name = "Not used";
							}
							map += name;
						}
						if (nk_button_label(ctx, map)) {
							showPopUp = true;
							currentActionEdit = action;
							nk_widget_bounds(ctx, rect);
							popUpPos.set(rect.x() - xPos - rect.w() / 2, rect.y() - yPos - rect.h() / 2);
						}
						nk_spacing(ctx, 2);
					}
				}
				nk_layout_row_dynamic(ctx, 30, 1);
				if (nk_button_label(ctx, "Back to Menu")) {
					sendCommand("Return");
				}
			}
			showKeySetPopUp(popUpPos.x(), popUpPos.y(), stack, ctx, ci);
			nk_end(ctx);
		}
	}

	private void showKeySetPopUp(float xPos, float yPos, MemoryStack stack, NkContext ctx, ContextInformation ci) {
		if (showPopUp) {
			NkRect rect = NkRect.mallocStack(stack);
			int width = 240;
			int height = 144;
			if (nk_popup_begin(ctx, NK_POPUP_STATIC, "Press Key for: \"" + currentActionEdit + "\"", NK_WINDOW_BORDER,
					nk_rect(xPos - width / 2f, yPos - height / 2f, width, height, rect))) {
				nk_layout_row_dynamic(ctx, 30, 1);
				nk_spacing(ctx, 1);
				nk_label(ctx, "Press Key for: \"" + currentActionEdit + "\"", NK_TEXT_ALIGN_CENTERED);
				int[] activeKeys = input.getActiveKeys();
				if (activeKeys.length == 1) {
					KeyboardInputMap kim = (KeyboardInputMap) Settings.controlScheme.get(controlSchemeIndex);
					kim.addMapping(currentActionEdit, activeKeys[0]);
					showPopUp = false;
					nk_popup_close(ctx);
				}
				nk_popup_end(ctx);
			}
		}

	}

	@Override
	public String transition(String command) {
		switch (command) {
		case "Return":
			return "MainMenu";
		}
		return "Settings";
	}

}
