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

	private float speedModifier;
	private boolean canTakeSC = false;
	private boolean grabbingMouse = false;

	public TestGame() {
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		speedModifier = 1.0f;
		grabbingMouse = false;
	}

	@Override
	public void init() throws Exception {
		renderer.init();


		Model model = loader.loadOBJModel("resources/models/bunny.obj");
		model.setTexture(new Texture(loader.loadTexture("resources/textures/blocks.png")), 1);
		entity = new Entity(model, new Vector3f(0, 0, -1), new Vector3f(0, 0, 0), 1);
	}

	private void takeSc() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(window.getWidth() * window.getHeight() * 3); // allocates a buffer to store pixels in
		GL11.glReadPixels(0,0, window.getWidth(), window.getHeight(), GL11.GL_RGB,GL11.GL_UNSIGNED_BYTE,byteBuffer); // fills the buffer
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm-ss.SSS"); LocalDateTime time = LocalDateTime.now(); String name = dtf.format(time);
		STBImageWrite.stbi_flip_vertically_on_write(true); // flip the image
		STBImageWrite.stbi_write_jpg("resources/screenshots/" + name + ".jpg", window.getWidth(), window.getHeight(),
				3, byteBuffer, window.getWidth()); // write it to a jpg to save
		System.out.println("Took screenshot and saved it to " + "resources/screenshots/" + name + ".jpg");
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
		if (window.wasKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)){
			if (!grabbingMouse) {
				GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
				grabbingMouse = true;
			} else {
				GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				GLFW.glfwSetCursorPos(window.getWindowHandle(), (float)window.getWidth() / 2, (float)window.getHeight() / 2);
				grabbingMouse = false;
			}
		}

		if (window.wasKeyPressed(GLFW.GLFW_KEY_F2)) {
			canTakeSC = true;
		}

		speedModifier = 1;
		if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
			speedModifier = 0.2f;
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		camera.movePosition(cameraInc.x * Constants.CAMERA_STEP * speedModifier, cameraInc.y * Constants.CAMERA_STEP * speedModifier, cameraInc.z * Constants.CAMERA_STEP * speedModifier);
		if (mouseInput.isLeftButtonPress() || grabbingMouse) {
			Vector2f rotVec = mouseInput.getDisplayVec();
			camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
		}
		entity.incrementRotation(0, 0.25f, 0);
	}

	@Override
	public void render() {
		window.setClearColor(0.12f, 0.11f,0.112f,1);

		renderer.render(entity, camera);

		if (canTakeSC) // after the draw
			takeSc();
		canTakeSC = false;
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}
}
