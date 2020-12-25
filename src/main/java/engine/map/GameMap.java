package engine.map;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import engine.collision.CollisionShape;
import graphics.core.OpenGLGraphics;
import graphics.texture.Texture;

public class GameMap implements Serializable {

	private static final long serialVersionUID = 7124537412308859456L;
	public Texture[] mapTexture;
	public Vector2f mapSize;
	public List<CollisionShape> collisionPolys = new ArrayList<>();
	// frame-id,pos,size
	public List<float[]> textureInfos = new ArrayList<>();
	public MapGraph graph;

	public GameMap(float width, float height) {
		mapSize = new Vector2f(width, height);
	}

	public void saveMap(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream("res/" + filename + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(OpenGLGraphics g) {
		// frame-id,pos,size
		for (float[] imageInfo : textureInfos) {
			int index = (int)imageInfo[0];
			mapTexture[index].setFrameID((int) imageInfo[1]);
			mapTexture[index].setSize(imageInfo[4], imageInfo[5]);
			mapTexture[index].repeatX = (int) imageInfo[6];
			mapTexture[index].repeatY = (int) imageInfo[7];
			g.drawImage(mapTexture[index], new Vector2f(imageInfo[2], imageInfo[3]));
		}
		/*
		 * for (List<Integer> path : graph.connectionInfo) { for (int i = 0; i <
		 * path.size() - 1; i++) { drawLine(graph.lookUp.get(path.get(i)),
		 * graph.lookUp.get(path.get(i + 1)), g); } }
		 */
	}

	public void addCollisionPolygons(List<CollisionShape> polys) {
		this.collisionPolys.addAll(polys);
	}
}
