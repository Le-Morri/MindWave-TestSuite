package eu.expandable.mindwave;

import eu.expandable.mindwave.GUI.Loading;
import eu.expandable.mindwave.GUI.TestWindow;
import eu.expandable.mindwave.Helper.Config;
import eu.expandable.mindwave.Helper.Graph;
import eu.expandable.mindwave.Helper.I18N;
import eu.expandable.mindwave.Helper.JSONFile;
import eu.expandable.mindwave.Helper.MindWaveException;
import eu.expandable.mindwave.ThinkGear.ThinkGear;
import eu.expandable.mindwave.ThinkGear.ThinkGearEvents;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Andre
 */
public class Test extends Thread implements WindowListener, ThinkGearEvents, ActionListener {
    
    private final TestWindow win;
    private final I18N i18n = I18N.getInstance();
    private final Config conf = Config.getInstance();
    
    private boolean requestExit = false;
    private boolean connected = false;
    private boolean testIsRunning = false;
    
    private String userName = "";
    
    private int step = 0;
    
    private JSONArray data = new JSONArray();
    private JSONArray testplot = null;
    
    private ThinkGear gear;
    private Thread gearThread;
    
    private Timer timer;
    
    public Test(TestWindow _win) throws MindWaveException{
        this.win = _win;
        this.win.startButton.setText(i18n.getString("label_start_button"));
        this.win.setTitle(i18n.getString("test_window_title"));
        this.win.startButton.setEnabled(false);
        this.win.progressbar.setEnabled(false);
        this.win.addWindowListener(this);
        this.win.commandField.setText(this.i18n.getString("waiting"));
        
        this.testplot = JSONFile.createArray("test");
        
        if(this.testplot == null){
            throw new MindWaveException("Could not read test scenario");
        }
        
        this.win.progressbar.setMaximum(this.testplot.length());
        this.timer = new Timer();
    }
    
    
    @Override
    public void run(){
        this.gear = new ThinkGear(this.conf.getString("ThinkGear.Host"), this.conf.getInt("ThinkGear.Port"));
        this.gear.setEventListener(this);
        this.gearThread = new Thread(this.gear);
        this.win.startButton.addActionListener(this);
        
        this.gearThread.start();
        
        while(!this.requestExit) {}
        
        this.gear.requestExit();
        this.win.setVisible(false);
        try {
            Thread.sleep(500);
        }catch(Exception e){}
        System.exit(0);
        
    }
    
    
    @Override
    public void onNewThought(int attention, int meditation, int delta, int theta, int lowAlpha, int highAlpha, int lowBeta, int highBeta, int lowGamma, int highGamma, int poorSignalLevel) {
        //first thought. Wait for signal strength
        if(!this.connected && poorSignalLevel < 30){
            System.out.println("Connection & Quality are good");
            this.connected = true;
            this.win.startButton.setEnabled(true);
            this.win.progressbar.setEnabled(true);
            this.win.commandField.setText(this.i18n.getString("intro"));
        }
        
        if(this.testIsRunning){//save data
            JSONObject o = new JSONObject();
            o.put("attention", attention);
            o.put("meditation", meditation);
            o.put("delta", delta);
            o.put("theta", theta);
            o.put("lowAlpha", lowAlpha);
            o.put("highAlpha", highAlpha);
            o.put("lowBeta", lowBeta);
            o.put("highBeta", highBeta);
            o.put("lowGamma", lowGamma);
            o.put("highGamma", highGamma);
            o.put("poorSignalLevel", poorSignalLevel);
            o.put("time", System.currentTimeMillis());
            
            data.put(o);
        }
        
    }
    

    @Override
    public void windowClosing(WindowEvent e) {
        //user wants to close the window
        this.requestExit = true;
        
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.win.startButton.setEnabled(false);
        
        //get the name of the user
        //and force an input
        String name = "";
        do {
            name = JOptionPane.showInputDialog(null, this.i18n.getString("question_name_question"),
                                                     this.i18n.getString("question_name_title"),
                                                     JOptionPane.QUESTION_MESSAGE);
            if(name != null){
                name = name.trim();
            }
        }while(name == null || name.isEmpty());
        
        this.userName = name;
        this.testIsRunning = true;
        this.nextTestStep();
        
    }
    
    private void nextTestStep(){
        int pause = 0;
        JSONObject test = this.testplot.getJSONObject(this.step);
        test.put("time", System.currentTimeMillis());
        this.data.put(test);
        String type = test.getString("type");
        String label = test.getString("label");
        
        this.win.assetRender.removeAll();
        this.win.assetRender.repaint();
        
        switch (type) {
            case "test":
                this.win.commandField.setText(i18n.getString(label));
                pause = this.conf.getInt("Test.Duration");
                
                String image = test.getString("image");
                
                if(!image.isEmpty()){
                    image = eu.expandable.mindwave.Helper.File.getAbsoluteFilePath(image);
                    try {
                        BufferedImage img = ImageIO.read(new File(image));
                        JPanel pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Dimension size = this.getSize();
                                int x = (size.width - img.getWidth()) / 2;
                                int y = (size.height - img.getHeight()) / 2;
                                g.drawImage(img, x, y, null);
                            }
                        };
                        pane.setSize(this.win.assetRender.getSize());
                        pane.setLocation(0, 0);
                        this.win.assetRender.add(pane);
                        this.win.assetRender.repaint();
                    }catch(Exception e){e.printStackTrace();}
                }
                
                break;
            case "showText":
                this.win.commandField.setText(i18n.getString(label));
                pause = this.conf.getInt("Test.PauseDuration");
                break;
        }
        
        System.out.println("Step Timeout: " + pause);
        
        this.step ++;
        this.win.progressbar.setValue(this.step);
        
        if(this.step == this.testplot.length()){//test finished => save data
            this.testIsRunning = false;
            this.saveData();
            return;
        }
        try {
            this.timer.schedule(new TimerTask(){

                @Override
                public void run() {
                    nextTestStep();
                }

            }, pause);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    private void saveData(){
        try {
            String folder = eu.expandable.mindwave.Helper.File.getAbsoluteFilePath("output");
            File folderFile = new File(folder);
            folderFile.mkdirs();
            
            File jsonFile = new File(folderFile, this.userName + ".json");

            FileWriter file = new FileWriter(jsonFile);
            file.write(data.toString());
            file.flush();
            file.close();
 
	} catch (IOException e) {
            e.printStackTrace();
	}
        
        this.timer.schedule(new TimerTask(){
            @Override
            public void run(){
                win.setVisible(false);
                Loading l = new Loading();
                
                File graph = new File(eu.expandable.mindwave.Helper.File.getAbsoluteFilePath("output/" + userName + ".png"));
                
                Graph g = new Graph();
                g.setData(data);
                g.setFile(graph);
                g.render();
                
                l.setVisible(false);
                requestExit = true;
            }
        }, this.conf.getInt("Test.Duration"));
        
    }
    
    

    //<editor-fold defaultstate="collapsed" desc="Window events we are not interested in">
    @Override
    public void windowOpened(WindowEvent e) {
    }
    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ThinkGear events we are not interested in">
    @Override
    public void onBlink(int strength) {}
    
    @Override
    public void onScan(int poorSignalLevel, String status) {}

    @Override
    public void onMentalEffort(float effort) {}

    @Override
    public void onFamiliarity(float familiarity) {}
    //</editor-fold>

}
