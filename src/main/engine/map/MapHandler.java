package map;

import java.util.List;

import org.joml.Vector2f;

import bus.CommunicationNode;
import bus.Message;
import bus.MessageBus;
import collision.CollisionShape;
import collision.SAT;
import core.OpenGLGraphics;
import interfaces.Collider;
import interfaces.RenderAble;
import math.Ray;

public class MapHandler implements RenderAble, CommunicationNode {

	public GameMap currentMap;
	protected MessageBus bus;

	public MapHandler(MessageBus bus) {
		this.bus = bus;
		bus.add(this);
	}

	public void setMap(GameMap gm) {
		currentMap = gm;
	}

	public List<Vector2f> getPathFromTo(Vector2f startPos, Vector2f endPos) {
		return currentMap.graph.getNodesFromTo(startPos, endPos);
	}

	public Vector2f pointCollidesMap(Vector2f point) {
		for (CollisionShape block : currentMap.collisionPolys) {
			Vector2f mtv = SAT.getMTVOfPoint(point, block);
			if (mtv != null) {
				return mtv;
			}
		}
		return null;
	}

	public CollisionShape testRayAgainstMap(Ray r) {
		CollisionShape closest = null;
		float minDistance = Float.MAX_VALUE;
		for (CollisionShape cs : currentMap.collisionPolys) {
			Vector2f cp = cs.getRayIntersectionPoint(r);
			if (cp != null) {
				if (cp.distance(r.start) < minDistance) {
					closest = cs;
					minDistance = cp.distance(r.start);
				}
			}
		}
		return closest;
	}

	public Vector2f getRayCollisionPoint(Ray r) {
		Vector2f closest = null;
		float minDistance = Float.MAX_VALUE;
		for (CollisionShape cs : currentMap.collisionPolys) {
			cs.rayIntersectionBroadPhase(r);
			Vector2f cp = cs.getRayIntersectionPoint(r);
			if (cp != null) {
				if (cp.distance(r.start) < minDistance) {
					closest = cp;
					minDistance = cp.distance(r.start);
				}
			}
		}
		return closest;
	}

	public void testColliderAgainstMap(Collider c) {
		CollisionShape entityColShape = c.getCollisionShape();
		for (CollisionShape block : currentMap.collisionPolys) {
			Vector2f mtv = SAT.getMTV(entityColShape, block);
			if (mtv != null && mtv.length() > 0) {
				c.onCollision(block, mtv, "MAP");
			}
		}
	}

	public Vector2f testCollisionShapeAgainstMap(CollisionShape c) {
		for (CollisionShape block : currentMap.collisionPolys) {
			Vector2f mtv = SAT.getMTV(c, block);
			if (mtv != null && mtv.length() > 0) {
				return mtv;
			}
		}
		return null;
	}

	@Override
	public void draw(OpenGLGraphics g) {
		if (currentMap != null) {
			currentMap.draw(g);
		}
	}

	public float getCurrentMapWidth() {
		return currentMap.mapSize.x;
	}

	public float getCurrentMapHeight() {
		return currentMap.mapSize.y;
	}

	@Override
	public void procesMessage(Message m) {
		System.out.println("i dont process messages. thats too slow");
	}

	@Override
	public Message answer(Message m) {
		switch (m.header) {
		case "getMapHandler":
			return new Message(this, "Answer to getRequest").setParameters(this);
		}
		return null;
	}

}
