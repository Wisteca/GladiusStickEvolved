package ch.wisteca.anarchy.events;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.utils.Serializer;

/**
 * L'interface de base de chaque �venement du jeu.
 * @author Wisteca
 */

public interface Event extends Serializer {
	
	/**
	 * Permet de cr�er un �venement en fonction d'un noeud xml.
	 * @param data l'�l�ment dans lequel un �venement s'est s�rialis� auparavant
	 * @return un �venement
	 */
	
	public static Event deserializeEvent(Element data)
	{
		try {
			
			Class<?> event = Class.forName("ch.wisteca.anarchy.events.list." + data.getAttribute("event"));
			Object eventObject = event.newInstance();
			Method init = event.getMethod("initObject", Element.class);
			init.invoke(eventObject, data);
			
			return (Event) eventObject;
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
}
