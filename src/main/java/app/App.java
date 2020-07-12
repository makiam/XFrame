/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.actions.NewViewAction;
import artofillusion.model.Scene;
import groovy.lang.GroovyShell;
import groovy.util.ObservableList;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;


/**
 *
 * @author MaksK
 */
@Log4j
public class App {

    @Override
    public String toString() {
        return "Art Of Illusion";
    }

    private static final CompilerConfiguration cc = new CompilerConfiguration();
    static {
        ImportCustomizer ic = new ImportCustomizer();
        ic.addImport(Scene.class.getSimpleName(), Scene.class.getName());
        cc.getCompilationCustomizers().add(ic);
        
        
    }
    
    @Getter
    private final GroovyShell shell = new GroovyShell(cc);
    
    private App() {
        shell.setVariable("app", this);
        shell.setVariable("log", log);
    }
    
    private static final App application = new App();

    public static App getApplication() {
        return application;
    }
    
    @Getter
    private final List<Scene> scenes = new ObservableList(new ArrayList<>());
    {
        ((ObservableList)scenes).addPropertyChangeListener(this::listPropertyChange);
    }
    
    private void listPropertyChange(PropertyChangeEvent event) {
        log.info(event);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        log.info("Starting...");
        
        new NewViewAction().actionPerformed(null);
        
        App.getApplication().scenes.add(new Scene());
        App.getApplication().scenes.add(new Scene());
    }
    
    
}
