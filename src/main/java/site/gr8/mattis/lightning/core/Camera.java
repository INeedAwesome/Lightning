package site.gr8.mattis.lightning.core;

import org.joml.Math;
import org.joml.Vector3f;

public class Camera {

	private Vector3f position, rotation;

	public Camera() {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
	}

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public void movePosition(float x, float y, float z)  {
		if (x != 0) {
			this.position.x += Math.sin(Math.toRadians(this.rotation.y - 90)) * -1 * x;
			this.position.z += Math.cos(Math.toRadians(this.rotation.y - 90)) * x;
		}
		this.position.y += y;
		if (z != 0) {
			this.position.x += Math.sin(Math.toRadians(this.rotation.y)) * -1 * z;
			this.position.z += Math.cos(Math.toRadians(this.rotation.y)) * z;
		}
	}

	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public void moveRotation(float x, float y, float z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
	}

	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
