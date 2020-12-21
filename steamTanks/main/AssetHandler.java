package main;

import org.lwjgl.opengl.GL11;

import sound.SoundManager;
import texture.TextureManager;

public class AssetHandler {
	public static void loadSounds() {
		SoundManager sm = SoundManager.get();
		sm.loadSound("wallHit");
		sm.loadSound("pistolShot");
		sm.loadSound("machineGunShot");
		sm.loadSound("reload");
		sm.loadSound("explosion");
		sm.loadSound("grenadeLauncher");
		sm.loadSound("deployMine");
		sm.loadSound("itemPickup");
	}

	public static void loadTextures() {
		TextureManager.loadTexture("panzerIconKette", 4, 4, GL11.GL_NEAREST, GL11.GL_CLAMP);
		TextureManager.loadTexture("panzerIconAtlas", 4, 4, GL11.GL_NEAREST, GL11.GL_CLAMP);
		TextureManager.loadTexture("box", 1, 1, GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("mariasPanzerAtlas", 4, 4, GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("mariasPanzerRumpf", 2, 2, GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("mauer", 1, 1, GL11.GL_LINEAR, GL11.GL_REPEAT);
		TextureManager.loadTexture("pickupTexture", 2, 2, GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("mine", 1, 1,  GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("tankAttackers", 2, 2,  GL11.GL_LINEAR, GL11.GL_CLAMP);
		TextureManager.loadTexture("particle", 2, 2,GL11.GL_LINEAR,GL11.GL_CLAMP);		
	}
}
