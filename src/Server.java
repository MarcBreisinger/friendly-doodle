import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;

public class Server {
	public Server(Observer o) {
		ServerSocket serverSocket = null;
		

		//An Port 13000 binden:
		try
		{
			serverSocket = new ServerSocket(13000);
		}
		catch (IOException e)
		{
			System.out.println("Binden an Port  13000 schlug fehl: " + e.getMessage());
			System.exit(-1);
		}

		//In einer Endlosschleife auf eingehende Anfragen warten.
		while (true)
		{
			try
			{
				//Blocken, bis eine Anfrage kommt:
				System.out.println ("ServerSocket - accepting");
				Socket clientSocket = serverSocket.accept();

				//Wenn die Anfrage da ist, dann wird ein Thread gestartet, der 
				//die weitere Verarbeitung übernimmt.
				System.out.println ("ServerSocket - accept done");
				TextServerHandler tsh = new TextServerHandler(clientSocket);
				tsh.addObserver(o);
				Thread threadHandler = new Thread(tsh);
				threadHandler.start();

				System.out.println ("ServerSocket - Thread started, next client please...");
			}
			catch (IOException e)
			{
				System.out.println("'accept' auf Port 13000 fehlgeschlagen");
				System.exit(-1);
			}
		}
    }
}
