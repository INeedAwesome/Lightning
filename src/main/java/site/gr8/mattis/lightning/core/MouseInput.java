package site.gr8.mattis.lightning.core;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import site.gr8.mattis.lightning.Launcher;

public class MouseInput {

	private final Vector2d previousPosition, currentPosition;
	private final Vector2f displayVec;

	private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;
	private boolean erase = true;

	public MouseInput() {
		this.previousPosition = new Vector2d(-1, 0);
		this.currentPosition = new Vector2d(0, 0);
		this.displayVec = new Vector2f();
	}

	public void init() {
		GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindowHandle(), (window, xpos, ypos) -> {
			currentPosition.x = xpos;
			currentPosition.y = ypos;
		});
		GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindowHandle(), (window, entered) -> {
			inWindow = entered;
		});
		GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindowHandle(), (window, button, action, mods) -> {
			leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
			rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
		});
	}

	public void input() {
		if (erase) {
			this.displayVec.x = 0;
			this.displayVec.y = 0;
		}
		if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
			double x = currentPosition.x - previousPosition.x;
			double y = currentPosition.y - previousPosition.y;
			boolean rotateX = x != 0;
			boolean rotateY = y != 0;
			if (rotateX) {
				this.displayVec.y = (float) x;
			}
			if (rotateY)
				this.displayVec.x = (float)y;
		}
		previousPosition.x = currentPosition.x;
		previousPosition.y = currentPosition.y;
	}

	public Vector2f getDisplayVec() {
		return displayVec;
	}

	public boolean isLeftButtonPress() {
		return leftButtonPress;
	}

	public boolean isRightButtonPress() {
		return rightButtonPress;
	}

	public boolean isErase() {
		return erase;
	}

	public void setErase(boolean erase) {
		this.erase = erase;
	}
}
