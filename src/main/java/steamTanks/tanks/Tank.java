package steamTanks.tanks;

import java.util.Random;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import graphics.animation.Animation;
import graphics.animation.Animator;
import engine.bus.Message;
import engine.bus.MessageBus;
import engine.collision.CollisionShape;
import engine.collision.OBB;
import graphics.core.OpenGLGraphics;
import engine.entities.Entity;
import engine.input.InputMap;
import steamTanks.mainGame.CustomEntityHandler;
import engine.math.Maths;
import engine.sound.SoundManager;
import steamTanks.tankAttackers.TankAttack;
import graphics.texture.Texture;
import graphics.texture.TextureManager;
import steamTanks.weapons.Pistol;
import steamTanks.weapons.Weapon;

public class Tank extends Entity {
	public static final String TYPE_NAME = "Tank";
	public float angle = 0f;
	float turnSpeed = (float) (Math.PI * 1.3f);
	public float turning = 0;
	public float driving = 0;
	public float driveSpeed = 3f;
	private Animator linkeKette;
	private Animator rechteKette;

	private InputMap inputMap;

	boolean playedReloadSound = true;
	Weapon standardWeapon;
	public Weapon extraWeapon;
	public final static float tankSize = 0.75f;
	public final static float tankWidth = 26 / 32f * tankSize;
	public final static float tankHeight = 23f / 32f * tankSize;
	public String tankName ="";

	public Tank(Vector2f position, MessageBus bus) {
		super(position, TYPE_NAME, new OBB(position, tankWidth, tankHeight),
				TextureManager.loadTexture("mariasPanzerAtlas", 4, 4,GL11.GL_LINEAR,GL11.GL_CLAMP), bus);
		this.texture.setSize(tankSize, tankSize);
		Texture rumpfTexture = TextureManager.loadTexture("mariasPanzerRumpf", 2, 2,GL11.GL_LINEAR,GL11.GL_CLAMP);
		rumpfTexture.setSize(tankSize, tankSize);
		Texture rumpfTexture2 = TextureManager.loadTexture("mariasPanzerRumpf", 2, 2,GL11.GL_LINEAR,GL11.GL_CLAMP);
		rumpfTexture2.setSize(tankSize, tankSize);
		rumpfTexture2.mirrorHorizontal = true;
		linkeKette = new Animator(rumpfTexture).enableSyncAnimations();
		linkeKette.addAnimation(new Animation("Move", 0, 3).setTimeToDisplayFrame(1f / 10f));
		linkeKette.playAnimation("Move");
		rechteKette = new Animator(rumpfTexture2).enableSyncAnimations();
		rechteKette.addAnimation(new Animation("Move", 0, 3).setTimeToDisplayFrame(1f / 10f));
		rechteKette.playAnimation("Move");
		((OBB) hitbox).calculateOffset(1f / 32f * tankSize, 4f / 32f * tankSize, tankSize, tankSize);
		angle = (float) (Math.random() * 2f * Math.PI);
		this.texture.setColor((float) Math.random());
		texture.layer = 0.5f;
		rechteKette.getTexture().layer = 0.51f;
		linkeKette.getTexture().layer = 0.51f;
		standardWeapon = new Pistol(bus);
	}

	Random r = new Random();

	@Override
	public void update(float dt) {
		updateTankAnimationState();
		linkeKette.update(dt);
		rechteKette.update(dt);
		updateWeapons(dt);
		updateTankState(dt);
		angle += turning * turnSpeed * dt;
		angle %= (float) (2f * Math.PI);
		((OBB) hitbox).angle = angle;
		Vector2f forwardVec = Maths.createUnitVecFromAngle(angle);
		velocity.set(forwardVec.x * driving * driveSpeed, forwardVec.y * driving * driveSpeed);
		playSounds();
		super.update(dt);
	}

	protected void updateWeapons(float dt) {
		standardWeapon.update(dt);
		if (extraWeapon != null) {
			extraWeapon.update(dt);
			if (extraWeapon.name.equals("GummiGun")) {
				this.texture.setFrameID(4);
			}
			if (extraWeapon.name.equals("GranatLauncher")) {
				this.texture.setFrameID(3);
			}
			if (extraWeapon.name.equals("MachineGun")) {
				this.texture.setFrameID(2);
			}
			if (extraWeapon.name.equals("MineSetter")) {
				this.texture.setFrameID(1);
			}
		} else {
			this.texture.setFrameID(0);
		}
	}

	private void playSounds() {
		if (getCurrentWeapon().reloading <= 0.5f && !playedReloadSound) {
			SoundManager.get().playSoundEffect("reload");
			playedReloadSound = true;
		}
	}

	protected void updateTankState(float dt) {
		if (inputMap.getValueForAction("changeColor") > 0f) {
			float currentColor = this.texture.getCustomColor();
			currentColor += 0.3f*dt;
			currentColor %= 1;
			this.texture.setColor(currentColor);
		}
		if (inputMap.getValueForAction("driveForward") > 0f) {
			driving = inputMap.getValueForAction("driveForward");
			driving *= driving > 0 ? 1 : -1;
		} else if (inputMap.getValueForAction("driveBackward") > 0f) {
			driving = inputMap.getValueForAction("driveBackward");
			driving *= driving < 0 ? 1 : -1;
		} else {
			driving = 0;
		}
		if (inputMap.getValueForAction("turnLeft") > 0f) {
			turning = inputMap.getValueForAction("turnLeft");
			turning *= turning < 0 ? 1 : -1;

		} else if (inputMap.getValueForAction("turnRight") > 0f) {
			turning = inputMap.getValueForAction("turnRight");
			turning *= turning > 0 ? 1 : -1;
		} else {
			turning = 0;
		}

		if (inputMap.getValueForAction("shoot") > 0) {
			shot();
		}
	}

	private void updateTankAnimationState() {
		if (turning == 0) {
			if (driving != 0) {
				boolean playBackwards = driving < 0;
				linkeKette.getCurrentAnimation().setPlaybackwards(playBackwards);
				linkeKette.start();
				rechteKette.getCurrentAnimation().setPlaybackwards(playBackwards);
				rechteKette.start();
			} else {
				linkeKette.stop();
				rechteKette.stop();
			}
		} else {
			if (driving != 0) {
				linkeKette.getCurrentAnimation().playForward();
				rechteKette.getCurrentAnimation().playForward();
				linkeKette.stopUpdateingFrames = turning < 0;
				rechteKette.stopUpdateingFrames = turning > 0;
			} else {
				linkeKette.start();
				rechteKette.start();
				linkeKette.getCurrentAnimation().setPlaybackwards(turning < 0);
				rechteKette.getCurrentAnimation().setPlaybackwards(turning > 0);

			}
		}
	}

	@Override
	public void onCollision(CollisionShape cs, Vector2f mtv, String flag) {
		super.onCollision(cs, mtv, flag);
		if (mtv != null && flag.startsWith("PICKUP")) {
			extraWeapon = Weapon.parseName(flag, bus);
			SoundManager.get().playSoundEffect("itemPickup");
		}

	}

	protected void shot() {
		if (getCurrentWeapon().canShoot()) {
			playedReloadSound = false;
			TankAttack ta = getCurrentWeapon().getTankAttack(this);
			bus.sendMessage(new Message(this, "Fire",CustomEntityHandler.class,ta));
		}
	}

	protected Weapon getCurrentWeapon() {
		if (extraWeapon == null) {
			return standardWeapon;
		} else {
			if (extraWeapon.hasAmmoLeft()) {
				return extraWeapon;
			} else {
				extraWeapon = null;
				standardWeapon.reloading = standardWeapon.reloadTime;
				return standardWeapon;
			}
		}
	}

	@Override
	public void draw(OpenGLGraphics g) {
		texture.angle = this.angle;
		g.drawImage(texture, position);
		Texture linkeKetteTexture = linkeKette.getTexture();
		linkeKetteTexture.angle = this.angle;
		g.drawImage(linkeKetteTexture, position);
		Texture rechteKetteTexture = rechteKette.getTexture();
		rechteKetteTexture.angle = this.angle;
		g.drawImage(rechteKetteTexture, position);
	}

	public void setControls(InputMap map) {
		this.inputMap = map;
	}

	public void redeploy(Vector2f pos) {
		isAlive = true;
		this.getCollisionShape().setCenter(pos);
		this.extraWeapon = null;
		this.standardWeapon.reloading = 0;
		playedReloadSound = true;
	}

	@Override
	public void die() {
		super.die();
		bus.sendMessage(new Message(this, "Particles").setRecievers(CustomEntityHandler.class)
				.setParameters(hitbox.getAbsoluteCenter(), this.texture.getCustomColor()));
		SoundManager.get().playSoundEffect("explosion");
	}
}
