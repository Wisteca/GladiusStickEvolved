package ch.wisteca.anarchy.events.list;

import java.util.UUID;

import org.w3c.dom.Element;

/**
 * Evenement appelé quand un élément apparaît dans le monde, que ce soit une entité, un joueur ou autre.
 * @author Wisteca
 */

public class ElementSpawnEvent extends ElementEvent {
	
	private UUID myUniqueId;
	
	public ElementSpawnEvent()
	{}
	
	public ElementSpawnEvent(UUID uniqueId)
	{
		myUniqueId = uniqueId;
	}
	
	@Override
	public void serialize(Element toWrite)
	{
		super.serialize(toWrite);
		Element element = (Element) toWrite.getElementsByTagName("element").item(0);
		element.setAttribute("uuid", myUniqueId.toString());
	}
	
	@Override
	public void initObject(Element data)
	{
		super.initObject(data);
		Element element = (Element) data.getElementsByTagName("element").item(0);
		myUniqueId = UUID.fromString(element.getAttribute("uuid"));
	}
	
	public UUID getUniqueId()
	{
		return myUniqueId;
	}
}
