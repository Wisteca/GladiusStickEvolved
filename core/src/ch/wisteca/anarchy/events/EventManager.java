package ch.wisteca.anarchy.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Singleton qui g�re l'enregistrement des �venements.
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
	 * Ajouter un objet qui pourra �couter les �venements.
	 * @param object l'objet qui �coutera
	 */
	
	public void addListener(Object object)
	{
		if(myListeningClasses.contains(object) == false)
			myListeningClasses.add(object);
	}
	
	/**
	 * @param object Supprimer cet objet de la liste des objets qui �coutent les �venements
	 */
	
	public void removeListener(Object object)
	{
		myListeningClasses.remove(object);
	}
	
	/**
	 * Tous les objets qui �coutent cet �venement seront appel�s.
	 * @param e l'�venement en question
	 */
	
	public void callEvent(Event e)
	{
		Object currentListener = null;
		Method currentMethod = null;
		try {
			
			for(Object listener : myListeningClasses)
			{
				currentListener = listener;
				for(Method method : listener.getClass().getMethods()) // pour chaque m�thodes de l'objet
				{
					currentMethod = method;
					if(method.isAnnotationPresent(EventListener.class)) // si la m�thode a bien l'annotation qui lui permet d'�couter les �venements
					{
						for(Parameter param : method.getParameters())
						{
							if(param.getType().equals(e.getClass()) || param.getType().isAssignableFrom(e.getClass())) // si le type de param�tre est bien l'�venement
								method.invoke(listener, e); // invoquation de la m�thode		
						}
					}
				}
			}
		
		} catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
			
			if(ex instanceof InvocationTargetException)
				System.err.println("Une exception de type " + ex.getCause() + " s'est produite dans un �venement de l'objet " + currentListener.getClass().getSimpleName()
									+ ", dans la m�thode " + currentMethod.getName() + ".");
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
