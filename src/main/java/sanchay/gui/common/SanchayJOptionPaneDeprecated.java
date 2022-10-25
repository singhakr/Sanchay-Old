/*
 * SanchayJOptionPaneDeprecated.java
 *
 * Created on October 19, 2008, 11:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.gui.common;

import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import sanchay.util.UtilityFunctions;

/**
 *
 * @author Anil Kumar Singh
 */
public class SanchayJOptionPaneDeprecated
{           
    private static final String uiClassID = "SanchayOptionPaneUI";
        
    public static String showInputDialog(Object message, String langEnc) 
        throws HeadlessException { 
        return showInputDialog(null, message, langEnc);
    }

    public static String showInputDialog(Object message, Object initialSelectionValue, String langEnc) {
        return showInputDialog(null, message, initialSelectionValue, langEnc);
    }

    public static String showInputDialog(Component parentComponent,
        Object message, String langEnc) throws HeadlessException { 
        return showInputDialog(parentComponent, message, getUIString(
            "OptionPane.inputDialogTitle", parentComponent), JOptionPane.QUESTION_MESSAGE, langEnc);
    }

    public static String getUIString(Object key, Component c) { 
        Locale l = (c == null) ? Locale.getDefault() : c.getLocale();
        return UIManager.getString(key, l);
    }

    public static String showInputDialog(Component parentComponent, Object message, 
					 Object initialSelectionValue, String langEnc) {
        return (String)showInputDialog(parentComponent, message,
                      getUIString("OptionPane.inputDialogTitle",
                      parentComponent), JOptionPane.QUESTION_MESSAGE, null, null,
                      initialSelectionValue, langEnc);
    }

    public static String showInputDialog(Component parentComponent,
        Object message, String title, int messageType, String langEnc) 
        throws HeadlessException {
        return (String)showInputDialog(parentComponent, message, title,
                                       messageType, null, null, null, langEnc);
    }

    public static Object showInputDialog(Component parentComponent,
        Object message, String title, int messageType, Icon icon,
        Object[] selectionValues, Object initialSelectionValue, String langEnc) 
        throws HeadlessException {
        JOptionPane pane = new JOptionPane(message, messageType,
                                              JOptionPane.OK_CANCEL_OPTION, icon,
                                              null, null);
        
        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        pane.setComponentOrientation(((parentComponent == null) ?
	    getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = SanchayJOptionPaneDeprecated.createDialog(parentComponent, title, style, pane);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object value = pane.getInputValue();

        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    public static void showMessageDialog(Component parentComponent,
        Object message, String langEnc) throws HeadlessException {
        showMessageDialog(parentComponent, message, getUIString(
                    "OptionPane.messageDialogTitle", parentComponent),
                    JOptionPane.INFORMATION_MESSAGE, langEnc);
    }

    public static void showMessageDialog(Component parentComponent,
        Object message, String title, int messageType, String langEnc) 
        throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null, langEnc);
    }

    public static void showMessageDialog(Component parentComponent,
        Object message, String title, int messageType, Icon icon, String langEnc) 
        throws HeadlessException {
        showOptionDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION, 
                         messageType, icon, null, null, langEnc);
    }

    public static int showConfirmDialog(Component parentComponent,
        Object message, String langEnc) throws HeadlessException {
        return showConfirmDialog(parentComponent, message,
                                 UIManager.getString("OptionPane.titleText"),
                                 JOptionPane.YES_NO_CANCEL_OPTION, langEnc);
    }

    public static int showConfirmDialog(Component parentComponent,
        Object message, String title, int optionType, String langEnc) 
        throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType,
                                 JOptionPane.QUESTION_MESSAGE, langEnc);
    }

    public static int showConfirmDialog(Component parentComponent,
        Object message, String title, int optionType, int messageType, String langEnc) 
        throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType,
                                messageType, null, langEnc);
    }

    public static int showConfirmDialog(Component parentComponent,
        Object message, String title, int optionType,
        int messageType, Icon icon, String langEnc) throws HeadlessException {
        return showOptionDialog(parentComponent, message, title, optionType,
                                messageType, icon, null, null, langEnc);
    }

    public static int showOptionDialog(Component parentComponent,
        Object message, String title, int optionType, int messageType,
        Icon icon, Object[] options, Object initialValue, String langEnc) 
        throws HeadlessException {
        JOptionPane pane = new JOptionPane(message, messageType,
                                                       optionType, icon,
                                                       options, initialValue);
        
        UtilityFunctions.setJOptionPaneFont(pane, langEnc);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
	    getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = SanchayJOptionPaneDeprecated.createDialog(parentComponent, title, style, pane);

        pane.selectInitialValue();
        dialog.show();
        dialog.dispose();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null)
            return JOptionPane.CLOSED_OPTION;
        if(options == null) {
            if(selectedValue instanceof Integer)
                return ((Integer)selectedValue).intValue();
            return JOptionPane.CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue))
                return counter;
        }
        return JOptionPane.CLOSED_OPTION;
    }
   
    private static JDialog createDialog(Component parentComponent, String title,
            int style, JOptionPane pane)
            throws HeadlessException {

        final JDialog dialog;

        Window window = SanchayJOptionPaneDeprecated.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame)window, title, true);	
        } else {
            dialog = new JDialog((Dialog)window, title, true);
        }
// 	if (window instanceof SwingUtilities.SharedOwnerFrame) {
//	    WindowListener ownerShutdownListener =
//		(WindowListener)SwingUtilities.getSharedOwnerFrameShutdownListener();
// 	    dialog.addWindowListener(ownerShutdownListener);
// 	}
        SanchayJOptionPaneDeprecated.initDialog(dialog, style, parentComponent, pane);
        return dialog;
    }

    private static void initDialog(final JDialog dialog, int style, Component parentComponent, JOptionPane pane) {
        dialog.setComponentOrientation(pane.getComponentOrientation());
        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(pane, BorderLayout.CENTER);
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations =
              UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.setUndecorated(true);
                pane.getRootPane().setWindowDecorationStyle(style);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        
        OptionPaneWindowAdapter adapter = new OptionPaneWindowAdapter(pane);
        
        dialog.addWindowListener(adapter);
        dialog.addWindowFocusListener(adapter);
        
        OptionPaneComponentAdapter optionPaneComponentAdapter = new OptionPaneComponentAdapter(pane);

//        pane.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent event) {
//                // Let the defaultCloseOperation handle the closing
//                // if the user closed the window without selecting a button
//                // (newValue = null in that case).  Otherwise, close the dialog.
//                if (dialog.isVisible() && event.getSource() == JOptionPane.this &&
//                  (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) &&
//                  event.getNewValue() != null &&
//                  event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
//                    dialog.setVisible(false);
//                }
//            }
//        });
    }

    public static void showInternalMessageDialog(Component parentComponent,
                                                 Object message, String langEnc) {
        showInternalMessageDialog(parentComponent, message, getUIString("OptionPane.messageDialogTitle",
                                 parentComponent), JOptionPane.INFORMATION_MESSAGE, langEnc);
    }

    public static void showInternalMessageDialog(Component parentComponent,
                                                 Object message, String title,
                                                 int messageType, String langEnc) {
        showInternalMessageDialog(parentComponent, message, title, messageType, null, langEnc);
    }

    public static void showInternalMessageDialog(Component parentComponent,
                                         Object message,
                                         String title, int messageType,
                                         Icon icon, String langEnc){
        showInternalOptionDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION,
                                 messageType, icon, null, null, langEnc);
    }

    public static int showInternalConfirmDialog(Component parentComponent,
                                                Object message, String langEnc) {
        return showInternalConfirmDialog(parentComponent, message,
                                 UIManager.getString("OptionPane.titleText"),
                                 JOptionPane.YES_NO_CANCEL_OPTION, langEnc);
    }

    public static int showInternalConfirmDialog(Component parentComponent,
                                                Object message, String title,
                                                int optionType, String langEnc) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType,
                                         JOptionPane.QUESTION_MESSAGE, langEnc);
    }

    public static int showInternalConfirmDialog(Component parentComponent, 
                                        Object message,
                                        String title, int optionType,
                                        int messageType, String langEnc) {
        return showInternalConfirmDialog(parentComponent, message, title, optionType,
                                         messageType, null, langEnc);
    }

    public static int showInternalConfirmDialog(Component parentComponent,
                                        Object message,
                                        String title, int optionType,
                                        int messageType, Icon icon, String langEnc) {
        return showInternalOptionDialog(parentComponent, message, title, optionType,
                                        messageType, icon, null, null, langEnc);
    }

    public static int showInternalOptionDialog(Component parentComponent,
                                       Object message,
                                       String title, int optionType,
                                       int messageType, Icon icon,
                                       Object[] options, Object initialValue, String langEnc) {
        JOptionPane pane = new JOptionPane(message, messageType,
                optionType, icon, options, initialValue);
//        pane.putClientProperty(PopupFactory.forceHeavyWeightPopupKey,
//                Boolean.TRUE);
        
        UtilityFunctions.setJOptionPaneFont(pane, langEnc);
        
        Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getFocusOwner();

        pane.setInitialValue(initialValue);

        JInternalFrame dialog =
            pane.createInternalFrame(parentComponent, title);
        pane.selectInitialValue();
        dialog.setVisible(true);

	/* Since all input will be blocked until this dialog is dismissed,
	 * make sure its parent containers are visible first (this component
	 * is tested below).  This is necessary for JApplets, because
	 * because an applet normally isn't made visible until after its
	 * start() method returns -- if this method is called from start(),
	 * the applet will appear to hang while an invisible modal frame
	 * waits for input.
	 */
	if (dialog.isVisible() && !dialog.isShowing()) {
	    Container parent = dialog.getParent();
	    while (parent != null) {
		if (parent.isVisible() == false) {
		    parent.setVisible(true);
		}
		parent = parent.getParent();
	    }
	}

        // Use reflection to get Container.startLWModal.
        try {
            Object obj;
            obj = AccessController.doPrivileged(new ModalPrivilegedAction(
                    Container.class, "startLWModal"));
            if (obj != null) {
                ((Method)obj).invoke(dialog, (Object[])null);
            }
        } catch (IllegalAccessException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        }

        if (parentComponent instanceof JInternalFrame) {
            try {
                ((JInternalFrame)parentComponent).setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
            }
        }

        Object selectedValue = pane.getValue();

        if (fo != null && fo.isShowing()) {
            fo.requestFocus();
        }
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (options == null) {
            if (selectedValue instanceof Integer) {
                return ((Integer)selectedValue).intValue();
            }
            return JOptionPane.CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

    public static String showInternalInputDialog(Component parentComponent,
                                                 Object message, String langEnc) {
        return showInternalInputDialog(parentComponent, message, getUIString("OptionPane.inputDialogTitle", parentComponent),
               JOptionPane.QUESTION_MESSAGE, langEnc);
    }

    public static String showInternalInputDialog(Component parentComponent,
                             Object message, String title, int messageType, String langEnc) {
        return (String)showInternalInputDialog(parentComponent, message, title,
                                       messageType, null, null, null, langEnc);
    }

    public static Object showInternalInputDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon,
            Object[] selectionValues, Object initialSelectionValue, String langEnc) {
        JOptionPane pane = new JOptionPane(message, messageType,
                JOptionPane.OK_CANCEL_OPTION, icon, null, null);
//        pane.putClientProperty(PopupFactory.forceHeavyWeightPopupKey,
//                Boolean.TRUE);
        
        UtilityFunctions.setJOptionPaneFont(pane, langEnc);
        
        Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getFocusOwner();

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);

        JInternalFrame dialog =
            pane.createInternalFrame(parentComponent, title);

        pane.selectInitialValue();
        dialog.setVisible(true);

	/* Since all input will be blocked until this dialog is dismissed,
	 * make sure its parent containers are visible first (this component
	 * is tested below).  This is necessary for JApplets, because
	 * because an applet normally isn't made visible until after its
	 * start() method returns -- if this method is called from start(),
	 * the applet will appear to hang while an invisible modal frame
	 * waits for input.
	 */
	if (dialog.isVisible() && !dialog.isShowing()) {
	    Container parent = dialog.getParent();
	    while (parent != null) {
		if (parent.isVisible() == false) {
		    parent.setVisible(true);
		}
		parent = parent.getParent();
	    }
	}

        // Use reflection to get Container.startLWModal.
        try {
            Object obj;
            obj = AccessController.doPrivileged(new ModalPrivilegedAction(
                    Container.class, "startLWModal"));
            if (obj != null) {
                ((Method)obj).invoke(dialog, (Object[])null);
            }
        } catch (IllegalAccessException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (InvocationTargetException ex) {
        }

        if (parentComponent instanceof JInternalFrame) {
            try {
                ((JInternalFrame)parentComponent).setSelected(true);
            } catch (java.beans.PropertyVetoException e) {
            }
        }

        if (fo != null && fo.isShowing()) {
            fo.requestFocus();
        }
        Object value = pane.getInputValue();

        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    public static Frame getFrameForComponent(Component parentComponent) 
        throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame)
            return (Frame)parentComponent;
        return SanchayJOptionPane.getFrameForComponent(parentComponent.getParent());
    }

    public static Window getWindowForComponent(Component parentComponent) 
        throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return SanchayJOptionPaneDeprecated.getWindowForComponent(parentComponent.getParent());
    }

    public static JDesktopPane getDesktopPaneForComponent(Component parentComponent) {
        if(parentComponent == null)
            return null;
        if(parentComponent instanceof JDesktopPane)
            return (JDesktopPane)parentComponent;
        return getDesktopPaneForComponent(parentComponent.getParent());
    }

    private static final Object sharedFrameKey = JOptionPane.class;

    public static void setRootFrame(Frame newRootFrame) {
        JOptionPane.setRootFrame(newRootFrame);
//        if (newRootFrame != null) {
//            SwingUtilities.appContextPut(sharedFrameKey, newRootFrame);
//        } else {
//            SwingUtilities.appContextRemove(sharedFrameKey);
//        }
    }

    public static Frame getRootFrame() throws HeadlessException {
        return JOptionPane.getRootFrame();
//        Frame sharedFrame = 
//            (Frame)SwingUtilities.appContextGet(sharedFrameKey);
//        if (sharedFrame == null) {
//            sharedFrame = SwingUtilities.getSharedOwnerFrame();
//            SwingUtilities.appContextPut(sharedFrameKey, sharedFrame);
//        }
//        return sharedFrame;
    }
    
    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
        case JOptionPane.ERROR_MESSAGE:
            return JRootPane.ERROR_DIALOG;
        case JOptionPane.QUESTION_MESSAGE:
            return JRootPane.QUESTION_DIALOG;
        case JOptionPane.WARNING_MESSAGE:
            return JRootPane.WARNING_DIALOG;
        case JOptionPane.INFORMATION_MESSAGE:
            return JRootPane.INFORMATION_DIALOG;
        case JOptionPane.PLAIN_MESSAGE:
        default:
            return JRootPane.PLAIN_DIALOG;
        }
    }    

    private static class ModalPrivilegedAction implements PrivilegedAction {
        private Class clazz;
        private String methodName;

        public ModalPrivilegedAction(Class clazz, String methodName) {
            this.clazz = clazz;
            this.methodName = methodName;
        }

        public Object run() {
            Method method = null;
            try {
                method = clazz.getDeclaredMethod(methodName, (Class[])null);
            } catch (NoSuchMethodException ex) {
            }
            if (method != null) {
                method.setAccessible(true);
            }
            return method;
        }
    }
}
    
class OptionPaneWindowAdapter extends WindowAdapter
    implements WindowListener, WindowStateListener, WindowFocusListener
{
    private boolean gotFocus = false;
    private JOptionPane pane;

    public OptionPaneWindowAdapter(JOptionPane pane)
    {
        this.pane = pane;                
    }

    public void windowClosing(WindowEvent we) {
        pane.setValue(null);
    }

    public void windowGainedFocus(WindowEvent we) {
        // Once window gets focus, set initial focus
        if (!gotFocus) {
            pane.selectInitialValue();
            gotFocus = true;
        }
    }
};

class OptionPaneComponentAdapter {
    private JOptionPane pane;

    public OptionPaneComponentAdapter(JOptionPane pane)
    {
        this.pane = pane;                
    }

    public void componentShown(ComponentEvent ce) {
        // reset value to ensure closing works properly
        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
    }
};