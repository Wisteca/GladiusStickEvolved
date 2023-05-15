package ch.wisteca.anarchy.personnages.behavior;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.GameScreen;
import ch.wisteca.anarchy.events.EventManager;
import ch.wisteca.anarchy.personnages.Entity;
import ch.wisteca.anarchy.personnages.Personnage;

/**
 * Ce comportement permet à l'entité d'attaquer ses ennemis.
 * @author Wisteca
 */

public class AttackBehavior implements Behavior {

	private Entity myEntity;
	private Personnage myTarget;
	
	boolean b = true;
	
	public AttackBehavior(Entity entity)
	{
		myEntity = entity;
		EventManager.getInstance().addListener(this);
	}
	/*
	@EventListener
	public void onArrive(DestinationEvent e)
	{
		if(e.getWho().equals(myEntity) == false)
			return;
		System.out.println("attack");
		myEntity.attack();
	}
	*/
	@Override
	public boolean checkCondition()
	{
		int distance = 100, testDistance;
		for(GameObject object : GameScreen.getInstance().getAllGameObjects())
		{
			if(object instanceof Personnage)
			{
				Personnage perso = (Personnage) object;
				if(perso.equals(myEntity))
					continue;
				
				testDistance = object.getPosition().distance(myEntity.getPosition());
				if(testDistance < distance)
				{
					myTarget = perso;
					distance = testDistance;
				}
			}
		}
		
		return myTarget != null;
	}

	@Override
	public void doBehavior()
	{
		myEntity.moveTo(myTarget.getPosition());
		//System.out.println("moveTo");
	}

	@Override
	public void finish()
	{}
}
