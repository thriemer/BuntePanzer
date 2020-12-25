package graphics.texture;

import java.nio.ByteBuffer;

import org.joml.Vector2f;

import graphics.core.OGLTexture;

public class Texture extends OGLTexture {

	public float alpha = 1, angle = 0f;
	private int frameCount;
	private int rows = 1, columns = 1;
	private int frameID = 0;
	private float width = 1;
	private float height = 1;
	private boolean useCustomColor = false;
	private float color;
	public boolean mirrorHorizontal = false;
	public boolean mirrorVertical = false;
	public float layer = 0;
	public int repeatX = 1, repeatY = 1;

	public Texture(ByteBuffer buffer, int width, int height) {
		super(buffer, width, height);
	}
	public Texture(ByteBuffer buffer, int width, int height, int filter, int wrap) {
		super(buffer, width, height,filter,wrap);
	}
	public Texture(int id) {
		super(id, TextureManager.getTexelSize(id));
		frameCount = rows = columns = 1;
	}

	public Texture(int id, int rows, int colums) {
		super(id, TextureManager.getTexelSize(id));
		this.textureID = id;
		frameCount = rows * colums;
		this.rows = rows;
		this.columns = colums;
	}

	public int getTotalFrameCount() {
		return frameCount;
	}

	public int getTextureRows() {
		return rows;
	}

	public int getTextureColumns() {
		return columns;
	}

	public int getFrameID() {
		return frameID;
	}

	public Texture setFrameID(int frameID) {
		this.frameID = frameID;
		return this;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Texture setRows(int rows) {
		this.rows = rows;
		frameCount = rows * columns;
		return this;
	}

	public Texture setColumns(int columns) {
		this.columns = columns;
		frameCount = rows * columns;
		return this;
	}

	public Texture setSize(float width, float height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public void setFrameCount(int count) {
		frameCount = count;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public int getID() {
		return textureID;
	}

	public void setID(int texture) {
		this.textureID = texture;
	}

	public Vector2f getSize() {
		return new Vector2f(width, height);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setColor(float color) {
		this.color = color;
		this.useCustomColor = true;
	}

	public boolean useCustomColor() {
		return useCustomColor;
	}

	public float getCustomColor() {
		return color;
	}

}
