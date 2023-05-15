package ch.wisteca.anarchy.events.list;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.world.Vector;
import ch.wisteca.anarchy.world.WorldElement;

/**
 * Evenement appelé lorsqu'un élément du monde est éjecté.
 * @author Wisteca
 */

public class ElementEjectEvent extends ElementEvent {

	private Vector myVector;
	
	public ElementEjectEvent()
	{}
	
	public ElementEjectEvent(WorldElement element, Vector vector)
	{
		super(element);
		myVector = vector;
	}
	
	@Override
	public void serialize(Element toWrite)
	{
		super.serialize(toWrite);
		Element element = (Element) toWrite.getElementsByTagName("element").item(0);
		Element vector = toWrite.getOwnerDocument().createElement("vector");
		element.appendChild(vector);
		myVector.serialize(vector);
	}
	
	@Override
	public void initObject(Element data)
	{
		super.initObject(data);
		Element element = (Element) data.getElementsByTagName("element").item(0);
		Element vector = (Element) element.getElementsByTagName("vector").item(0);
		myVector = new Vector(vector);
	}
	
	public Vector getVector()
	{
		return myVector;
	}
}
