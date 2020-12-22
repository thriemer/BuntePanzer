package tankAttackers;

import org.joml.Vector2f;

import animation.Animation;
import animation.Animator;
import bus.Message;
import bus.MessageBus;
import collision.ArcHitbox;
import main.CustomEntityHandler;
import map.MapHandler;
import math.Maths;
import sound.SoundManager;
import tanks.Tank;
import texture.TextureManager;

public class AntiTankMine extends TankAttack {

	private float activationTime = 2f;
	private float aliveTimeTime = 10f;

	private Animator mineAnimations;

	public AntiTankMine(Tank home, Vector2f position,MessageBus bus) {
		super(home, position, new ArcHitbox(position, 0.125f), TextureManager.getTexture("mine", 2, 2), bus);
		texture.setSize(0.25f, 0.25f);
		mineAnimations = new Animator(texture);
		mineAnimations.addAnimation(new Animation("Harmless", 0, 0).dontLoop());
		mineAnimations.addAnimation(new Animation("Activated", 1, 2).setTimeToDisplayFrame(1f / 4f));
		mineAnimations.playAnimation("Harmless");
		hitbox.setCenter(position);
		enabled = false;
		this.texture.layer = 0.6f;
		this.texture.setAlpha(0.75f);
	}

	@Override
	public void update(float dt) {
		activationTime -= dt;
		enabled = activationTime <= 0;
		mineAnimations.update(dt);
		if (enabled) {
			mineAnimations.playAnimation("Activated");
			aliveTimeTime -= dt;
			mineAnimations.getCurrentAnimation()
					.setTimeToDisplayFrame(Maths.map(aliveTimeTime, 10, 0, 1f / 2f, 1f / 15f));
			if (aliveTimeTime <= 0) {
				super.die();
				bus.sendMessage(new Message(this, "Particles",CustomEntityHandler.class,hitbox.getAbsoluteCenter(),0f));
				SoundManager.get().playSoundEffect("explosion");
			}
		}
		texture.setFrameID(mineAnimations.getCurrentAnimation().getCurrentFrame());
	}
	

}
