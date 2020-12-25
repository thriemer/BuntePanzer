package steamTanks.tankAttackers;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.collision.CollisionShape;
import engine.math.Maths;
import engine.sound.SoundManager;
import steamTanks.tanks.Tank;
import graphics.texture.TextureManager;

public class BouncingProjectile extends Projectile {

	protected int bouncesLeft = 5;

	public BouncingProjectile(Tank home, Vector2f position, Vector2f velocity, float textureIndex, MessageBus bus) {
		super(home, position, velocity, textureIndex, bus);
		this.texture = TextureManager.getTexture("steamTanks/tankAttackers", 2, 2);
		this.texture.setFrameID(0);
		texture.setSize(0.15f, 0.15f).setColor(textureIndex);
	}

	@Override
	public void onCollision(CollisionShape collidedWith, Vector2f mtv, String flag) {
		if (mtv != null) {
			if (flag.equals("MAP")) {
				position.add(mtv);
				velocity = Maths.reflect(velocity, new Vector2f(mtv).normalize());
				SoundManager.get().playSoundEffect("wallHit");
				bouncesLeft--;
			}
			if (flag.equals("KILLER") || bouncesLeft <= 0) {
				die();
			}
		}
	}

}
