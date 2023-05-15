package ch.wisteca.anarchy.events.list;

import java.util.UUID;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.events.Event;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.Utils;
import ch.wisteca.anarchy.world.WorldElement;

/**
 * Représente un évenement qui traite un élément du monde.
 * @author Wisteca
 */

public abstract class ElementEvent implements Event {

	private WorldElement myElement;
	public Position myPosition;
	
	public ElementEvent()
	{}
	
	public ElementEvent(WorldElement element)
	{
		myElement = element;
	}
	
	@Override
	public void serialize(Element toWrite)
	{
		Element element = toWrite.getOwnerDocument().createElement("element");
		toWrite.appendChild(element);
		element.setAttribute("uuid", myElement.getUniqueId().toString());
		
		Element position = toWrite.getOwnerDocument().createElement("position");
		element.appendChild(position);
		myElement.getPosition().serialize(position);
	}

	@Override
	public void initObject(Element data)
	{
		Element element = (Element) data.getElementsByTagName("element").item(0);
		myElement = Utils.getElementByUUID(UUID.fromString(element.getAttribute("uuid")));
		
		Element position = (Element) element.getElementsByTagName("position").item(0);
		myPosition = new Position(position);
	}
	
	public WorldElement getElement()
	{
		return myElement;
	}
	
	public Position getPosition()
	{
		return myPosition;
	}
}
