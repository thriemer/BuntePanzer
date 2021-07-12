package steamTanks.tankAttackers;

import org.joml.Vector2f;

import engine.bus.Message;
import engine.bus.MessageBus;
import engine.collision.CollisionShape;
import steamTanks.mainGame.CustomEntityHandler;
import steamTanks.tanks.Tank;
import graphics.texture.TextureManager;

public class Grenade extends BouncingProjectile {

	int number = 50;
	float timeToExplode = 4f;
	float angleVel;

	public Grenade(Tank home, Vector2f position, Vector2f velocity, float textureIndex, MessageBus bus) {
		super(home, position, velocity, textureIndex, bus);
		this.bouncesLeft = 15;
		this.texture = TextureManager.getTexture("tankAttackers", 2, 2);
		this.texture.setFrameID(2);
		texture.setSize(0.22f, 0.22f).setColor(textureIndex);
		this.hitbox.radius=0.11f;
		getNewRotationSpeed();
	}

	@Override
	public void onCollision(CollisionShape collidedWith, Vector2f mtv, String flag) {
		super.onCollision(collidedWith, mtv, flag);
		if (flag.equals("MAP")) {
			getNewRotationSpeed();
		}
	}

	@Override
	public void update(float delta) {
		this.texture.angle += angleVel * delta;
		timeToExplode -= delta;
		if (timeToExplode <= 0) {
			die();
		}
		super.update(delta);
	}

	@Override
	public void die() {
		bus.sendMessage(new Message(this, "Explode",CustomEntityHandler.class,position, number, texture.getCustomColor()));
		super.die();
	}

	private void getNewRotationSpeed() {
		angleVel = (float) ((Math.random() * 2d - 1d) * 4d * Math.PI);
	}

}
