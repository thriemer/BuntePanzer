package interfaces;

import org.joml.Vector2f;

import collision.CollisionShape;

public interface Collider {

	void onCollision(CollisionShape collidedWith, Vector2f mtv, String flag);

	CollisionShape getCollisionShape();

}
