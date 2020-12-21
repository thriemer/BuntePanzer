package gui;

import static org.lwjgl.nuklear.Nuklear.nk_stroke_line;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkCommandBuffer;
import org.lwjgl.nuklear.NkRect;

public class NKUtils {

	public static void drawRect(NkCommandBuffer buffer, NkRect rect, float thickness, NkColor color) {
		nk_stroke_line(buffer, rect.x() - thickness / 2f, rect.y(), rect.x() + rect.w(), rect.y(), thickness, color);
		nk_stroke_line(buffer, rect.x() + rect.w(), rect.y() - thickness / 2f, rect.x() + rect.w(),
				rect.y() + rect.h() + thickness / 2f, thickness, color);
		nk_stroke_line(buffer, rect.x() - thickness / 2f, rect.y() + rect.h(), rect.x() + rect.w(), rect.y() + rect.h(),
				thickness, color);
		nk_stroke_line(buffer, rect.x(), rect.y(), rect.x(), rect.y() + rect.h(), thickness, color);

	}
	
	public static NkColor hueToNKColor(NkColor in, float hue) {
		int c = Color.HSBtoRGB(hue, 1f, 1f);
		return in.set((byte) ((c >> 16)), (byte) ((c >> 8)), (byte) (c), (byte) 255);
	}
	public static String getValue(ByteBuffer content, IntBuffer length) {
		byte[] bytes = new byte[length.get(0)];
		content.mark();
		content.get(bytes, 0, length.get(0));
		content.reset(); // Return to the previous marker so that Nuklear can write here again
		String out = new String(bytes, Charset.forName("ASCII"));
		return out;
	}
	
	public static void setValue(String val,ByteBuffer content, IntBuffer length) {
		content.put(val.getBytes(Charset.forName("ASCII")));
		content.position(0);
		length.put(0, val.length());
	}

}
