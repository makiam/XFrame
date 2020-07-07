/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.xframe.XFrame;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author MaksK
 */
@XFrame.Resource(path = "app/ScriptEditor.xml")
public class ScriptEditor extends XFrame {
    
    private JMenuItem runScript;
    private JMenu editMenu;
    
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;
    
    
    private RSyntaxTextArea textArea;
    
    
    public ScriptEditor() {
        super();
    }
    
    public ScriptEditor(Path path) {
        this(); 
        try {
            textArea.setText(new String(Files.readAllBytes(path)));
        } catch(IOException ioe) {
        }
        
    }
    
    
    @Override
    protected void frameInit() {
        super.frameInit();

        
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        textArea.setCodeFoldingEnabled(true);

        textArea.getDocument().addUndoableEditListener(this::undoListener);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        
        JPanel cp = new JPanel(new BorderLayout());
        cp.add(sp);
        setContentPane(cp);
        
        pack();
        setLocationRelativeTo(null);
        
        runScript.addActionListener(this:: runScriptAction);
        undoMenuItem.addActionListener(this::undoAction);
        undoMenuItem.setEnabled(textArea.canUndo());
        redoMenuItem.addActionListener(this::redoAction);
        redoMenuItem.setEnabled(textArea.canRedo());
        
    }

    private void runScriptAction(ActionEvent event) {
        SwingUtilities.invokeLater(() -> {
            App.getApplication().getShell().evaluate(textArea.getText());
        });
        
    }

    private void undoListener(UndoableEditEvent event) {
        UndoableEdit edit = event.getEdit();
        undoMenuItem.setEnabled(edit.canUndo());
        undoMenuItem.setText(edit.getUndoPresentationName());
        redoMenuItem.setEnabled(edit.canRedo());
        redoMenuItem.setText(edit.getRedoPresentationName());
    }

    private void undoAction(ActionEvent e) {
        textArea.undoLastAction();
    }

    private void redoAction(ActionEvent e) {
        textArea.redoLastAction();
    }
    
    
}
