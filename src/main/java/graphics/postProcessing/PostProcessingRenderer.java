package graphics.postProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import graphics.context.ContextInformation;
import graphics.core.FrameBufferObject;
import graphics.core.Vao;

public class PostProcessingRenderer {

	private float[] screenCoords = new float[] { -1f, -1f, -1f, 1f, 1f, 1f, 1f, -1f };
	private Vao screenFillingQuad;
	private FrameBufferObject sceneRenderTargetMultisampled;
	private FrameBufferObject sceneRenderTarget;
	private Map<String, Integer> requirementsMap = new HashMap<>();

	List<PostProcessingEffect> effects = new ArrayList<PostProcessingEffect>();

	public PostProcessingRenderer(int width, int height) {
		screenFillingQuad = new Vao().addDataAttributes(0, 2, screenCoords).addIndicies(new int[] { 0, 1, 2, 3 });
		screenFillingQuad.unbind();
		sceneRenderTargetMultisampled = new FrameBufferObject(width, height, true, 16);
		sceneRenderTarget = new FrameBufferObject(width, height);
		effects.add(new BrightFilter(width, height));
		effects.add(new Blur(width / 2, height / 2, false));
		effects.add(new Blur(width / 2, height / 2, true));
		effects.add(new Combine(width, height));
	}

	public void applyPostProccessingAffects(ContextInformation ci) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		sceneRenderTargetMultisampled.resolveToFbo(sceneRenderTarget);
		int textureId = sceneRenderTarget.getColorTexture();
		requirementsMap.put("current", textureId);
		requirementsMap.put("original", textureId);
		screenFillingQuad.bind();
		for (int i = 0; i < effects.size(); i++) {
			boolean lastStage = i == effects.size() - 1;
			if (lastStage) {
				GL11.glViewport(0, 0, ci.renderBufferWidth, ci.renderBufferHeight);
			}
			effects.get(i).prepare(requirementsMap, lastStage);
			GL11.glDrawElements(GL11.GL_QUADS, screenFillingQuad.getIndiciesLength(), GL11.GL_UNSIGNED_INT, 0);
			effects.get(i).afterRender();
			requirementsMap.put("current", effects.get(i).fbo.getColorTexture());
		}
		screenFillingQuad.unbind();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
	}

	public void bindFbo() {
		sceneRenderTargetMultisampled.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void unbindFbo() {
		sceneRenderTargetMultisampled.unbind();
	}

	public ContextInformation getContextInformation() {
		return new ContextInformation(sceneRenderTargetMultisampled.getBufferWidth(),
				sceneRenderTargetMultisampled.getBufferHeight(), sceneRenderTargetMultisampled.getBufferWidth(),
				sceneRenderTargetMultisampled.getBufferHeight());
	}
}
