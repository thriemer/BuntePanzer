package engine.collision;

import java.util.List;

import org.joml.Vector2f;

public class SAT {

	public static boolean broadPhaseCollision(CollisionShape cs1, CollisionShape cs2) {
		return cs1.getAbsoluteCenter().distance(cs2.getAbsoluteCenter()) <= cs1.radius + cs2.radius;
	}

	public static Vector2f getMTV(CollisionShape moveable, CollisionShape staticCS) {
		if (broadPhaseCollision(moveable, staticCS)) {
			if (moveable instanceof PointHitbox) {
				return getMTVOfPoint(moveable.topLeft, staticCS);
			}
			return getMTVOfCollisionShapes(moveable, staticCS);
		} else {
			return null;
		}
	}

	public static Vector2f getMTVOfPoint(Vector2f point, CollisionShape poly) {
		Vector2f mtvAxis = null;
		float smallestOverlap = Float.MAX_VALUE;
		for (Vector2f axis : poly.getAxes()) {
			Vector2f pp1 = poly.projectOnAxis(axis);
			float dotProj = point.dot(axis);
			if (pp1.x <= dotProj && dotProj <= pp1.y) {
				float overlap = Math.min(dotProj - pp1.x, pp1.y - dotProj);
				if (overlap < smallestOverlap) {
					smallestOverlap = overlap;
					mtvAxis = axis;
				}
			} else {
				return null;
			}
		}
		Vector2f mtv = new Vector2f(mtvAxis.x * smallestOverlap, mtvAxis.y * smallestOverlap);
		correctMTV(point, poly.getAbsoluteCenter(), mtv);
		return mtv;
	}

	public static Vector2f getMTVOfCollisionShapes(CollisionShape cs1, CollisionShape cs2) {
		Vector2f mtvAxis = null;
		float smallestOverlap = Float.MAX_VALUE;
		List<Vector2f> axes = getAxes(cs1, cs2);
//		System.out.println(cs1.getAbsoluteCenter()+" "+cs2.getAbsoluteCenter());
//		System.out.println(cs1 instanceof ArcHitbox);
		for (Vector2f axis : axes) {
			Vector2f pp1 = cs1.projectOnAxis(axis);
			Vector2f pp2 = cs2.projectOnAxis(axis);
			if (isOverlapping(pp1, pp2)) {
				float overlap = returnSmallestOverlap(pp1, pp2);
				if (overlap < smallestOverlap) {
					smallestOverlap = overlap;
					mtvAxis = axis;
				}
			} else {
				return null;
			}
		}
//		System.out.println(mtvAxis);
		Vector2f mtv = new Vector2f(mtvAxis.x * smallestOverlap, mtvAxis.y * smallestOverlap);
		mtv = correctMTV(cs1.getAbsoluteCenter(), cs2.getAbsoluteCenter(), mtv);
		return mtv;
	}

	private static Vector2f correctMTV(Vector2f movablePoly, Vector2f staticPoly, Vector2f mvt) {
		boolean pointInSameDir = new Vector2f(staticPoly).sub(movablePoly).dot(mvt) > 0;
		if (pointInSameDir) {
			mvt.x = -mvt.x;
			mvt.y = -mvt.y;
		}
		return mvt;
	}

	private static boolean isOverlapping(Vector2f pp1, Vector2f pp2) {
		return !(pp2.x > pp1.y || pp1.x > pp2.y);
	}

	private static float returnSmallestOverlap(Vector2f pp1, Vector2f pp2) {
		if (projectionIsInsideProjection(pp1, pp2)) {
			return Math.min(Math.abs(pp1.x - pp2.y), Math.abs(pp1.y - pp2.x));
		} else {
			float x3 = Math.max(pp1.x, pp2.x);
			float y3 = Math.min(pp1.y, pp2.y);
			return Math.abs(x3 - y3);
		}
	}

	private static boolean projectionIsInsideProjection(Vector2f pp1, Vector2f pp2) {
		return (pp1.x < pp2.x && pp2.y < pp1.y) || (pp2.x < pp1.x && pp1.y < pp2.y);
	}

	private static List<Vector2f> getAxes(CollisionShape cs1, CollisionShape cs2) {
		List<Vector2f> axes = cs1.getAxes();
		if (cs1 instanceof ArcHitbox) {
//			System.out.print("Found Architbox: ");
//			System.out.println  (cs1.getAbsoluteCenter()+" "+cs2.getClosestPointTo(cs1.getAbsoluteCenter()));
			Vector2f axis = cs1.getAbsoluteCenter().sub(cs2.getClosestPointTo(cs1.getAbsoluteCenter()));
			axis.normalize();
			axes.add(axis);
		}
		axes.addAll(cs2.getAxes());
		if (cs2 instanceof ArcHitbox) {
			Vector2f axis = cs2.getAbsoluteCenter().sub(cs1.getClosestPointTo(cs2.getAbsoluteCenter()));
			axis.normalize();
			axes.add(axis);
		}
		return axes;
	}

}
