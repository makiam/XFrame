/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import groovy.lang.GroovyShell;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 *
 * @author MaksK
 */
public class App {

    @Override
    public String toString() {
        return "Art Of Illusion";
    }

    private final GroovyShell shell = new GroovyShell();

    public GroovyShell getShell() {
        return shell;
    }
    
    private App() {
        shell.setVariable("app", this);
        
    }
    
    private static final App application = new App();

    public static App getApplication() {
        return application;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        SwingUtilities.invokeLater(() -> {
            new LayoutWindow().setVisible(true);
        });
    }
    
}
