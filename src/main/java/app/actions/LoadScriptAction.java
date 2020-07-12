/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.actions;

import app.ScriptEditor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author MaksK
 */
public class LoadScriptAction extends AbstractAction {

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("Groovy Script", "groovy");
    
    private static final JFileChooser chooser = new JFileChooser();
    static {
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
    }

    public LoadScriptAction() {
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            new ScriptEditor(chooser.getSelectedFile().toPath()).setVisible(true);
        }
    }
    
}
