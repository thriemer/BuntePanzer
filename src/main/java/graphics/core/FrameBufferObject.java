package graphics.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBufferObject {

	private static List<FrameBufferObject> allFbos = new ArrayList<>();

	private int width, height;

	private int fbo;
	private int colorTexture;
	private int depthTexture;

	public FrameBufferObject(int width, int height) {
		this(width, height, false, 0);
	}

	public FrameBufferObject(int width, int height, boolean multisampled, int samples) {
		this.width = width;
		this.height = height;
		fbo = createFrameBuffer();
		colorTexture = multisampled ? createMultisampledColorbuffer(width, height, samples)
				: createTextureAttachment(width, height);
		depthTexture = createDepthBufferAttachment(width, height, multisampled, samples);
		unbind();
		allFbos.add(this);
	}

	public void resolveToFbo(FrameBufferObject out) {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.fbo);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fbo);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, out.getBufferWidth(), out.getBufferHeight(),
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbind();
	}

	public void resolveToScreen() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fbo);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		this.unbind();
	}

	public int getColorTexture() {
		return colorTexture;
	}

	public void cleanUp() {// call when closing the game
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL30.glDeleteFramebuffers(fbo);
		GL11.glDeleteTextures(colorTexture);
		GL11.glDeleteTextures(depthTexture);
	}

	public FrameBufferObject bind() {
		bindFrameBuffer(fbo, width, height);
		return this;
	}

	public void unbind() {// call to switch to default frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	private void bindFrameBuffer(int frameBuffer, int width, int height) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	private int createFrameBuffer() {
		int frameBuffer = GL30.glGenFramebuffers();
		// generate name for frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		// create the framebuffer
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		// indicate that we will always render to color attachment 0
		return frameBuffer;
	}

	private int createTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}

	private int createMultisampledColorbuffer(int width, int height, int samples) {
		int colorBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colorBuffer);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL11.GL_RGBA8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER,
				colorBuffer);
		return colorBuffer;
	}

	private int createDepthBufferAttachment(int width, int height, boolean multisampled, int samples) {
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		if (!multisampled) {
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		} else {
			GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL11.GL_DEPTH_COMPONENT, width,
					height);
		}
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);
		return depthBuffer;
	}

	public static void cleanUpAllFbos() {
		allFbos.forEach(fbo -> fbo.cleanUp());
	}

	public int getBufferHeight() {
		return height;
	}

	public int getBufferWidth() {
		return width;
	}

}