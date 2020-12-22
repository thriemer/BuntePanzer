package tankAttackers;

import org.joml.Vector2f;

import bus.MessageBus;
import collision.ArcHitbox;
import collision.CollisionShape;
import sound.SoundManager;
import tanks.Tank;
import texture.TextureManager;

public class Projectile extends TankAttack {

	public Projectile(Tank home, Vector2f position, Vector2f velocity, float textureIndex, MessageBus bus) {
		super(home, position, new ArcHitbox(position, 0.05f), TextureManager.getTexture("tankAttackers", 2, 2), bus);
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
