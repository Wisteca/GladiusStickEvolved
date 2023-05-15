package ch.wisteca.anarchy.utils;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Une texture avec des points à ses bords, pour montrer quelles positions sont à tester lors de déplacements.
 * @author Wisteca
 */

public class ShapedTexture extends Texture {
	
	private ArrayList<int[]> myCheckPoints = new ArrayList<>();
	
	public ShapedTexture(Pixmap map, Element points)
	{
		super(map);

		NodeList list = points.getChildNodes();
		for(int i = 0 ; i < list.getLength() ; i++)
		{
			if(list.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element point = (Element) list.item(i);
			myCheckPoints.add(new int[] {Integer.valueOf(point.getAttribute("x")), Integer.valueOf(point.getAttribute("y"))});
		}
	}
	
	/**
	 * @param internalPath le chemin vers la texture
	 * @param points l'élément contenant les infos de la texture
	 */
	
	public ShapedTexture(String internalPath, Element points)
	{
		super(internalPath);
		
		NodeList list = points.getChildNodes();
		for(int i = 0 ; i < list.getLength() ; i++)
		{
			if(list.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			Element point = (Element) list.item(i);
			myCheckPoints.add(new int[] {Integer.valueOf(point.getAttribute("x")), Integer.valueOf(point.getAttribute("y"))});
		}
	}
	
	/**
	 * @return la liste des points à tester, int[0] est le point sur l'axe x par rapport A LA TAILLE REELLE DE l'image, int[1] pour l'axe y
	 */
	
	public ArrayList<int[]> getCheckPoints()
	{
		return new ArrayList<>(myCheckPoints);
	}
	
}
