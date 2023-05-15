package ch.wisteca.anarchy.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.utils.Lock;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.ShapedTexture;

/**
 * La classe de base d'un élément du jeu, avec fonctionnalité de changement de texture tous les tant de temps.
 * @author Wisteca
 */

public class WorldElement implements GameObject {
	
	private boolean myPhysic;
	private int myLayer, myLoopTime, myCurrentTime = 0; // CONTINUER UML, EVENTS DE SPAWN PUIS VOIR SINGLETON/WORLD ECOUTE EVENTS ET CREE LES ELEMENTS
	private float myThreadLate;
	private World myWorld;
	private Position myPosition;
	private UUID myUniqueId;
	
	private ConcurrentHashMap<Integer, Texture> myTextures = new ConcurrentHashMap<>();
	private HashMap<Integer, Pixmap> myPixmaps = new HashMap<>();
	private int myCurrentTexture;
	
	private Lock myLock = new Lock();
	
	/**
	 * @param element l'élément XML dans lequel se trouve les paramètres à prendre
	 * @param world le monde dans lequel se trouve l'élément de jeu
	 */
	
	public WorldElement(World world)
	{
		myWorld = world;
		myUniqueId = UUID.randomUUID();
	}
	
	/**
	 * Initialisation de l'objet, TOUJOURS appeler cette méthode après la création de l'objet.
	 */
	
	public void initElement(Element data)
	{
		myLayer = Integer.valueOf(data.getAttribute("layer"));
		myPhysic = Boolean.valueOf(data.getAttribute("physic"));
		myLoopTime = Integer.valueOf(data.getAttribute("loopTime"));
		myPosition = new Position((Element) data.getElementsByTagName("position").item(0));
		
		NodeList list = data.getElementsByTagName("textures").item(0).getChildNodes();
		for(int i = 0, textureCount = 0 ; i < list.getLength() ; i++)
		{
			if(list.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element textureElement = (Element) list.item(i);
			Texture texture = myWorld.getTextureFromName(textureElement.getAttribute("name"));
			myTextures.put(textureCount, texture);
			
			texture.getTextureData().prepare();
			myPixmaps.put(textureCount, texture.getTextureData().consumePixmap());
			
			textureCount++;
		}
	}
	
	@Override
	public void doTime(float late)
	{
		myThreadLate += late;
		if(myThreadLate > 1)
		{
			int toSubstract = (int) myThreadLate;
			myThreadLate -= toSubstract;
			myCurrentTime += toSubstract;
		}
		else
			myCurrentTime++;
		
		if(myCurrentTime >= myLoopTime)
		{
			myCurrentTime = 0;
			changeTexture();
		}
	}
	
	@Override
	public Texture getTexture()
	{
		try {
			
			synchronized(myLock)
			{
				while(myLock.isLocked())
					myLock.wait();
			}
			
			return myTextures.get(myCurrentTexture);	
		
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private void changeTexture()
	{
		if(myCurrentTexture + 1 >= myTextures.size())
			myCurrentTexture = 0;
		else
			myCurrentTexture++;
	}
	
	/**
	 * Changer la liste de textures de l'élément.
	 * @param textureToLoad les nouvelles textures
	 * @param pixmaps les pixmaps correspondantes aux textures
	 */
	
	public void setTextureList(ArrayList<ShapedTexture> textureToLoad, ArrayList<Pixmap> pixmaps)
	{
		myLock.lock();
		myCurrentTexture = 0;
		myTextures.clear();
		myPixmaps.clear();
		for(int i = 0 ; i < textureToLoad.size() ; i++)
		{
			myTextures.put(i, textureToLoad.get(i));
			myPixmaps.put(i, pixmaps.get(i));
		}
		
		synchronized(myLock)
		{
			myLock.unlock();
			myLock.notify();	
		}
	}
	
	/**
	 * @param loop le nombre de ticks entre chaque changements de texture
	 */
	
	public void setLoopTime(int loop)
	{
		myLoopTime = loop;
	}
	
	/**
	 * @param layer la couche sur laquelle l'élément apparaît
	 */
	
	public void setLayer(int layer)
	{
		myLayer = layer;
	}
	
	/**
	 * Un objet physique ne pourra pas passer à travers d'autres objets.
	 * @param physic true pour que l'élément soit physique
	 */
	
	public void setPhysic(boolean physic)
	{
		myPhysic = physic;
	}
	
	/**
	 * @param changeX le changement de position sur l'axe x
	 * @param changeY le changement de position sur l'axe y
	 * @return true si aucun obstacle ne se présente sur l'élément
	 */
	
	public boolean canGoAt(int changeX, int changeY)
	{
		if(myPhysic == false)
			return true;
		
		ArrayList<int[]> points = new ArrayList<>();
		int newX = myPosition.getPosX() + changeX;
		int newY = myPosition.getPosY() + changeY;
		
		if(getTexture() instanceof ShapedTexture)
		{
			ShapedTexture texture = (ShapedTexture) getTexture();
			for(int[] point : texture.getCheckPoints())
			{
				int pointX = (int) Math.round(((double) point[0] / (double) texture.getWidth()) * myPosition.getWidth());
				int pointY = (int) Math.round(((double) point[1] / (double) texture.getHeight()) * myPosition.getHeight());
				points.add(new int[] {pointX + newX, pointY + newY});
			}
		}
		else
		{
			points.add(new int[] {newX, newY});
			points.add(new int[] {newX + myPosition.getWidth(), newY});
			points.add(new int[] {newX + myPosition.getWidth(), newY + myPosition.getHeight()});
			points.add(new int[] {newX, newY + myPosition.getHeight()});
		}
		
		for(int[] pos : points)
		{
			WorldElement elementAt = myWorld.getElementAt(pos[0], pos[1], this);
			if(elementAt != null && elementAt.isPhysic(pos[0], pos[1]))
			{
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Si ignorePhysic est sur false, va commencer par faire un canGoAt puis sur le retour est true changera la position de l'élément.
	 * @param changeX le changement de position sur l'axe x
	 * @param changeY le changement de position sur l'axe y
	 * @param ignorePhysic true pour ignorer les obstacles
	 * @return false si ignorePhysic était égal à false et qu'un obstacle a empêcher l'élément se se déplacer
	 */
	
	public boolean changePosition(int changeX, int changeY, boolean ignorePhysic)
	{
		int newX = myPosition.getPosX() + changeX;
		int newY = myPosition.getPosY() + changeY;
		
		if(ignorePhysic == false && canGoAt(changeX, changeY) == false)
			return false;
		
		myPosition.setPosX(newX);
		myPosition.setPosY(newY);
		
		return true;
	}
	
	/**
	 * Changer la position de l'élément, sans vérification ou quoi que se soit d'autre.
	 * @param pos la nouvelle position
	 */
	
	public void setPosition(Position pos)
	{
		myPosition = pos;
	}
	
	@Override
	public int getLayer()
	{
		return myLayer;
	}

	@Override
	public Position getPosition()
	{
		return myPosition;
	}
	
	/**
	 * @return le monde dans lequel se trouve l'objet
	 */
	
	public World getWorld()
	{
		return myWorld;
	}
	
	/**
	 * @return l'uuid de l'élément qui permet de l'identifier
	 */
	
	public UUID getUniqueId()
	{
		return myUniqueId;
	}
	
	/**
	 * @return true si l'élément est physique et que le pixel pointé dans l'élément n'est pas transparent.
	 * @param x la coordonnée x du monde, cette position DOIT se trouver dans l'objet
	 * @param y la coordonnée y du monde, cette position DOIT se trouver dans l'objet
	 */
	
	public boolean isPhysic(int x, int y)
	{
		if(myPhysic == false)
			return false;
		
		Texture texture = getTexture();
		
		int posX = x - getPosition().getPosX();
		int posY = texture.getHeight() - (y - getPosition().getPosY());
		
		int count = 0;
		for(int axisX = -4 ; axisX <= 4 ; axisX++)
		{
			for(int axisY = -4 ; axisY <= 4 ; axisY++)
			{
				if(new Color(myPixmaps.get(myCurrentTexture).getPixel(posX + axisX, posY + axisY)).a != 0)
					count++;
			}
		}
		
		if(count >= 80)
			return true;
		else
			return false;
	}
}
