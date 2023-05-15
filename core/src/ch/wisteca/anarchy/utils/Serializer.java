package ch.wisteca.anarchy.utils;

import org.w3c.dom.Element;

/**
 * Interface de sérialisation.
 * @author Wisteca
 */

public interface Serializer {
	
	/**
	 * Permet de se sérialiser dans un élément xml.
	 * @param toWrite l'élément dans lequel s'écrire
	 */
	
	public void serialize(Element toWrite);
	
	/**
	 * Permet de faire prendre à l'objet les attributs donnés dans l'élément xml.
	 * @param data l'élément de données
	 */
	
	public void initObject(Element data);
	
}
