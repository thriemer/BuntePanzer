package graphics.postProcessing;

import java.util.Map;

import graphics.core.Shader;

public class Blur extends PostProcessingEffect{
	
	private boolean blurHorizontal;
	
	public Blur(int width, int height, boolean blurHorizontal) {
		super(width, height);
		this.blurHorizontal=blurHorizontal;
		shader = new Shader(Shader.loadShaderCode("blurVS"), Shader.loadShaderCode("blurFragmentShader"))
		.bindAtrributs("aPos").combine();
		shader.loadUniforms("targetSize","blurHorizontal");
		shader.bind();
		shader.loadInt("blurHorizontal", blurHorizontal?1:0);
		shader.unbind();
	}

	@Override
	public void prepare(Map<String,Integer> requirements, boolean isFinalStage) {
		if(!isFinalStage) {
			fbo.bind();
		}
		shader.bind();
		shader.loadFloat("targetSize", blurHorizontal?fbo.getBufferWidth():fbo.getBufferHeight());
		bindTexture(requirements.get("current"), 0);
		requirements.put("blur", fbo.getColorTexture());
		requirements.put("current", fbo.getColorTexture());
	}

}
