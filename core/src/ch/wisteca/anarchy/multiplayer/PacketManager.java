package ch.wisteca.anarchy.multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ch.wisteca.anarchy.events.Event;
import ch.wisteca.anarchy.events.EventListener;
import ch.wisteca.anarchy.events.EventManager;

/**
 * PacketManager écoute les évenements locaux et les envoit aux autres joueurs. 
 * Il écoute les paquets venant des autres joueurs et les appelle sous forme  d'évenements en locale. 
 * Il est la base de la communiquation.
 * @author Wisteca
 */

public class PacketManager {
	
	private static PacketManager myInstance;
	
	private Socket myConnection;
	private BufferedReader myReader;
	private PrintWriter myWriter;
	
	private ConcurrentLinkedQueue<Event> myListedEvents = new ConcurrentLinkedQueue<>();
	
	public PacketManager(String clientName)
	{
		myInstance = this;
		
		// Evenements
		EventManager.getInstance().addListener(this);
		
		// Connexion
		try {
			
			myConnection = new Socket(InetAddress.getLocalHost(), 2009);
			
			myReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
			myWriter = new PrintWriter(myConnection.getOutputStream());
			
			new SenderThread();
			new ReceiverThread();
			
			//EventManager.getInstance().callEvent(new ConnectionEvent(clientName));
			
		} catch(IOException | ParserConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	private class SenderThread implements Runnable {
		
		private Element myCurrentPacket;
		
		public SenderThread() throws ParserConfigurationException
		{
			Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("packet");
			myCurrentPacket = root.getOwnerDocument().createElement("event");
			root.appendChild(myCurrentPacket);
			
			new Thread(this, "Sender-Thread").start();
		}
		
		@Override
		public void run()
		{
			while(myConnection.isClosed() == false)
			{
				// envoi des paquets en attente
				if(myListedEvents.isEmpty() == false)
				{
					Event currentEvent = myListedEvents.remove(); 
					myCurrentPacket.setAttribute("event", currentEvent.getClass().getSimpleName());
					currentEvent.serialize(myCurrentPacket);
					
					myWriter.println(elementToString(myCurrentPacket));
					myWriter.flush();
					
					NodeList nodesToRemove = myCurrentPacket.getChildNodes();
					for(int i = 0 ; i < nodesToRemove.getLength() ; i++)
						myCurrentPacket.removeChild(nodesToRemove.item(i));
				}
			}
		}
	}
	
	private class ReceiverThread implements Runnable {
		
		private DocumentBuilder myBuilder;
		
		public ReceiverThread() throws ParserConfigurationException
		{
			myBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			new Thread(this, "Receiver-Thread").start();
		}
		
		@Override
		public void run()
		{
			try {
				
				while(myConnection.isClosed() == false)
				{
					// lecture des paquets et envoit de ceux-ci sous forme d'event
					String receivePacket = myReader.readLine();
					if(receivePacket != null)
						EventManager.getInstance().callEvent(Event.deserializeEvent(myBuilder.parse(new InputSource(new StringReader(receivePacket))).getDocumentElement()));
				}
				
			} catch(IOException | SAXException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@EventListener
	public void onEvent(Event e)
	{
		myListedEvents.add(e);
		System.out.println(e.getClass().getSimpleName());
	}
	
	private String elementToString(Element element)
	{
		try	{
			
			DOMSource domSource = new DOMSource(element);
		    StringWriter writer = new StringWriter();
		    StreamResult result = new StreamResult(writer);
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
		    transformer.transform(domSource, result);
		    
		    return writer.toString();
		
		} catch(TransformerFactoryConfigurationError | TransformerException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * @return l'unique instance de PacketManager
	 */
	
	public static PacketManager getInstance()
	{
		return myInstance;
	}
}
