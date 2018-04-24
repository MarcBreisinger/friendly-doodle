import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Dimension;
import java.awt.font.TextMeasurer;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class MessageWindow implements Observer {
	
	JTextArea textArea = null;
	
	String vin = "WBA7D01080GJ35426";
	
	 String topic        = "eno/raw/";
     String[] messages   = {"F4 53 2E D7 E3 01 00"};
     int qos             = 2;
     String broker       = "tcp://localhost:1883";
     String clientId     = "JavaSample";
     MemoryPersistence persistence = new MemoryPersistence();
     
     IPBroadcaster ipbroadcaster = null;
     
     MqttClient mqttClient;
     


	public MessageWindow() {
		
		connect2Mqtt();
		//find my local IP
		String host = "unknown";
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//start backend connection
		DatabaseReader dbr = new DatabaseReader();
		dbr.addObserver(this);
		Thread dbrt = new Thread(dbr);
		dbrt.start();
		
		TCPClient tcpclient = new TCPClient();
		tcpclient.addObserver(this);
		Thread tcpclient_thread = new Thread(tcpclient);
		//tcpclient_thread.start();
		
		
		//start local server
		log("Starting local server on "+host);
		new Server(this);
	
		
	}
	private void connect2Mqtt() {
	//Mosquitto connection
		try {
			mqttClient = new MqttClient(broker, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        System.out.println("Connecting to broker: "+broker);
	        mqttClient.connect(connOpts);
	        System.out.println("Connected");
	 
		} catch(MqttException me) {
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
	}
	
	private void log(String msg) {
		System.out.println(msg);
	}

	@Override
	public void update(Observable o, Object arg) {
		
		String[] msgs;
		if(o instanceof DatabaseReader) {
			Message[] ms = (Message[])arg;
			System.out.println(ms[0]);
			for (Message message : ms) {
				msgs = message2DIAGString(message);
				for (int i = 0; i < msgs.length; i++) {
					publish2Mqtt(msgs[i]);
				}
			}
			
		} else if(o instanceof TCPClient){
			//TODO
		
		} else if(o instanceof TextServerHandler){
			Message m = (Message)arg;
			msgs = message2DIAGString(m);
			for (int i = 0; i < msgs.length; i++) {
				publish2Mqtt(msgs[i]);
			}
		} else {
			System.err.println("Unknown message: "+arg.toString()+" from "+o.toString());
		}
		
	}
	public void publish2Mqtt(String msg) {
		System.out.println("Publishing message: "+msg);
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
			mqttClient.publish(topic, message);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       System.out.println("Message published");
	}
	public static String[] message2DIAGString(Message message) {
		String[] res = new String[] {};
		String msg = "F4";//Ethernet
		String category = message.getCategory();
		String job = message.getJob().replaceAll("\\s+", " ");
		if(category.contains("seat_massage")) 
		{
			//ok, its about the massage seats. What's the job?
			//Which seat?
			if(job.contains("front")) {
				if(job.contains("left")) {
					msg +=" 53";
				} else {
					msg +=" 59";
				}
			} else if(job.contains("rear")) {
				if(job.contains("left")) {
					msg +=" 5A";
				} else {
					msg +=" 5B";
				}
			}
			msg += " 2E";//Service ID
			msg += " D7 E3";//Diagnosebefehl Steuern Luftblasen
			String p = (job.contains("pressed")) ? " 01" : " 02";
			msg += p;
			String[] parts = message.getJob().split(" ");
			if(parts.length==4) {
				String buttonnr = parts[parts.length-1];
				msg  += " "+MessageWindow.toHexString(new Integer(buttonnr.trim()));
			} else {
				msg = "error: massage message malformatted - job="+job;
			}
			res = new String[] { msg};
		} else if (category.contains("seat_heating")) {
			//ok, its about the seat heating. What's the job?
			//Which seat?
			if(job.contains("front")) {
				if(job.contains("left")) {
					msg +=" 6D";
				} else {
					msg +=" 6E";
				}
			} else if(job.contains("rear")) {
				if(job.contains("left")) {
					msg +=" 69";
				} else {
					msg +=" 6A";
				}
			}
			msg += " 2E";//Service ID
			msg += " D7 14";//Diagnosebefehl Steuern Temperatur
			String[] parts = job.split(" ");
			if(parts.length==4) {
				String temp = parts[parts.length-1];
				msg  += " "+MessageWindow.toHexString(new Integer(temp.trim()));
				msg += " 00 00 00 00 00 00";
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			res = new String[] { msg};
		} else if (category.contains("seat_ventilation")) {
			//ok, its about the seat ventilation. What's the job?
			//Which seat?
			if(job.contains("front")) {
				if(job.contains("left")) {
					msg +=" 6D";
				} else {
					msg +=" 6E";
				}
			} else if(job.contains("rear")) {
				if(job.contains("left")) {
					msg +=" 69";
				} else {
					msg +=" 6A";
				}
			}
			msg += " 2E";//Service ID
			msg += " D7 15";//Diagnosebefehl Steuern Lueftung
			job = job.replaceAll("\\s+", " ");
			String[] parts = job.split(" ");
			for (int i = 0; i < parts.length; i++) {
				if(parts[i].contains("level")) {
					msg  += " "+MessageWindow.toHexString(new Integer(parts[i+1].trim()));
					i++;
				}
				if (parts[i].contains("vent")) {
					msg  += " "+MessageWindow.toHexString(new Integer(parts[i+1].trim()));
					i++;
				}
			}
			msg += job.contains("cushion_high") ? " 01" : " 00";
			msg += job.contains("backrest_high") ? " 01" : " 00";
			if(parts.length==8) {
				msg += " 00 00 00 00 00 00";
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			res = new String[] { msg};
		} else if (category.contains("seat_adjustment")) {
			//ok, its about the seat adjustment. What's the job?
			//Which seat?
			if(job.contains("front")) {
				if(job.contains("left")) {
					msg +=" 6D";
				} else {
					msg +=" 6E";
				}
			} else if(job.contains("rear")) {
				if(job.contains("left")) {
					msg +=" 69";
				} else {
					msg +=" 6A";
				}
			}
			msg += " 2E";//Service ID
			msg += " D7 08";//Diagnosebefehl Steuern Sitzverstellung
			//if(job.contains("backrest")) {
				msg += " 02";
			//}
			if(job.contains("stop")) {msg += " 00";}
			else if(job.contains("plus")) {msg += " 01";}
			else {msg += " 02";}
			String[] parts = job.split(" ");
			if(parts.length==5) {
				String speed = parts[4];
				msg  += " "+MessageWindow.toHexString(new Integer(speed.trim()));
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			res = new String[] { msg};
		} else if (category.contains("seat_memory")) {
			//TODO no signals yet
		} else if (category.contains("climate_ventilation")) {
			msg += " 78";
			msg += " 31 01 A1 28";
			//ok, its about the climate ventilation. What's the job?
			//Where?
			if(job.contains("front")) {
				msg += " 01";
			} else if (job.contains("fond")) {
				msg += " 02";
			} if (job.contains("hka")) {
				msg += " 03";
			} else {//all
				msg +=" XX";
			}
			String[] parts = job.split(" ");
			if(parts.length==2) {
				String speed = parts[1];
				msg  += " "+MessageWindow.toHexString(new Integer(speed.trim()));
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			if(msg.contains("XX")) {
				res = new String[3];
				for (int i = 0; i < 3; i++) {
					String n = msg.replace("XX", "0"+(i+1));
					res[i] = n;
				}
			} else {
				res = new String[] {msg};
			}
		} else if (category.contains("climate_temperature")) {
			msg += " 78";
			msg += " 31 01 A1 2A";
			//ok, its about the climate temperature. What's the job?
			//Where
			if(job.contains("front")) {
				msg += " 01";
			} else if (job.contains("left")) {
				msg += " 02";
			} else if (job.contains("right")) {
				msg += " 03";
			} else if (job.contains("fond_left")) {
				msg += " 04";
			} else if (job.contains("fond_right")) {
				msg += " 05";
			} else if (job.contains("rear")) {
				msg += " 06";
			} else {//all
				msg += " XX";
			}
			
			String[] parts = job.split(" ");
			if(parts.length==2) {
				String temp = parts[1];
				msg  += " "+MessageWindow.toHexString(new Integer(temp.trim()));
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			if(msg.contains("XX")) {
				res = new String[6];
				for (int i = 0; i < 6; i++) {
					String n = msg.replace("XX", "0"+(i+1));
					res[i] = n;
				}
			} else {
				res = new String[] {msg};
			}
		} else if (category.contains("window")) {
			//ok, its about the windows. 
			msg += " 40";
			msg += " 31 01 A1 7F";
			//What's the job?
			//Which window
			if(job.contains("front")) {
				if(job.contains("left")) {
					msg +=" 11";
				} else {
					msg +=" 12";
				}
			} else if(job.contains("rear")) {
				if(job.contains("left")) {
					msg +=" 13";
				} else {
					msg +=" 14";
				}
			}
			String[] parts = job.split(" ");
			if(parts.length==3) {
				String temp = parts[2];
				msg  += " "+MessageWindow.toHexString(new Integer(temp.trim()));
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			res = new String[] { msg};
		} else if (category.contains("lighting_color")) {
			//TODO msgs not found yet
		} else if (category.contains("sunroof")) {
			//ok, its about the sunroof. 
			msg += " 56";
			msg += " 31 01 A1 86";
			//What's the job?
			
			//Which seat?
			if(job.contains("sunroof")) {
				msg += " A1";
			} else if(job.contains("sunshade")) {
				msg += " A2";
			} if(job.contains("aed")) {
				msg += " A3";
			} else {//all
				msg += " B0";
			}
			String[] parts = job.split(" ");
			if(parts.length==3) {
				String pos = parts[1];
				msg  += " "+MessageWindow.toHexString(new Integer(pos.trim()));
				if(job.contains("tilted")){
					msg += " 01";
				} else {//flat
					msg += " 02";
				}
			} else {
				msg = "error: "+category+" message malformatted - job="+job;
			}
			res = new String[] { msg};
		}
		return res;
	}
	private static String toHexString(int i) {
		String res = "";
		if(i<16)res="0";
		res += Integer.toHexString(i);
		return res;
	}
}
