import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Diese Klasse übernimmt die Kommunikation zwischen einem einzelnen Client und dem Server.
 * Sie nimmt Strings entgegen und liefert das "toUpper" zurück.
 * 
 * @author Wolfgang Knauf
 * 
 */
public class TextServerHandler extends Observable implements Runnable
{
  /**
   * Dies ist die Client-Verbindung.
   * 
   */
  private Socket clientSocket;
  private Message msg = null;
  private JSONParser parser;
  private ContainerFactory containerFactory;

  /**
   * Konstruktor, dem der Client-Socket übergeben wird.
   * 
   * @param _clientSocket Socket-Verbindung zum Client.
   */
  public TextServerHandler(Socket _clientSocket)
  {
    this.clientSocket = _clientSocket;
    parser = new JSONParser();

	containerFactory = new ContainerFactory(){
	    public List creatArrayContainer() {
	        return new LinkedList();
	    }

	    public Map createObjectContainer() {
	        return new LinkedHashMap();
	    }                     
	};
  }
  public Message getMessage() {
	  return msg;
  }

  /**Thread wird ausgeführt: alle Daten, die der Client schickt, in Upper Case konvertieren
   * und zurückschicken. 
   * 
   */
  @Override
  public void run()
  {
    try
    {
      System.out.println("Thread " + Thread.currentThread().getId() + " für Clientanfrage gestartet !");
      // Eingabe-Reader und Ausgabe-Writer erzeugen.
      // Es wird hier nur mit Text gearbeitet und zeilenweise eingelesen bzw. geschrieben.
      PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // In einer Endlosschleife auf Eingaben horchen bis die Verbindung beendet wird
      // und ein "readLine" dadurch nichts mehr zurückliefert. 
      String strInput = null;
      System.out.println ("Thread " + Thread.currentThread().getId() + " für Clientanfrage wartet auf Antwort...");
      while ((strInput = in.readLine()) != null)
      {
	    	  try {
					
		        Map json = (Map)parser.parse(strInput, containerFactory);
		        
		        Iterator iter = json.entrySet().iterator();
		        JSONObject jo = (JSONObject) parser.parse(strInput);
		        msg = new Message(jo);
	    	  
		        System.out.println("Thread " + Thread.currentThread().getId() + ">  " + strInput);
		        setChanged();
		        notifyObservers(msg);
	    	  } catch(ParseException pe) {
	    		  System.out.println(pe);
	    	  }
      }
      System.out.println("Thread " + Thread.currentThread().getId() + " ist fertig!");
    }
    catch (IOException ioEx)
    {
      System.out.println("Fehler beim Schreiben:" + ioEx.getMessage());
    }
  }
}