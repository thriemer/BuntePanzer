package collision;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import core.OpenGLGraphics;
import math.Ray;
import texture.Texture;
import texture.TextureManager;

public class ArcHitbox extends CollisionShape {

	public ArcHitbox(Vector2f topleft, float radius) {
		super(topleft, radius);
		calcRelativeCenter();
	}

	@Override
	public Vector2f projectOnAxis(Vector2f axis) {
		Vector2f greatestDistance = new Vector2f(axis);
		greatestDistance.normalize().mul(radius);
		Vector2f center = getAbsoluteCenter();
		float d1 = axis.dot(new Vector2f(center).add(greatestDistance));
		float d2 = axis.dot(center.sub(greatestDistance));
		return new Vector2f(Math.min(d1, d2), Math.max(d1, d2));
	}

	@Override
	public List<Vector2f> getAxes() {
		return new ArrayList<Vector2f>();
	}

	@Override
	public Vector2f getClosestPointTo(Vector2f point) {
		Vector2f center = getAbsoluteCenter();
		Vector2f toPoint = point.sub(center);
		if (toPoint.length() == 0) {
			toPoint.x = 1;
		}
		toPoint.normalize().mul(radius);
		toPoint.add(center);
		return toPoint;
	}

	@Override
	public void outline(OpenGLGraphics g) {
		Texture t = TextureManager.loadTexture("circle", 1, 1,GL11.GL_EYE_LINEAR,GL11.GL_CLAMP);
		t.setSize(2f * radius, 2f * radius);
		g.drawImage(t, getAbsolutePosition());
	}

	@Override
	protected void calcRelativeCenter() {
		relativeCenter = new Vector2f(radius, radius);
	}

	@Override
	public Vector2f getRayIntersectionPoint(Ray r) {
		if (rayIntersectionBroadPhase(r)) {
			Vector2f toCenter = getAbsoluteCenter().sub(r.start);
			Vector2f normalizedDir = new Vector2f(r.direction).normalize();
			float tca = toCenter.dot(normalizedDir);
			if (tca < 0) {
				return null;
			}
			Vector2f lotPoint = normalizedDir.mul(tca).add(r.start);
			float d = getAbsoluteCenter().distance(lotPoint);
			float x = (float) Math.sqrt(Math.pow(radius, 2) + Math.pow(d, 2));
			Vector2f intersectionPoint1 = new Vector2f(r.direction).mul(tca + x).add(r.start);
			Vector2f intersectionPoint2 = new Vector2f(r.direction).mul(tca - x).add(r.start);
			if (intersectionPoint1.distance(r.start) < intersectionPoint2.distance(r.start)) {
				return intersectionPoint1;
			} else {
				return intersectionPoint2;
			}
		}
		return null;
	}

	@Override
	public Vector2f getNormal(Ray r) {
		Vector2f intersectionPoint = getRayIntersectionPoint(r);
		if (intersectionPoint == null)
			return null;
		return intersectionPoint.sub(getAbsoluteCenter()).normalize();
	}
}
