package site.gr8.mattis.lightning.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImageWrite;
import site.gr8.mattis.lightning.Launcher;
import site.gr8.mattis.lightning.core.*;
import site.gr8.mattis.lightning.core.entity.Entity;
import site.gr8.mattis.lightning.core.entity.Model;
import site.gr8.mattis.lightning.core.entity.Texture;
import site.gr8.mattis.lightning.core.utils.Constants;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestGame implements ILogic {

	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	private final Camera camera;
	private Entity entity;
	Vector3f cameraInc;

	private boolean canTakeSC = false;

	public TestGame() {
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
	}

	@Override
	public void init() throws Exception {
		renderer.init();

		float[] vertices = new float[] {
				-0.5f, 0.5f, 0.5f,
				-0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				-0.5f, 0.5f, -0.5f,
				0.5f, 0.5f, -0.5f,
				-0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				0.5f, 0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
				-0.5f, 0.5f, 0.5f,
				-0.5f, -0.5f, 0.5f,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, 0.5f,
		};
		float[] textCoords = new float[]{
				0.0f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.5f, 0.0f,
				0.0f, 0.0f,
				0.5f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.0f, 0.5f,
				0.5f, 0.5f,
				0.0f, 1.0f,
				0.5f, 1.0f,
				0.0f, 0.0f,
				0.0f, 0.5f,
				0.5f, 0.0f,
				0.5f, 0.5f,
				0.5f, 0.0f,
				1.0f, 0.0f,
				0.5f, 0.5f,
				1.0f, 0.5f,
		};
		int[] indices = new int[]{
				0, 1, 3, 3, 1, 2,
				8, 10, 11, 9, 8, 11,
				12, 13, 7, 5, 12, 7,
				14, 15, 6, 4, 14, 6,
				16, 18, 19, 17, 16, 19,
				4, 6, 7, 5, 4, 7,
		};

		Model model = loader.loadModel(vertices, textCoords, indices);
		model.setTexture(new Texture(loader.loadTexture("resources/textures/blocks.png")));
		entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
	}

	private void takeSc() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(window.getWidth() * window.getHeight() * 3);
		GL11.glReadPixels(0,0, window.getWidth(), window.getHeight(), GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE,byteBuffer);
		STBImageWrite.stbi_flip_vertically_on_write(true);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm-ss.SSS");
		LocalDateTime time = LocalDateTime.now();
		String name = dtf.format(time);
		STBImageWrite.stbi_write_jpg("resources/screenshots/" + name + ".jpg", window.getWidth(), window.getHeight(), 3, byteBuffer, window.getWidth()  );
	}

	@Override
	public void input() {
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW.GLFW_KEY_W))
			cameraInc.z = -1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_S))
			cameraInc.z = 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_A))
			cameraInc.x = -1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_D))
			cameraInc.x = 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_Q))
			cameraInc.y = -1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_E))
			cameraInc.y = 1;

		if (window.isKeyPressed(GLFW.GLFW_KEY_F2)) {
			canTakeSC = true;
			try {	Thread.sleep(40);
			} catch (InterruptedException ignored) {}
		}
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		camera.movePosition(cameraInc.x * Constants.CAMERA_STEP, cameraInc.y * Constants.CAMERA_STEP, cameraInc.z * Constants.CAMERA_STEP);
		if (mouseInput.isLeftButtonPress()) {
			Vector2f rotVec = mouseInput.getDisplayVec();
			camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
		}
		entity.incrementRotation(0, 0.f, 0);
	}

	@Override
	public void render() {
		if (window.isResize()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(false);
		}
		renderer.render(entity, camera);

		if (canTakeSC)
			takeSc();
		canTakeSC = false;
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}
}
