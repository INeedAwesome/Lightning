package site.gr8.mattis.lightning.core;

public interface ILogic {

	void init() throws Exception;
	void input();
	void update(float interval, MouseInput mouseInput);
	void render();
	void cleanup();
}
