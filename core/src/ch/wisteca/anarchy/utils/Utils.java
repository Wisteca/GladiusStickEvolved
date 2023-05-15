package ch.wisteca.anarchy.utils;

import java.io.File;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.GameScreen;
import ch.wisteca.anarchy.world.WorldElement;

import com.badlogic.gdx.graphics.Texture;

/**
 * Classe statique regroupant les constantes et paramètres du jeu.
 * @author Wisteca
 */

public class Utils {
	
	public static final String ASSETS_PATH = new File("").getAbsolutePath().replace("desktop", "") + "\\core\\assets";
	
	public static final int TICKS_TIME_MILLIS = 30;
	
	public static final Texture[] HEALTH_LABEL = getHealthLabel();
	
	
	/**
	 * @param uniqueId l'uuid de l'élément que l'on veut récupérer
	 * @return l'élément portant l'uuid passée en paramètre ou null si il n'y en a pas
	 */
	
	public static WorldElement getElementByUUID(UUID uniqueId)
	{
		for(GameObject object : GameScreen.getInstance().getAllGameObjects())
		{
			if(object instanceof WorldElement && ((WorldElement) object).getUniqueId().equals(uniqueId))
				return (WorldElement) object;
		}
		
		return null;
	}
	
	private static Texture[] getHealthLabel()
	{
		Pixmap map = new Pixmap(50, 10, Format.RGBA8888);
		Texture[] labels = new Texture[50];
		
		int width = 0;
		for(int i = 0 ; i < 50 ; i++)
		{
			map.setColor(Color.RED);
			width = i * (map.getWidth() / 50);
			for(int y = 0 ; y < map.getHeight() ; y++)
				map.drawLine(0, y, (int) (width + Math.random() * 2), y);
			
			map.setColor(Color.BLACK);
			map.drawRectangle(0, 0, 75, 50);
			
			labels[i] = new Texture(map);
		}
		
		return labels;
	}
}
