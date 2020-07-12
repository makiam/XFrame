/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.actions;

import app.LayoutWindow;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author MaksK
 */
public class NewViewAction extends AbstractAction {

    public NewViewAction() {
        super("New View");
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            new LayoutWindow().setVisible(true);
        });
    }
    
}
