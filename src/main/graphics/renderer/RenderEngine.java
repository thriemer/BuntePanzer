package renderer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL14C.glBlendEquation;

import org.lwjgl.opengl.GL14;

import context.ContextInformation;
import core.OpenGLGraphics;
import core.Shader;
import core.Vao;
import interfaces.Renderer;

public class RenderEngine extends Renderer {
	private static final float[] quad = new float[] { -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	private Shader shader;
	private Vao vao;
	OpenGLGraphics g;

	public RenderEngine() {
		shader = new Shader(Shader.loadShaderCode("quadVertexShader"), Shader.loadShaderCode("quadFragmentShader"))
				.bindAtrributs("aPos").combine();
		shader.loadUniform("projMatrix");
		shader.loadUniform("viewMatrix");
		shader.loadUniform("transformationMatrix");
		shader.loadUniforms("picture", "textureOffset", "alpha", "color", "useCustomColor", "mirrorVertical",
				"mirrorHorizontal", "repeat","textureSize");
		shader.connectSampler("picture", 0).unbind();
		vao = new Vao().addDataAttributes(0, 2, quad).addIndicies(new int[] { 0, 1, 2, 3 });
		vao.unbind();
	}

	public void render(ContextInformation ci) {
		glEnable(GL_BLEND);
		glBlendEquation(GL14.GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE);
		g = new OpenGLGraphics(shader, vao, ci);
		this.renderAllRenderAbles(g);
		g.show();
	}

}
