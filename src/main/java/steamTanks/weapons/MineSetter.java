package steamTanks.weapons;

import engine.bus.MessageBus;
import engine.sound.SoundManager;
import steamTanks.tankAttackers.AntiTankMine;
import steamTanks.tankAttackers.TankAttack;
import steamTanks.tanks.Tank;

public class MineSetter extends Weapon {

	public MineSetter(MessageBus bus) {
		super(bus,"MineSetter", 3, 1f, 1);

	}

	@Override
	protected TankAttack shoot(Tank sender, MessageBus bus) {
		SoundManager.get().playSoundEffect("deployMine");
		return new AntiTankMine(sender, sender.hitbox.getAbsoluteCenter(), bus);
	}

}
