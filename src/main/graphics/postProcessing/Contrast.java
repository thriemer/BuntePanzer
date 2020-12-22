package postProcessing;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import core.Shader;
import texture.Texture;

public class Contrast extends PostProcessingEffect {

	public Contrast(int width, int height) {
		super(width, height);
		shader = new Shader(Shader.loadShaderCode("postProcessingVS"), Shader.loadShaderCode("contrastFS")).combine();
	}

	@Override
	public void prepare(Map<String,Integer> requirements, boolean isFinalStage) {
		if(!isFinalStage) {
			fbo.bind();
		}
		shader.bind();
		bindTexture(requirements.get("current"), 0);
		requirements.put("current", fbo.getColorTexture());
	}

}
