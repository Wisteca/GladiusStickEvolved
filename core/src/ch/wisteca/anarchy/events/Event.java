package ch.wisteca.anarchy.events;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.utils.Serializer;

/**
 * L'interface de base de chaque évenement du jeu.
 * @author Wisteca
 */

public interface Event extends Serializer {
	
	/**
	 * Permet de créer un évenement en fonction d'un noeud xml.
	 * @param data l'élément dans lequel un évenement s'est sérialisé auparavant
	 * @return un évenement
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
