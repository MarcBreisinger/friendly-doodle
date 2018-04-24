import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DatabaseReader extends Observable implements Runnable {

	private static final int POLL_INTERVAL_TIME = 200;
	private Client client;
	//private String REST_SERVICE_URL = "http://localhost/api/product/read_messages.php";
	private String REST_SERVICE_URL_LAST = "http://cs.marcbreisinger.de/api/product/read_last_message.php";
	private String REST_SERVICE_URL_FROMID = "http://cs.marcbreisinger.de/api/product/read_message_from.php";
	  
	private boolean running = true;
	private Message[] messages;
	private int lastID = 0;
	
	@Override
	public void run() {
		while (running) {
			client = ClientBuilder.newClient();
			try {
				
				URL url = new URL(REST_SERVICE_URL_FROMID + "?id="+(lastID));
				if(lastID == 0) {
					url = new URL(REST_SERVICE_URL_LAST);
				}
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				JSONParser parser = new JSONParser();

				ContainerFactory containerFactory = new ContainerFactory(){
				    public List creatArrayContainer() {
				        return new LinkedList();
				    }

				    public Map createObjectContainer() {
				        return new LinkedHashMap();
				    }                     
				};
				String output;
				//System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					try {
					
				        Map json = (Map)parser.parse(output, containerFactory);
				        Iterator iter = json.entrySet().iterator();
				        JSONObject jo = (JSONObject) parser.parse(output);
				        if(jo.containsValue("No messages found.")) {
				        		System.out.print(".");
				        		continue;
				        }
				        JSONArray ja = (JSONArray)jo.get("records");
				        messages = new Message[ja.size()];
				        for (int i = 0; i < ja.size(); i++) {
							messages[i] = new Message((JSONObject)ja.get(i));
							
						}
				        if(lastID==0 && messages.length>0) {
					        	//check if this message is too old
					        	SimpleDateFormat dateFormat = new SimpleDateFormat(
					                    "yyyy-MM-dd hh:mm:ss:SSS");
					        
					        Date parsedTimeStamp = dateFormat.parse(messages[0].getTimestamp());
					        if(new Date().getTime() - parsedTimeStamp.getTime() > 2000) {
					        		System.out.print("Last message is too old.");
					        		if(messages.length>0) {
						        		lastID = new Integer(messages[0].getId())+1;
						        }
					        		continue;
					        	}
				        }

				        if(messages.length>0) {
				        		lastID = new Integer(messages[0].getId())+1;
				        }
				        setChanged();
				        notifyObservers(messages);
				      } catch(ParseException pe) {
				    	    System.out.println(pe);
				      } catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				System.err.println("DatabaseReader: Can't connect to "+e.getMessage());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }

			try {
				Thread.sleep(POLL_INTERVAL_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
