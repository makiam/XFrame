/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.xframe.XFrame;
import java.util.Locale;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 *
 * @author MaksK
 */
@XFrame.Resource(path = "app/LayoutWindow.xml")
public class LayoutWindow extends XFrame {
    
    private JMenu toolsMenu;
    private JMenuItem editPreferences;
    
    @Override
    protected void frameInit() {
        super.frameInit();
    }

    @Override
    protected void initLocale() {
        setLocale(new Locale("ru", "RU"));
    }
    
    
    
}
