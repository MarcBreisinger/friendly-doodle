import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

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
  private String msg = null;

  /**
   * Konstruktor, dem der Client-Socket übergeben wird.
   * 
   * @param _clientSocket Socket-Verbindung zum Client.
   */
  public TextServerHandler(Socket _clientSocket)
  {
    this.clientSocket = _clientSocket;
  }
  public String getMessage() {
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
      msg = "Client connected";
      setChanged();
      notifyObservers();
      while ((strInput = in.readLine()) != null)
      {
        System.out.println("Thread " + Thread.currentThread().getId() + ">  " + strInput);
        msg = "Client:"+strInput;
        setChanged();
        notifyObservers();
        // Die Eingabe in "UpperCase" umwandeln und als komplette Zeile an Client zurückschieben.
        // "println" sorgt dafür, dass die Daten gesendet werden.
        out.println(strInput.toUpperCase());
        
        System.out.println("Thread " + Thread.currentThread().getId() + " für Clientanfrage next round");
      }
      msg = "Client disconnected";
      setChanged();
      notifyObservers();
      System.out.println("Thread " + Thread.currentThread().getId() + " ist fertig!");
    }
    catch (IOException ioEx)
    {
      System.out.println("Fehler beim Schreiben:" + ioEx.getMessage());
    }
  }
}