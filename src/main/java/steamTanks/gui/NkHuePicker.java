package steamTanks.gui;

import static steamTanks.gui.NKUtils.drawRect;
import static org.lwjgl.nuklear.Nuklear.NK_BUTTON_LEFT;
import static org.lwjgl.nuklear.Nuklear.nk_fill_rect_multi_color;
import static org.lwjgl.nuklear.Nuklear.nk_input_any_mouse_click_in_rect;
import static org.lwjgl.nuklear.Nuklear.nk_input_is_mouse_down;
import static org.lwjgl.nuklear.Nuklear.nk_widget;
import static org.lwjgl.nuklear.Nuklear.nk_window_get_canvas;

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkCommandBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkInput;
import org.lwjgl.nuklear.NkRect;

public class NkHuePicker {

	private float hue = 128f / 360f;
	private NkColor current;

	NkColor[] hue_colors;

	public NkHuePicker(float hue) {
		hue_colors = new NkColor[] { createColor(255, 0, 0, 255), createColor(255, 255, 0, 255),
				createColor(0, 255, 0, 255), createColor(0, 255, 255, 255), createColor(0, 0, 255, 255),
				createColor(255, 0, 255, 255), createColor(255, 0, 0, 255) };
		current = NkColor.create();
		this.hue = hue;
		NKUtils.hueToNKColor(current, hue);
	}

	public NkHuePicker() {
		this(0f);
	}

	public void draw(NkContext ctx) {
		NkCommandBuffer buffer = nk_window_get_canvas(ctx);
		NkRect rect = NkRect.create();
		nk_widget(rect, ctx);
		NkInput input = ctx.input();
		if (nk_input_any_mouse_click_in_rect(input, rect) || nk_input_is_mouse_down(input, NK_BUTTON_LEFT)) {
			float mouseX = input.mouse().pos().x();
			float mouseY = input.mouse().pos().y();
			if (pointInsideRect(mouseX, mouseY, rect))
				hue = (mouseX - rect.x()) / rect.w();
		}
		float rectWidth = (float) Math.floor(rect.w() / (float) (hue_colors.length - 1));
		NkRect rect2 = NkRect.create();
		for (int i = 0; i < hue_colors.length - 1; i++) {
			rect2.x(rect.x() + i * rectWidth).y(rect.y()).w(rectWidth).h(rect.h());
			nk_fill_rect_multi_color(buffer, rect2, hue_colors[i], hue_colors[i + 1], hue_colors[i + 1], hue_colors[i]);
		}
		NKUtils.hueToNKColor(current, hue);
		NkColor borderColor = ctx.style().button().border_color();
		drawRect(buffer, rect, 2, borderColor);
	}

	public final float getHue() {
		return hue;
	}

	public final NkColor getCurrentColor() {
		return current;
	}

	private boolean pointInsideRect(float x, float y, NkRect rect) {
		return x >= rect.x() && x <= rect.x() + rect.w() && y >= rect.y() && y <= rect.y() + rect.h();
	}

	private NkColor createColor(int r, int g, int b, int a) {
		return NkColor.create().set((byte) r, (byte) g, (byte) b, (byte) a);
	}
}
