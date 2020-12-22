package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import context.ContextInformation;
import texture.Texture;

public class OpenGLGraphics {

	private ContextInformation contextInfo;

	private Matrix4f projMatrix = new Matrix4f();
	private Matrix4f camMatrix = new Matrix4f();
	private Vector2f translation = new Vector2f();
	private List<SpriteInfo> spriteInfoStack = new ArrayList<>();
	int spriteStackIndex = 0;
	private float rotation = 0f;
	private float frustumWidth, frustumHeight;

	private Map<Integer, List<SpriteInfo>> toRenderSprites = new HashMap<>();
	private Shader shader;
	private Vao quad;

	public OpenGLGraphics(Shader shader, Vao quad, ContextInformation ci) {
		this.shader = shader;
		this.quad = quad;
		contextInfo = ci;
	}

	public static boolean useRainbow = false;

	public void show() {
		spriteStackIndex = 0;
		GL11.glViewport(0, 0, contextInfo.renderBufferWidth, contextInfo.renderBufferHeight);
		createOrthoProjMatrix(frustumWidth, frustumHeight);
		shader.bind();
		shader.loadMatrix("projMatrix", projMatrix);
		shader.loadMatrix("viewMatrix", camMatrix);
		quad.bind();
		for (int texture : toRenderSprites.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 );
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			List<SpriteInfo> locations = toRenderSprites.get(texture);
			locations.sort((s1, s2) -> Float.compare(s1.toRender.layer, s2.toRender.layer));
			for (SpriteInfo si : locations) {
				shader.loadMatrix("transformationMatrix", si.transformation);
				shader.load4DVector("textureOffset", si.textureOffset);
				shader.loadFloat("alpha", si.toRender.alpha);
				shader.loadInt("useCustomColor", si.toRender.useCustomColor() ? 1 : 0);
				shader.loadInt("mirrorHorizontal", si.toRender.mirrorHorizontal ? 1 : 0);
				shader.loadInt("mirrorVertical", si.toRender.mirrorVertical ? 1 : 0);
				shader.load2DVector("repeat", si.repeat);
				shader.load2DVector("textureSize", si.toRender.texelSize);
				if (si.toRender.useCustomColor()) {
					shader.loadFloat("color", si.toRender.getCustomColor());
				}
				GL11.glDrawElements(GL11.GL_QUADS, quad.getIndiciesLength(), GL11.GL_UNSIGNED_INT, 0);
			}
		}
		quad.unbind();
		shader.unbind();
		toRenderSprites.clear();
	}

	public void drawImage(Texture texture, Vector2f pos) {
		List<SpriteInfo> matricies = toRenderSprites.get(texture.getID());
		if (matricies == null) {
			matricies = new ArrayList<>();
			toRenderSprites.put(texture.getID(), matricies);
		}
		if (spriteStackIndex >= spriteInfoStack.size()) {
			spriteInfoStack.add(new SpriteInfo());
		}
		SpriteInfo si = spriteInfoStack.get(spriteStackIndex++);
		si.set(texture, createModelMatrix(si.transformation, pos, texture.layer, texture.angle, texture.getSize()),
				getTextureOffset(si.textureOffset, texture));
		si.setRepeat(texture.repeatX, texture.repeatY);
		matricies.add(si);
	}

	public void setTranslation(Vector2f trans) {
		translation.set(trans);
		updateCamMatrix();
	}

	public void setRotation(float rot) {
		rotation = rot;
		camMatrix.setRotationXYZ(0, 0, rot);
	}

	public void addTranslation(Vector2f trans) {
		translation.add(trans);
		updateCamMatrix();
	}

	public void subTranslation(Vector2f trans) {
		translation.sub(trans);
		updateCamMatrix();
	}

	public void addRotation(float angle) {
		rotation += angle;
		updateCamMatrix();
	}

	private void updateCamMatrix() {
		camMatrix.translate(translation.x, translation.y, 0);
		camMatrix.setRotationXYZ(0, 0, rotation);
	}

	public Vector2f getTranslation() {
		return translation;
	}

	public float getRotation() {
		return rotation;
	}

	private void createOrthoProjMatrix(float width, float height) {
		projMatrix = projMatrix.ortho(0, width, height, 0, -1f, 1f);
	}

	private Matrix4f createModelMatrix(Matrix4f modelMatrix, Vector2f pos, float layer, float angle, Vector2f scale) {
		modelMatrix.identity();
		modelMatrix.translate(pos.x + scale.x / 2f, pos.y + scale.y / 2f, layer);
		modelMatrix.rotate(angle, 0, 0, 1);
		modelMatrix.scale(scale.x, scale.y, 0);
		return modelMatrix;
	}

	private Vector4f getTextureOffset(Vector4f textureOffset, Texture t) {
		textureOffset.x = (float) (t.getFrameID() % t.getTextureRows()) / (float) t.getTextureRows();
		textureOffset.y = (float) (t.getFrameID() / t.getTextureRows()) / (float) t.getTextureColumns();
		textureOffset.z = t.getTextureColumns();
		textureOffset.w = t.getTextureRows();
		return textureOffset;
	}

	public void setViewFrustum(Vector2f frustum) {
		frustumWidth = frustum.x;
		frustumHeight = frustum.y;
	}

	public final ContextInformation getContextInfo() {
		return contextInfo;
	}
}

class SpriteInfo {
	public Matrix4f transformation;
	public Vector4f textureOffset;

	public Vector2f repeat = new Vector2f(1, 1);
	public Texture toRender;

	public SpriteInfo set(Texture torender, Matrix4f transformation, Vector4f textureOffset) {
		this.transformation = transformation;
		this.textureOffset = textureOffset;
		this.toRender = torender;
		return this;
	}

	public SpriteInfo() {
		transformation = new Matrix4f();
		textureOffset = new Vector4f();
	}

	public SpriteInfo setRepeat(int x, int y) {
		repeat.set(x, y);
		return this;
	}

}
