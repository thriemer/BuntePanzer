package steamTanks.mainGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;

import engine.collision.AABB;
import engine.collision.CollisionShape;
import engine.map.BiMap;
import engine.map.GameMap;
import engine.map.MapGraph;
import engine.math.Maths;
import graphics.texture.Texture;
import graphics.texture.TextureManager;

public class MazeGenerator {
	private static Random rnd;
	private static int internMapWidth = 10;
	private static int internMapHeight = 10;
	private static float wallThicknes = 0.15f;
	public static float wallDistance = 1.5f;
	public static int mapHeight = (int) (internMapHeight * (wallDistance + wallThicknes));
	public static int mapWidth = (int) (internMapWidth * (wallDistance + wallThicknes));

	private static List<Vector2f> stack = new ArrayList<>();
	private static boolean[][] visited = new boolean[internMapWidth][internMapHeight];
	// 0 horizontal, 1 vertikal
	private static boolean[][][] barriers = new boolean[2][internMapWidth][internMapHeight];
	private static Vector2f currentCell;
	private static GameMap gm;

	private static int uniqueId = 0;
	private static MapGraph graph;
	private static List<Integer> currentPath;
	private static List<Vector2f> openSpace = new ArrayList<>();

	public static GameMap getNewGeneratedMap(long seed) {
		init();
		rnd.setSeed(seed);
		generateMaze();
		initMap();
		addCollisionRects();
		textureGameMap();
		gm.graph = graph;
		return gm;
	}

	public static GameMap getNewGeneratedMap() {
		init();
		generateMaze();
		initMap();
		addCollisionRects();
		translateLookupIntoWorldSpace();
		textureGameMap();
		gm.graph = graph;
		return gm;
	}

	private static void addCollisionRects() {
		for (int x = 0; x < internMapWidth; x++) {
			for (int y = 0; y < internMapHeight; y++) {
				if (barriers[0][x][y]) {
					int widthMultiplier = 1;
					for (int i = x + 1; i < internMapWidth; i++) {
						if (barriers[0][i][y]) {
							widthMultiplier++;
							barriers[0][i][y] = false;
						} else {
							break;
						}
					}
					gm.collisionPolys.add(new AABB(new Vector2f(x * wallDistance, (y + 1) * wallDistance),
							wallDistance * widthMultiplier, wallThicknes));

				}
				if (barriers[1][x][y]) {
					int heightMultiplier = 1;
					for (int i = y + 1; i < internMapHeight; i++) {
						if (barriers[1][x][i]) {
							heightMultiplier++;
							barriers[1][x][i] = false;
						} else {
							break;
						}
					}
					gm.collisionPolys.add(new AABB(new Vector2f((x + 1f) * wallDistance, y * wallDistance),
							wallThicknes, wallDistance * heightMultiplier + wallThicknes));
				}
			}
		}
		graph.lookUp.getValueSet().forEach(v -> {
			if ((int) v.x == 0) {
				gm.collisionPolys.add(new AABB(new Vector2f(0f, v.y * wallDistance), wallThicknes, wallDistance));
			}
			if ((int) v.y == 0) {
				gm.collisionPolys
						.add(new AABB(new Vector2f(v.x * wallDistance, 0), wallDistance + wallThicknes, wallThicknes));
			}
		});

	}

	private static void textureGameMap() {
		gm.mapTexture = new Texture[2];
		gm.mapTexture[0] = TextureManager.getTexture("mauer", 1, 1);
		gm.mapTexture[0].layer = -0.8f;
		for (CollisionShape cs : gm.collisionPolys) {
			AABB aabb = (AABB) cs;
			int repeatX = (int) (aabb.width / wallThicknes);
			int repeatY = (int) (aabb.height / wallThicknes);
			float[] textureInfo = new float[8];
			int i = 0;
			textureInfo[i++] = 0;
			textureInfo[i++] = 0;
			textureInfo[i++] = aabb.topLeft.x;
			textureInfo[i++] = aabb.topLeft.y;
			textureInfo[i++] = aabb.width;
			textureInfo[i++] = aabb.height;
			textureInfo[i++] = repeatX;
			textureInfo[i++] = repeatY;
			gm.textureInfos.add(textureInfo);
		}
	}

	private static void initMap() {
		gm = new GameMap(mapWidth, mapHeight);
	}

	private static void generateMaze() {
		while (unvisitedCellsCount() > 0) {
			List<Vector2f> unvisNb = getNeighbourUnvisitedCells();
			if (unvisNb.size() > 0) {
				stack.add(new Vector2f(currentCell));
				Vector2f next = unvisNb.get(rnd.nextInt(unvisNb.size()));
				visitCell(next);
				pushCurrentCellToGraph();
			} else if (stack.size() > 0) {
				currentCell = stack.get(0);
				stack.remove(0);
				graph.connectionInfo.add(currentPath);
				currentPath = new ArrayList<>();
				currentPath.addAll(graph.getFullPathName(graph.lookUp.getKey(currentCell)));
			}
		}
		graph.connectionInfo.add(currentPath);
	}

	private static int unvisitedCellsCount() {
		int unvisCells = 0;
		for (int i = 0; i < internMapWidth; i++) {
			for (int j = 0; j < internMapHeight; j++) {
				if (!visited[i][j]) {
					unvisCells++;
				}
			}
		}
		return unvisCells;
	}

	private static void visitCell(Vector2f to) {
		if (currentCell.x == to.x) {
			barriers[0][(int) to.x][(int) Math.min(currentCell.y, to.y)] = false;
		} else {
			barriers[1][(int) Math.min(currentCell.x, to.x)][(int) to.y] = false;
		}
		currentCell.set(to);
		visited[(int) to.x][(int) to.y] = true;
	}

	private static List<Vector2f> getNeighbourUnvisitedCells() {
		int cx = (int) currentCell.x;
		int cy = (int) currentCell.y;
		List<Vector2f> rtList = new ArrayList<>();
		if (!isCellVisited(cx - 1, cy))
			rtList.add(new Vector2f(cx - 1, cy));
		if (!isCellVisited(cx + 1, cy))
			rtList.add(new Vector2f(cx + 1, cy));
		if (!isCellVisited(cx, cy - 1))
			rtList.add(new Vector2f(cx, cy - 1));
		if (!isCellVisited(cx, cy + 1))
			rtList.add(new Vector2f(cx, cy + 1));
		return rtList;
	}

	private static boolean isCellVisited(int cx, int cy) {
		if (cx >= 0 && cx < internMapWidth && cy >= 0 && cy < internMapHeight) {
			return visited[cx][cy];
		}
		return true;
	}

	private static void init() {
		rnd = new Random();
		stack.clear();
		internMapWidth = rnd.nextInt(5) + 7;
		internMapHeight = rnd.nextInt(5) + 7;
		visited = new boolean[internMapWidth][internMapHeight];
		openSpace.clear();
		initPlayArea(40);
		barriers = new boolean[2][internMapWidth][internMapHeight];
		mapHeight = (int) (internMapHeight * (wallDistance + wallThicknes));
		mapWidth = (int) (internMapWidth * (wallDistance + wallThicknes));
		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < barriers[k].length; i++) {
				for (int j = 0; j < barriers[k][i].length; j++) {
					barriers[k][i][j] = placeWall(k, i, j);
				}
			}
		}
		graph = new MapGraph();
		currentPath = new ArrayList<>();
		do {
			currentCell = new Vector2f(rnd.nextInt(internMapWidth), rnd.nextInt(internMapHeight));
		} while (visited[(int) currentCell.x][(int) currentCell.y]);
		visited[(int) currentCell.x][(int) currentCell.y] = true;
		stack.add(new Vector2f(currentCell));
		pushCurrentCellToGraph();
	}

	private static boolean placeWall(int k, int x, int y) {
		int nextX = k == 1 ? x + 1 : x;
		int nextY = k == 0 ? y + 1 : y;
		boolean nextCellVisited = false;
		if (nextX >= internMapWidth || nextY >= internMapHeight) {
			nextCellVisited = true;
		} else {
			nextCellVisited = visited[nextX][nextY];
		}
		return !(visited[x][y] && nextCellVisited);
	}

	private static void initPlayArea(int playAreaSize) {
		for (boolean[] unvisitedCols : visited) {
			Arrays.fill(unvisitedCols, true);
		}
		int posX = rnd.nextInt(internMapWidth);
		int posY = rnd.nextInt(internMapHeight);
		int minX, minY;
		minX = minY = Integer.MAX_VALUE;
		int maxX, maxY;
		maxX = maxY = -Integer.MAX_VALUE;
		int breakCondition = Math.min(playAreaSize, internMapWidth * internMapHeight);
		while (unvisitedCellsCount() < breakCondition) {
			visited[posX][posY] = false;
			minX = Math.min(minX, posX);
			minY = Math.min(minY, posY);
			maxX = Math.max(maxX, posX);
			maxY = Math.max(maxY, posY);

			int move = rnd.nextInt(4);
			switch (move) {
			case 0:
				posX--;
				break;
			case 1:
				posX++;
				break;
			case 2:
				posY--;
				break;
			case 3:
				posY++;
				break;
			}
			posX = (int) Maths.clamp(posX, 0, internMapWidth - 1);
			posY = (int) Maths.clamp(posY, 0, internMapHeight - 1);
		}
		internMapWidth = maxX - minX + 1;
		internMapHeight = maxY - minY + 1;
		boolean[][] tmpVisited = new boolean[internMapWidth][internMapHeight];
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				int newX = x - minX;
				int newY = y - minY;
				tmpVisited[newX][newY] = visited[x][y];
				if (tmpVisited[newX][newY]) {
					openSpace.add(new Vector2f(newX, newY));
				}
			}
		}
		visited = tmpVisited;
	}

	private static void translateLookupIntoWorldSpace() {
		BiMap<Integer, Vector2f> newLookup = new BiMap<>();
		for (Integer i : graph.lookUp.getSet()) {
			Vector2f toTransform = graph.lookUp.get(i);
			toTransform.add(0.5f, 0.5f);
			newLookup.put(i, new Vector2f(toTransform.x * wallDistance, toTransform.y * wallDistance));
		}
		graph.lookUp = newLookup;
	}

	private static void pushCurrentCellToGraph() {
		int cellId = getUniqueId();
		graph.lookUp.put(cellId, new Vector2f(currentCell));
		currentPath.add(cellId);
	}

	private static int getUniqueId() {
		return uniqueId++;
	}

}
