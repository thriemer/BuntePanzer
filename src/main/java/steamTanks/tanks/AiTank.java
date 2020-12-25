package steamTanks.tanks;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import engine.bus.MessageBus;
import engine.entities.Entity;
import engine.math.Maths;
import engine.math.Ray;
import steamTanks.tankAttackers.TankAttack;

public class AiTank extends Tank {

	public AiTank(Vector2f position, MessageBus bus) {
		super(position, bus);
		this.tankName = "AI Tank";
	}

	private Tank closestTank;
	private TankAttack closestThread;
	private List<Entity> tankList;
	private List<Entity> threadList;

	private Vector2f targetPoint = new Vector2f();
	List<Vector2f> path = new ArrayList<>();
	private float activationTime = 1.5f;

	public void update(float dt) {
		activationTime -= dt;
		if (activationTime <= 0) {
			super.update(dt);
		} else {
			mapHandler.testColliderAgainstMap(this);
			updateWeapons(dt);
		}
	}

	@Override
	public void redeploy(Vector2f pos) {
		super.redeploy(pos);
		activationTime = 1.5f;
		path.clear();
	}

	@Override
	protected void updateTankState(float dt) {
		getClosestTank();
		getClosestThread();
		Vector2f forward = Maths.createUnitVecFromAngle(angle);
		Vector2f sideWay = Maths.createUnitVecFromAngle(angle + (float) Math.PI / 2f);
		turning = 1;
		driving = 0;
		boolean important = false;
		if (closestTank != null && (path.size() == 0)) {
			path.clear();
			path = mapHandler.getPathFromTo(this.position, closestTank.position);
		}
		if (path.size() > 0 && path.get(0).distance(getCollisionShape().getAbsoluteCenter()) < 0.5) {
			path.remove(0);
		}
		if (path.size() > 0)
			targetPoint = path.get(0);
		if (closestThread != null) {
			Vector2f ownCenter = getCollisionShape().getAbsoluteCenter();
			Vector2f nearPoint = getClosestPointNearestAttackerGets();
			float saveDistance = getCollisionShape().radius + closestThread.getCollisionShape().radius * 2f;
			boolean isThread = nearPoint.distance(ownCenter) < saveDistance;
			if (isThread) {
				Vector2f awayFromThread = new Vector2f(ownCenter).sub(nearPoint);
				Ray spaceray1 = new Ray(ownCenter, new Vector2f(awayFromThread));
				Ray spaceray2 = new Ray(ownCenter, new Vector2f(awayFromThread));
				Vector2f col1 = mapHandler.getRayCollisionPoint(spaceray1);
				Vector2f col2 = mapHandler.getRayCollisionPoint(spaceray2);
				if (col1 != null && col2 != null & col1.distance(spaceray1.start) < col2.distance(spaceray2.start)) {
					awayFromThread.negate();
				}
				awayFromThread.normalize(saveDistance);
				targetPoint = awayFromThread.add(nearPoint);
				important = true;
				path.clear();
			}
		}
		boolean driveToTarget = true;
		if (closestTank != null) {
			Vector2f enemyCenter = closestTank.getCollisionShape().getAbsoluteCenter();
			Vector2f toEnemy = new Vector2f(enemyCenter).sub(getCollisionShape().getAbsoluteCenter());
			Ray wallinBetweeen = new Ray(getCollisionShape().getAbsoluteCenter(), new Vector2f(toEnemy).normalize());
			Vector2f hitPoint = mapHandler.getRayCollisionPoint(wallinBetweeen);
			boolean isWallBetweeen = hitPoint != null && hitPoint.distance(wallinBetweeen.start) < toEnemy.length();
			float sideWaysDot = sideWay.dot(new Vector2f(toEnemy).normalize());
			boolean aimsAtEnemy = forward.dot(toEnemy) > 0
					&& Math.abs(sideWaysDot) < (getCollisionShape().radius / 2f) / toEnemy.length();
			if (!isWallBetweeen) {
				if (!important) {
					driveToTarget = enemyCenter.distance(position) > 2;
					targetPoint = enemyCenter;
				}
				if (aimsAtEnemy && getCurrentWeapon().canShoot()) {
					shot();
					path.clear();
				}
			}
		}
		if (targetPoint != null) {
			Vector2f toTarget = new Vector2f(targetPoint).sub(getCollisionShape().getAbsoluteCenter()).normalize();
			double angle = (float) (Math.atan2(toTarget.y, toTarget.x) - Math.atan2(forward.y, forward.x));
			if (angle > Math.PI) {
				angle -= 2f * Math.PI;
			} else if (angle <= -Math.PI) {
				angle += 2 * Math.PI;
			}
			if (driveToTarget)
				driving = toTarget.dot(forward) > 0 ? 1 : -1;
			turning = (angle > 0 ? 1 : -1);
		}
	}

	private Vector2f getClosestPointNearestAttackerGets() {
		Vector2f ownCenter = getCollisionShape().getAbsoluteCenter();
		Vector2f threadCenter = closestThread.getCollisionShape().getAbsoluteCenter();
		Vector2f fromProjectile = new Vector2f(ownCenter).sub(threadCenter);
		Ray bulletPath = new Ray(threadCenter, new Vector2f(closestThread.velocity));
		Vector2f lotDir = Maths.getNormal(closestThread.velocity);
		if (lotDir.dot(fromProjectile) > 0) {
			lotDir.mul(-1f);
//			dafuer sorgen, dass der lot vektor zum Kugel weg zeigt
		}
		Ray lot = new Ray(ownCenter, lotDir);
		Vector2f closestIntersection = lot.getRayIntersectionPoint(bulletPath);
		if (closestIntersection == null || closestThread.velocity.length() < 0.01f) {
			closestIntersection = threadCenter;
		}
		return closestIntersection;
	}

	private void getClosestTank() {
		closestTank = null;
		if (tankList != null) {
			for (Entity e : tankList) {
				Tank t = (Tank) e;
				if (!t.equals(this) && (closestTank == null
						|| closestTank.position.distance(position) > t.position.distance(position))) {
					closestTank = t;
				}
			}
		}
	}

	private void getClosestThread() {
		closestThread = null;
		if (threadList != null) {
			for (Entity e : threadList) {
				TankAttack t = (TankAttack) e;
				if (closestThread == null || (closestThread.position.distance(position) > t.position.distance(position)
						&& new Vector2f(position).sub(t.position).dot(t.velocity) > -0.25)) {
					closestThread = t;
				}
			}
		}
	}

	public void setTankList(List<Entity> entity) {
		tankList = entity;
	}

	public void setThreadList(List<Entity> p) {
		threadList = p;
	}

}