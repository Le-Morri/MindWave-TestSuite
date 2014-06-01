package eu.expandable.mindwave.Helper;

import java.io.FileInputStream;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Andre
 */
public class JSONFile {
    /**
     * Returns a JSONObject for the given file. The file must be locatet in the 
     * <i>config</i> folder. Furthermore this param has no need for the file-
     * extension (.json)
     * @param fileName
     * @return Returns the JSONObject if successfull, otherwilse <i>NULL</i>
     */
    public static JSONObject create(String fileName){
        String absolutePath = File.getAbsoluteFilePath("config/" + fileName + ".json");
        try {
            
            InputStream is = new FileInputStream(absolutePath);
            JSONTokener tokener = new JSONTokener(is);
            return new JSONObject(tokener);
            
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Returns a JSONArray for the given file. The file must be locatet in the 
     * <i>config</i> folder. Furthermore this param has no need for the file-
     * extension (.json)
     * @param fileName
     * @return Returns the JSONArray if successfull, otherwilse <i>NULL</i>
     */
    public static JSONArray createArray(String fileName){
        String absolutePath = File.getAbsoluteFilePath("config/" + fileName + ".json");
        try {
            
            InputStream is = new FileInputStream(absolutePath);
            JSONTokener tokener = new JSONTokener(is);
            return new JSONArray(tokener);
            
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
