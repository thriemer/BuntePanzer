package math;

import org.joml.Vector2f;

public class Maths {

	public static Vector2f reflect(Vector2f incoming, Vector2f normal) {
		float dot = 2 * incoming.dot(normal);
		Vector2f projOnNormal = normal.mul(dot, new Vector2f());
		return incoming.sub(projOnNormal, projOnNormal);
	}

	public static float lerp(float v1, float v2, float b) {
		return (1f - b) * v1 + b * v2;
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static Vector2f getNormal(Vector2f in) {
		Vector2f normal = new Vector2f(-in.y, in.x);
		normal.normalize();
		return normal;
	}

	public static Vector2f rotate(Vector2f in, float angle) {
		float oldX = in.x;
		float oldY = in.y;
		in.x = (float) (oldX * Math.cos(angle) - oldY * Math.sin(angle));
		in.y = (float) (oldX * Math.sin(angle) + oldY * Math.cos(angle));
		return in;
	}

	public static Vector2f createUnitVecFromAngle(float angle) {
		return new Vector2f((float) Math.cos(angle), (float) Math.sin(angle));
	}

}
