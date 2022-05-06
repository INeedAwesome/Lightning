package site.gr8.mattis.lightning;

import org.lwjgl.Version;
import site.gr8.mattis.lightning.core.EngineManager;
import site.gr8.mattis.lightning.core.WindowManager;
import site.gr8.mattis.lightning.core.utils.Constants;
import site.gr8.mattis.lightning.test.TestGame;

import java.io.File;
import java.io.FileWriter;

public class Launcher {

	private static WindowManager window;
	private static TestGame game;

	public static void main(String[] args) { // https://www.youtube.com/channel/UCRXbTFWiybV245qXsC6OrYw/videos

		window = new WindowManager(Constants.TITLE, 1664, 936, false);
		game = new TestGame();
		EngineManager engine = new EngineManager();

		try {
			engine.start();
		}
		catch (Exception ignored) {}
	}

	public static WindowManager getWindow() {
		return window;
	}

	public static TestGame getGame() {
		return game;
	}
}
