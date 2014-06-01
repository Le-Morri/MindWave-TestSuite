package eu.expandable.mindwave.ThinkGear;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;

/**
 *
 * @author Andre
 */
public class Connector {
    
    /**
     * The default port for the think gear connector
     */
    private static final int DEFAULT_PORT = 13854;
    
    /**
     * The default host for the think gear connector
     */
    private static final String DEFAULT_HOST = "127.0.0.1";
    /**
     * the port to be used for connecting
     */
    private int port = 0;
    /**
     * the host to be used for connecting
     */
    private String host = "";
    
    /**
     * a boolean flag to indicate if the conection is established or not
     */
    private boolean connected = false;
    
    private SocketChannel channel;
    private Scanner in;
    
    public Connector(){
        
        //load prefs
        this.port = DEFAULT_PORT;
        this.host = DEFAULT_HOST;
        
    }
    
    public Connector(String host, int port){
        this.host = host;
        this.port = port;
    }
    /**
     * sets the port
     * @param port 
     */
    public void setPort(int port){
        this.port = port;
    }
    /**
     * returns the current port
     * @return 
     */
    public int getPort(){
        return this.port;
    }
    
    /**
     * sets the host
     * @param host 
     */
    public void setHost(String host){
        this.host = host;
    }
    /**
     * returns the current host
     * @return 
     */
    public String getHost(){
        return this.host;
    }
    /**
     * indicates if this client is connected or not
     * @return 
     */
    public boolean isConnected(){
        return this.connected;
    }
    /**
     * Establish a connection to the think gear connector
     * @throws IOException 
     */
    public void connect() throws IOException {

        if (!this.connected) {
            System.out.println("Connecting");
            this.channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));

            CharsetEncoder enc = Charset.forName("US-ASCII").newEncoder();
            String jsonCommand = "{\"enableRawOutput\": false, \"format\": \"Json\"}\n";
            this.channel.write(enc.encode(CharBuffer.wrap(jsonCommand)));

            this.in = new Scanner(channel);
            this.connected = true;
        } else {
            System.out.println("already connected");
        }

    }
    
    /**
     * indicates if there is data to read or not
     * @return 
     */
    public boolean isDataAvailable() {
        if (this.connected) {
            return this.in.hasNextLine();
        } else {
            return false;
        }
    }
    
    /**
     * Returns the Data
     * @return 
     */
    public String getData() {
        return this.in.nextLine();
    }
    
    /**
     * closes the connection to the think gear connector
     * @throws IOException 
     */
    public void close() throws IOException {

        if (this.connected) {
            System.out.println("closing connection");
            this.in.close();
            this.channel.close();
            this.connected = false;
            System.out.println("connection closed");
        }
    }
    
}
