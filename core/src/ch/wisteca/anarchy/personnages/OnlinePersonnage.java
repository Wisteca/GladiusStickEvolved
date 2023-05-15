package ch.wisteca.anarchy.personnages;

import ch.wisteca.anarchy.events.EventListener;
import ch.wisteca.anarchy.events.EventManager;
import ch.wisteca.anarchy.events.list.ElementEjectEvent;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.world.World;

/**
 * Un objet OnlinePersonnage représente une entité qui est contrôlée sur un autre ordinateur.
 *  Il va écouter  les évenements venant du réseau et effectuer les actions décrites par ceux-ci.
 * @author Wisteca
 */

public class OnlinePersonnage extends Personnage {

	public OnlinePersonnage(World world, Position spawnPosition)
	{
		super(world, spawnPosition);
		EventManager.getInstance().addListener(this);
	}

	@EventListener
	public void onJump(ElementEjectEvent e)
	{
		if(e.getElement().equals(this))
			eject(e.getVector());
		
	}
	
	@Override
	protected void death()
	{}
}
