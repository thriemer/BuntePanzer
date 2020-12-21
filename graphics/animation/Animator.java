package animation;

import java.util.HashMap;
import java.util.Map;

import interfaces.TimeDependentUpdate;
import texture.Texture;

public class Animator implements TimeDependentUpdate {

	private Texture toAnimate;
	public boolean stopUpdateingFrames = false;
	private Map<String, Animation> allAnimationsInAtlas = new HashMap<String, Animation>();

	private Animation currentAnimation;
	private boolean syncAnimations = false;

	public Animator(Texture toAnimate) {
		this.toAnimate = toAnimate;
	}

	public void addAnimation(Animation animation) {
		allAnimationsInAtlas.put(animation.name, animation);
		if (currentAnimation == null) {
			currentAnimation = animation;
		}
	}

	public void playAnimation(String name) {
		if (!name.equals(currentAnimation.name)) {
			stopUpdateingFrames = false;
			int currentFrame = currentAnimation.getRelativeCurrentFrame();
			currentAnimation = allAnimationsInAtlas.get(name);
			currentAnimation.startAnimation(syncAnimations ? currentFrame : 0);
		}
	}

	@Override
	public void update(float delta) {
		if (!stopUpdateingFrames)
			currentAnimation.update(delta);
	}

	public Texture getTexture() {
		toAnimate.setFrameID(currentAnimation.getCurrentFrame());
		return toAnimate;
	}

	public Animator enableSyncAnimations() {
		syncAnimations = true;
		return this;
	}

	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	public void stop() {
		stopUpdateingFrames = true;
	}

	public void start() {
		stopUpdateingFrames = false;
	}
}
