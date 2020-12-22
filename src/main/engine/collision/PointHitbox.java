package collision;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import core.OpenGLGraphics;
import math.Ray;

public class PointHitbox extends CollisionShape {

	public PointHitbox(Vector2f topleft) {
		super(topleft, 0f);
	}

	@Override
	public Vector2f getClosestPointTo(Vector2f point) {
		return topLeft;
	}

	@Override
	public List<Vector2f> getAxes() {
		return new ArrayList<Vector2f>();
	}

	@Override
	public void outline(OpenGLGraphics g) {
		// TODO:grafik für üoint hitbox
	}

	@Override
	public Vector2f projectOnAxis(Vector2f axis) {
		float dot = topLeft.dot(axis);
		return new Vector2f(dot, dot);
	}

	@Override
	protected void calcRelativeCenter() {
		relativeCenter = new Vector2f(0, 0);
	}

	@Override
	public Vector2f getRayIntersectionPoint(Ray r) {
		Vector2f toPoint = new Vector2f(topLeft).sub(r.start);
		return toPoint.dot(r.direction) >= 1f ? new Vector2f(topLeft) : null;
	}

	@Override
	public Vector2f getNormal(Ray r) {
		if (getRayIntersectionPoint(r) != null)
			return new Vector2f(r.direction).negate().normalize();
		return null;
	}
}
