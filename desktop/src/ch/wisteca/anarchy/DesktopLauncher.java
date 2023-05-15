package ch.wisteca.anarchy;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.fullscreen = false;
		//config.height = 800;
		//config.width = 1600;
		config.setWindowedMode(1600, 800);
		config.setTitle("Gladius Stick");
		
		new Lwjgl3Application(new GladiusGame(), config);
	}
}

