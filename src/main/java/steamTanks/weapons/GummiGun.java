package steamTanks.weapons;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.math.Maths;
import engine.sound.SoundManager;
import steamTanks.tankAttackers.BouncingProjectile;
import steamTanks.tankAttackers.TankAttack;
import steamTanks.tanks.Tank;

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
