package eu.expandable.mindwave.GUI;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * A small loading Frame
 * @author Andre
 */
public class Loading extends JFrame {
    public Loading() {
        super("");
        setLayout(null);

        setSize(500,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setSize(getSize());
        bar.setLocation(0, 0);
        add(bar);
        setVisible(true);
        
    }
}
