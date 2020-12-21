package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import collision.SAT;
import core.OpenGLGraphics;
import entities.Entity;
import entities.EntityHandler;
import map.MapHandler;
import particles.ParticleSystem;
import tankAttackers.Shrapnel;
import tankAttackers.TankAttack;
import tanks.AiTank;
import tanks.Tank;
import weapons.GrenadeLauncher;
import weapons.GummiGun;
import weapons.MachineGun;
import weapons.MineSetter;
import weapons.Weapon;
import weapons.WeaponPickup;

public class CustomEntityHandler extends EntityHandler {

	private MapHandler mapHandler;
	private ParticleSystem particles;

	float pickupTimeDistance = 5f;
	float timeToNextPickupSpawn = 0;
	private static Weapon[] allPickups;

	private List<Entity> toAdd = new ArrayList<>();

	public CustomEntityHandler(MapHandler mapHandler, MessageBus bus) {
		super(bus);
		this.mapHandler = mapHandler;
		particles = new ParticleSystem(mapHandler);
		allPickups = new Weapon[] { new GrenadeLauncher(bus), new GummiGun(bus), new MachineGun(bus),
				new MineSetter(bus) };

	}

	@Override
	public void update(float delta) {
		updateAiTankView();
		super.update(delta);
		toAdd.forEach(e -> addEntity(e));
		toAdd.clear();
		spawnPickups(delta);
		testTankBulletCollision();
		testTankPickupCollision();
		particles.update(delta);
	}

	private void updateAiTankView() {
		if (allEntities.get(AiTank.TYPE_NAME) != null)
			for (Entity e : allEntities.get(AiTank.TYPE_NAME)) {
				if (e instanceof AiTank) {
					AiTank ait = ((AiTank) e);
					ait.setTankList(allEntities.get(Tank.TYPE_NAME));
					ait.setThreadList(allEntities.get(TankAttack.TYPE_NAME));
				}
			}
	}

	@Override
	public void draw(OpenGLGraphics g) {
		super.draw(g);
		particles.draw(g);
	}

	private void spawnPickups(float delta) {
		timeToNextPickupSpawn -= delta;
		if (timeToNextPickupSpawn <= 0) {
			timeToNextPickupSpawn = pickupTimeDistance;
			Random r = new Random();
			Vector2f spawnPos = Gameplay.getRandomSpawnPos(r, this.mapHandler.currentMap);
			addEntity(new WeaponPickup(spawnPos, allPickups[r.nextInt(allPickups.length)], bus));
		}
	}

	private void testTankBulletCollision() {
		for (Entity tank : allEntities.get(Tank.TYPE_NAME)) {
			if (allEntities.get(TankAttack.TYPE_NAME) != null)
				for (Entity attacker : allEntities.get(TankAttack.TYPE_NAME)) {
					Vector2f mtv = SAT.getMTV(attacker.getCollisionShape(), tank.getCollisionShape());
					if (((TankAttack) attacker).enabled && mtv != null) {
						attacker.onCollision(tank.getCollisionShape(), mtv, "KILLER");
						tank.onCollision(attacker.getCollisionShape(), mtv, "KILLER");
					}
				}
		}
	}

	private void testTankPickupCollision() {
		for (Entity tank : allEntities.get(Tank.TYPE_NAME)) {
			if (((Tank) tank).extraWeapon == null && allEntities.get(WeaponPickup.TYPE_NAME) != null)
				for (Entity pickup : allEntities.get(WeaponPickup.TYPE_NAME)) {
					Vector2f mtv = SAT.getMTV(tank.getCollisionShape(), pickup.getCollisionShape());
					if (mtv != null) {
						tank.onCollision(pickup.getCollisionShape(), mtv,
								"PICKUP" + ((WeaponPickup) pickup).pickup.name);
						pickup.onCollision(tank.getCollisionShape(), mtv, "KILLER");
					}
				}
		}
	}

	public void fire(TankAttack ta) {
		toAdd.add(ta);
	}

	public void explode(Vector2f position, int numberOfBits, float textureIndex) {
		float dAngle = (float) (2d * Math.PI) / numberOfBits;
		for (int i = 0; i < numberOfBits; i++) {
			TankAttack p = new Shrapnel(null, new Vector2f(position), i * dAngle, textureIndex, bus);
			toAdd.add(p);
		}
	}

	public Tank spawnTank(Vector2f pos) {
		Tank t = new Tank(pos, bus);
		toAdd.add(t);
		return t;
	}

	public Tank spawnAiTank(Vector2f pos) {
		Tank t = new AiTank(pos, bus);
		toAdd.add(t);
		return t;
	}

	public void died(Vector2f location, float dieColor) {
		particles.addRadialEmittedParticles(location, 0.2f, 1.5f, 0.5f, 1.1f, 160, 0.4f, 0.025f, 0.25f, dieColor);
	}

	@Override
	public void procesMessage(Message m) {
		switch (m.header) {
		case "Particles":
			died((Vector2f) m.params[0], (float) m.params[1]);
			break;
		case "Fire":
			fire((TankAttack) m.params[0]);
			break;
		case "Explode":
			explode((Vector2f) m.params[0], (int) m.params[1], (float) m.params[2]);
		}
	}

	@Override
	public Message answer(Message m) {
		return null;
	}

}
