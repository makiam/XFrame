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
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author MaksK
 */
public class ScriptEditorAction extends AbstractAction {

    public ScriptEditorAction() {
        super("Script Editor");
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control T"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            new ScriptEditor().setVisible(true);
        });
    }

}
