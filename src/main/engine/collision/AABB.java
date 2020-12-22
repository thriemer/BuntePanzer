package collision;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import core.OpenGLGraphics;
import math.Maths;
import math.Ray;
import texture.Texture;
import texture.TextureManager;

public class AABB extends CollisionShape {

	public float width, height;
	protected static final Vector2f[] edgeOrder = new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 0),
			new Vector2f(1, 1), new Vector2f(0, 1) };

	public AABB(Vector2f topleft, float width, float height) {
		super(topleft, (float) Math.sqrt(width * width + height * height) / 2f);
		this.width = width;
		this.height = height;
		calcRelativeCenter();
	}

	@Override
	public Vector2f projectOnAxis(Vector2f axis) {
		float minProj = Float.MAX_VALUE;
		float maxProj = -Float.MAX_VALUE;
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				float dot = axis.dot(getCornerPoint(x, y));
				minProj = Math.min(minProj, dot);
				maxProj = Math.max(maxProj, dot);

			}
		}
		return new Vector2f(minProj, maxProj);
	}

	public Vector2f getCornerPoint(int onLeftRight, int onTopBot) {
		return new Vector2f(onLeftRight * width + topLeft.x + offset.x, onTopBot * height + topLeft.y + offset.y);
	}

	@Override
	public List<Vector2f> getAxes() {
		List<Vector2f> rtList = new ArrayList<Vector2f>();
		rtList.add(getCornerPoint(0, 0).sub(getCornerPoint(1, 0)).normalize());
		rtList.add(getCornerPoint(0, 0).sub(getCornerPoint(0, 1)).normalize());
		return rtList;
	}

	@Override
	public Vector2f getClosestPointTo(Vector2f point) {
		Vector2f closest = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
		float minDist = Float.MAX_VALUE;
		for (int i = 0; i <= edgeOrder.length; i++) {
			Vector2f edge1 = edgeOrder[i % edgeOrder.length];
			Vector2f edge2 = edgeOrder[(i + 1) % edgeOrder.length];
			Vector2f p = getCornerPoint((int) edge1.x, (int) edge1.y);
			Vector2f end = getCornerPoint((int) edge2.x, (int) edge2.y);
			Vector2f d = end.sub(p);
			Vector2f e = Maths.getNormal(d);
			float top = (point.x * e.y - point.y * e.x + e.x * p.y - e.y * p.x);
			float bot = d.x * e.y - d.y * e.x;
			float s = Maths.clamp(top / bot, 0, 1);
			Vector2f closestOnSide = p.add(d.mul(s));
			if (closestOnSide.distance(point) <= minDist) {
				closest = closestOnSide;
				minDist = closestOnSide.distance(point);
			}
		}
		return closest;
	}

	@Override
	public void outline(OpenGLGraphics g) {
		Texture t = TextureManager.loadTexture("mPlatform", 1, 1,GL11.GL_EYE_LINEAR,GL11.GL_CLAMP);
		t.setSize(width, height);
		g.drawImage(t, getAbsolutePosition());
	}

	@Override
	protected void calcRelativeCenter() {
		relativeCenter = new Vector2f(width / 2f, height / 2f);
	}

	@Override
	public Vector2f getRayIntersectionPoint(Ray r) {
		if (rayIntersectionBroadPhase(r)) {
			float dist = Float.MAX_VALUE;
			Vector2f smallest = null;
			for (int i = 0; i <= edgeOrder.length; i++) {
				Vector2f start = getLoopedCorner(i);
				Vector2f direction = getLoopedCorner(i + 1).sub(start);
				Vector2f v1 = r.getRayIntersectionPoint(new Ray(start, direction), 1f);
				if (v1 != null) {
					if (v1.distance(r.start) <= dist) {
						smallest = v1;
						dist = smallest.distance(r.start);
					}
				}
			}
			return smallest;
		}
		return null;
	}

	private Vector2f getLoopedCorner(int index) {
		Vector2f edge1 = edgeOrder[index % edgeOrder.length];
		return getCornerPoint((int) edge1.x, (int) edge1.y);
	}

	@Override
	public Vector2f getNormal(Ray r) {
		if (rayIntersectionBroadPhase(r)) {
			float dist = Float.MAX_VALUE;
			Ray smallestLs = null;
			for (int i = 0; i <= edgeOrder.length; i++) {
				Vector2f start = getLoopedCorner(i);
				Vector2f direction = getLoopedCorner(i + 1).sub(start);
				Ray ls = new Ray(start, direction);
				Vector2f v1 = r.getRayIntersectionPoint(ls, 1f);
				if (v1 != null) {
					if (v1.distance(r.start) <= dist) {
						smallestLs = ls;
						dist = v1.distance(r.start);
					}
				}
			}
			if (smallestLs != null) {
				return Maths.getNormal(smallestLs.direction);
			} else {
				return null;
			}
		}
		return null;
	}
}
