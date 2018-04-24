import java.io.Serializable;

import org.json.simple.JSONObject;

public class Message implements Serializable {

   private static final long serialVersionUID = 1L;
   private String category;
   private String job;
   private String duration;
   private String timestamp;
   private String id;
   
   public static String key_category = "category";
   public static String key_job = "job";
   public static String key_duration = "duration";
   public static String key_timestamp = "timestamp";
   public static String key_id = "id";

   public Message(){}

   public Message(String category, String job, String duration, String timestamp, String id){

      this.category = category;
      this.job = job;
      this.duration = duration;
      this.timestamp = timestamp;
      this.id = id;
   }


public Message(JSONObject object) {
	this.category = (String)object.get(key_category);
	this.job = (String)object.get(key_job);
	this.duration = (String)object.get(key_duration);
	setTimestamp((String)object.get(key_timestamp));
	this.id = (String)object.get(key_id);
}

public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public String getCategory() {
      return category;
   }
      public void setCategory(String category) {
      this.category = category;
   }
   public String getJob() {
      return job;
   }
   public void setJob(String job) {
      this.job = job;
   }	
   public String getDuration() {
	      return duration;
	   }
   public void setDuration(String duration) {
      this.duration = duration;
   }	
   public String getTimestamp() {
	      return timestamp;
	   }
   public void setTimestamp(String timestamp) {
	   	if (timestamp.length()==19) {
	   		timestamp+=":000";
	   	}
	   	this.timestamp = timestamp;
   }	

   @Override
   public boolean equals(Object object){
      if(object == null){
         return false;
      }else if(!(object instanceof Message)){
         return false;
      }else {
         Message m = (Message)object;
         if(id == m.getId()
         ){
            return true;
         }			
      }
      return false;
   }

	public void print() {
		System.out.println("Message:");
		System.out.println("\tcategory:\t"+this.category);
		System.out.println("\tjob:\t\t"+this.job);
		System.out.println("\tduration:\t"+this.duration);
		System.out.println("\ttimestamp:\t"+this.timestamp);
		System.out.println("\tid:\t\t"+this.id);
	}	
	public String toString() {
		String s = "Message:\n";
		s+="\tcategory:\t"+this.category+"\n";
		s+="\tjob:\t\t"+this.job+"\n";
		s+="\tduration:\t"+this.duration+"\n";
		s+="\ttimestamp:\t"+this.timestamp+"\n";
		s+="\tid:\t\t"+this.id;
		return s;
	}	
}