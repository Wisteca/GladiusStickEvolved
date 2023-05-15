package ch.wisteca.anarchy.utils;

import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.GameScreen;
import ch.wisteca.anarchy.world.WorldElement;

/**
 * Une �tiquette qui vole au-dessus d'un �l�ment.
 * @author Wisteca
 */

public class Label implements GameObject {

	private Texture[] myLabels;
	private int myCurrentLabel = 0;
	
	private WorldElement myOwner;
	private int myBetweenX, myBetweenY;
	
	/**
	 * Construire une �tiquette.
	 * @param labels la liste des textures qu'une �tiquette peut prendre
	 * @param owner l'�l�ment que l'�tiquette  doit suivre
	 * @param betweenX la diff�rence sur l'axe x entre la position de l'�l�ment et la position de l'�tiquette
	 * @param betweenY la diff�rence sur l'axe Y entre la position de l'�l�ment et la position de l'�tiquette
	 */
	
	public Label(Texture[] labels, WorldElement owner, int betweenX, int betweenY)
	{
		myLabels = labels.clone();
		myOwner = owner;
		myBetweenX = betweenX;
		myBetweenY = betweenY;
	}
	
	/**
	 * @param label le num�ro de la texture � prendre
	 */
	
	public void setCurrentLabel(int label)
	{
		if(label >= myLabels.length)
			throw new ArrayIndexOutOfBoundsException("Le num�ro de texture demand� sur l'�tiquette est trop �lev�.");
		
		myCurrentLabel = label;
	}
	
	/**
	 * @return le nombre de textures
	 */
	
	public int getLabelNumber()
	{
		return myLabels.length;
	}
	
	/**
	 * @return la texture actuelle
	 */
	
	public int getCurrentLabel()
	{
		return myCurrentLabel;
	}
	
	/**
	 * Rend l'�tiquette visible ou non.
	 */
	
	public void setVisible(boolean visible)
	{
		if(visible && isVisible() == false)
			GameScreen.getInstance().addGameObject(this);
		else
			GameScreen.getInstance().removeGameObject(this);
	}
	
	/**
	 * @return true si l'�tiquette est visible
	 */
	
	public boolean isVisible()
	{
		return GameScreen.getInstance().containsGameObject(this);
	}
	
	/**
	 * Donner l'ordre � l'�tiquette de suivre un �l�ment.
	 * @param owner l'�l�ment � suivre
	 * @param betweenX la diff�rence sur l'axe x entre la position de l'�l�ment et la position de l'�tiquette
	 * @param betweenY la diff�rence sur l'axe Y entre la position de l'�l�ment et la position de l'�tiquette
	 */
	
	public void followElement(WorldElement owner, int betweenX, int betweenY)
	{
		myOwner = owner;
		myBetweenX = betweenX;
		myBetweenY = betweenY;
	}
	
	/**
	 * @return l'�l�ment que l'�tiquette suit
	 */
	
	public WorldElement getOwner()
	{
		return myOwner;
	}
	
	@Override
	public Texture getTexture()
	{
		return myLabels[myCurrentLabel];
	}

	@Override
	public Position getPosition()
	{
		Texture texture = getTexture();
		return myOwner.getPosition().add(myBetweenX, myBetweenY).setWidth(texture.getWidth()).setHeight(texture.getHeight());
	}

	@Override
	public void doTime(float late)
	{}

	@Override
	public int getLayer()
	{
		return myOwner.getLayer();
	}
	
}
