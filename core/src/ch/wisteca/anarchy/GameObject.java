package ch.wisteca.anarchy;

import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.Utils;

/**
 * Interface de base de chaque chose qui peut appara�tre � l'�cran.
 * @author Wisteca
 */

public interface GameObject {
	
	/**
	 * @return la texture de l'objet
	 */
	
	public Texture getTexture();
	
	/**
	 * @return la position de l'objet
	 */
	
	public Position getPosition();
	
	/**
	 * M�thode appel�e tant de fois par secondes, pour effectuer des changements de textures ou autre.
	 * @see Utils
	 * @param late le temps de retard du thread lorsqu'il appelle la m�thode (rapport late / tickTime)
	 */
	
	public void doTime(float late);
	
	/**
	 * @return la couche de l'objet, un objet de couche 4 appara�tra devant un objet de couche 3
	 */
	
	public int getLayer();
}
