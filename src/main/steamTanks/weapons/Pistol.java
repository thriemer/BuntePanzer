package weapons;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import map.MapHandler;
import math.Maths;
import sound.SoundManager;
import tankAttackers.Projectile;
import tankAttackers.TankAttack;
import tanks.Tank;

public class Pistol extends Weapon {

	public Pistol(MessageBus bus) {
		super(bus, "Pistol", 4, 2f, 1);
	}

	@Override
	protected TankAttack shoot(Tank sender, MessageBus bus) {
		TankAttack bullet = new Projectile(sender, new Vector2f(), Maths.createUnitVecFromAngle(sender.angle).mul(4f),
				sender.texture.getCustomColor(), bus);
		bullet.hitbox.setCenter(this.getCenterOfSpawnPosition(sender.hitbox.getAbsoluteCenter(), sender.angle));
		mapHandler.testColliderAgainstMap(bullet);
SoundManager.get().playSoundEffect("pistolShot");
		return bullet;
	}

	@Override
	protected void resetTimerAndDecreaseAmmo() {
		reloading = reloadTime;
	}

}
