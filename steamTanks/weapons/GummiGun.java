package weapons;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import map.MapHandler;
import math.Maths;
import sound.SoundManager;
import tankAttackers.BouncingProjectile;
import tankAttackers.TankAttack;
import tanks.Tank;

public class GummiGun extends Weapon {

	public GummiGun(MessageBus bus) {
		super(bus, "GummiGun", 1, 2f, 2);
	}

	@Override
	protected TankAttack shoot(Tank sender, MessageBus bus) {
		TankAttack bullet = new BouncingProjectile(sender, new Vector2f(),
				Maths.createUnitVecFromAngle(sender.angle).mul(4f), sender.texture.getCustomColor(), bus);
		bullet.hitbox.setCenter(this.getCenterOfSpawnPosition(sender.hitbox.getAbsoluteCenter(), sender.angle));
		mapHandler.testColliderAgainstMap(bullet);
		SoundManager.get().playSoundEffect("grenadeLauncher");
		return bullet;
	}

}
