package site.gr8.mattis.lightning.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import site.gr8.mattis.lightning.Launcher;
import site.gr8.mattis.lightning.core.entity.Entity;
import site.gr8.mattis.lightning.core.lighting.DirectionalLight;
import site.gr8.mattis.lightning.core.lighting.PointLight;
import site.gr8.mattis.lightning.core.utils.Constants;
import site.gr8.mattis.lightning.core.utils.Transformation;
import site.gr8.mattis.lightning.core.utils.Utils;

public class RenderManager {

	private final WindowManager window;
	private ShaderManager shader;

	public RenderManager() {
		window = Launcher.getWindow();
	}

	public void init() throws Exception {
		shader = new ShaderManager();
		shader.createVertexShader(Utils.loadResource("resources/shaders/vertex.vs"));
		shader.createFragmentShader(Utils.loadResource("resources/shaders/fragment.fs"));
		shader.link();
		shader.createUniform("textureSampler");
		shader.createUniform("transformationMatrix");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("ambientLight");
		shader.createMaterialUniform("material");
		shader.createUniform("specularPower");
		shader.createDirectionalLightUniform("directionalLight");
		shader.createPointLightUniform("pointLight");

	}
	public void render(Entity entity, Camera camera, DirectionalLight directionalLight, PointLight pointLight) {
		clear();

		if (window.isResize()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(false);
		}

		shader.bind();
		shader.setUniform("textureSampler", 0);
		shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
		shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
		shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
		shader.setUniform("material", entity.getModel().getMaterial());
		shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
		shader.setUniform("specularPower", Constants.SPECULAR_POWER);
		shader.setUniform("directionalLight", directionalLight);
		shader.setUniform("pointLight", pointLight);
		
		GL30.glBindVertexArray(entity.getModel().getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.unbind();
	}
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void cleanup() {
		shader.cleanup();
	}
}

