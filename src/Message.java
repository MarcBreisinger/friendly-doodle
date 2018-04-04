import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "message")
public class Message implements Serializable {

   private static final long serialVersionUID = 1L;
   private String category;
   private String job;
   private String duration;
   private String timestamp;
   private String id;

   public Message(){}

   public Message(String category, String job, String duration, String timestamp, String id){
      this.id = id;
      this.category = category;
      this.duration = duration;
   }

   public String getId() {
      return id;
   }
   @XmlElement
   public void setId(String id) {
      this.id = id;
   }
   public String getCategory() {
      return category;
   }
   @XmlElement
      public void setCategory(String category) {
      this.category = category;
   }
   public String getJob() {
      return job;
   }
   @XmlElement
   public void setJob(String job) {
      this.job = job;
   }	
   public String getDuration() {
	      return duration;
	   }
   @XmlElement
   public void setDuration(String duration) {
      this.duration = duration;
   }	
   public String getTimestamp() {
	      return timestamp;
	   }
   @XmlElement
   public void setTimestamp(String timestamp) {
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
}