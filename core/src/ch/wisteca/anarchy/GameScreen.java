package ch.wisteca.anarchy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ch.wisteca.anarchy.personnages.Entity;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.Utils;
import ch.wisteca.anarchy.world.World;

/**
 * Singleton de base de l'affichage du jeu. S'occupe d'afficher tous les éléments sur l'écran.
 * @author Wisteca
 */

public class GameScreen implements Screen {

	private static GameScreen myInstance;
	
	boolean pressed = false;
	
	private SpriteBatch myBatch;
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<GameObject>> myObjects;
	private World myWorld;
	
	public GameScreen()
	{
		myBatch = new SpriteBatch();
		myObjects = new ConcurrentHashMap<>();
	
		myInstance = this;
	}
	
	/**
	 * Charge le monde en jeu.
	 * @param world le monde à charger
	 */
	
	public void initWorld(World world)
	{
		myWorld = world;
		myWorld.initWorld();
		
		for(int layer = 0 ; layer < 10 ; layer++)
			myObjects.put(layer, new ConcurrentLinkedQueue<>(world.getAllObjects(layer)));
		
		new Timer();
	}
	
	/**
	 * @return l'instance unique du singleton
	 */
	
	public static GameScreen getInstance()
	{
		return myInstance;
	}
	
	/**
	 * @return le monde actuellement chargé en jeu
	 */
	
	public World getWorld()
	{
		return myWorld;
	}
	
	/**
	 * Ajoute un objet à afficher dans le jeu.
	 * @param object l'objet à afficher
	 */
	
	public void addGameObject(GameObject object)
	{
		myObjects.get(object.getLayer()).add(object);
	}
	
	/**
	 * @param object l'objet à supprimer
	 */
	
	public void removeGameObject(GameObject object)
	{
		for(int layer = 0 ; layer < 10 ; layer++)
			myObjects.get(layer).remove(object);
	}
	
	/**
	 * @return true si l'objet passé en paramètre en bien afficher dans le jeu
	 */
	
	public boolean containsGameObject(GameObject object)
	{
		for(int layer = 0 ; layer < 10 ; layer++)
		{
			if(myObjects.get(layer).contains(object))
				return true;
		}
		
		return false;
	}
	
	/**
	 * @return la liste de tous les objets affichés
	 */
	
	public ArrayList<GameObject> getAllGameObjects()
	{
		ArrayList<GameObject> objects = new ArrayList<>();
		for(int layer = 0 ; layer < 10 ; layer++)
			objects.addAll(myObjects.get(layer));
		
		return objects;
	}
	
	@Override
	public void show()
	{}
	
	@Override
	public void render(float delta)
	{
		myBatch.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		for(int layer = 0 ; layer < 10 ; layer++)
		{
			for(GameObject object : myObjects.get(layer))
			{
				Position pos = object.getPosition();
				Texture texture = object.getTexture();
				myBatch.draw(texture, (float) pos.getPosX(), (float) pos.getPosY(), pos.getWidth(), pos.getHeight());
			}
		}
		
		myBatch.end();
	}
	
	@Override
	public void resize(int width, int height)
	{}

	@Override
	public void pause()
	{}

	@Override
	public void resume()
	{}

	@Override
	public void hide()
	{}

	@Override
	public void dispose()
	{
		myBatch.dispose();
	}
	
	private class Timer implements Runnable {
		
		public Timer()
		{
			new Thread(this, "GameObject-Timer").start();
		}
		
		@Override
		public void run()
		{
			long timeBefore = 0;
			float late = 0;
			
			while(true)
			{
				int x = Gdx.input.getX();
				int y = Gdx.graphics.getHeight() - Gdx.input.getY();
				
				timeBefore = System.currentTimeMillis();
				for(int layer = 0 ; layer < 10 ; layer++)
				{
					for(GameObject object : myObjects.get(layer))
					{
						object.doTime(late);
						
						if(object instanceof Entity && Gdx.input.isKeyPressed(Keys.ENTER))
							((Entity) object).moveTo(new Position(x, y));
					}
				}
				
				long duration = System.currentTimeMillis() - timeBefore;
				//System.out.println(duration);
				try {
					
					long timeToSleep = Utils.TICKS_TIME_MILLIS - duration;
					if(timeToSleep > 0)
					{
						late = 0;
						Thread.sleep(timeToSleep);
					}
					else
						late = (int) Math.abs(timeToSleep) / Utils.TICKS_TIME_MILLIS;
					
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
