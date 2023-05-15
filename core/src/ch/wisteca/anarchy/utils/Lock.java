package ch.wisteca.anarchy.utils;

/**
 * Petite classe utile qui permet ed bloquer des Threads.
 * @author Wisteca
 */

public class Lock {
	
	private boolean myIsLocked = false;
	
	/**
	 * @return true si le lock est fermé.
	 */
	
	public boolean isLocked()
	{
		return myIsLocked;
	}
	
	/**
	 * Ferme le lock.
	 */
	
	public void lock()
	{
		myIsLocked = true;
	}
	
	/**
	 * Ouvre le lock.
	 */
	
	public void unlock()
	{
		myIsLocked = false;
	}
}
