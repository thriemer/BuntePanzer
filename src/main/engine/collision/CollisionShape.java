package collision;

import java.util.List;

import org.joml.Vector2f;

import core.OpenGLGraphics;
import math.Ray;

public abstract class CollisionShape {

	public Vector2f topLeft;
	public Vector2f offset = new Vector2f(0, 0);
	public Vector2f relativeCenter;
	public float radius = 0f;

	public CollisionShape(Vector2f topleft, float radius) {
		this.topLeft = topleft;
		this.radius = radius;
//		calcRelativeCenter();
	}

	public abstract Vector2f getRayIntersectionPoint(Ray r);

	public abstract Vector2f getNormal(Ray r);

	public abstract Vector2f getClosestPointTo(Vector2f point);

	public abstract Vector2f projectOnAxis(Vector2f axis);

	public abstract List<Vector2f> getAxes();

	protected abstract void calcRelativeCenter();

	public abstract void outline(OpenGLGraphics g);

	public boolean rayIntersectionBroadPhase(Ray r) {
		Vector2f toCenter = getAbsoluteCenter().sub(r.start);
		Vector2f normalizedDir = new Vector2f(r.direction).normalize();
		float tca = toCenter.dot(normalizedDir);
		Vector2f lot = normalizedDir.mul(tca).add(r.start);
		return getAbsoluteCenter().distance(lot) <= radius;
	}

	public Vector2f getAbsoluteCenter() {
		return getAbsolutePosition().add(relativeCenter);
	}

	public Vector2f getAbsolutePosition() {
		return new Vector2f(topLeft.x, topLeft.y).add(offset);
	}

	public void setCenter(Vector2f newCenterPos) {
		topLeft.set(newCenterPos);
		topLeft.sub(relativeCenter);
		topLeft.sub(offset);
	}

}
