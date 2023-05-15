package ch.wisteca.anarchy.personnages.behavior;

/**
 * Repr�sente un comportement d'une entit�.
 * @author Wisteca
 */

public interface Behavior {
	
	/**
	 * @return true si la condition pour laquelle le comportement doit se d�clencher est respect�e
	 */
	
	public boolean checkCondition();
	
	/**
	 * M�thode appel�e tous les ticks tout pendant que le comportement doit faire effet.
	 */
	
	public void doBehavior();
	
	/**
	 * M�thode appel�e � la fin du comportement, lorsque l'entit� change de comportement.
	 */
	
	public void finish();
}
