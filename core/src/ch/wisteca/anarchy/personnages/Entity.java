package ch.wisteca.anarchy.personnages;

import ch.wisteca.anarchy.personnages.behavior.AttackBehavior;
import ch.wisteca.anarchy.personnages.behavior.BehaviorMap;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.world.World;

public class Entity extends Personnage {
	
	private Position myPosToGo;
	private BehaviorMap myBehaviors;
	
	public Entity(World world, Position spawnPosition)
	{
		super(world, spawnPosition);
		myBehaviors = new BehaviorMap();
		myBehaviors.put(0, new AttackBehavior(this));
	}
	
	public void moveTo(Position to)
	{
		myPosToGo = to;
	}
	/*
	@Override
	protected void onBlocked()
	{
		if(myPosToGo == null)
			return;
		
		System.out.println("JUMP PARCE QUE BLOCKED");
		getPosition().setPosY(getPosition().getPosY() + 3);
	//	jump();
	//	move(getDirectionToGo() ? 5 : -5);
	}
	*/
	private boolean getDirectionToGo()
	{
		return myPosToGo.getPosX() > getPosition().getPosX();
	}
	
	@Override
	public void doTime(float late)
	{
		super.doTime(late);
		
		myBehaviors.doTime();
		
		if(myPosToGo != null)
		{
		//	if(getDirectionToGo() == getSpeedX() < 0)
			
			move(getDirectionToGo() ? 5 : -5);
			System.out.println("move");
			
			/*
			int x = getPosition().getPosX() + (getDirectionToGo() ? 110 : -110), y = getPosition().getPosY() - 30;
			WorldElement element = getWorld().getElementAt(x, y);
	//		System.out.print("myPos x : " + getPosition().getPosX() + " y : " + getPosition().getPosY() + "  checked x : " + x + " y : " + y + " --> ");
			
			if(element == null || element.isPhysic(x, y) == false)
			{
		//		System.out.print("true !");
				jump();
			} //else System.out.print("false");
	//		System.out.println();
			*/
			if(Math.abs(getPosition().getPosX() - myPosToGo.getPosX()) < 5) // arrivé à destination
			{
				resetSpeedX();
				myPosToGo = null;
			//	EventManager.getInstance().callEvent(new DestinationEvent(this));
			}
		}
	}

	@Override
	protected void death()
	{
		setHealth(100);
		getPosition().setPosY(800);
	}
}
