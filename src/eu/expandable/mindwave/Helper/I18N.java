package eu.expandable.mindwave.Helper;

import org.json.JSONObject;


/**
 * The i18n class.
 * <p> Use <code>Config.getInstance();</code> instead of the constructor.
 * @author Andre
 */
public class I18N {
    /**
     * a private instance of this class
     */
    private static I18N instance = null;
    
    /**
     * the actual config object
     */
    private JSONObject config;
    
    /**
     * Returns the instance of this class
     * @return 
     */
    public static I18N getInstance(){
        return I18N.instance;
    }
    
    
    public I18N() throws MindWaveException {
        //check if there is already a instance
        if(I18N.instance != null){
            //only one instance is allowed
            throw new MindWaveException ("Use I18N.getInstance(); instead of the constructor");
        }
        
        //read config
        this.config = JSONFile.create("locale_de");
        
        //check if there was an error
        if(this.config == null){
            throw new MindWaveException ("Error reading locale_de.json file");
        }
        
    }
    
    /**
     * Get the String value associated with a key.
     * @param key A key string.
     * @return The String value. If the Value is not found it will return <i>(empty)</i>
     */
    public String getString(String key){
        if(this.config.has(key))//key exists
            return this.config.getString(key);
        
        return "";
    }
    /**
     * initializing the class
     */
    static {
        try {
            I18N.instance = new I18N();
        } catch (MindWaveException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
}
