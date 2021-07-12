package steamTanks.tankAttackers;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.math.Maths;
import steamTanks.tanks.Tank;
import graphics.texture.TextureManager;

public class Shrapnel extends BouncingProjectile {

	private static float shrapnelStartVel = 3f;
	private static float explosionRadius = 2f;
	private static float spawnRadius = 0.0f;
	private static float longestTimeAlive = 1f;
	private Vector2f centerPos;
	private float decayTime = longestTimeAlive;
	private float decayDistance;
	private float turnSpeed;

	public Shrapnel(Tank home, Vector2f centerOfSpawn, float angle, float customColor, MessageBus bus) {
		super(home, getStartPos(centerOfSpawn, angle),
				Maths.createUnitVecFromAngle(angle).mul(-shrapnelStartVel * ((float) Math.random() * 1.5f + 1f)),
				customColor, bus);
		centerPos = new Vector2f(centerOfSpawn);
		this.texture = TextureManager.getTexture("tankAttackers", 2, 2);
		this.texture.setFrameID(1);
		this.hitbox.topLeft = this.position;
		this.hitbox.radius = 0.15f / 2f;
		this.texture.setSize(0.15f, 0.15f).setColor(customColor);
		this.acceleration = Maths.createUnitVecFromAngle(angle)
				.mul(getStopAccel(this.velocity.length(), explosionRadius + spawnRadius));
		decayDistance = explosionRadius * (float) (1f + Math.random() * 0.25f - 0.5f);
		bouncesLeft = 1;
		turnSpeed = Maths.map(velocity.length(), 0, 2f * shrapnelStartVel, 6, 16) + 6f;
	}

	public void update(float dt) {
		this.decayTime -= dt;
		if (decayTime <= 0 || centerPos.distance(position) > decayDistance) {
			die();
		}
		super.update(dt);
		this.texture.angle += turnSpeed * dt;
	}

	private static Vector2f getStartPos(Vector2f center, float angle) {
		return Maths.createUnitVecFromAngle(angle).mul(spawnRadius * (float) (1f - Math.random() * 0.8f)).add(center);
	}

	private static float getStopAccel(float startVel, float wayUntilStop) {
		return 1f / wayUntilStop * (0.5f * (float) Math.pow(startVel, 2) - startVel);
	}
}
