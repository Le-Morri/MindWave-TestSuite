package eu.expandable.mindwave.Helper;

/**
 *
 * @author Andre
 */
public class File {
    /**
     * Returns the working directory
     * @return 
     */
    public static String getWorkingDir(){
        return System.getProperty("user.dir");
    }
    /**
     * Returns the absolute file path. Starts in the working direcotry. Each 
     * slash will be replaced by the Systems Seperator
     * @param file
     * @return 
     */
    public static String getAbsoluteFilePath(String file){
        String separator = java.io.File.separator;
        file = file.replace("/", separator);
        return File.getWorkingDir() + separator + file;
    }
    
}
