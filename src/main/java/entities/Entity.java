package entities;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import collision.CollisionShape;
import core.OpenGLGraphics;
import interfaces.Collider;
import interfaces.RenderAble;
import interfaces.TimeDependentUpdate;
import map.MapHandler;
import math.Ray;
import texture.Texture;

public abstract class Entity implements Collider, TimeDependentUpdate, RenderAble {

	public Vector2f position, velocity, acceleration;
	public String type = "Basic Entity";
	public CollisionShape hitbox;
	public Texture texture;
	protected boolean isAlive = true;
	protected MessageBus bus;
	protected MapHandler mapHandler;

	public Entity(Vector2f position, CollisionShape collisionShape, Texture texture, MessageBus bus) {
		this.position = position;
		this.velocity = new Vector2f(0, 0);
		this.acceleration = new Vector2f(0, 0);
		this.hitbox = collisionShape;
		this.texture = texture;
		this.bus = bus;
		this.mapHandler=(MapHandler) bus.request(new Message(this, "getMapHandler",MapHandler.class))[0].params[0];
	}

	public Entity(Vector2f position, String type, CollisionShape collisionShape, Texture texture, MessageBus bus) {
		this(position, collisionShape, texture, bus);
		this.type = type;

	}

	public void die() {
		isAlive = false;
	}

	@Override
	public void update(float delta) {
		velocity.add(new Vector2f(acceleration).mul(delta));
		Vector2f dS = doSweepTest(new Vector2f(velocity).mul(delta));
		position.add(dS);
		mapHandler.testColliderAgainstMap(this);
	}

	private Vector2f doSweepTest(Vector2f dS) {
		Ray sweepTester = new Ray(hitbox.getAbsoluteCenter(), new Vector2f(velocity));
		Vector2f collisionPoint=mapHandler.getRayCollisionPoint(sweepTester);
		if (collisionPoint != null) {
			float distanceToWall = collisionPoint.distance(hitbox.getAbsoluteCenter());
			if (dS.length() > distanceToWall) {
				dS.normalize(distanceToWall);
			}
		}
		return dS;
	}

	@Override
	public void onCollision(CollisionShape collidedWith, Vector2f mtv, String flag) {
		if (mtv != null) {
			if (flag.equals("MAP")) {
				position.add(mtv);
				velocity.set(0, 0);
			}
			if (flag.equals("KILLER")) {
				die();
				velocity.set(0, 0);
			}
		}
	}

	@Override
	public CollisionShape getCollisionShape() {
		return hitbox;
	}

	@Override
	public void draw(OpenGLGraphics g) {
		g.drawImage(texture, position);
//		hitbox.outline(g);
	}
}
