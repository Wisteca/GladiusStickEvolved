package ch.wisteca.anarchy.personnages.behavior;

import java.util.HashMap;

/**
 * Une map contenant un index (priorit�) et son comportement correspondant. La priorit� 1 sera execut�e avant la 2, ainsi de suite.
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
	 * Ajoute le comportement � la priorit� demand�e, si un comportement se trouve d�j� sous cette priorit�, celui-ci est d�plac� � la priorit� au-dessus, ainsi de suite.
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
	 * @return le comportement actuellement ex�cut� par l'entit�, ou -1 si elle n'en ex�cute aucun
	 */
	
	public int getCurrentBehavior()
	{
		return myCurrentBehavior;
	}
}
