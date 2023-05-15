package ch.wisteca.anarchy.world;

import org.w3c.dom.Element;

import ch.wisteca.anarchy.utils.Position;

/**
 * Un élément de jeu soumit à la gravité.
 * @author Wisteca
 */

public class GravityElement extends WorldElement {
	
	private double mySpeedY, mySpeedX;
	private int myTimeX, myTimeY;
	private Position myInitialPosition;
	private boolean myIgnorePhysic = false;
	private float myThreadLate;
	
	public GravityElement(World world)
	{
		super(world);
	}
	
	@Override
	public void initElement(Element data)
	{
		super.initElement(data);
		myInitialPosition = new Position(getPosition().getPosX(), getPosition().getPosY(), getPosition().getWidth(), getPosition().getHeight());
		mySpeedX = mySpeedY = myTimeX = myTimeY = 0;
	}
	
	/**
	 * Ejecte l'objet là où pointe le vecteur. 
	 * @param vector le vecteur donnant la direction
	 */
	
	public void eject(Vector vector)
	{
		double strengthY = vector.getStrength() * Math.sin(Math.toRadians(vector.getDegree()));
		myTimeX = myTimeY = 0;
		myInitialPosition = new Position(getPosition().getPosX(), getPosition().getPosY(), getPosition().getWidth(), getPosition().getHeight());
		mySpeedY = Math.sqrt(2 * getWorld().getGravity() * strengthY); // V0 = sqrt(2 * g * Hmax)
		mySpeedX += vector.getStrength() * ((double) Math.round(Math.cos(Math.toRadians(vector.getDegree())) * 1_000_000) / 1_000_000);
		myIgnorePhysic = true;
	}
	
	@Override
	public void doTime(float late)
	{
		super.doTime(late);
		
		int addLate = 1;
		myThreadLate += late;
		if(myThreadLate > 1)
		{
			int toSubstract = (int) myThreadLate;
			myThreadLate -= toSubstract;
			addLate += toSubstract;
		}
		//System.out.println(isFalling(3));
		// gestion du temps
		if(mySpeedY != 0 || isFalling(3))
			myTimeY += addLate;
		else
		{
			myTimeY = 0;
			myInitialPosition.setPosY(getPosition().getPosY());
		}
		
		if(mySpeedX != 0)
			myTimeX += addLate;
		else
		{
			myTimeX = 0;
			myInitialPosition.setPosX(getPosition().getPosX());
		}
		
		// gestion du décollage
		if(myTimeY > 1)
			myIgnorePhysic = false;
		
		// V0 * t + a * t^2 / 2
		// distance par rapport au point initial
		int heightY = (int) Math.floor(mySpeedY * myTimeY + -getWorld().getGravity() * Math.pow(myTimeY, 2) / 2);
		int distanceX = (int) Math.round(mySpeedX * myTimeX); // d = v * t
		
		// distance par rapport à la position actuelle de l'élément
		int moveY = heightY - (getPosition().getPosY() - myInitialPosition.getPosY());
		int moveX = distanceX - (getPosition().getPosX() - myInitialPosition.getPosX());
		
		if(moveY < getWorld().getMaxSpeed())
			moveY = getWorld().getMaxSpeed();
	
		if(myIgnorePhysic)
		{
			changePosition(moveX, moveY, true);
			return;
		}
		
		double ratio = Math.abs((double) moveX / (double) moveY); // pixel à parcourir en x lorsqu'on descend de 1 en y
		int toY = 0, toX = 0;
		
		if(Double.isNaN(ratio) == false) // NaN -> moveX & moveY = 0
		{
			if(Double.isInfinite(ratio))
			{
				// l'élément avance sur l'axe x
				int change = moveX < 0 ? -1 : 1;
				while(toX < Math.abs(moveX))
				{
					toX++;
					
					if(canGoAt(change, 0) == false)
					{
						mySpeedX = 0;
						break;
					}
					
					changePosition(change, 0, true);
				}
			}
			else
			{
				// l'élément avance sur les deux axes
				int initialX = getPosition().getPosX();
				double normalX = 0;
				int changeY = moveY < 0 ? -1 : 1;
				
				if(moveX < 0 && ratio > 0)
					ratio = -ratio;
				
				while(toY < Math.abs(moveY))
				{
					toY++;
					normalX = ratio * toY;
					int changeX = (int) Math.round(normalX - (getPosition().getPosX() - initialX));
					
					if(canGoAt(0,  changeY) == false)
					{
						if(getDistanceToGround() == 0)
							getPosition().setPosY(getPosition().getPosY() - 10);
					
						onTouchGround();
						break;
					}
					
					if(canGoAt(changeX, 0) == false)
					{
						System.out.println("staupX");
						mySpeedX = 0;
						break;
					}
					
					if(mySpeedX == 0 && mySpeedY == 0)
						onBlocked();
					
					changePosition(changeX, changeY, true);
				}
			}
		}
	}
	
	private void onBlocked()
	{
		//System.out.println("BLOCKED " + this);
		setPosition(getPosition().add(3, 0));
	}
	
	protected void onTouchGround()
	{
		mySpeedY = 0;
		mySpeedX = 0;
	}
	
	protected void resetSpeedX()
	{
		myTimeX = 0;
		myInitialPosition.setPosX(getPosition().getPosX());
		mySpeedX = 0;
	}
	
	protected void setSpeedX(double speed)
	{
		mySpeedX = speed;
	}
	
	protected void setInitialPosition(Position pos)
	{
		myInitialPosition = new Position(pos.getPosX(), pos.getPosY(), pos.getWidth(), pos.getHeight());
	}
	
	/**
	 * @return la vitesse de l'élément sur l'axe x
	 */
	
	public double getSpeedX()
	{
		return mySpeedX;
	}
	
	/**
	 * @return la vitesse de l'élément sur l'axe y
	 */
	
	public double getSpeedY()
	{
		return mySpeedY;
	}
	
	/**
	 * @return true si l'objet tombe ou bouge
	 */
	
	public boolean isMobile()
	{
		return mySpeedX != 0 || mySpeedY != 0;
	}
	
	/**
	 * Va vérifier si les pixels du bas sont physique ou non. Equivalent de canGoAt(0, y).
	 * @param distance la distance sous l'objet à vérifiée
	 * @return true si l'objet tombe
	 */
	
	public boolean isFalling(int distance)
	{
		return canGoAt(0, -distance);
	}
	
	/**
	 * @return la distance séparant l'objet du sol, ou -1 si le joueur n'est pas au-dessus du sol
	 */
	
	public int getDistanceToGround()
	{
		for(int i = 0 ; true ; i++)
		{
			if(isFalling(i) == false)
				return i;
			
			if(i > getPosition().getPosY())
				return -1;
		}
	}
}
