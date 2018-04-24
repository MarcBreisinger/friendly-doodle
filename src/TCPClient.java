import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

public class TCPClient extends Observable implements Runnable {
	
	private String host = "localhost";
	private int port = 9999;
	private boolean connected = false;
	private String message = "N/A";
	private boolean running = true;
	
	
	public TCPClient() {
		
	}


	@Override
	public void run() {
		
		Socket socket;
		while(running) {
			try {
				socket = new Socket(host, port);
				connected = true;
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
				writer.writeBytes("Hello from Pi!\n");
				while(connected) {
					while ((message = reader.readLine()) != null) {
						setChanged();
						notifyObservers(message);
					}
				}
			} catch (UnknownHostException e) {
				System.err.println(getClass()+">UnknownHostException. Can't connect to "+e.getMessage());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				System.err.println(getClass()+">IOException: "+e.getMessage());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	
}
