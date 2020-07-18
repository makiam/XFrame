/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.xframe.XFrame;
import groovy.lang.GroovyShell;
import groovy.lang.Script;


import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import lombok.extern.log4j.Log4j;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author MaksK
 */
@Log4j
@XFrame.Resource(path = "app/ScriptEditor.xml")
public class ScriptEditor extends XFrame {
    
    private JMenuItem runScript;
    
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;
    
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;
    private JMenuItem cutMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem deleteMenuItem;
    
    private JMenuItem selectAllMenuItem;
    
    
    private RSyntaxTextArea textArea;
    private TextArea output;
    
    
    private String backup = "";
    private Path path;
    
    public ScriptEditor() {
        super();
    }
    
    public ScriptEditor(Path path) {
        this();         
        try {
            this.path = path;
            backup = new String(Files.readAllBytes(path));
            textArea.setText(backup);
            pack();
            this.setTitle("Script Editor: " + path.getFileName());
            
        } catch(IOException ioe) {
        }
        
    }
    
    void close(WindowEvent event) {
        log.info("Script Editor Is about to be closed");

        if(textArea.getText().equals(backup)) {
            this.dispose();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Script is modified. Close window?");
        if(confirm != JOptionPane.YES_OPTION) return;
        this.dispose();
    }
    
    
    @Override
    protected void frameInit() {
        super.frameInit();

        WindowAdapter wa = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                ScriptEditor.this.close(event);
            }            
        };
        this.addWindowListener(wa);
        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        textArea.setCodeFoldingEnabled(true);
 
        RTextScrollPane sp = new RTextScrollPane(textArea);
        

        
        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, output = new TextArea());
        setContentPane(jsp);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pack();
        setLocationRelativeTo(null);
         
        runScript.addActionListener(this:: runScriptAction);
        undoMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.UNDO_ACTION));
        redoMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.REDO_ACTION));
        cutMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.CUT_ACTION));
        copyMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.COPY_ACTION));
        pasteMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.PASTE_ACTION));
        deleteMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.DELETE_ACTION));
        selectAllMenuItem.setAction(RSyntaxTextArea.getAction(RSyntaxTextArea.SELECT_ALL_ACTION));
        
        
    }

    private void runScriptAction(ActionEvent event) {
        
        SwingUtilities.invokeLater(() -> {
            output.setText("");
            GroovyShell shell = App.getApplication().getShell();
            Script script = null;
            try {
                script = shell.parse(textArea.getText());
            } catch(MultipleCompilationErrorsException mcee) {
                mcee.getErrorCollector().getErrors().forEach(message -> {
                    if(message instanceof SyntaxErrorMessage) {
                        SyntaxErrorMessage sm = (SyntaxErrorMessage)message;
                        output.append("\n" + sm.getCause().getMessage());
                    }
                });
                return;
            } catch(CompilationFailedException ex) {
                log.error("Script syntax error", ex);
                return;
            }
            
            script.setProperty("out", new ConsolePrintWriter(output));            
            script.run();
            output.append("\nDone");
            script.setProperty("out", null);
        });
        
    }
    
    private class ConsolePrintWriter {

        private final TextArea output;
        
        public ConsolePrintWriter(TextArea output) {
            this.output = output;
        }
        
        public void println() {
            this.output.append("\n");
        }
        
        public void print(Object value) {
            output.append(value.toString());
        }
        
        public void println(Object value) {
            output.append(value.toString()+"\n");
        }
    }
    
    
    
}
