package steamTanks.gui;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_button_label;
import static org.lwjgl.nuklear.Nuklear.nk_combo_begin_color;
import static org.lwjgl.nuklear.Nuklear.nk_combo_end;
import static org.lwjgl.nuklear.Nuklear.nk_group_begin;
import static org.lwjgl.nuklear.Nuklear.nk_group_end;
import static org.lwjgl.nuklear.Nuklear.nk_image_color;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_subimage_id;
import static org.lwjgl.nuklear.Nuklear.nk_window_get_content_region;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.NkPluginFilterI;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;

import engine.input.InputMap;
import steamTanks.mainGame.Settings;
import graphics.texture.TextureManager;

public class NkTankWidget {

	private NkHuePicker huePicker;
	private NkVec2 huePickerSize;
	private NkImage image;
	public String name = "Player";
	private final int options;
	private final int maxLength = 20;
	private ByteBuffer content;
	private IntBuffer length;
	private NkPluginFilterI filter;

	public boolean isAI = false;
	public int controlSchemeIndex = 0;

	public NkTankWidget() {
		huePicker = new NkHuePicker((float) Math.random());
		huePickerSize = NkVec2.create();
		image = NkImage.create();
		int id = TextureManager.getTexture("panzerIconAtlas",1,1).textureID;
		nk_subimage_id(id, (short) 128, (short) 128, NkRect.create().set(32, 32, 32, 32), image);
		options = NK_EDIT_FIELD;
		content = BufferUtils.createByteBuffer(maxLength + 1);
		length = BufferUtils.createIntBuffer(1); // BufferUtils from LWJGL
		filter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
		NKUtils.setValue(name, content, length);
		controlSchemeIndex = Settings.unusedControlIndex;
	}

	public void draw(NkContext ctx) {
		if(!isAI)
		Settings.unusedControlIndex = Math.max(Settings.unusedControlIndex, controlSchemeIndex+1);
		NkRect rect = NkRect.create();
		if (nk_group_begin(ctx, "Tank Group", NK_WINDOW_NO_SCROLLBAR)) {
			nk_window_get_content_region(ctx, rect);
			nk_layout_row_dynamic(ctx, 30, 1);
			if (isAI) {
				nk_label(ctx, name, NK_TEXT_CENTERED);
			} else {
				int action = nk_edit_string(ctx, options, content, length, maxLength + 1, filter);
				if ((action & (NK_EDIT_COMMITED | NK_EDIT_DEACTIVATED)) > 0) {
					name = NKUtils.getValue(content, length);
				}
			}
			nk_layout_row_dynamic(ctx, 100, 1);
			nk_image_color(ctx, image, huePicker.getCurrentColor());
			nk_layout_row_dynamic(ctx, 20, 1);
			if (nk_combo_begin_color(ctx, huePicker.getCurrentColor(), huePickerSize.set(400, 100))) {
				nk_layout_row_dynamic(ctx, 80, 1);
				huePicker.draw(ctx);
				nk_combo_end(ctx);
			}
			List<InputMap> inputsMaps = Settings.controlScheme;
			boolean invalidControlPointer = controlSchemeIndex == -1 || controlSchemeIndex >= inputsMaps.size();
			controlSchemeIndex = invalidControlPointer ? -1 : controlSchemeIndex;
			String name = invalidControlPointer ? "NO CONTROL" : inputsMaps.get(controlSchemeIndex).deviceName;
			if (!isAI && nk_combo_begin_label(ctx, name, NkVec2.create().set(200, 400))) {
				nk_layout_row_dynamic(ctx, 30, 1);
				for (int i = 0; i < Settings.controlScheme.size(); i++) {
					if (nk_button_label(ctx, Settings.controlScheme.get(i).deviceName)) {
						controlSchemeIndex = i;
						nk_combo_close(ctx);
					}
				}
				nk_combo_end(ctx);
			}
			nk_group_end(ctx);
		}
	}

	public float getHue() {
		return huePicker.getHue();
	}
}
