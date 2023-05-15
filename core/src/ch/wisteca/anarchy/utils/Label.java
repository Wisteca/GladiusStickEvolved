package ch.wisteca.anarchy.utils;

import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.GameScreen;
import ch.wisteca.anarchy.world.WorldElement;

/**
 * Une étiquette qui vole au-dessus d'un élément.
 * @author Wisteca
 */

public class Label implements GameObject {

	private Texture[] myLabels;
	private int myCurrentLabel = 0;
	
	private WorldElement myOwner;
	private int myBetweenX, myBetweenY;
	
	/**
	 * Construire une étiquette.
	 * @param labels la liste des textures qu'une étiquette peut prendre
	 * @param owner l'élément que l'étiquette  doit suivre
	 * @param betweenX la différence sur l'axe x entre la position de l'élément et la position de l'étiquette
	 * @param betweenY la différence sur l'axe Y entre la position de l'élément et la position de l'étiquette
	 */
	
	public Label(Texture[] labels, WorldElement owner, int betweenX, int betweenY)
	{
		myLabels = labels.clone();
		myOwner = owner;
		myBetweenX = betweenX;
		myBetweenY = betweenY;
	}
	
	/**
	 * @param label le numéro de la texture à prendre
	 */
	
	public void setCurrentLabel(int label)
	{
		if(label >= myLabels.length)
			throw new ArrayIndexOutOfBoundsException("Le numéro de texture demandé sur l'étiquette est trop élevé.");
		
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
	 * Rend l'étiquette visible ou non.
	 */
	
	public void setVisible(boolean visible)
	{
		if(visible && isVisible() == false)
			GameScreen.getInstance().addGameObject(this);
		else
			GameScreen.getInstance().removeGameObject(this);
	}
	
	/**
	 * @return true si l'étiquette est visible
	 */
	
	public boolean isVisible()
	{
		return GameScreen.getInstance().containsGameObject(this);
	}
	
	/**
	 * Donner l'ordre à l'étiquette de suivre un élément.
	 * @param owner l'élément à suivre
	 * @param betweenX la différence sur l'axe x entre la position de l'élément et la position de l'étiquette
	 * @param betweenY la différence sur l'axe Y entre la position de l'élément et la position de l'étiquette
	 */
	
	public void followElement(WorldElement owner, int betweenX, int betweenY)
	{
		myOwner = owner;
		myBetweenX = betweenX;
		myBetweenY = betweenY;
	}
	
	/**
	 * @return l'élément que l'étiquette suit
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
