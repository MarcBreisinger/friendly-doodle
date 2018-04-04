import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class DatabaseReader extends Observable implements Runnable {

	private Client client;
	//private String REST_SERVICE_URL = "http://localhost/api/product/read_messages.php";
	private String REST_SERVICE_URL = "http://cs.marcbreisinger.de/api/product/read_messages.php";
	  
	private boolean running = true;
	
	@Override
	public void run() {
		while (running) {
			client = ClientBuilder.newClient();
			/*GenericType<List<Message>> list = new GenericType<List<Message>>() {};
		      List<Message> messages = client
		         .target(REST_SERVICE_URL)
		         .request(MediaType.APPLICATION_JSON)
		         .get(list);
		      
			*/
			
			try {

				URL url = new URL(REST_SERVICE_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }
			//WebTarget target = client.target(REST_SERVICE_URL);
			//javax.ws.rs.client.Invocation.Builder response = target.request(MediaType.APPLICATION_JSON);
			
			//System.out.println(response);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
