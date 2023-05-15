package ch.wisteca.anarchy.world;

import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.utils.Position;

/**
 * Une simple texture du monde.
 * @author Wisteca
 */

public class SimpleObject implements GameObject {
	
	private Texture myTexture;
	private int myLayer;
	private Position myPosition;
	
	/**
	 * @param world le monde dans lequel doit apparaître la texture
	 * @param textureName le nom de la texture (récupéré par world.getTextureByName(String))
	 * @param layer la couche sur laquelle la texture doit apparaître
	 * @param pos la position de la texture
	 */
	
	public SimpleObject(World world, String textureName, int layer, Position pos)
	{
		myTexture = world.getTextureFromName(textureName);
		myLayer = layer;
		myPosition = pos;
	}
	
	@Override
	public Texture getTexture()
	{
		return myTexture;
	}

	@Override
	public Position getPosition()
	{
		return myPosition;
	}

	@Override
	public void doTime(float late)
	{}

	@Override
	public int getLayer()
	{
		return myLayer;
	}
}
