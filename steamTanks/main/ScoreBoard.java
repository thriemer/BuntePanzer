package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.joml.Vector2f;

import animation.Animation;
import animation.Animator;
import core.OpenGLGraphics;
import interfaces.RenderAble;
import interfaces.TimeDependentUpdate;
import tanks.Tank;
import texture.DynamicTexture;
import texture.Texture;
import texture.TextureManager;
import weapons.Weapon;

public class ScoreBoard implements TimeDependentUpdate, RenderAble {

	public Vector2f position;
	private int tankCount;
	private DynamicTexture[] scoreVisulized;
	private Tank[] referenceTanks;
	private Texture[] tankIcon;
	private Animator[] chainAnimator;
	private float uiSize = 1f;
	private float uiSpace = 0.1f;
	private int textureSize = 512;

	private int[] currentScore;

	public ScoreBoard(int tankCount) {
		this.tankCount = tankCount;
		this.currentScore = new int[tankCount];
		scoreVisulized = new DynamicTexture[tankCount];
		setupScoreVisulizers();
		tankIcon = new Texture[tankCount];
		Texture tankChain = TextureManager.getTexture("panzerIconKette", 4, 4);
		chainAnimator = new Animator[tankCount];
		for (int i = 0; i < tankCount; i++) {
			chainAnimator[i] = new Animator(tankChain);
			chainAnimator[i]
					.addAnimation(new Animation("rolling", 0, 5).setTimeToDisplayFrame(1f / 10f).playBackwards());
			tankIcon[i] = TextureManager.getTexture("panzerIconAtlas", 4, 4);
		}
		position = new Vector2f();
	}

	public void setTanksToAnimate(Tank[] tanks) {
		referenceTanks = tanks;
		updateScoreTextures();
	}

	@Override
	public void draw(OpenGLGraphics g) {
//		g.addTranslation(position);
		float x = -2 * (uiSize + uiSpace);
		for (int i = 0; i < tankCount; i++) {
			setTextureColor(i, tankIcon[i], scoreVisulized[i]);
			setTextureSize(uiSize, uiSize, tankIcon[i], chainAnimator[i].getTexture(), scoreVisulized[i]);
			g.drawImage(tankIcon[i], new Vector2f(x, i * uiSize + uiSpace));
			g.drawImage(chainAnimator[i].getTexture(), new Vector2f(x, i * uiSize + uiSpace));
			g.drawImage(scoreVisulized[i], new Vector2f(uiSize + uiSpace + x, i * uiSize + uiSpace));
		}
//		g.subTranslation(position);
	}

	private void setTextureSize(float width, float height, Texture... toResize) {
		for (Texture t : toResize) {
			t.setSize(width, height);
		}
	}

	private void setTextureColor(int id, Texture... toColor) {
		for (Texture t : toColor) {
			t.setColor(referenceTanks[id].texture.getCustomColor());
		}
	}

	public void updateScore(int[] newScore) {
		System.arraycopy(newScore, 0, currentScore, 0, currentScore.length);
		updateScoreTextures();
	}

	public void addScore(int index, int score) {
		currentScore[index] += score;
		updateScoreTextures();
	}

	private void updateScoreTextures() {
		for (int i = 0; i < tankCount; i++) {

			String s = Integer.toString(currentScore[i]);
			BufferedImage img = scoreVisulized[i].image;
			scoreVisulized[i].fillWith(0);
			scoreVisulized[i].setSize(uiSize, uiSize);

			Graphics2D g = img.createGraphics();
			Color bg = Color.gray;
			g.setColor(bg);
			g.setFont(new Font("Sans Serif", Font.BOLD, textureSize / s.length()));
			int width = g.getFontMetrics().stringWidth(s);
			int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			FontRenderContext frc = new FontRenderContext(null, false, false);
			TextLayout tl = new TextLayout(s, g.getFont(), frc);
			AffineTransform textAt = new AffineTransform();
			textAt.translate(img.getWidth() / 2 - width / 2, img.getHeight() / 2 + height / 2);
			Shape outline = tl.getOutline(textAt);
			g.setColor(Color.white);
			g.fill(outline);
			g.setColor(Color.gray);
			BasicStroke wideStroke = new BasicStroke(20);
			g.setStroke(wideStroke);
			g.draw(outline);
			g.dispose();
			scoreVisulized[i].update();
		}
	}

	private void setupScoreVisulizers() {
		for (int i = 0; i < tankCount; i++) {
			BufferedImage img = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
			scoreVisulized[i] = new DynamicTexture(img);
		}
	}

	private int getMaxScoreIndex() {
		int index = 0;
		for (int i = 0; i < currentScore.length; i++) {
			if (currentScore[index] < currentScore[i]) {
				index = i;
			}
		}

		return index;
	}

	public int getMaxScore() {
		return currentScore[getMaxScoreIndex()];
	}

	@Override
	public void update(float delta) {
		for (int i = 0; i < tankCount; i++) {
			Tank t = referenceTanks[i];
			if (t.driving != 0 || t.turning != 0) {
				chainAnimator[i].start();
				chainAnimator[i].getCurrentAnimation().playBackwards = t.driving == 1 || t.turning == 1;
			} else {
				chainAnimator[i].stop();
			}
			if (t.extraWeapon != null) {
				tankIcon[i].setFrameID(parseWeaponToId(t.extraWeapon));
			} else {
				tankIcon[i].setFrameID(0);
			}
			chainAnimator[i].update(delta);
		}
	}

	private static int parseWeaponToId(Weapon w) {
		if (w.name.contains("GranatLauncher"))
			return 4;
		if (w.name.contains("GummiGun"))
			return 3;
		if (w.name.contains("MachineGun"))
			return 2;
		if (w.name.contains("MineSetter"))
			return 1;

		return -1;
	}

	public float getUIWidth() {
		return 2f * (uiSize + uiSpace);
	}

	public Tank getMaxScoreTank() {
		return referenceTanks[getMaxScoreIndex()];
	}
}
