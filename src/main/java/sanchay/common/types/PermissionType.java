/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import sanchay.GlobalProperties;

/**
 *
 * @author anil
 */
public final class PermissionType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    /** Creates a new instance of PermissionType */
    protected PermissionType(String id, String pk) {
        super(id, pk);

        if (PermissionType.last() != null) {
            this.prev = PermissionType.last();
            PermissionType.last().next = this;
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
        return new TypeEnumerator(PermissionType.first());
    }


    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = PermissionType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = PermissionType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final PermissionType CREATION = new PermissionType("Creation", "sanchay.users");
    public static final PermissionType DELETION = new PermissionType("Deletion", "sanchay.users");
    public static final PermissionType FULL_EDITING = new PermissionType("FullEditing", "sanchay.users");
    public static final PermissionType ADJUDICATION = new PermissionType("Adjudication", "sanchay.users");
    public static final PermissionType LIMITED_EDITING = new PermissionType("LimitedEditing", "sanchay.users");
}
