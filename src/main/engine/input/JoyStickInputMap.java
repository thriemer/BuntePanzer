package input;

import org.lwjgl.glfw.GLFW;

public class JoyStickInputMap extends GamepadInputMap {

	public JoyStickInputMap(int device) {
		super(device);
		deviceName = GLFW.glfwGetJoystickName(device);
	}

}
