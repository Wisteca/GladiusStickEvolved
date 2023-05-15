package ch.wisteca.anarchy.personnages.behavior;

import java.util.HashMap;

/**
 * Une map contenant un index (priorité) et son comportement correspondant. La priorité 1 sera executée avant la 2, ainsi de suite.
 * @author Wisteca
 */

@SuppressWarnings("serial")
public class BehaviorMap extends HashMap<Integer, Behavior> {
	
	private int myCurrentBehavior;
	
	public void doTime()
	{
		for(int index = 0 ; true ; index++)
		{
			if(get(index) == null)
				break;
			
			Behavior behavior = get(index);
			if(myCurrentBehavior == index)
			{
				if(behavior.checkCondition())
					behavior.doBehavior();
				else
				{
					behavior.finish();
					myCurrentBehavior = -1;
				}
			}
			else if(index < myCurrentBehavior || myCurrentBehavior == -1)
			{
				if(behavior.checkCondition())
				{
					if(myCurrentBehavior != -1)
						get(myCurrentBehavior).finish();
					
					behavior.doBehavior();
					myCurrentBehavior = index;
					break;
				}
			}
		}
	}
	
	/**
	 * Ajoute le comportement à la priorité demandée, si un comportement se trouve déjà sous cette priorité, celui-ci est déplacé à la priorité au-dessus, ainsi de suite.
	 */
	
	@Override
	public Behavior put(Integer priority, Behavior behavior)
	{
		Behavior last = super.put(priority, behavior);
		if(last != null)
			put(priority + 1, last);
		
		return last;
	}
	
	/**
	 * @return le comportement actuellement exécuté par l'entité, ou -1 si elle n'en exécute aucun
	 */
	
	public int getCurrentBehavior()
	{
		return myCurrentBehavior;
	}
}
