/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.common.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author anil
 */
public final class ShortcutType extends SanchayType implements Serializable {

    public final int ord;
    private static Vector types = new Vector();

    protected ShortcutType(String id, String pk) {
        super(id, pk);

        if (ShortcutType.last() != null) {
            this.prev = ShortcutType.last();
            ShortcutType.last().next = this;
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
        return new TypeEnumerator(ShortcutType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        Enumeration enm = ShortcutType.elements();
        return SanchayType.findFromClassName(enm, className);
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = ShortcutType.elements();
        return SanchayType.findFromId(enm, i);
    }

    public static final ShortcutType FRAMESET_EDITING_INTERFACE = new ShortcutType("Frameset Editing Interface", "sanchay");
}
