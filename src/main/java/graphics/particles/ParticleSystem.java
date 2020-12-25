package graphics.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.joml.Vector2f;

import graphics.core.OpenGLGraphics;
import engine.interfaces.RenderAble;
import engine.interfaces.TimeDependentUpdate;
import engine.map.MapHandler;

public class ParticleSystem implements TimeDependentUpdate, RenderAble {

	private List<Particle> allParticles = new ArrayList<>();
	private MapHandler mapHandler;

	public ParticleSystem(MapHandler mh) {
		mapHandler = mh;
	}

	public void addParticle(Particle p) {
		allParticles.add(p);
	}

	public void addRadialEmittedParticles(Vector2f position, float circleError, float velocity, float velError,
			float dampening, int count, float lifeLength, float minRadius, float maxRadius, float color) {
		float deltaAlpha = (float) (2f * Math.PI) / count;
		Random r = new Random();
		for (int i = 0; i < count; i++) {
			float currentAngle = deltaAlpha * i * (1f + (r.nextFloat() * 2f - 1f) * circleError);
			float directionX = (float) Math.cos(currentAngle);
			float directionY = (float) Math.sin(currentAngle);
			float currentVelError = 1f + (r.nextFloat() * 2f - 1f) * velError;
			Particle p = new Particle(new Vector2f(position),
					new Vector2f(directionX * velocity * currentVelError, directionY * velocity * currentVelError),
					lifeLength, r.nextFloat() * (maxRadius - minRadius) + minRadius);
			p.setDampening(dampening);
			p.texture.setColor(color);
			p.texture.setAlpha(0.2f);
			allParticles.add(p);
		}
	}

	public void addParticleCone(Vector2f position, float direction, float angle, float spreadingError, float velocity,
			float velError, float dampening, int count, float lifeLength, float minRadius, float maxRadius,
			int frameID) {
		float deltaAlpha = 2f * angle / count;
		Random r = new Random();
		for (int i = 0; i < count; i++) {
			float currentAngle = (direction - angle + i * deltaAlpha)
					* (1f + (r.nextFloat() * 2f - 1f) * spreadingError);
			float directionX = (float) Math.cos(currentAngle);
			float directionY = (float) Math.sin(currentAngle);
			float currentVelError = 1f + (r.nextFloat() * 2f - 1f) * velError;
			Particle p = new Particle(new Vector2f(position),
					new Vector2f(directionX * velocity * currentVelError, directionY * velocity * currentVelError),
					lifeLength, r.nextFloat() * (maxRadius - minRadius) + minRadius);
			p.setDampening(dampening);
			p.texture.setFrameID(frameID);
			allParticles.add(p);
		}
	}

	@Override
	public void update(float dt) {
		ListIterator<Particle> itr = allParticles.listIterator();
		while (itr.hasNext()) {
			Particle p = itr.next();
			p.moveParticle(dt, mapHandler);
			if (p.isDead()) {
				itr.remove();
			}
		}

	}

	@Override
	public void draw(OpenGLGraphics g) {
		float layer = +0.6f;
		for (Particle p : allParticles) {
			if (p != null) {
				layer += 0.001f;
				p.texture.layer = layer;
				p.draw(g);
			}
		}
	}

	public void clear() {
		allParticles.clear();
	}
}
