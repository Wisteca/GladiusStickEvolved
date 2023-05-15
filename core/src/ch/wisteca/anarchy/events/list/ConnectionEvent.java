package ch.wisteca.anarchy.events.list;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.events.Event;

/**
 * Evenement appel� lorsque le client se connecte � un serveur-
 * @author Wisteca
 */

public class ConnectionEvent implements Event {

	private String myName;
	
	public ConnectionEvent()
	{}
	
	public ConnectionEvent(String clientName)
	{
		myName = clientName;
	}
	
	@Override
	public void serialize(Element toWrite)
	{
		toWrite.setAttribute("clientName", myName);
	}

	@Override
	public void initObject(Element data)
	{
		myName = data.getAttribute("clientName");
	}
}
