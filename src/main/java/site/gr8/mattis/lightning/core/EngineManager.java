package site.gr8.mattis.lightning.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import site.gr8.mattis.lightning.Launcher;
import site.gr8.mattis.lightning.core.utils.Constants;

public class EngineManager {

	public static final long NANOSECOND = 1000000000L;
	public static final float FRAMERATE = 165;

	private static int fps;
	private static float frametime = 1.0f / FRAMERATE;

	private boolean isRunning;

	private WindowManager windowManager;
	private GLFWErrorCallback errorCallback;
	private ILogic gameLogic;
	private MouseInput mouseInput;

	public void init() throws Exception {
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err).set());
		windowManager = Launcher.getWindow();
		gameLogic = Launcher.getGame();
		mouseInput = new MouseInput();
		windowManager.init();
		mouseInput.init();
		gameLogic.init();
	}

	public void start() throws Exception {
		init();
		if (isRunning)
			return;
		run();
	}

	public void run() {
		this.isRunning = true;

		int frames = 0;
		long frameCounter = 0;
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;

		while (isRunning) {
			boolean render = false;
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double)NANOSECOND;
			frameCounter += passedTime;

			input();

			while (unprocessedTime > frametime) {
				render = true;
				unprocessedTime -= frametime;

				if (windowManager.windowShouldClose())
					stop();

				if (frameCounter >= NANOSECOND) {
					setFps(frames);
					windowManager.setTitle(Constants.TITLE + " FPS: " + getFps());
					frames = 0;
					frameCounter = 0;
				}
			}

			if (render) {
				update(frametime);
				render();
				frames++;
				mouseInput.setErase(true);
			}
			else mouseInput.setErase(false);
		}
		cleanup();
	}

	private void stop() {
		if (!isRunning)
			return;
		isRunning = false;
	}

	private void input() {
		mouseInput.input();
		gameLogic.input();
	}

	private void update(float interval) {
		gameLogic.update(interval, mouseInput);
	}

	private void render() {
		gameLogic.render();
		windowManager.update();
	}

	private void cleanup() {
		windowManager.cleanup();
		gameLogic.cleanup();
		errorCallback.free();
		GLFW.glfwTerminate();
	}

	public static float getFrameTime() {
		return frametime;
	}

	public static int getFps() {
		return fps;
	}

	public static void setFps(int fps) {
		EngineManager.fps = fps;
	}

}
