package coreEngine;

import java.util.ArrayList;
import java.util.List;

import interfaces.TimeDependentUpdate;

public class Zeitgeist {

	public static float FPS_CAP = 900;
	long lastFrame = System.currentTimeMillis();
	float delta;

	public void sleep() {
		updateTimeDelta();
		sleep(getDelay());
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateTimeDelta() {
		delta = (System.currentTimeMillis() - lastFrame) / 1000f;
		lastFrame = System.currentTimeMillis();
	}

	public float getDelta() {
		return delta;
	}

	public int getDelay() {
		return (int) Math.max((1f / FPS_CAP - delta) * 1000, 0);
	}

	private List<TimeDependentUpdate> toUpdate = new ArrayList<TimeDependentUpdate>();

	public void updateSystems(float delta) {
		for (TimeDependentUpdate tdu : toUpdate) {
			tdu.update(delta);
		}
	}

	public void addTimeDependentSystem(TimeDependentUpdate tdu) {
		toUpdate.add(tdu);
	}

	public void clear() {
		toUpdate.clear();
	}
}
