package renderer;

import org.joml.Vector2f;

import context.ContextInformation;
import core.OpenGLGraphics;
import interfaces.RenderAble;
import interfaces.Renderer;

public class Camera extends Renderer implements RenderAble {

	private Vector2f target;
	private Vector2f targetSize;
	private Vector2f viewFrustumSize = new Vector2f(16, 9);
	private Vector2f contentSize = new Vector2f(1, 1);

	public Vector2f translation = new Vector2f(0, 0);

	public void setTarget(Vector2f t) {
		this.target = t;
	}

	public void setTargetSize(Vector2f s) {
		this.targetSize = s;
	}

	public void setContentSize(float contentWidth, float contentHeight) {
		contentSize.set(contentWidth, contentHeight);
	}

	private void updateCameraTranslation(ContextInformation ci) {
		calculateViewFrustum(ci);
		translation.x = (target.x) - viewFrustumSize.x / 2f;
		translation.y = (target.y) - viewFrustumSize.y / 2f;
		if (targetSize != null) {
			translation.x += 0.5f * targetSize.x;
			translation.y += 0.5f * targetSize.y;
		}
	}

	private void calculateViewFrustum(ContextInformation info) {
		float contentHeight = contentSize.y;
		float contentWidth = contentSize.x;
		float correctAspectRatioHeight = contentSize.x / info.aspectRatio;
		if(contentHeight<correctAspectRatioHeight) {
			contentHeight=correctAspectRatioHeight;
		}else {
			contentWidth = contentHeight*info.aspectRatio;
		}
		viewFrustumSize.set(contentWidth, contentHeight);
	}

	@Override
	public void draw(OpenGLGraphics g) {
		updateCameraTranslation(g.getContextInfo());
		g.setViewFrustum(viewFrustumSize);
		g.subTranslation(translation);
		this.renderAllRenderAbles(g);
		g.addTranslation(translation);
	}
}
