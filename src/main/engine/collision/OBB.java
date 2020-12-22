package collision;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import core.OpenGLGraphics;
import math.Maths;
import texture.Texture;
import texture.TextureManager;

public class OBB extends AABB {

	public float angle = 0f;
	Texture t;
	private Vector2f parentRelativCenter;

	public OBB(Vector2f topleft, float width, float height) {
		super(topleft, width, height);
		t = TextureManager.loadTexture("box", 1, 1,GL11.GL_EYE_LINEAR,GL11.GL_CLAMP);
	}

	public void calculateOffset(float offsetX, float offsetY, float parentWidth, float parentHeight) {
		parentRelativCenter = new Vector2f(parentWidth / 2f, parentHeight / 2f);
		offset = new Vector2f(offsetX, offsetY).add(new Vector2f(width / 2f, height / 2f)).sub(parentRelativCenter);
	}

	public Vector2f getAbsoluteCenter() {
		if (parentRelativCenter != null) {
			Vector2f aco = Maths.rotate(new Vector2f(offset), angle);
			return new Vector2f(topLeft.x + aco.x + parentRelativCenter.x, topLeft.y + aco.y + parentRelativCenter.y);
		} else {
			return super.getAbsoluteCenter();
		}
	}

	public Vector2f getAbsolutePosition() {
		return getAbsoluteCenter().sub(relativeCenter);
	}

	@Override
	public Vector2f getCornerPoint(int onLeftRight, int onTopBot) {
		Vector2f center = getAbsoluteCenter();
		Vector2f corner = new Vector2f((onLeftRight - 0.5f) * width, (onTopBot - 0.5f) * height);
		Maths.rotate(corner, angle);
		corner.add(center);
		return corner;
	}

	@Override
	public void outline(OpenGLGraphics g) {
		t.setSize(width, height);
		t.angle = this.angle;
		g.drawImage(t, getAbsolutePosition());
	}

}
