package engine.input;

import java.util.Set;

import org.lwjgl.glfw.GLFW;

import engine.map.BiMap;

public class GamepadInputMap extends InputMap {

	protected BiMap<String, Integer> actionToAxisMap = new BiMap<>();
	protected BiMap<String, Integer> actionToKeyMap = new BiMap<>();

	public GamepadInputMap(int device) {
		this.deviceNumber = device;
		this.deviceName = GLFW.glfwGetGamepadName(deviceNumber);
	}

	public Set<Integer> getAllMapedKeys() {
		return actionToKeyMap.getValueSet();
	}

	public String getActionForKey(int key) {
		return actionToKeyMap.getKey(key);
	}

	public Set<Integer> getAllMapedAxis() {
		return actionToAxisMap.getValueSet();
	}

	public String getActionForAxis(int key) {
		return actionToAxisMap.getKey(key);
	}

	public GamepadInputMap addKeyMapping(String action, int key) {
		actionToKeyMap.put(action, key);
		return this;
	}

	public GamepadInputMap addAxisMapping(String action, int axis) {
		actionToAxisMap.put(action, axis);
		return this;
	}

}
