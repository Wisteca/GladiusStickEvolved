package ch.wisteca.anarchy.world;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.utils.Serializer;

/**
 * Un vecteur, la force en Newtons du vecteur est égal à sa longeur, l'orientation en degré 0 représente la droite.
 * @author Wisteca
 */

public class Vector implements Serializer {
	
	private int myStrength, myDegree;
	
	public Vector(Element data)
	{
		initObject(data);
	}
	
	public Vector(int strength, int degree)
	{
		myStrength = strength;
		myDegree = degree;
	}
	
	public int getStrength() // la longueur du vecteur est égal à sa force
	{
		return myStrength;
	}
	
	public int getDegree()
	{
		return myDegree;
	}

	@Override
	public void serialize(Element toWrite)
	{
		toWrite.setAttribute("strength", String.valueOf(myStrength));
		toWrite.setAttribute("degree", String.valueOf(myDegree));
	}

	@Override
	public void initObject(Element data)
	{
		myStrength = Integer.valueOf(data.getAttribute("strength"));
		myDegree = Integer.valueOf(data.getAttribute("degree"));
	}
}
