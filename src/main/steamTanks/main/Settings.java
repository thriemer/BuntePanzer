package main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import input.InputMap;
import input.KeyboardInputMap;

public class Settings {

	public static int playerCount = 1, aiCount;
	public static int winningScore = 20;
	public static int unusedControlIndex =0;
	public static String[] actions = { "driveForward", "driveBackward", "turnLeft", "turnRight", "shoot",
			"changeColor" };

	public static List<InputMap> controlScheme = new ArrayList<>();

	public static void init() {
		controlScheme.add(new KeyboardInputMap().addMapping("driveForward", GLFW.GLFW_KEY_W)
				.addMapping("driveBackward", GLFW.GLFW_KEY_S).addMapping("turnLeft", GLFW.GLFW_KEY_A)
				.addMapping("turnRight", GLFW.GLFW_KEY_D).addMapping("shoot", GLFW.GLFW_KEY_Q)
				.addMapping("changeColor", GLFW.GLFW_KEY_E).setName("WASD"));
		controlScheme.add(new KeyboardInputMap().addMapping("driveForward", GLFW.GLFW_KEY_UP)
				.addMapping("driveBackward", GLFW.GLFW_KEY_DOWN).addMapping("turnLeft", GLFW.GLFW_KEY_LEFT)
				.addMapping("turnRight", GLFW.GLFW_KEY_RIGHT).addMapping("shoot", GLFW.GLFW_KEY_SPACE)
				.addMapping("changeColor", GLFW.GLFW_KEY_C).setName("Arrow Keys"));
		controlScheme.add(new KeyboardInputMap().addMapping("driveForward", GLFW.GLFW_KEY_KP_8)
				.addMapping("driveBackward", GLFW.GLFW_KEY_KP_5).addMapping("turnLeft", GLFW.GLFW_KEY_KP_4)
				.addMapping("turnRight", GLFW.GLFW_KEY_KP_6).addMapping("shoot", GLFW.GLFW_KEY_KP_7)
				.addMapping("changeColor", GLFW.GLFW_KEY_KP_9).setName("Numpad"));
	}

	public static InputMap[] usedControlScheme;

	public static float[] hue;
	public static String[] names;
}
