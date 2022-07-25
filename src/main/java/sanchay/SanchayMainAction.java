/*
 * SanchayMainAction.java
 *
 * Created on October 2, 2008, 1:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import sanchay.common.types.ClientType;

/**
 *
 * @author eklavya
 */
public class SanchayMainAction extends AbstractAction implements Action {
    
    SanchayMain mainFrame;
    String code;

//    public static final int LOGIN = 1;
    public static final int CONNECT_REMOTE = 0;
    public static final int DISCONNECT_REMOTE = 1;
    public static final int LOGOUT = 2;
    public static final int _TOTAL_ACTIONS_ = 3;
    
    /** Creates a new instance of SanchayMainAction */
    public SanchayMainAction(SanchayMain mainFrame) {
        super();
        
        this.mainFrame = mainFrame;
    }
    
    public SanchayMainAction(SanchayMain mainFrame, String name) {
        super(name);
        
        this.mainFrame = mainFrame;
    }

    public SanchayMainAction(SanchayMain mainFrame, String name, String code) {
        super(name);

        this.mainFrame = mainFrame;
        this.code = code;
    }
    
    public SanchayMainAction(SanchayMain mainFrame, String name, Icon icon) {
        super(name, icon);
        
        this.mainFrame = mainFrame;
    }

    public SanchayMainAction(SanchayMain mainFrame, String name, String code, Icon icon) {
        super(name, icon);

        this.mainFrame = mainFrame;
        this.code = code;
    }

    public void actionPerformed(ActionEvent e)
    {
        ClientType ctype = (ClientType) ClientType.findFromId(e.getActionCommand());

        if(ctype == null)
            ctype = (ClientType) ClientType.findFromCode(e.getActionCommand());

        mainFrame.createNewApplication(ctype);            
    }    

    public static SanchayMainAction createAction(SanchayMain jframe, int mode)
    {
	SanchayMainAction act = null;

	switch(mode)
	{
	    case CONNECT_REMOTE:
		act = new SanchayMainAction(jframe, GlobalProperties.getIntlString("Connect_Remote")) {
		    public void actionPerformed(ActionEvent e) {
			this.mainFrame.connectRemote(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Connect_to_a_remote_server."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		return act;

	    case DISCONNECT_REMOTE:
		act = new SanchayMainAction(jframe, GlobalProperties.getIntlString("Disconnect_Remote")) {
		    public void actionPerformed(ActionEvent e) {
			this.mainFrame.disconnectRemote(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Disconnect_from_the_remote_server."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		act.setEnabled(false);
		return act;

//	    case LOGIN:
//		act = new SanchayMainAction(jframe, GlobalProperties.getIntlString("Login")) {
//		    public void actionPerformed(ActionEvent e) {
//			this.mainFrame.sanchayLogin(e);
//		    }
//		};
//
//		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Login_to_the_server."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
//		return act;

	    case LOGOUT:
		act = new SanchayMainAction(jframe, GlobalProperties.getIntlString("Logout")) {
		    public void actionPerformed(ActionEvent e) {
			this.mainFrame.sanchayLogout(e);
		    }
		};

		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Logoff_from_the_server."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		act.setEnabled(false);
		return act;
	}

	return act;
    }
}
