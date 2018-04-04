import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Dimension;
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
     
     MqttClient sampleClient;
     


	public MessageWindow() {
		//this.setPreferredSize(new Dimension(300, 500));
		
		//textArea = new JTextArea();
		//textArea.setEditable(false);
		//this.add(textArea);
		
		//this.pack();
		//this.setVisible(true);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			sampleClient = new MqttClient(broker, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        System.out.println("Connecting to broker: "+broker);
	        sampleClient.connect(connOpts);
	        System.out.println("Connected");
	 
		} catch(MqttException me) {
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
		String host = "unknown";
		try {
			host = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log("Hi! Java Server started on "+host);
		
		DatabaseReader dbr = new DatabaseReader();
		Thread dbrt = new Thread(dbr);
		dbrt.start();
		new Server(this);
		
		
	
	}

	
	private void log(String msg) {
		//textArea.setText(textArea.getText()+msg+"\n");
		System.out.println(msg);
	}

	@Override
	public void update(Observable o, Object arg) {
		log(((TextServerHandler)o).getMessage());
		int i = 0;
		
			String base = "F4 53 2E D7 E3 ";
			String air = "02";
			String button = "ff";
			String fromPad = ((TextServerHandler)o).getMessage();
			if(fromPad.contains("Button")) {
				if (fromPad.contains("Pressed")) {
					air = "01";
				}
				String[] parts = fromPad.split(":");
				if(parts.length>1) {
					button = parts[parts.length-1];
					button = toHex(new Integer(button.trim()));
				}
				String mes = base + air + " " + button;
				System.out.println("Publishing message: "+mes);
	             MqttMessage message = new MqttMessage(mes.getBytes());
	             message.setQos(qos);
	             try {
					sampleClient.publish(topic, message);
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             System.out.println("Message published");
			}
         
	}
	private String toHex(int i) {
		String res = "";
		switch (i) {
		case 10:
			res = "0A";
			break;
		case  11:
			res = "0B";
			break;
		case 12:
			res = "0C";
			break;
		case 13:
			res = "0D";
			break;
		case 14:
			res = "0E";
			break;
		default:
				res = "0"+i; 
		}
		return res;
	}
}
