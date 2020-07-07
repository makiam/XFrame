/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.xframe;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author MaksK
 */
public class XFrame extends JFrame {
    
    private static final Logger logger = Logger.getLogger(XFrame.class.getName());
    
    private static final XStream stream = new XStream();    
    static {
        stream.processAnnotations(XFrame.WindowNode.class);
    }
    
    private Locale locale = Locale.getDefault();
    
    
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public Locale getLocale() {
        return locale;
    }
    
    protected void initLocale() {        
    }
    
    @Override
    protected void frameInit() {
        super.frameInit();
        
        initLocale();
        if(validateResource()) process();
    }
    
    
    private boolean validateResource() {
        Class<?> clazz = this.getClass();
        Optional<Resource> mr = Optional.ofNullable(clazz.getDeclaredAnnotation(Resource.class));
        if(mr.isPresent()) {
            URL resource = clazz.getClassLoader().getResource(clazz.getDeclaredAnnotation(Resource.class).path());
            return resource != null;
        }
        return false;
    }
    
    
    private void process() {
        Class<?> clazz = this.getClass();        
        URL resource = clazz.getClassLoader().getResource(clazz.getDeclaredAnnotation(Resource.class).path());
        WindowNode root = (WindowNode)stream.fromXML(resource);
        
        int height = this.getHeight();
        int width = this.getWidth();
        if(null!= root.icon) this.setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(root.icon)).getImage());
        
        if(null != root.name) this.setTitle(root.name);
        if(null != root.width) width = root.width;
        if(null != root.height) height = root.height;
        
        this.setSize(width, height);
        if(null != root.defaultCloseOperation) { this.setDefaultCloseOperation(root.defaultCloseOperation);}
        
        ResourceBundle bundle = ResourceBundle.getBundle(root.resource, locale);
        
        if( root.getMenu().isPresent()) {
            JMenuBar bar = new JMenuBar();      
            root.menu.stream().peek(item -> { item.bundle = bundle; }).map(this::processMenuNode).forEach( (JMenu item) -> bar.add(item));
            this.setJMenuBar(bar);
        }
        
    }

    private JMenu processMenuNode(MenuNode node) {
        JMenu menuItem = MenuNode.build(node);
        node.bind(this, menuItem);
        if(node.getMenu().isPresent()) {
            node.menu.forEach( item -> { item.bundle = node.bundle; });
            processNodesRecursive(node.menu, menuItem);            
        }
        return menuItem;
    }

    private void processNodesRecursive(List<MenuItemNode> menu, JMenu target) {
        menu.forEach((MenuItemNode node) -> {
            if(node.separator) {
                target.addSeparator();
                return;
            }
            if(node.getMenu().isEmpty()) { // No more nested nodes
                JMenuItem next = MenuItemNode.build(node);
                node.bind(this, next);
                target.add(next);
            } else {
                JMenu next = (JMenu)MenuItemNode.build(node);
                node.bind(this, next);
                target.add(next);
                node.menu.forEach( item -> { item.bundle = node.bundle; });
                processNodesRecursive(node.menu, next);
            }
            
        });
    }

    
    @Target(value = ElementType.TYPE)
    @Retention(value = RetentionPolicy.RUNTIME)
    public static @interface Resource {
        String path();
    }
    
    @XStreamAlias(value = "window")
    private static class WindowNode {
        
        @XStreamAsAttribute
        public String resource;
        
        @XStreamAsAttribute
        public String name;
        
        public Integer height;
        public Integer width;
        
        public Integer defaultCloseOperation;
        
        public String icon;
        
        @XStreamImplicit(itemFieldName = "menu")
        public List<MenuNode> menu;
        
        public Optional<List<MenuNode>> getMenu() {
            return Optional.ofNullable(menu);
        }
    }
    private static class BindableNode {
        
        @XStreamOmitField
        public ResourceBundle bundle;
  
        @XStreamAsAttribute
        public String name;
        
        @XStreamAsAttribute
        public String binding;
        
        public final void bind(Object bindingTarget, Object value) {
            
            if(this.binding == null) return;

            Set<Field> fields = Set.of(bindingTarget.getClass().getDeclaredFields());
            
            Optional<Field> lookup = fields.stream().filter( field -> field.getName().equals(this.binding)).findFirst(); 
            if(lookup.isEmpty()) {
                logger.log(Level.INFO, "Binding field {0} not declared in target class", this.binding);
                return;
            }
            
            Field targetField = lookup.get();
            targetField.setAccessible(true);

            try {
                targetField.set(bindingTarget, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                logger.log(Level.SEVERE, null, ex);
            }   
        }
    }
    
    @XStreamAlias(value = "menu")
    private static class MenuNode extends BindableNode {

        @XStreamAsAttribute
        public String customClass;
        
        @XStreamImplicit(itemFieldName = "menuitem")
        public List<MenuItemNode> menu = new ArrayList<>();
        
        public Optional<List<MenuItemNode>> getMenu() {
            return Optional.ofNullable(menu);
        }
        
        public static JMenu build(MenuNode node) {
            String name = "Undefined";            
            name = node.name == null ? name : node.bundle.containsKey(node.name) ? node.bundle.getString(node.name) : node.name;
            
            String mnemonic = node.name == null ? null : node.bundle.containsKey(node.name + ".mnemonic") ? node.bundle.getString(node.name + ".mnemonic") : null;
            
            JMenu result = null; 
            if(node.customClass == null) {
                result = new JMenu(name);
                if(mnemonic != null) result.setMnemonic(mnemonic.charAt(0));                
                return result;
            }

            Class<?> clazz;
            try {
                clazz = Class.forName(node.customClass);
                result = (JMenu)clazz.getConstructor().newInstance();
            } catch(ClassNotFoundException exx) {
                logger.log(Level.INFO, "Custom Menu class {0} not found", new Object[]{node.customClass});
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                logger.log(Level.INFO, "Custom Menu class {0} cannot instantiate {1}", new Object[]{node.customClass, ex.getMessage()});
            }

            return result;
        }

    }
    
    @XStreamAlias(value = "menu")
    private static class MenuItemNode extends BindableNode {
        private static final Logger logger = Logger.getLogger(MenuItemNode.class.getName());
        
        @XStreamAsAttribute
        public String shortcut;
        
        @XStreamAsAttribute
        public boolean separator;
        
        @XStreamAsAttribute
        public String icon;
        
        @XStreamAsAttribute
        public String actionClass;
        
        @XStreamImplicit(itemFieldName = "menuitem")
        public List<MenuItemNode> menu = new ArrayList<>();
        
        public Optional<List<MenuItemNode>> getMenu() {
            return Optional.ofNullable(menu);
        }
        
        public static JMenuItem build(MenuItemNode node) {
            Optional<Action> action = node.getAction();
            
            ResourceBundle bundle = node.bundle;
            
            String name = "Undefined";            
            name = node.name == null ? name : bundle.containsKey(node.name) ? bundle.getString(node.name) : node.name;
            String iconName = node.icon == null ? null : bundle.containsKey(node.icon) ? bundle.getString(node.icon) : node.icon;
                    
            ImageIcon icon = null;
            if(iconName != null) {
                icon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(iconName));
            }
            String mnemonic = node.name == null ? null : bundle.containsKey(node.name + ".mnemonic") ? bundle.getString(node.name + ".mnemonic") : null;
            String accelerator = node.shortcut == null ? null : bundle.containsKey(node.shortcut) ? bundle.getString(node.shortcut) : null;
            
            JMenuItem result = node.menu == null ? new JMenuItem() : new JMenu();
            if(action.isPresent()) result.setAction(action.get());
            result.setText(name);
            result.setIcon(icon);
            if(mnemonic != null) result.setMnemonic(mnemonic.charAt(0));  
            if(accelerator != null) result.setAccelerator(KeyStroke.getKeyStroke(accelerator));
            
            
            return result;
        }
        
        private Optional<Action> getAction() {
            if (this.actionClass == null) {
                return Optional.empty();
            }
            try {
                return Optional.of((Action) Class.forName(this.actionClass).getConstructor().newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            return Optional.empty();
        }
    }
}
