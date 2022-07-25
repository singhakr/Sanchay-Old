/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay;

import sanchay.common.types.ClientType;
import sanchay.gui.clients.SanchayClient;
import sanchay.gui.common.SanchayEvent;

/**
 *
 * @author anil
 */
public class SanchayMainEvent extends SanchayEvent {

    protected SanchayClient sanchayClient;
    protected ClientType clientType;

    protected Object displayObject;
    protected String file;
    protected String charset;

    public static final int OPEN_TAB = 0;
    public static final int DISPLAY_FILE = 1;

    /** Creates a new instance of TreeViewerEvent */
    public SanchayMainEvent(Object source, int id, SanchayClient sanchayClient) {
        super(source, id);

        this.sanchayClient = sanchayClient;
    }

    public SanchayMainEvent(Object source, int id, ClientType clientType) {
        super(source, id);

        this.clientType = clientType;
    }

    public SanchayMainEvent(Object source, int id, ClientType clientType, String file, String charset) {
        super(source, id);

        this.clientType = clientType;
        this.file = file;
        this.charset = charset;
    }

    public SanchayMainEvent(Object source, int id, ClientType clientType, Object displayObject, String charset) {
        super(source, id);

        this.clientType = clientType;
        this.displayObject = displayObject;
        this.charset = charset;
    }

    public ClientType getClientType()
    {
        return clientType;
    }

    public Object getDisplayObject()
    {
        return displayObject;
    }

    public String getFilePath()
    {
        return file;
    }

    public String getCharset()
    {
        return charset;
    }
}
