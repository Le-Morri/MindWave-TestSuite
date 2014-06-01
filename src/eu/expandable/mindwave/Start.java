package eu.expandable.mindwave;

import eu.expandable.mindwave.GUI.TestWindow;
import eu.expandable.mindwave.Helper.MindWaveException;


/**
 *
 * @author Andre
 */
public class Start {
    
    /**
     * Set the Nimbus look and feel
     */
    public void setLookAndFeel(){
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }
    
    public void createTest(){
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            TestWindow win = new TestWindow();
            win.setLocation(50, 50);
            win.setVisible(true);
            Test t = null;
            try {
                t = new Test(win);
            }catch(MindWaveException e){
                e.printStackTrace();
                System.exit(-1);
            }
            t.start();
        });
        
        
        
    }
    
    /**
     * The main function
     * @param args 
     */
    public static void main(String[] args){
       Start s = new Start();
       s.setLookAndFeel();
       s.createTest();
    }
}
