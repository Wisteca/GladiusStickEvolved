package ch.wisteca.anarchy.personnages;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import ch.wisteca.anarchy.GameObject;
import ch.wisteca.anarchy.GameScreen;
import ch.wisteca.anarchy.events.EventManager;
import ch.wisteca.anarchy.events.list.ElementEjectEvent;
import ch.wisteca.anarchy.utils.Label;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.ShapedTexture;
import ch.wisteca.anarchy.utils.Utils;
import ch.wisteca.anarchy.world.GravityElement;
import ch.wisteca.anarchy.world.SimpleObject;
import ch.wisteca.anarchy.world.Vector;
import ch.wisteca.anarchy.world.World;

/**
 * La classe de base de toute chose "vivante".
 * @author Wisteca
 */

public abstract class Personnage extends GravityElement {
	
	private HashMap<PersonnagePosition, ArrayList<ShapedTexture>> myTextures = new HashMap<>();
	private HashMap<PersonnagePosition, ArrayList<Pixmap>> myPixmaps = new HashMap<>();
	private PersonnagePosition myLastPosition = PersonnagePosition.STATIC;
	
	private float mySpeed;
	private int myJump, myHealth, myMaxHealth, myRegenTime, myCurrentRegenTime, myCurrentAttackTime;
	private Label myHealthLabel;
	
	private Position myRightSword, myLeftSword; // position de la zone d'effet de l'épée par rapport au coin à gauche de la texture du personnage.
	
	/**
	 * @param element l'élément xml dans lequel sont contenues les données de base du personnage
	 * @param world le monde dans lequel se trouve le personnage
	 * @param spawnPosition la position de spawn du personnage
	 */
	
	public Personnage(World world, Position spawnPosition)
	{
		super(world);
		setPosition(spawnPosition);
	}
	
	@Override
	public void initElement(Element data)
	{
		setInitialPosition(getPosition());
		setLayer(5);
		setPhysic(true);
		
		for(PersonnagePosition pos : PersonnagePosition.values())
		{
			myTextures.put(pos, new ArrayList<ShapedTexture>());
			myPixmaps.put(pos, new ArrayList<Pixmap>());
		}
		
		Element persoData = (Element) data.getElementsByTagName("data").item(0);
		getPosition().setWidth(Integer.valueOf(persoData.getAttribute("width")));
		getPosition().setHeight(Integer.valueOf(persoData.getAttribute("height")));
		mySpeed = Float.valueOf(persoData.getAttribute("speed"));
		myJump = Integer.valueOf(persoData.getAttribute("jump"));
		myHealth = myMaxHealth = Integer.valueOf(persoData.getAttribute("maxHealth"));
		myRegenTime = Integer.valueOf(persoData.getAttribute("regenTime"));
		myHealthLabel = new Label(Utils.HEALTH_LABEL, this, 25, getPosition().getHeight() + 10);
		myHealthLabel.setVisible(true);
		myCurrentRegenTime = 0;
		
		NodeList texturesList = persoData.getElementsByTagName("textures").item(0).getChildNodes();
		for(int i = 0 ; i < texturesList.getLength() ; i++)
		{
			if(texturesList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element textureElement = (Element) texturesList.item(i);
			String path = Utils.ASSETS_PATH + "\\" + persoData.getAttribute("persoFolderPath") + "\\" + textureElement.getAttribute("name") + ".png";
			ShapedTexture texture = new ShapedTexture(path, textureElement);
			
			try {
				
				PersonnagePosition persoPos = PersonnagePosition.valueOf(textureElement.getNodeName().toUpperCase());
				myTextures.get(persoPos).add(texture);
				
				texture.getTextureData().prepare();
				myPixmaps.get(persoPos).add(texture.getTextureData().consumePixmap());
				
				if(persoPos.equals(PersonnagePosition.LEFT_ATTACK)) // ajout des swordPoints
					myLeftSword = getSwordPos(textureElement, texture);
				else if(persoPos.equals(PersonnagePosition.RIGHT_ATTACK))
					myRightSword = getSwordPos(textureElement, texture);
				
			} catch (IllegalArgumentException ex) {
				
				System.err.println("Impossible de reconnaître le mot : " + textureElement.getNodeName() + ", il ne peut prendre comme valeur seulement :");
				for(PersonnagePosition pos : PersonnagePosition.values())
					System.err.println("- " + pos.toString().toLowerCase());
			}
		}
		
		setTextureList(new ArrayList<ShapedTexture>(myTextures.get(myLastPosition)), new ArrayList<>(myPixmaps.get(myLastPosition)));
	}
	
	private Position getSwordPos(Element element, Texture texture)
	{
		int x1OnTexture = Integer.valueOf(element.getAttribute("x1"));
		int y1OnTexture = Integer.valueOf(element.getAttribute("y1"));
		
		int x2OnTexture = Integer.valueOf(element.getAttribute("x2"));
		int y2OnTexture = Integer.valueOf(element.getAttribute("y2"));
		
		double ratio = (double) getPosition().getWidth() / texture.getWidth();
		int width = (int) Math.round((double) Math.abs(x1OnTexture - x2OnTexture) * ratio);
		int height = (int) Math.round((double) Math.abs(y1OnTexture - y2OnTexture) * ratio);
		int x1OnScreen = (int) Math.round((double) x1OnTexture / texture.getWidth() * width);
		int y1OnScreen = (int) Math.round((double) y1OnTexture / texture.getHeight() * height);
		
		return new Position(x1OnScreen, y1OnScreen, width, height);
	}
	
	/**
	 * Bouger à la vitesse demandée, négative pour la gauche et positive pour la droite.
	 */
	
	public void move(double speed)
	{
		if(getSpeedX() != speed)
			setSpeedX(speed);
		updatePersoPos();
	}
	
	/**
	 * Bouger à droite (true) ou à gauche (false) à la vitesse donnée dans le fichier du personnage.
	 */
	
	public void move(boolean toRight)
	{
		move(toRight ? mySpeed : -mySpeed);
		getPosition().setPosY(getPosition().getPosY() + 1);
	}
	
	@Override
	public void doTime(float late)
	{
		super.doTime(late);
		updatePersoPos();
		
		int label = Math.round(getHealthInPercent() / 10 * 5 - 1);
		if(label < 0)
			label = 0;
		myHealthLabel.setCurrentLabel(label);
		
		myCurrentRegenTime++;
		if(myCurrentRegenTime >= myRegenTime)
		{
			myCurrentRegenTime = 0;
			
			if(myHealth + 1 <= myMaxHealth)
				myHealth++;
		}
		
		if(myLastPosition.equals(PersonnagePosition.LEFT_ATTACK) || myLastPosition.equals(PersonnagePosition.RIGHT_ATTACK))
			myCurrentAttackTime++;
	}
	
	/**
	 * Vérifie si le personnage est l'air, si il ne l'est pas, saute à la hauteur demandée.
	 */
	
	public void jump(int height)
	{
		if(isFalling(3) == false)
		{
			Vector vector;
			eject(vector = new Vector(height, 90));
			EventManager.getInstance().callEvent(new ElementEjectEvent(this, vector));
		}
	}
	
	/**
	 * Vérifie si le personnage est l'air, si il ne l'est pas, saute à la hauteur donnée dans le fichier du personnage.
	 */
	
	public void jump()
	{
		jump(myJump);
	}
	
	private void updatePersoPos()
	{
		setLoopTime(4);
		PersonnagePosition pos = myLastPosition;
		
		if((myLastPosition.equals(PersonnagePosition.LEFT_ATTACK) || myLastPosition.equals(PersonnagePosition.RIGHT_ATTACK)) && myCurrentAttackTime >= 4)
			myCurrentAttackTime = 0;
		
		if(myCurrentAttackTime == 0)
		{
			if(getSpeedX() < 0) // le perso se dirige vers la gauche
			{
				if(getSpeedY() != 0)
					pos = PersonnagePosition.LEFT_JUMP;
				else
					pos = PersonnagePosition.LEFT;
			}
			else if(getSpeedX() > 0) // vers la droite
			{
				if(getSpeedY() != 0)
					pos = PersonnagePosition.RIGHT_JUMP;
				else
					pos = PersonnagePosition.RIGHT;
			}
			else // ne bouge pas
				pos = PersonnagePosition.STATIC;
		}
		
		if(myLastPosition.equals(pos) == false || myCurrentAttackTime > 0)
		{
			myLastPosition = pos;
			ArrayList<ShapedTexture> textureToLoad = new ArrayList<>(myTextures.get(pos));
			ArrayList<Pixmap> pixmapToLoad = new ArrayList<>(myPixmaps.get(pos));
			setTextureList(textureToLoad, pixmapToLoad);
		}
	}
	
	@Override
	public boolean changePosition(int changeX, int changeY, boolean ignorePhysic)
	{
		boolean canGo = super.changePosition(changeX, changeY, ignorePhysic);
		
		updatePersoPos();
		if(canGoAt(0, 0) == false)
			getPosition().setPosY(getPosition().getPosY() + 3); // résolution d'un problème qui faisait rentrer le perso dans le sol quand sa texture changeait
	
		return canGo;
	}
	
	/**
	 * Afflige des dégâts au personnage.
	 */
	
	public void damage(int damages)
	{
		damages = Math.abs(damages);
		if(myHealth - damages <= 0)
		{
			myHealth = 0;
			death();
		}
		else
		{
			myHealth -= damages;
			GameScreen.getInstance().addGameObject(new SimpleObject(getWorld(), "sang", 2, getPosition().clone()));
		//	EventManager.getInstance().callEvent(new DamageEvent(this));
		}
	}
	
	/**
	 * Ajouter ou retirer de la vie, sans causer de dégâts.
	 * @param change le nombre de vie à ajouter/enlever
	 */
	
	public void changeHealth(int change)
	{
		if(myHealth + change > myMaxHealth)
		{
			myHealth = myMaxHealth;
			return;
		}
		else if(myHealth + change <= 0)
		{
			myHealth = 0;
			death();
			return;
		}
		
		myHealth += change;
	}
	
	/**
	 * @return la vie du personnage
	 */
	
	public int getHealth()
	{
		return myHealth;
	}
	
	/**
	 * Changer la vie du personnage en passant par un pourcentage, que le personnage ait une vie maximale de 2541 ou 841, mettre la vie à 50% sera toujours la moitié.
	 * @param percent le pourcentage de vie
	 */
	
	public void setHealth(float percent)
	{
		if(percent < 0 || percent > 100)
			throw new IllegalArgumentException("Le pourcentage de vie doit se situer entre 0 et 100 !");
		
		myHealth = Math.round(myMaxHealth * (percent / 100));
		
		if(myHealth == 0)
			death();
	}
	
	/**
	 * @return la vie en pourcent
	 */
	
	public float getHealthInPercent()
	{
		return 100 * ((float) myHealth / myMaxHealth);
	}
	
	/**
	 * @return la vie maximum que le personnage peut avoir
	 */
	
	public int getMaxHealth()
	{
		return myMaxHealth;
	}
	
	/**
	 * Le personnage attaque et blesse les cibles autour de lui.
	 */
	
	public void attack()
	{
		if(myCurrentAttackTime > 0)
			return;
		
		myCurrentAttackTime = 1;
		Position swordPos = null;
		Vector eject = null;
		
		if(getSpeedX() < 0)
		{
			myLastPosition = PersonnagePosition.LEFT_ATTACK;
			swordPos = myLeftSword;
			eject = new Vector(50, 100);
		}
		else
		{
			myLastPosition = PersonnagePosition.RIGHT_ATTACK;
			swordPos = myRightSword;
			eject = new Vector(50, 80);
		}
		
		for(GameObject object : GameScreen.getInstance().getAllGameObjects())
		{
			if(object instanceof Personnage && object.equals(this) == false)
			{
				Personnage perso = (Personnage) object;
				ShapedTexture texture = (ShapedTexture) perso.getTexture();
				
				for(int[] point : texture.getCheckPoints())
				{	
					int posX = (int) Math.round(perso.getPosition().getPosX() + ((double) point[0] / texture.getWidth() * perso.getPosition().getWidth()));
					int posY = (int) Math.round(perso.getPosition().getPosY() + ((double) point[1] / texture.getHeight() * perso.getPosition().getHeight()));
					
					if(swordPos.add(getPosition(), true).isInside(posX, posY))
					{
						// dégâts !
						perso.damage(10);
						perso.eject(eject);
						break;
					}
				}
			}
				
		}
	}
	
	protected abstract void death();

	public enum PersonnagePosition {
		
		STATIC,
		RIGHT,
		LEFT,
		RIGHT_JUMP,
		LEFT_JUMP,
		RIGHT_STATIC,
		LEFT_STATIC,
		RIGHT_ATTACK,
		LEFT_ATTACK;
		
	}
}
