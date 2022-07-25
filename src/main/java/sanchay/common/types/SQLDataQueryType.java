/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.common.types;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author User
 */
public class SQLDataQueryType extends SanchayType implements Serializable {
    public final int ord;
    private static Vector types = new Vector();

    protected SQLDataQueryType(String id) {
        super(id, id);

        if (SQLDataQueryType.last() != null) {
            this.prev = SQLDataQueryType.last();
            SQLDataQueryType.last().next = this;
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
        return new TypeEnumerator(SQLDataQueryType.first());
    }

    public static SanchayType findFromClassName(String className)
    {
        return null;
    }

    public static SanchayType findFromId(String i)
    {
        Enumeration enm = SQLDataQueryType.elements();
        return SanchayType.findFromId(enm, i);
    }

    /** All queries are for a given JDBC database connection **/
    /** Search queries **/
    public static final SQLDataQueryType TABLES_IN_SCHEMA = new SQLDataQueryType("Get all tables in a schema");
    public static final SQLDataQueryType ALL_COLUMN_DATA_FROM_TABLE = new SQLDataQueryType("Get data for all columns from a table");
    public static final SQLDataQueryType MULTI_COLUMN_DATA_FROM_TABLE = new SQLDataQueryType("Get data for miltiple columns from a table");
    public static final SQLDataQueryType GENERAL_BASIC_DATA_QUERY_TABLE = new SQLDataQueryType("Get data for a general basic query from a table");
    
    /** Data definition queries **/
    public static final SQLDataQueryType CREATE_DATABASE = new SQLDataQueryType("Create a new database with the given name");
    public static final SQLDataQueryType CREATE_TABLE = new SQLDataQueryType("Create a new table with the given name");
    public static final SQLDataQueryType DROP_TABLE = new SQLDataQueryType("Drop a table from the database");
    public static final SQLDataQueryType ALTER_TABLE = new SQLDataQueryType("Alter the data definition in a table");
    
    /** Data updation queries **/
    public static final SQLDataQueryType UPDATE_TABLE = new SQLDataQueryType("Update the data in a table with a general basic query");
    public static final SQLDataQueryType INSERT_DATA = new SQLDataQueryType("Insert multiple rows into a table");
    public static final SQLDataQueryType DELETE_DATA = new SQLDataQueryType("Delete some data in a table with a general basic query");
}
