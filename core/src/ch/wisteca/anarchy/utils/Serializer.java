package ch.wisteca.anarchy.utils;

import org.w3c.dom.Element;

/**
 * Interface de s�rialisation.
 * @author Wisteca
 */

public interface Serializer {
	
	/**
	 * Permet de se s�rialiser dans un �l�ment xml.
	 * @param toWrite l'�l�ment dans lequel s'�crire
	 */
	
	public void serialize(Element toWrite);
	
	/**
	 * Permet de faire prendre � l'objet les attributs donn�s dans l'�l�ment xml.
	 * @param data l'�l�ment de donn�es
	 */
	
	public void initObject(Element data);
	
}
