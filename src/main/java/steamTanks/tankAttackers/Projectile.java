package steamTanks.tankAttackers;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.collision.ArcHitbox;
import engine.collision.CollisionShape;
import engine.sound.SoundManager;
import steamTanks.tanks.Tank;
import graphics.texture.TextureManager;

public class Projectile extends TankAttack {

	public Projectile(Tank home, Vector2f position, Vector2f velocity, float textureIndex, MessageBus bus) {
		super(home, position, new ArcHitbox(position, 0.05f), TextureManager.getTexture("steamTanks/tankAttackers", 2, 2), bus);
		texture.setSize(0.15f, 0.15f).setColor(textureIndex);
		this.texture.setFrameID(3);
		this.velocity.set(velocity);
	}

	@Override
	public void onCollision(CollisionShape collidedWith, Vector2f mtv, String flag) {
		if (mtv != null && flag.equals("MAP")) {
			position.add(mtv);
			velocity.set(0, 0);
			SoundManager.get().playSoundEffect("wallHit");
			die();
		}
		super.onCollision(collidedWith, mtv, flag);
	}
}
