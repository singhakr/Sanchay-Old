/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author User
 */
public final class DBDriverType extends SanchayType implements Serializable {

    public final int ord;
    
    private static final ArrayList types = new ArrayList();
    
    public final String DRIVER;
    public final String PROTOCOL;

    protected DBDriverType(String id, String drvr, String prtcl, String pk) {
        super(id, pk);
        
        DRIVER = drvr;
        PROTOCOL = prtcl;

        if (DBDriverType.last() != null) {
            this.prev = DBDriverType.last();
            DBDriverType.last().next = this;
        }

        types.add(this);
	ord = types.size();
    }

    public static int size()
    {
        return types.size();
    }
    
    public static SanchayType first()
    {
        return (SanchayType) types.get(0);
    }
    
    public static SanchayType last()
    {
        if(types.size() > 0)
            return (SanchayType) types.get(types.size() - 1);
        
        return null;
    }

    public static SanchayType getType(int i)
    {
        if(i >=0 && i < types.size())
            return (SanchayType) types.get(i);
        
        return null;
    }

    public static Enumeration elements()
    {
        return new TypeEnumerator(DBDriverType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = DBDriverType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = DBDriverType.elements();
        return SanchayType.findFromId(enm, i);
    }
    
    public static SanchayType findFromDriver(Enumeration enm, String drv)
    {
        DBDriverType dt = null;

        while(enm.hasMoreElements())
        {
            dt = (DBDriverType) enm.nextElement();

            if(drv.equals(dt.DRIVER.toString()))
                return dt;
        }

        return null;
    }
    
    @Override
    public String toString()
    {
        return PROTOCOL;
    }

    public static final DBDriverType JDBC_DERBY = new DBDriverType("JDBC_Derby", "org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby", "sanchay.common.types");
    public static final DBDriverType JDBS_SQLITE = new DBDriverType("JDBC_SQLite", "org.sqlite.JDBC", "jdbc:sqlite", "sanchay.common.types");
}
