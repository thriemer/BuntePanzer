package animation;

import interfaces.TimeDependentUpdate;
import math.Maths;

public class Animation implements TimeDependentUpdate {

	public String name;
	protected int startFrameID, endFrameID, currentID;
	public float timeToDisplayFrames;
	private float timeUntilNextFrameSwitch;

	public boolean loopAnimation = true;
	public boolean playBackwards = false;

	public Animation(String name, int startIdInclusiv, int endidInclusiv) {
		this.name = name;
		this.startFrameID = startIdInclusiv;
		this.endFrameID = endidInclusiv;
		this.currentID = this.startFrameID;
		this.timeToDisplayFrames = 1f / (endidInclusiv - startIdInclusiv);
		timeUntilNextFrameSwitch = timeToDisplayFrames;
	}

	@Override
	public void update(float delta) {
		timeUntilNextFrameSwitch -= delta;
		if (timeUntilNextFrameSwitch <= 0) {
			timeUntilNextFrameSwitch = timeToDisplayFrames;
			currentID = (int) Maths.clamp(currentID + (playBackwards ? -1 : 1), startFrameID - 1, endFrameID + 1);
			if (loopAnimation && currentID == (playBackwards ? startFrameID - 1 : endFrameID + 1))
				currentID = playBackwards ? endFrameID : startFrameID;
		}
	}

	public Animation startAnimation() {
		return startAnimation(0);
	}

	public Animation startAnimation(int startFrame) {
		timeUntilNextFrameSwitch = timeToDisplayFrames;
		currentID = startFrame + startFrameID;
		return this;
	}

	public int getCurrentFrame() {
		return currentID;
	}

	public int getRelativeCurrentFrame() {
		return currentID - startFrameID;
	}

	public Animation setTimeToDisplayFrame(float fct) {
		this.timeToDisplayFrames = fct;
		return this;
	}

	public Animation dontLoop() {
		this.loopAnimation = false;
		return this;
	}

	public Animation playBackwards() {
		playBackwards = true;
		return this;
	}

	public Animation playForward() {
		playBackwards = false;
		return this;
	}

	public Animation setPlaybackwards(boolean backwards) {
		playBackwards = backwards;
		return this;
	}
}
