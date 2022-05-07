package site.gr8.mattis.lightning.core;

import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import site.gr8.mattis.lightning.core.utils.Constants;

public class WindowManager {

	public static final float FOV = Math.toRadians(90);

	private String title;
	private int width, height;
	private long windowHandle;

	private boolean resize, vsync;
	private final Matrix4f projectionMatrix;
	private int[] keyStates = new int[GLFW.GLFW_KEY_LAST];
	private boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

	public WindowManager(String title, int width, int height, boolean vsync) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.vsync = vsync;
		projectionMatrix = new Matrix4f();
	}

	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW.");

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, Constants.MSAA_SAMPLES);

		boolean maximized = false;
		if (width == 0 || height == 0) {
			width = 896;
			height = 504;
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
			maximized = true;
		}

		windowHandle = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (windowHandle == 0)
			throw new RuntimeException("Failed to create GLFW window");

		GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.setResize(true);
		});

		GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true);
			else {
				keyStates[key] = action;
				keys[key] = action != GLFW.GLFW_RELEASE;
			}
		});

		if (maximized)
			GLFW.glfwMaximizeWindow(windowHandle);
		else  {
			GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			if (vidMode != null)
				GLFW.glfwSetWindowPos(windowHandle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
		}

		GLFW.glfwMakeContextCurrent(windowHandle);

		if (isVsync())
			GLFW.glfwSwapInterval(1);
		else GLFW.glfwSwapInterval(0);

		GLFW.glfwShowWindow(windowHandle);
		GL.createCapabilities();

		GL11.glClearColor(0, 0, 0, 0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public void update() {
		GLFW.glfwSwapBuffers(windowHandle);
		GLFW.glfwPollEvents();
	}

	public void cleanup() {
		GLFW.glfwDestroyWindow(windowHandle);
	}

	public void setClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	/**
	 *
	 * @param keycode
	 * @returns true if button is pressed currently
	 */
	public boolean isKeyPressed(int keycode	) {
		return GLFW.glfwGetKey(windowHandle, keycode) == GLFW.GLFW_PRESS;
	}

	/**
	 * @param keycode
	 * @returns True if key was pressed and returns false the frame after
	 */
	public boolean wasKeyPressed(int keycode) {
		if (keyStates[keycode] == GLFW.GLFW_PRESS) {
			boolean isDown = keys[keycode];
			keys[keycode] = false;
			return isDown;
			// Thanks to illuminator3#0001 on discord for helping me with this,
			// in discord server Together Java in #frameworks_help_0, 2022.01.01
		}
		return false;
	}

	public boolean windowShouldClose() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		GLFW.glfwSetWindowTitle(windowHandle, title);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getWindowHandle() {
		return windowHandle;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f updateProjectionMatrix() {
		return projectionMatrix.setPerspective(FOV, (float)width / (float)height, Constants.Z_NEAR, Constants.Z_FAR);
	}

	public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
		return matrix.setPerspective(FOV, (float)width / (float)height, Constants.Z_NEAR, Constants.Z_FAR);
	}

	public boolean isVsync() {
		return vsync;
	}

	public void setVsync(boolean vsync) {
		this.vsync = vsync;
	}

	public boolean isResize() {
		return resize;
	}

	public void setResize(boolean resize) {
		this.resize = resize;
	}
}
