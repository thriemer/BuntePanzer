package graphics.particles;

import org.joml.Vector2f;

import graphics.core.OpenGLGraphics;
import engine.interfaces.RenderAble;
import engine.map.MapHandler;
import graphics.texture.Texture;
import graphics.texture.TextureManager;

public class Particle implements RenderAble {

	public Vector2f position, velocity;
	float lifeLength, radius, error, dampening;
	float angleVel = 12f;
	boolean collided;
	public boolean dieOnCollision = false;
	int affectedByGravity = 0;
	protected Texture texture;

	public Particle(Vector2f position, Vector2f velocity, float lifeLength, float radius) {
		texture = TextureManager.getTexture("particle", 2, 2);
		this.texture.setFrameID(Math.random()>0.5f?0:1);
		this.position = position;
		this.velocity = velocity;
		this.lifeLength = lifeLength;
		this.radius = radius;
		angleVel += (Math.random()*2f-1f)*2*angleVel;
		texture.setSize(radius, radius);
	}

	public Particle setDampening(float dampening) {
		this.dampening = dampening;
		return this;
	}

	public void revive(float lifelength) {
		collided = false;
		this.lifeLength = lifelength;
	}

	public void moveParticle(float dt, MapHandler mapHandler) {
		this.texture.angle+=angleVel*dt;
		velocity.y *= 1f + dt / dampening;
		velocity.x *= 1f + dt / dampening;
		position.add(new Vector2f(velocity).mul(dt));
		lifeLength -= dt;
		Vector2f mtv = mapHandler.pointCollidesMap(position);
		if (mtv != null) {
			position.add(mtv);
			velocity.set(0);
			collided = true;
		}
	}

	public boolean isDead() {
		return lifeLength < 0 || (collided && dieOnCollision);
	}

	@Override
	public void draw(OpenGLGraphics g) {
		g.drawImage(texture, position);
	}

	@Override
	public Particle clone() {
		return new Particle(new Vector2f(position), new Vector2f(velocity), lifeLength, radius);
	}

	public void setColor(float color) {
		this.texture.setColor(color);
	}

}
