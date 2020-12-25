package steamTanks.weapons;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.math.Maths;
import engine.sound.SoundManager;
import steamTanks.tankAttackers.Grenade;
import steamTanks.tankAttackers.TankAttack;
import steamTanks.tanks.Tank;

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
