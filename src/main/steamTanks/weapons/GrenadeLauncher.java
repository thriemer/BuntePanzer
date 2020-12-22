package weapons;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import map.MapHandler;
import math.Maths;
import sound.SoundManager;
import tankAttackers.Grenade;
import tankAttackers.TankAttack;
import tanks.Tank;

public class GrenadeLauncher extends Weapon {

	public GrenadeLauncher(MessageBus bus) {
		super(bus, "GranatLauncher", 0, 1, 1);
	}

	@Override
	protected TankAttack shoot(Tank sender, MessageBus bus) {
		TankAttack bullet = new Grenade(sender, new Vector2f(), Maths.createUnitVecFromAngle(sender.angle).mul(4f),
				sender.texture.getCustomColor(), bus);
		bullet.hitbox.setCenter(this.getCenterOfSpawnPosition(sender.hitbox.getAbsoluteCenter(), sender.angle));
		mapHandler.testColliderAgainstMap(bullet);
		SoundManager.get().playSoundEffect("grenadeLauncher");
		return bullet;
	}

}
