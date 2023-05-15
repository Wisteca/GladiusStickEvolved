package ch.wisteca.anarchy.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Singleton qui gère l'enregistrement des évenements.
 * @author Wisteca
 */

public class EventManager {
	
	private ArrayList<Object> myListeningClasses = new ArrayList<>();
	private static EventManager myInstance;
	
	private EventManager()
	{
		myInstance = this;
	}
	
	/**
	 * Ajouter un objet qui pourra écouter les évenements.
	 * @param object l'objet qui écoutera
	 */
	
	public void addListener(Object object)
	{
		if(myListeningClasses.contains(object) == false)
			myListeningClasses.add(object);
	}
	
	/**
	 * @param object Supprimer cet objet de la liste des objets qui écoutent les évenements
	 */
	
	public void removeListener(Object object)
	{
		myListeningClasses.remove(object);
	}
	
	/**
	 * Tous les objets qui écoutent cet évenement seront appelés.
	 * @param e l'évenement en question
	 */
	
	public void callEvent(Event e)
	{
		Object currentListener = null;
		Method currentMethod = null;
		try {
			
			for(Object listener : myListeningClasses)
			{
				currentListener = listener;
				for(Method method : listener.getClass().getMethods()) // pour chaque méthodes de l'objet
				{
					currentMethod = method;
					if(method.isAnnotationPresent(EventListener.class)) // si la méthode a bien l'annotation qui lui permet d'écouter les évenements
					{
						for(Parameter param : method.getParameters())
						{
							if(param.getType().equals(e.getClass()) || param.getType().isAssignableFrom(e.getClass())) // si le type de paramètre est bien l'évenement
								method.invoke(listener, e); // invoquation de la méthode		
						}
					}
				}
			}
		
		} catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
			
			if(ex instanceof InvocationTargetException)
				System.err.println("Une exception de type " + ex.getCause() + " s'est produite dans un évenement de l'objet " + currentListener.getClass().getSimpleName()
									+ ", dans la méthode " + currentMethod.getName() + ".");
			ex.printStackTrace();
		}
	}
	
	static
	{
		new EventManager();
	}
	
	/**
	 * @return l'unique instance d'EventManager
	 */
	
	public static EventManager getInstance()
	{
		return myInstance;
	}
}
