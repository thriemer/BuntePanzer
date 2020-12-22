package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.joml.Vector2f;

import bus.Message;
import gui.Menu;
import interfaces.TimeDependentUpdate;
import map.GameMap;
import tanks.AiTank;
import tanks.Tank;
import weapons.WeaponPickup;

public class Gameplay implements TimeDependentUpdate {

	private int tankCount = 2;
	private int aiTankCount = 1;
	private float color=0;

	private Tank[] tanks = new Tank[tankCount + aiTankCount];
	public ScoreBoard board;
	Random r = new Random();

	long oneTankRemains = 0;
	float timeToSurvive = 3;
	float secCounter = 0;
	int counter = 0;
	BuntePanzer buntePanzer;

	public Gameplay(BuntePanzer buntePanzer) {
		this.buntePanzer = buntePanzer;
		buntePanzer.zeitgeist.addTimeDependentSystem(this);
	}

	@Override
	public void update(float delta) {
		color+=0.05f*delta;
		secCounter += delta;
		buntePanzer.mapHandler.currentMap.mapTexture[0].setColor(color);
		counter++;
		if (secCounter > 1) {
			buntePanzer.display.setFrameTitle("Bunte Panzer: " + (int) (1f / (secCounter / counter)) + " FPS");
			secCounter = 0;
			counter = 0;
		}
		if (buntePanzer.entityHandler.allEntities.get("Tank").size() <= 1 && oneTankRemains == 0) {
			oneTankRemains = System.currentTimeMillis();
		}
		if (oneTankRemains != 0 && System.currentTimeMillis() - oneTankRemains - timeToSurvive * 1000 > 0) {
			buntePanzer.entityHandler.allEntities.get("Tank").forEach(e -> {
				Tank t = (Tank) e;
				for (int i = 0; i < tanks.length; i++) {
					if (t.equals(tanks[i])) {
						board.addScore(i, 1);
					}
				}
			});
			buntePanzer.entityHandler.allEntities.clear();
			newgame();
			oneTankRemains = 0;
		}
	}

	protected void newgame() {
		spawnPos.clear();
		buntePanzer.bus.remove(WeaponPickup.class);
		GameMap gm = MazeGenerator.getNewGeneratedMap();
		buntePanzer.camera.setContentSize(gm.mapSize.x + 2f * board.getUIWidth(), gm.mapSize.y + 1);
		buntePanzer.mapHandler.setMap(gm);
		redeployTanks(gm);
		buntePanzer.camera.setTarget(
				new Vector2f(MazeGenerator.mapWidth / 2f - board.getUIWidth(), MazeGenerator.mapHeight / 2f));
		spawnPos.clear();
		if (board.getMaxScore() >= Settings.winningScore) {
			Tank winner = board.getMaxScoreTank();
			buntePanzer.bus.sendMessage(
					new Message(this, "finishedGame").setRecievers(Menu.class,BuntePanzer.class).setParameters(winner.tankName, winner.texture.getCustomColor()));
		}
	}

	private void redeployTanks(GameMap gm) {
		for (Tank t : tanks) {
			t.redeploy(getRandomSpawnPos(r, gm));
			buntePanzer.entityHandler.addEntity(t);
		}
	}

	protected void initTanks() {

		tankCount = Settings.playerCount;
		aiTankCount = Settings.aiCount;
		tanks = new Tank[tankCount + aiTankCount];
		for (int i = 0; i < tankCount; i++) {
			Tank t = new Tank(new Vector2f(), buntePanzer.bus);
			t.setControls(Settings.usedControlScheme[i]);
			t.texture.setColor(Settings.hue[i]);
			t.tankName=Settings.names[i];
			tanks[i] = t;
		}
		for (int i = 0; i < aiTankCount; i++) {
			Tank t = new AiTank(new Vector2f(), buntePanzer.bus);
			t.texture.setColor(Settings.hue[Settings.hue.length - 1]);
			tanks[i + tankCount] = t;
		}
		board = new ScoreBoard(tanks.length);
		board.setTanksToAnimate(tanks);
		buntePanzer.zeitgeist.addTimeDependentSystem(board);
		buntePanzer.camera.addRenderAble(board);
	}

	private static List<Integer> spawnPos = new ArrayList<>();

	public static Vector2f getRandomSpawnPos(Random r, GameMap gm) {
		Set<Vector2f> nodes = gm.graph.lookUp.getValueSet();
		int index = r.nextInt(nodes.size());
		if (spawnPos.size() >= nodes.size()) {
			spawnPos.clear();
		}
		while (spawnPos.contains(index)) {
			index = r.nextInt(nodes.size());
		}
		spawnPos.add(index);
		Iterator<Vector2f> iter = nodes.iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		Vector2f node = new Vector2f(iter.next()).add(r.nextFloat() - 0.5f, r.nextFloat() - 0.5f);
		return node;
	}

}
