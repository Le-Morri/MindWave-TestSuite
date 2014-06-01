package eu.expandable.mindwave.Helper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Andre
 */
public class Graph {
    private Config conf = Config.getInstance();
    private JSONArray data;
    private java.io.File file;
    
    private int width = 0;
    private int height = 0;
    private long rootTime = 0;
    private final int legendWidth = 150;
    
    private int margin = 0;
    
    private int scale = 0;
    
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final Color GRID_COLOR = Color.black;
    private static final Color TEXT_COLOR = Color.black;
    private static final Color SECTION_COLOR = Color.gray;
    
    private static final Color ATTENTION = Color.red;
    private static final Color MEDITATION = Color.blue;
    private static final Color DELTA = Color.green;
    private static final Color THETA = Color.cyan;
    private static final Color LOW_ALPHA = Color.MAGENTA;
    private static final Color HIGH_ALPHA = Color.PINK;
    private static final Color LOW_BETA = Color.yellow;
    private static final Color HIGH_BETA = Color.orange;
    private static final Color LOW_GAMMA = Color.lightGray;
    private static final Color HIGH_GAMMA = Color.darkGray;
    
    public Graph(){
    
        this.margin = this.conf.getInt("Graph.Margin");
        this.scale = this.conf.getInt("Graph.Scale");
    }
    
    public void setData(JSONArray _data){
        this.data = _data;
    }
    public void setFile(java.io.File _file){
        this.file = _file;
    }
    
    
    public void render(){
        this.deriveSize();
        //create image
        BufferedImage img = new BufferedImage(width + legendWidth, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        //draw background
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, width + legendWidth, height);
        //highlight sections
        this.implementSections(g2);
        //draw grid
        this.drawGrid(g2);
        this.drawLine(g2, "attention", ATTENTION);
        this.drawLine(g2, "meditation", MEDITATION);
        this.drawLine(g2, "delta", DELTA);
        this.drawLine(g2, "theta", THETA);
        this.drawLine(g2, "lowAlpha", LOW_ALPHA);
        this.drawLine(g2, "highAlpha", HIGH_ALPHA);
        this.drawLine(g2, "lowBeta", LOW_BETA);
        this.drawLine(g2, "highBeta", HIGH_BETA);
        this.drawLine(g2, "lowGamma", LOW_GAMMA);
        this.drawLine(g2, "highGamma", HIGH_GAMMA);
        //draw legend
        this.drawLegend(g2);
        //save image
        try {
            ImageIO.write(img, "png", file);
            //now open the file
            Desktop.getDesktop().open(file);
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    private void deriveSize(){
        //add margin
        width = height = margin * 2;
        
        JSONObject root = this.data.getJSONObject(0);
        JSONObject last = this.data.getJSONObject(this.data.length() - 1);
        
        
        this.rootTime = root.getLong("time");
        long lastTime = last.getLong("time");
        
        long diff = lastTime - this.rootTime;
        
        width += diff / scale;
        height += this.conf.getInt("Graph.Height");
        
    }
    
    private void drawGrid(Graphics2D g){
        g.setColor(GRID_COLOR);
        g.setStroke(new BasicStroke(2));
        //x axis
        g.drawLine(margin, height - margin, width - margin, height - margin);
        //y axis
        g.drawLine(margin, margin, margin, height - margin);
    }
    
    
    private void implementSections(Graphics2D g){
        long lastTime = this.rootTime;
        boolean skip = false;
        for(int i = 0; i < this.data.length(); i++){
            JSONObject o = this.data.getJSONObject(i);
            if(o.has("type")){
                
                if(!skip){
                    
                    long t = o.getLong("time");
                    g.setColor(SECTION_COLOR);
                    int from = (int)((lastTime - rootTime) / scale) + margin;
                    int to = (int)((t - rootTime) / scale) + margin;
                    
                    g.fillRect(from, margin, to-from, height - margin* 2);
                }
                
                lastTime = o.getLong("time");
                skip = !skip;
            }
        }
    }
    
    private void drawLine(Graphics2D g, String key, Color c){
        g.setColor(c);
        g.setStroke(new BasicStroke(2));
        
        int x = 0;
        int y = 0;
        
        int lastX = 0;
        int lastY = 0;
        
        long min = 0;
        long max = 0;
        
        List<Point> points = new ArrayList<>();
        
        for(int i = 0; i < this.data.length(); i++){
            
            JSONObject o = this.data.getJSONObject(i);
            
            if(o.has(key)){
                Point p = new Point();
                
                p.time = o.getLong("time");
                p.value = o.getLong(key);
                p.x = (int)((p.time - rootTime) / scale) + margin;
                
                if(p.value < min) min = p.value;
                if(p.value > max) max = p.value;
                
                points.add(p);
            }
            
        }
        
        int space = this.conf.getInt("Graph.Height");
        
        for(int i = 0; i < points.size(); i++){
            
            Point p = points.get(i);
            x = p.x;
            
            long v = p.value;
            if(v == 0) v = 1;
            float pos = max / v;
            y = (int) (height - margin - (space / pos));
            
            
            if(lastX > 0){
                g.drawLine(x,y,lastX,lastY);
            }
            
            lastX = x;
            lastY = y;
        }
        
        
    }
    
    private void drawLegend(Graphics2D g){
        
        int x = width;
        int text_x = width + 30;
        
        int y = margin;
        int row_height = 20;
        int row_spacing = 10;
        
        g.setColor(ATTENTION);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("Attention", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(MEDITATION);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("Meditation", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(DELTA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("delta", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(THETA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("theta", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(LOW_ALPHA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("lowAlpha", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(HIGH_ALPHA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("highAlpha", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(LOW_BETA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("lowBeta", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(HIGH_BETA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("highBeta", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(LOW_GAMMA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("lowGamma", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        g.setColor(HIGH_GAMMA);
        g.fillRect(x, y, 20, row_height);
        g.setColor(TEXT_COLOR);
        g.drawString("highGamma", text_x, y + row_height - (row_height / 4));
        y += row_height + row_spacing;
        
        
        //label the axis
        g.setColor(TEXT_COLOR);
        g.drawString("0%", 5, height - margin);
        g.drawString("100%", 5, margin + 10);
        g.drawString("t", width - margin, height - margin + 10);
    }
    
    private class Point{
        public long time;
        public long value;
        public int x;
    }
    
}
