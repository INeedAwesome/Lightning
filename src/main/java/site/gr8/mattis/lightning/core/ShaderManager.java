package site.gr8.mattis.lightning.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import site.gr8.mattis.lightning.core.entity.Material;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {

	private final int programID;
	private int vertexShaderID, fragmentShaderID;

	private final Map<String, Integer> uniforms;

	public ShaderManager() throws Exception{
		programID = GL20.glCreateProgram();
		if (programID == 0)
			throw new Exception("Could not create shader program.");

		uniforms = new HashMap<>();
	}

	public void createUniform(String name) throws Exception {
		int uniformLocation = GL20.glGetUniformLocation(programID, name);
		if (uniformLocation < 0) System.out.println("Could not find uniform '" + name+ "'.");
		uniforms.put(name, uniformLocation);
	}

	public void createMaterialUniform(String name) throws Exception {
		createUniform(name + ".ambient");
		createUniform(name + ".diffuse");
		createUniform(name + ".specular");
		createUniform(name + ".hasTexture");
		createUniform(name + ".reflectance");
	}

	public void createVertexShader(String shaderCode) throws Exception {
		vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
	}
	public void createFragmentShader(String shaderCode) throws Exception {
		fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
	}

	public int createShader(String shaderCode, int shaderType) throws Exception {
		int shaderID = GL20.glCreateShader(shaderType);
		if (shaderID == 0)
			throw new Exception("Error creating shader type: " + shaderType);

		GL20.glShaderSource(shaderID, shaderCode);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
			throw new Exception(
					"Error compiling shader code, from type: " + shaderType + "\n" +
					"Info : " + GL20.glGetShaderInfoLog(shaderID, 1024));

		GL20.glAttachShader(programID, shaderID);

		return shaderID;
	}

	public void link() throws Exception {
		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
			throw new Exception(
					"Error linking shader code. \n" +
					"Info : " + GL20.glGetProgramInfoLog(programID, 1024));

		if (vertexShaderID != 0)
			GL20.glDetachShader(programID, vertexShaderID);

		if (fragmentShaderID != 0)
			GL20.glDetachShader(programID, fragmentShaderID);

		GL20.glValidateProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
			throw new Exception("Error validating shader code: " + GL20.glGetProgramInfoLog(programID, 1024));
	}


	public void setUniform(String name, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			GL20.glUniformMatrix4fv(uniforms.get(name), false, value.get(stack.mallocFloat(16)));
		}
	}

	public void setUniform(String name, Material value) {
		setUniform(name + ".ambient", value.getAmbientColor());
		setUniform(name + ".diffuse", value.getDiffuseColor());
		setUniform(name + ".specular", value.getSpecularColor());
		setUniform(name + ".hasTexture", value.hasTexture() ? 1 : 0);
		setUniform(name + ".reflectance", value.getReflectance());
	}

	public void setUniform(String name, Vector3f value) {
		GL20.glUniform3f(uniforms.get(name), value.x, value.y, value.z);
	}

	public void setUniform(String name, Vector4f value) {
		GL20.glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
	}

	public void setUniform(String name, boolean value) {
		GL20.glUniform1f(uniforms.get(name), value ? 1 : 0);
	}

	public void setUniform(String name, float value) {
		GL20.glUniform1f(uniforms.get(name), value );
	}

	public void setUniform(String name, int value) {
		GL20.glUniform1i(uniforms.get(name), value);
	}

	public void bind() {
		GL20.glUseProgram(programID);
	}

	public void unbind() {
		GL20.glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		if (programID != 0)
			GL20.glDeleteProgram(programID);
	}
}
