package math;

import org.joml.Vector2f;

public class Ray {
	private static final float FLOAT_ABWEICHUNG = 0.001f;
	public Vector2f start, direction;

	public Ray(Vector2f start, Vector2f direction) {
		this.start = start;
		this.direction = direction;
	}

	public Vector2f getRayIntersectionPoint(Ray a, float bound) {
		float dx = start.x - a.start.x;
		float dy = start.y - a.start.y;
		float det = direction.x * a.direction.y - direction.y * a.direction.x;
		float u = (dy * direction.x - dx * direction.y) / det; // gehoert zu ray a
		float v = (dy * a.direction.x - dx * a.direction.y) / det; // gehort zu ray this
		if (u < 0 || v < 0 || u > bound || Math.abs(det) <= FLOAT_ABWEICHUNG)
			return null;
		return new Vector2f(start).add(direction.mul(v));
	}

	public Vector2f getRayIntersectionPoint(Ray r) {
		return getRayIntersectionPoint(r, Float.MAX_VALUE);
	}

	public boolean intersectsRay(Ray r) {
		return getRayIntersectionPoint(r) != null;
	}

	public boolean intersectsCircle(Vector2f middle, float radius) {
		Ray r = new Ray(middle, Maths.getNormal(direction));
		Vector2f intersectionPoint = this.getRayIntersectionPoint(r);
		float distanceToCircle = middle.distance(intersectionPoint);
		return distanceToCircle <= radius;
	}

	public boolean intersectsPoint(Vector2f point) {
		float tx = (point.x - start.x) / direction.x;
		float ty = (point.y - start.y) / direction.y;
		return Math.abs(tx - ty) < FLOAT_ABWEICHUNG && tx > 0 && ty > 0;
	}
}
