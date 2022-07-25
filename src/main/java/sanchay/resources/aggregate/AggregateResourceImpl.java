/*
 * SanchayTask.java
 *
 * Created on November 1, 2005, 5:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.resources.aggregate;


import sanchay.resources.*;
import sanchay.properties.*;

/**
 *
 *  @author Anil Kumar Singh
 * Tasks will be used locally. If some specific has to shared by users
 * then it will have a corresponding remote object (server).
 */
public class AggregateResourceImpl extends ResourceImpl implements AggregateResource {

    java.util.ResourceBundle bundle = sanchay.GlobalProperties.getResourceBundle(); // NOI18N

    protected SanchayProperties taskProps;
    
    /** Creates a new instance of AggregateResourceImpl */
    public AggregateResourceImpl() {
	super();
    }

    public AggregateResourceImpl(String fp, String cs) {
	filePath = fp;
	charset = cs;
    }

    public AggregateResourceImpl(String fp, String cs, String langEnc, String nm) {
        filePath = fp;
        charset = cs;

        this.langEnc = langEnc;
        name = nm;
    }

    public SanchayProperties getProperties()
    {
	return taskProps;
    }
    
    public void setProperties(SanchayProperties tp)
    {
	taskProps = tp;
    }
}
