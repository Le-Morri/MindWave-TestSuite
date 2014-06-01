package eu.expandable.mindwave.Helper;

import org.json.JSONObject;

/**
 * The config class.
 * <p> Use <code>Config.getInstance();</code> instead of the constructor.
 * @author Andre
 */
public class Config {
    /**
     * a private instance of this class
     */
    private static Config instance = null;
    
    /**
     * the actual config object
     */
    private JSONObject config;
    
    /**
     * Returns the instance of this class
     * @return 
     */
    public static Config getInstance(){
        return Config.instance;
    }
    
    
    public Config() throws MindWaveException {
        //check if there is already a instance
        if(Config.instance != null){
            //only one instance is allowed
            throw new MindWaveException ("Use Config.getInstance(); instead of the constructor");
        }
        
        //read config
        this.config = JSONFile.create("config");
        
        //check if there was an error
        if(this.config == null){
            throw new MindWaveException ("Error reading config.json file");
        }
        
    }
    
    /**
     * Get the int value associated with a key.
     * @param key A key string.
     * @return The integer value. If the Value is not found it will return <i>0</i>
     */
    public int getInt(String key){
        if(this.config.has(key))//key exists
            return this.config.getInt(key);
        
        return 0;
    }
    /**
     * Get the String value associated with a key.
     * @param key A key string.
     * @return The String value. If the Value is not found it will return <i>NULL</i>
     */
    public String getString(String key){
        if(this.config.has(key))//key exists
            return this.config.getString(key);
        
        return null;
    }
    /**
     * Get the Object value associated with a key.
     * @param key A key string.
     * @return The Object value. If the Value is not found it will return <i>NULL</i>
     */
    public Object get(String key){
        if(this.config.has(key))//key exists
            return this.config.get(key);
        
        return null;
    }
    
    /**
     * initializing the class
     */
    static {
        try {
            Config.instance = new Config();
        } catch (MindWaveException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
}
