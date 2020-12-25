package graphics.context;

public class ContextInformation {

	public int displayWidth, displayHeight;
	public int renderBufferWidth, renderBufferHeight;
	public float aspectRatio;

	public ContextInformation(int displayWidth, int displayHeight, int renderBufferWidth, int renderBufferHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.renderBufferWidth = renderBufferWidth;
		this.renderBufferHeight = renderBufferHeight;
		this.aspectRatio = (float) renderBufferWidth / (float) renderBufferHeight;
	}

	public String toString() {
		return "(" + displayWidth + " | " + displayHeight + ") \n (" + renderBufferWidth + " | " + renderBufferHeight
				+ ")";
	}

}
