package input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.glfw.GLFWJoystickCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

public class InputHandler {

	private final int KEYBOARD_SIZE = 512;

	private int[] keyStates = new int[KEYBOARD_SIZE];
	private boolean[] activeKeys = new boolean[KEYBOARD_SIZE];

	private List<InputMap> deviceInputs = new ArrayList<>();
	private List<GLFWKeyCallback> keyCallbacks = new ArrayList<>();

	public void addKeyCallback(GLFWKeyCallback callback) {
		keyCallbacks.add(callback);
	}
	
	public void addInputMapping(InputMap map) {
		deviceInputs.add(map);
	}
	
	public boolean isKeyDown(int key) {
		return activeKeys[key];
	}
	
	public int[] getActiveKeys() {
		List<Integer> pressedKeys = new ArrayList<>();
		for(int i=0;i<activeKeys.length;i++) {
			if(activeKeys[i]) {
				pressedKeys.add(i);
			}
		}
		return pressedKeys.stream().mapToInt(i->i).toArray();
	}

	public InputHandler(long window) {
		GLFW.glfwSetKeyCallback(window, keyboard);
		GLFW.glfwSetJoystickCallback(joystickListner);
	}

	public void updateInputMaps() {
		for (InputMap im : deviceInputs) {
			if (im instanceof JoyStickInputMap) {
				updateJoystick((JoyStickInputMap) im);
			} else if (im instanceof GamepadInputMap) {
				updateGamepad((GamepadInputMap) im);
			} else if (im instanceof KeyboardInputMap) {
				updateKeyboard((KeyboardInputMap) im);
			}
		}
	}

	protected GLFWJoystickCallback joystickListner = new GLFWJoystickCallback() {

		@Override
		public void invoke(int jid, int event) {
			System.out.println(jid + "/" + GLFW.glfwGetGamepadName(jid) + " "
					+ (event == GLFW.GLFW_CONNECTED ? "connected" : "disconnected"));
		}
	};

	protected GLFWKeyCallback keyboard = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key != -1) {
				activeKeys[key] = action != GLFW.GLFW_RELEASE;
				keyStates[key] = action;
			}
			keyCallbacks.forEach(c->c.invoke(window, key, scancode, action, mods));
		}
	};

	private void updateKeyboard(KeyboardInputMap keyboardMap) {
		for (int key : keyboardMap.getAllMapedKeys()) {
			keyboardMap.setValue(keyboardMap.getActionForKey(key), activeKeys[key] ? 1 : 0);
		}
	}

	private void updateGamepad(GamepadInputMap controllerMap) {
		GLFWGamepadState gs = new GLFWGamepadState(BufferUtils.createByteBuffer(40));
		if (GLFW.glfwGetGamepadState(controllerMap.deviceNumber, gs)) {
			for (int key : controllerMap.getAllMapedKeys()) {
				controllerMap.setValue(controllerMap.getActionForKey(key), gs.buttons(key));
			}
			for (int axis : controllerMap.getAllMapedAxis()) {
				controllerMap.setValue(controllerMap.getActionForAxis(axis), gs.axes(axis));
			}
		} else {
			System.out.println("Controller " + controllerMap.deviceNumber + " not found, "
					+ (GLFW.glfwJoystickPresent(controllerMap.deviceNumber) ? " but is present as joystick"
							: "and not even found as joystick"));
		}
	}

	private void updateJoystick(JoyStickInputMap controllerMap) {
		ByteBuffer buttons = GLFW.glfwGetJoystickButtons(controllerMap.deviceNumber);
		FloatBuffer axis = GLFW.glfwGetJoystickAxes(controllerMap.deviceNumber);
		byte[] buttonsArray = new byte[buttons.capacity()];
		float[] axisArray = new float[axis.capacity()];
		buttons.get(buttonsArray);
		axis.get(axisArray);
		for (int key : controllerMap.getAllMapedKeys()) {
			controllerMap.setValue(controllerMap.getActionForKey(key), buttonsArray[key]);
		}
		for (int axe : controllerMap.getAllMapedAxis()) {
			controllerMap.setValue(controllerMap.getActionForAxis(axe), axisArray[axe]);
		}

	}

	public void remove(InputMap... inputmaps) {
		deviceInputs.removeAll(Arrays.asList(inputmaps));
	}

}
