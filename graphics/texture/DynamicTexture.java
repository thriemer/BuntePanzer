package texture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class DynamicTexture extends Texture {

	public BufferedImage image;
	private ByteBuffer buffer;
	private int[] pixels;

	public DynamicTexture(BufferedImage img) {
		//this is super hacky but needs to happen since i cant figure out fast enough
//		how nuklear works
		super(BufferUtils.createByteBuffer(img.getWidth()*img.getHeight() * 4),img.getWidth(),img.getHeight());
		this.image=img;
		setupPixelsAndBuffer();
		update();
	}
	
	public void update() {
		this.drawOnTexture(buffer, pixels, image.getWidth(), image.getHeight());
	}

	private void setupPixelsAndBuffer() {
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		buffer = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight() * 4); // 4 for RGBA, 3 for RGB
	}

	public void fillWith(int color) {
		Arrays.fill(pixels, color);
	}

}
