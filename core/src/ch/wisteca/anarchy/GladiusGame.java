package ch.wisteca.anarchy;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.badlogic.gdx.Game;

import ch.wisteca.anarchy.multiplayer.PacketManager;
import ch.wisteca.anarchy.personnages.LocalPlayer;
import ch.wisteca.anarchy.personnages.OnlinePersonnage;
import ch.wisteca.anarchy.personnages.Personnage;
import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.utils.Utils;
import ch.wisteca.anarchy.world.World;

public class GladiusGame extends Game {

	@Override
	public void create()
	{
		/*Pixmap map = new Pixmap(new FileHandle(new File(Utils.ASSETS_PATH + "\\test.png")));
		
		for(int x = 0 ; x < map.getWidth() ; x++)
		{
			for(int y = 0 ; y < map.getHeight() ; y++)
				System.out.println(map.getPixel(x, y));
		}*/
		this.setScreen(new GameScreen());
		
		World world = new World("default");
		GameScreen.getInstance().initWorld(world);
		
		Personnage perso = null;
	//	Entity entity = null;
		OnlinePersonnage oPerso = null;
		
		try {
			
			perso = new LocalPlayer(world);
			perso.initElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(Utils.ASSETS_PATH + "\\players\\Gabriel\\Gabriel.xml")).getDocumentElement());
			
			oPerso = new OnlinePersonnage(world, new Position(50, 600, 100, 100));
			oPerso.initElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(Utils.ASSETS_PATH + "\\players\\Gabriel\\Gabriel.xml")).getDocumentElement());
			/*
			entity = new Entity(world, new Position(500, 500));
			entity.initElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(Utils.ASSETS_PATH + "\\players\\Gabriel\\Gabriel.xml")).getDocumentElement());
			*/
		} catch(SAXException | IOException | ParserConfigurationException ex) {
			ex.printStackTrace();
		}
		
		GameScreen.getInstance().addGameObject(perso);
	//	GameScreen.getInstance().addGameObject(entity);
		GameScreen.getInstance().addGameObject(oPerso);
		
		new PacketManager("Gabriel le Grand c");
	}
}
