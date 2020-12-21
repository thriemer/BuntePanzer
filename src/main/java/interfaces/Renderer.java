package interfaces;

import java.util.ArrayList;
import java.util.List;

import core.OpenGLGraphics;

public abstract class Renderer {

	List<RenderAble> toRender = new ArrayList<>();

	public void addRenderAble(RenderAble ra) {
		toRender.add(ra);
	}

	public void renderAllRenderAbles(OpenGLGraphics g) {
		for (RenderAble ra : toRender) {
			ra.draw(g);
		}
	}

	public void clear() {
		toRender.clear();
	}
}