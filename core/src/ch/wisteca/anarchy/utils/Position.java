package ch.wisteca.anarchy.utils;

import org.w3c.dom.Element;

public class Position implements Cloneable, Serializer {
	
	private int myPosX, myPosY;
	private int myWidth, myHeight;
	
	public Position(int x, int y, int width, int height)
	{
		myPosX = x;
		myPosY = y;
		myWidth = width;
		myHeight = height;
	}
	
	public Position(int x, int y)
	{
		myPosX = x;
		myPosY = y;
		myWidth = 0;
		myHeight = 0;
	}
	
	public Position(Element element)
	{
		initObject(element);
	}
	
	public int getPosX()
	{
		return myPosX;
	}
	
	public int getPosY()
	{
		return myPosY;
	}
	
	public Position setPosX(int x)
	{
		myPosX = x;
		return this;
	}
	
	public Position setPosY(int y)
	{
		myPosY = y;
		return this;
	}
	
	public int getWidth()
	{
		return myWidth;
	}
	
	public int getHeight()
	{
		return myHeight;
	}
	
	public Position setWidth(int width)
	{
		myWidth = width;
		return this;
	}
	
	public Position setHeight(int height)
	{
		myHeight = height;
		return this;
	}
	
	public int distance(Position other)
	{
		int x = other.getPosX() - myPosX;
		int y = other.getPosY() - myPosY;
		return (int) Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
	}
	
	public boolean isSameXY(Position otherPos)
	{
		return myPosX == otherPos.getPosX() && myPosY == otherPos.getPosY();
	}
	
	public boolean isInside(int x, int y)
	{
		if(myPosX < x && myPosX + myWidth > x && myPosY < y && myPosY + myHeight > y)
			return true;
		return false;
	}
	
	public Position add(int x, int y)
	{
		return new Position(myPosX + x, myPosY + y, myWidth, myHeight);
	}
	
	public Position add(Position other, boolean keepDimensions)
	{
		if(keepDimensions)
			return add(other.getPosX(), other.getPosY());
		else
			return add(other.getPosX(), other.getPosY()).setWidth(myWidth + other.getWidth()).setHeight(myHeight + other.getHeight());
	}
	
	@Override
	public Position clone()
	{
		return new Position(myPosX, myPosY, myWidth, myHeight);
	}

	@Override
	public void serialize(Element toWrite)
	{
		toWrite.setAttribute("x", String.valueOf(myPosX));
		toWrite.setAttribute("y", String.valueOf(myPosY));
		toWrite.setAttribute("width", String.valueOf(myWidth));
		toWrite.setAttribute("height", String.valueOf(myHeight));
	}

	@Override
	public void initObject(Element data)
	{
		myPosX = Integer.valueOf(data.getAttribute("x"));
		myPosY = Integer.valueOf(data.getAttribute("y"));
		myWidth = Integer.valueOf(data.getAttribute("width"));
		myHeight = Integer.valueOf(data.getAttribute("height"));
	}
}
