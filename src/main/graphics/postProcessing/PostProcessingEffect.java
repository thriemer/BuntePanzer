package postProcessing;

import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import core.FrameBufferObject;
import core.Shader;

public abstract class PostProcessingEffect {

	public Shader shader;
	public FrameBufferObject fbo;
	
	public PostProcessingEffect(int width, int height) {
		fbo = new FrameBufferObject(width, height);
	}

	public abstract void prepare(Map<String,Integer> requirements, boolean isFinalStage);

	public void afterRender() {
		shader.unbind();
		fbo.unbind();
	}

	protected void bindTexture(int textureID,int pos) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + pos);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
}
