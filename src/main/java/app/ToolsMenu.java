/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.actions.ScriptEditorAction;
import javax.swing.JMenu;

/**
 *
 * @author MaksK
 */
public class ToolsMenu extends JMenu {

    public ToolsMenu() {
        super("Tools");
        this.add(new ScriptEditorAction());
    }
}
