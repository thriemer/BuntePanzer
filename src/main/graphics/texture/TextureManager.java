package texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memFree;
import util.Loader;

public class TextureManager {

	private static Map<String, Integer> textureNameIdMap = new HashMap<String, Integer>();
	private static Map<Integer, Vector2f> idToTexelSizeMap = new HashMap<>();

	public static Texture loadTexture(String name, int rows, int colums, int filter, int wrap) {
		if (textureNameIdMap.containsKey(name)) {
			return new Texture(textureNameIdMap.get(name), rows, colums);
		} else {
			Texture newText;
			ByteBuffer image;
			ByteBuffer imageBuffer;
			imageBuffer = Loader.ioResourceToByteBuffer("textures/" + name + ".tga", 8 * 1024);

			try (MemoryStack stack = stackPush()) {
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);
				if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
					throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
				}
				image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
				if (image == null) {
					throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
				}
				int texelWidth = w.get(0);
				int texelHeight = h.get(0);
				newText = new Texture(image, texelWidth,texelHeight,filter,wrap);
				newText.setRows(rows).setColumns(colums);
				idToTexelSizeMap.put(newText.textureID, new Vector2f(texelWidth,texelHeight));
				textureNameIdMap.put(name, newText.textureID);
				stbi_image_free(image);
			}
			return newText;
		}
	}
	
	public static Texture getTexture(String name, int rows, int colums) {
		return new Texture(textureNameIdMap.get(name), rows, colums);
	}
	
	public static final Vector2f getTexelSize(int id) {
		return idToTexelSizeMap.get(id);
	}
	public static void addTexelSize(int id, Vector2f size) {
		idToTexelSizeMap.put(id, size);
	}
	public static Texture loadTexture(String name) {
		return loadTexture(name, 1, 1,GL11.GL_LINEAR,GL11.GL_CLAMP);
	}

}
