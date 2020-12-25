package steamTanks.weapons;

import org.joml.Vector2f;

import engine.bus.Message;
import engine.bus.MessageBus;
import engine.map.MapHandler;
import engine.math.Maths;
import engine.math.Ray;
import steamTanks.tankAttackers.TankAttack;
import steamTanks.tanks.Tank;

public abstract class Weapon {

	protected MessageBus bus;
	public int pickupTexture;
	public String name;

	public float reloadTime, reloading;
	public int ammoCount = 1;
	protected MapHandler mapHandler;

	public Weapon(MessageBus bus,String name, int pickupTexture, float reloadTime, int ammoCount) {
		this.reloadTime = reloadTime;
		this.ammoCount = ammoCount;
		reloading = 0.2f;
		this.pickupTexture = pickupTexture;
		this.name = name;
		this.bus=bus;
		this.mapHandler=(MapHandler) bus.request(new Message(this, "getMapHandler",MapHandler.class))[0].params[0];
	}

	public void update(float dt) {
		reloading -= dt;
	}

	public TankAttack getTankAttack(Tank sender) {
		resetTimerAndDecreaseAmmo();
		return shoot(sender, bus);
	}

	protected void resetTimerAndDecreaseAmmo() {
		ammoCount--;
		reloading = reloadTime;
	}

	protected Vector2f getCenterOfSpawnPosition(Vector2f tankCenter, float angle) {
		Vector2f spawn = Maths.createUnitVecFromAngle(angle).mul(Tank.tankWidth*1.1f).add(tankCenter);
		Ray spawnThroughWallTester = new Ray(tankCenter, Maths.createUnitVecFromAngle(angle));
		Vector2f wallHitpoint = mapHandler.getRayCollisionPoint(spawnThroughWallTester);
		if (wallHitpoint != null && tankCenter.distance(wallHitpoint) < tankCenter.distance(spawn)) {
			return wallHitpoint;
		}
		return spawn;
	}

	protected abstract TankAttack shoot(Tank sender, MessageBus bus);

	public boolean hasAmmoLeft() {
		return ammoCount > 0;
	}

	public boolean canShoot() {
		return reloading <= 0 && hasAmmoLeft();
	}

	public static Weapon parseName(String flag,MessageBus bus) {
		if (flag.contains("Pistol")) {
			return new Pistol(bus);
		}
		if (flag.contains("GranatLauncher")) {
			return new GrenadeLauncher(bus);
		}
		if (flag.contains("GummiGun")) {
			return new GummiGun(bus);
		}
		if (flag.contains("MachineGun")) {
			return new MachineGun(bus);
		}
		if (flag.contains("MineSetter")) {
			return new MineSetter(bus);
		}

		System.out.println("Flag: " + flag + " is not a known Weapon");
		return null;
	}
}
