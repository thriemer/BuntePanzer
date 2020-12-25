package graphics.postProcessing;

import java.util.Map;

import graphics.core.Shader;

public class BrightFilter extends PostProcessingEffect{

	public BrightFilter(int width, int height) {
		super(width, height);
		shader =new Shader(Shader.loadShaderCode("postProcessingVS"), Shader.loadShaderCode("brightFilterFS")).bindAtrributs("aPos").combine();
		shader.loadUniforms("picture");
		shader.connectSampler("picture", 0);
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
