package graphics.postProcessing;

import java.util.Map;

import graphics.core.Shader;

public class Combine extends PostProcessingEffect{
	
	public Combine(int width, int height) {
		super(width, height);
		shader =new Shader(Shader.loadShaderCode("postProcessingVS"), Shader.loadShaderCode("combineFS")).bindAtrributs("aPos").combine();
		shader.loadUniforms("original","bloom");
		shader.connectSampler("original", 0);
		shader.connectSampler("bloom", 1);
		
	}

	@Override
	public void prepare(Map<String,Integer> requirements, boolean isFinalStage) {
		if(!isFinalStage) {
			fbo.bind();
		}
		shader.bind();
		bindTexture(requirements.get("original"), 0);
		bindTexture(requirements.get("current"), 1);
		requirements.put("current", fbo.getColorTexture());
	}

}
