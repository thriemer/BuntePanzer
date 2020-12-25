package graphics.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import graphics.texture.TextureManager;

public class OGLTexture {

	private static List<Integer> allCreatedTextures = new ArrayList<Integer>();
	public int textureID;
	
	public Vector2f texelSize;

	public OGLTexture(int id, Vector2f texelSize) {
		this.textureID = id;
		this.texelSize=texelSize;
		if (!allCreatedTextures.contains(id)) {
			allCreatedTextures.add(id);
		}
	}

	public OGLTexture(ByteBuffer buffer, int width, int height) {
		createTexture(GL_LINEAR,GL_CLAMP);
		texelSize=new Vector2f(width,height);
		TextureManager.addTexelSize(textureID, texelSize);
		drawOnTexture(buffer, width, height);
	}
	public OGLTexture(ByteBuffer buffer, int width, int height, int filter, int wrap) {
		createTexture(filter,wrap);
		texelSize=new Vector2f(width,height);
		TextureManager.addTexelSize(textureID, texelSize);
		drawOnTexture(buffer, width, height);
	}
	protected void createTexture(int filter, int wrap) {
		textureID = glGenTextures(); // Generate texture ID
		allCreatedTextures.add(textureID);
		bind();
		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		// Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
		unbind();
	}

	public void drawOnTexture(int[] pixels, int width, int height) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);
		drawOnTexture(buffer, pixels, width, height);
	}

	public void drawOnTexture(ByteBuffer buffer, int[] pixels, int width, int height) {
		buffer.clear();
		for (int i = 0; i < pixels.length; i++) {
			int pixel = pixels[i];
			buffer.put((byte) (pixel >> 16 & 0xFF));
			buffer.put((byte) (pixel >> 8 & 0xFF));
			buffer.put((byte) (pixel & 0xFF));
			buffer.put((byte) (pixel >> 24 & 0xFF));
		}
		buffer.flip();
		drawOnTexture(buffer, width, height);
	}
	
	public void drawOnTexture(ByteBuffer buffer, int width, int height) {
		bind();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		unbind();
	}

	public void bind(int pos) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + pos);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureID);
	}

	public void bind() {
		bind(0);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void cleanUpAllTextures() {
		allCreatedTextures.forEach(i -> glDeleteTextures(i));
	}

}
