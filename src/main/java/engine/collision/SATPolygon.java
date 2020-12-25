package engine.collision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import graphics.core.OpenGLGraphics;
import engine.math.Maths;
import engine.math.Ray;

public class SATPolygon extends CollisionShape implements Serializable {

	private static final long serialVersionUID = -711646470310931784L;
	public List<Vector2f> verticies;
	public float radius;
	private Vector2f projection;

	public SATPolygon(Vector2f pos) {
		super(pos, 0f);
		verticies = new ArrayList<Vector2f>();
	}

	@Override
	public Vector2f projectOnAxis(Vector2f axis) {
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		for (int p1i = 0; p1i < getVertexCount(); p1i++) {
			float val = axis.dot(getVertex(p1i));
			min = Math.min(min, val);
			max = Math.max(val, max);

		}
		if (projection == null) {
			projection = new Vector2f(min, max);
		} else {
			projection.x = min;
			projection.y = max;
		}
		return projection;
	}

	public Vector2f getFace(int i) {
		if (i > -1 && i < getVertexCount() - 1) {
			return getVertex(i).sub(getVertex(i + 1));
		} else if (i == getVertexCount() - 1) {
			return getVertex(getVertexCount() - 1).sub(getVertex(0));
		}
		return null;
	}

	public Vector2f getVertex(int i) {
		if (i > -1 && i < getVertexCount()) {
			return new Vector2f(verticies.get(i).x + topLeft.x + offset.x, verticies.get(i).y + topLeft.y + offset.y);
		}
		return null;
	}

	public Vector2f getTopLeft() {
		return topLeft;
	}

	public int getVertexCount() {
		return verticies.size();
	}

	public void addPoint(Vector2f vec) {
		verticies.add(vec);
		calcRelativeCenter();
	}

	public Vector2f getRelativeVertex(int i) {
		return verticies.get(i);
	}

	@Override
	protected void calcRelativeCenter() {
		if (relativeCenter == null) {
			relativeCenter = new Vector2f(0, 0);
		}
		float x = 0;
		float y = 0;
		for (Vector2f vertex : verticies) {
			x += vertex.x;
			y += vertex.y;
		}
		relativeCenter.x = x / verticies.size();
		relativeCenter.y = y / verticies.size();
		radius = relativeCenter.distance(verticies.stream()
				.max((a, b) -> Float.compare(a.distance(relativeCenter), b.distance(relativeCenter))).get());
	}

	@Override
	public void outline(OpenGLGraphics g) {
		for (int i = 0; i <= verticies.size(); i++) {
			// TODO: g.drawLine(getLoopedVertex(i),getLoopedVertex(i+1));
		}
	}

	@Override
	public SATPolygon clone() {
		SATPolygon rt = new SATPolygon(new Vector2f(this.topLeft.x, this.topLeft.y));
		for (Vector2f v : this.verticies) {
			rt.addPoint(new Vector2f(v.x, v.y));
		}
		return rt;

	}

	public Vector2f getLoopedVertex(int index) {
		int i = index % getVertexCount();
		if (i < 0) {
			i += getVertexCount();
		}
		return getVertex(i);
	}

	public boolean containsVertex(float x, float y) {
		return containsVertex(new Vector2f(x, y));
	}

	public boolean containsVertex(Vector2f vertex) {
		for (Vector2f vec : verticies) {
			if (vertex.equals(vec)) {
				return true;
			}
		}
		return false;
	}

	public void deleteVertex(Vector2f vertex) {
		verticies.remove(vertex);
	}

	@Override
	public List<Vector2f> getAxes() {
		List<Vector2f> axis = new ArrayList<>();
		for (int i = 0; i < getVertexCount(); i++) {
			axis.add(Maths.getNormal(getFace(i)));
		}
		return axis;
	}

	@Override
	public Vector2f getClosestPointTo(Vector2f point) {
		Vector2f closest = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
		float minDist = Float.MAX_VALUE;
		for (int i = 0; i <= getVertexCount(); i++) {
			Vector2f p = getLoopedVertex(i);
			Vector2f end = getLoopedVertex(i + 1);
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
	public Vector2f getRayIntersectionPoint(Ray r) {
		if (rayIntersectionBroadPhase(r)) {
			float dist = Float.MAX_VALUE;
			Vector2f smallest = null;
			for (int i = 0; i <= getVertexCount(); i++) {
				Vector2f start = getLoopedVertex(i);
				Vector2f direction = getLoopedVertex(i + 1).sub(start);
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

	@Override
	public Vector2f getNormal(Ray r) {
		if (rayIntersectionBroadPhase(r)) {
			float dist = Float.MAX_VALUE;
			Ray smallestLs = null;
			for (int i = 0; i <= getVertexCount(); i++) {
				Vector2f start = getLoopedVertex(i);
				Vector2f direction = getLoopedVertex(i + 1).sub(start);
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
