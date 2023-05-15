package ch.wisteca.anarchy.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.utils.Utils;

/**
 * Représente le monde, contient la liste des objets présents dans ce dernier.
 * @author Wisteca
 */

public class World {
	
	private HashMap<String, Texture> myWorldTextures = new HashMap<>();
	private ArrayList<WorldElement> myElements = new ArrayList<>();
	private String myName;
	private double myGravity;
	private int myMaxSpeed;
	
	/**
	 * @param name le nom du monde
	 */
	
	public World(String name)
	{
		myName = name;
	}
	
	/**
	 * Initialise le monde après sa création, il faut toujours appeler cette méthode après avoir créer une instance de monde.
	 */
	
	public void initWorld()
	{
		// initialisation des textures du monde
		String worldPath = Utils.ASSETS_PATH + "\\worlds\\" + myName;
		File texturesFolder = new File(worldPath + "\\textures");
		System.out.println(texturesFolder.getAbsolutePath());
		
		for(File file : texturesFolder.listFiles())
		{
			System.out.println(file.getName());
			myWorldTextures.put(file.getName().replaceAll(".png", ""), new Texture(new FileHandle(file)));
		}
			
		// initialisation des éléments du monde
		Document doc = null;
		try {
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(worldPath + "\\objects.xml"));
		
		} catch(ParserConfigurationException | SAXException | IOException ex) {
			ex.printStackTrace();
		}
		
		NodeList list = doc.getDocumentElement().getChildNodes();
		for(int i = 0 ; i < list.getLength() ; i++)
		{
			if(list.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if(list.item(i).getNodeName().equals("data"))
			{
				Element data = (Element) list.item(i);
				myGravity = Math.abs(Double.valueOf(data.getAttribute("gravity")) / Utils.TICKS_TIME_MILLIS); // division par le nombre de ticks par secondes pour obtenir une seconde dans le fichier
				myMaxSpeed = Integer.valueOf(data.getAttribute("maxSpeed"));
				
				if(myMaxSpeed > 0)
					throw new IllegalArgumentException("La vitesse maximale du monde doit être négative !");
				
				continue;
			}
			
			WorldElement element = null;
			if(list.item(i).getNodeName().equals("basicElement"))
				element = new WorldElement(this);
			else if(list.item(i).getNodeName().equals("gravityElement"))
				element = new GravityElement(this);
				
			if(element == null)
				throw new IllegalArgumentException("Un élément du monde n'est pas reconnu.");
				
			element.initElement((Element) list.item(i));
			myElements.add(element);
		}
	}
	
	/**
	 * Toutes les textures sont sauvegardées dans le monde en un exemplaire, pour éviter que plusieurs textures semblables occupent de la mémoire inutilement.
	 * @param name le nom de la texture à récupérer
	 * @return la texture portant le nom spécifié
	 */
	
	public Texture getTextureFromName(String name)
	{
		return myWorldTextures.get(name);
	}
	
	/**
	 * @return la gravité du monde (divisée par 50 pour correspondre aux timer du jeu)
	 */
	
	public double getGravity()
	{
		return myGravity;
	}
	
	/**
	 * @return la vitesse maximale à laquelle un objet peut tomber.
	 */
	
	public int getMaxSpeed()
	{
		return myMaxSpeed;
	}
	
	/**
	 * @return tous les objets contenus dans le monde
	 */
	
	public ArrayList<WorldElement> getAllObjects()
	{
		return new ArrayList<>(myElements);
	}
	
	/**
	 * @return tous les éléments se trouvant dans la couche spécifiée (layer de 0 à 9)
	 */
	
	public ArrayList<GameObject> getAllObjects(int layer)
	{
		ArrayList<GameObject> list = new ArrayList<>();
		for(GameObject object : myElements)
		{
			if(object.getLayer() == layer)
				list.add(object);
		}
		return list;
	}
	
	/**
	 * @param x la position x dans le monde
	 * @param y la position y dans le monde
	 * @return l'élément se trouvant à la position donnée ou null si il n'y en a pas
	 */
	
	public WorldElement getElementAt(int x, int y)
	{
		return getElementAt(x, y, null);
	}
	
	/**
	 * @param x la position x dans le monde
	 * @param y la position y dans le monde
	 * @param ignore un élément à ignorer lors de la séléction
	 * @return l'élément se trouvant à la position donnée ou null si il n'y en a pas
	 */
	
	public WorldElement getElementAt(int x, int y, WorldElement ignore)
	{
		for(int layer = 10 ; layer >= 0 ; layer--)
		{
			for(WorldElement element : myElements)
			{
				if(element.equals(ignore))
					continue;
				
				if(element.getLayer() == layer)
				{
					if(element.getPosition().isInside(x, y))
						return element;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @return le nom du monde
	 */
	
	public String getName()
	{
		return myName;
	}
}
