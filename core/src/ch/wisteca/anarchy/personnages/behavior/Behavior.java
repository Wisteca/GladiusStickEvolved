package ch.wisteca.anarchy.personnages.behavior;

/**
 * Représente un comportement d'une entité.
 * @author Wisteca
 */

public interface Behavior {
	
	/**
	 * @return true si la condition pour laquelle le comportement doit se déclencher est respectée
	 */
	
	public boolean checkCondition();
	
	/**
	 * Méthode appelée tous les ticks tout pendant que le comportement doit faire effet.
	 */
	
	public void doBehavior();
	
	/**
	 * Méthode appelée à la fin du comportement, lorsque l'entité change de comportement.
	 */
	
	public void finish();
}
