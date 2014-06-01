package eu.expandable.mindwave.ThinkGear;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Andre
 */
public class ThinkGear implements Runnable {
    /**
     * the connector
     */
    private Connector connector;
    
    /**
     * the event listener
     */
    private ThinkGearEvents el;
    
    /**
     * if set true the connection will be closed
     */
    private boolean requestExit = false;
    
    public ThinkGear(){
        this.connector = new Connector();
    }
    
    public ThinkGear(String host, int port){
        this.connector = new Connector(host, port);
    }
    
    public void requestExit(){
        this.requestExit = true;
    }
    public void setEventListener(ThinkGearEvents el){
        this.el = el;
    }
    
    @Override
    public void run() {
        
        try {
            this.connector.connect();
            System.out.println("Connected");
        }catch(IOException e){
            System.err.println("Could not establish connection to ThinkGear Connector at " + this.connector.getHost() + ":" + this.connector.getPort());
        }
        
        
        
        while(!this.requestExit){
            
            if(this.connector.isDataAvailable()){
                
                String data = this.connector.getData();
                try {
                    JSONObject json = new JSONObject(data);
                    
                    //detect which input we have
                    if(json.has("status")){//scanning
                        el.onScan(json.getInt("poorSignalLevel"), json.getString("status"));
                    }else if(json.has("eSense")){//actual data
                        JSONObject eSense = json.getJSONObject("eSense");
                        JSONObject eegPower = json.getJSONObject("eegPower");
                        
                        el.onNewThought(  eSense.getInt("attention"), 
                                            eSense.getInt("meditation"), 
                                            eegPower.getInt("delta"),  
                                            eegPower.getInt("theta"),  
                                            eegPower.getInt("lowAlpha"),  
                                            eegPower.getInt("highAlpha"),  
                                            eegPower.getInt("lowBeta"),  
                                            eegPower.getInt("highBeta"),  
                                            eegPower.getInt("lowGamma"),  
                                            eegPower.getInt("highGamma"),
                                            json.getInt("poorSignalLevel"));
                    }else if(json.has("blinkStrength")){//blink
                        el.onBlink(json.getInt("blinkStrength"));
                    }else if(json.has("mentalEffort")){//metal effort
                        el.onMentalEffort(json.getInt("mentalEffort"));
                    }else if(json.has("familiarity")){//familiarity
                        el.onFamiliarity(json.getInt("familiarity"));
                    }
                    
                    
                }catch(JSONException e){}
                
            }else{
                try {
                    Thread.sleep(200);
                }catch(InterruptedException e){}
            }
            
        }
        
        
        try {
            this.connector.close();
        }catch(IOException e){
            System.err.println("Could not close connection to ThinkGear Connector");
        }
        
        
    }
    
}
