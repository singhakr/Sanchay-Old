/*
 * Created on Aug 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package sanchay.table;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import javax.swing.table.*;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;
import sanchay.GlobalProperties;
import sanchay.db.DBConnection;
import sanchay.db.DatabaseTableColumnSpec;

import sanchay.resources.*;
import sanchay.table.gui.Cell;
import sanchay.xml.dom.SanchayDOMElement;

/**
 *  @author Anil Kumar Singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SanchayTableModel extends DefaultTableModel implements Resource, SanchayDOMElement {
    
    protected String name;
    protected String langEnc;

    protected String filePath;
    protected String charset;

    /**
     * Table schema is represented by SanchayTableModel itself.
     * The common attributes for a schema are:
     * ColumnName, DataType, DefaultValue, PropType, PropFile.
     * Each row of schema table will represent one column (attribute) of this table.
     * The schema table (and schemaFile and schemaCharset) of the schema table will be null.
     */
    protected String schemaFilePath;
    protected String schemaCharset;
    protected SanchayTableModel schema;
    
    protected boolean tableEditable;
    protected boolean save;
    
    protected int editableRows[];
    protected int editableCols[];

    /**
     * Database mode
     */
    protected boolean databaseMode;
    protected boolean databaseSchemaMode;
    protected boolean noStore;

    protected ResultSet resultSet;
    protected DBConnection connection;

//    protected  Object[][] contents;
//    protected String[] columnNames;
    protected Class[] columnClasses;   
    /**
     * 
     */
    public SanchayTableModel() {
        super();
        
        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(DBConnection c, String tableName) {
        super();
        
        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
        
        databaseMode = true;
        
        connection = c;
        
        try {
            
            getTableContents (c, tableName);        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }        
    }

    public SanchayTableModel(String url, String tableName, String driverName, String user, String passwd)
    {
        super();
        
        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;

        databaseMode = true;
        
        DBConnection conn = connectToDB(url, driverName, user, passwd);
        
        try {
            getTableContents (conn, tableName);        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }        
    }

    public SanchayTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(Vector columnNames, int rowCount) {
        super(columnNames, rowCount);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(ArrayList<String> columnNames, int rowCount) {
        super(columnNames.toArray(), rowCount);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);

        charset = GlobalProperties.getIntlString("UTF-8");
        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(String f, String charset) throws FileNotFoundException, IOException
    {
	super();

        filePath = f;
        this.charset = charset;
        read(f, charset);

        schemaCharset = GlobalProperties.getIntlString("UTF-8");
        tableEditable = true;
    }

    public SanchayTableModel(String f, String charset, SanchayTableModel sch) throws FileNotFoundException, IOException
    {
        super();

        setSchema(sch);
        
        filePath = f;
        this.charset = charset;
        
        read(f, charset);
        tableEditable = true;
    }

    public SanchayTableModel(String f, String charset, String sf, String scharset) throws FileNotFoundException, IOException
    {
        super();

        schema = new SanchayTableModel();
        schema.read(sf, scharset);
        
        filePath = f;
        this.charset = charset;
        
        read(f, charset);

        schemaFilePath = sf;
        schemaCharset = scharset;
        tableEditable = true;
    }
    
    public SanchayTableModel(String charset, SanchayTableModel sch) throws FileNotFoundException, IOException
    {
        super();

        setSchema(sch);
        
        this.charset = charset;
       
    }
    public void setDatabaseSchemaMode(boolean m)
    {
        databaseSchemaMode = m;
    }
    
    public boolean isInDatabaseSchemaMode()
    {
        return databaseSchemaMode;
    }

    // BEGIN: For the database mode //
    public void setDatabaseMode(boolean m)
    {
        databaseMode = m;
    }
    
    public boolean isInDatabaseMode()
    {
        return databaseMode;
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
    
    public DBConnection connectToDB(String url, String driverName, String user, String passwd)
    {
        
        
        return null;
    }
        
    public void getTableContents (DBConnection conn, String tableName) throws SQLException
    {
        clear();
        
        databaseMode = true;
        
        // get metadata: what columns exist and what
        // types (classes) are they?
        DatabaseMetaData meta = conn.getConnection().getMetaData();
        System.out.println (GlobalProperties.getIntlString("got_meta_=_") + meta);
        ResultSet results = meta.getColumns (null, null, tableName, null);
        System.out.println (GlobalProperties.getIntlString("got_column_results"));
//        List colNamesList = new ArrayList();
        List colClassesList = new ArrayList();
        
        while (results.next())
        {
//            colNamesList.add (results.getString ("COLUMN_NAME")); 
            addColumn(results.getString ("COLUMN_NAME"));
            
            System.out.println (GlobalProperties.getIntlString("name:_") +
                    results.getString ("COLUMN_NAME"));
            
            int dbType = results.getInt ("DATA_TYPE");

            switch (dbType)
            {
                case Types.BOOLEAN:
                    colClassesList.add(Boolean.class);
                    break; 
                case Types.CHAR:
                    colClassesList.add(Character.class);
                    break; 
                case Types.INTEGER:
                    colClassesList.add(Integer.class);
                    break; 
                case Types.FLOAT:
                    colClassesList.add(Float.class);
                    break; 
                case Types.DOUBLE: 
                case Types.REAL:
                    colClassesList.add(Double.class);
                    break; 
                case Types.DATE: 
                case Types.TIME: 
                case Types.TIMESTAMP:
                    colClassesList.add (java.sql.Date.class);
                    break; 
                default:
                    colClassesList.add (String.class);
                    break; 
            };
            
            System.out.println (GlobalProperties.getIntlString("type:_") + results.getInt ("DATA_TYPE"));
        }

//        columnNames = new String [colNamesList.size()];
//        colNamesList.toArray (columnNames);
        columnClasses = new Class [colClassesList.size()];
        colClassesList.toArray (columnClasses);

        // get all data from table and put into
        // contents array

        Statement statement = conn.getConnection().createStatement();

        results = statement.executeQuery ("SELECT * FROM " + tableName);
//        List rowList = new ArrayList();

        int rowIndex = 0;
        
        while (results.next())
        {
//            List cellList = new ArrayList(); 
            
            addRow();
            
            for (int colIndex = 0; colIndex<columnClasses.length; colIndex++)
            { 
                Object cellValue = null;

                if (columnClasses[colIndex] == String.class) 
                    cellValue = results.getString (getColumnName(colIndex)); 
//                    cellValue = results.getString (columnNames[colIndex]); 
                else if (columnClasses[colIndex] == Integer.class) 
                    cellValue = Integer.valueOf(results.getInt(getColumnName(colIndex)));
//                    cellValue = new Integer (results.getInt (columnNames[colIndex])); 
                else if (columnClasses[colIndex] == Float.class) 
                    cellValue = Float.valueOf((results.getInt(getColumnName(colIndex))));
//                    cellValue = new Float (results.getInt (columnNames[colIndex])); 
                else if (columnClasses[colIndex] == Double.class) 
                    cellValue = Double.valueOf(results.getDouble(getColumnName(colIndex)));
//                    cellValue = new Double (results.getDouble (columnNames[colIndex]));
                else if (columnClasses[colIndex] == java.sql.Date.class) 
                    cellValue = results.getDate (getColumnName(colIndex)); 
//                    cellValue = results.getDate (columnNames[colIndex]); 
                else 
                    System.out.println (GlobalProperties.getIntlString("Can't_assign_") + getColumnName(colIndex));
//                    System.out.println (GlobalProperties.getIntlString("Can't_assign_") + columnNames[colIndex]);

                setValueAt(cellValue, rowIndex, colIndex);
//                cellList.add (cellValue);
            }// for

//            Object[] cells = cellList.toArray();
//            rowList.add (cells);
            
            rowIndex++;
        } // while

        // finally create contents two-dim array
//	contents = new Object[rowList.size()] [];
//	
//        for (int i=0; i<contents.length; i++)
//            contents[i] = (Object []) rowList.get (i);
//        
//	System.out.println (GlobalProperties.getIntlString("Created_model_with_") + contents.length + GlobalProperties.getIntlString("_rows"));

        // close stuff
        results.close();
        statement.close();
    }    

    public int getRowCount()
    {
//        if(databaseMode == false)
            return super.getRowCount();
 
//        if(contents == null)
//            return 0;
//
//        return contents.length;
    }

    public int getColumnCount()
    {
//        if(databaseMode == false)
            return super.getColumnCount();
        
//        if (contents.length == 0)
//            return 0;
//        else
//            return contents[0].length;
    }

    public int getFilledColumnCount(int rowIndex)
    {
//        if(databaseMode == false)
//            return super.getColumnCount();
        
        int ccount = getColumnCount();
        
        int fccount = 0;
        
        for (int j = 0; j < ccount; j++)
        {
            Object val = getValueAt(rowIndex, j);
            
            if(val != null && (val instanceof String && val.equals("") == false))
                fccount++;
            else
                break;
        }
        
        return fccount;
    }

    public Object getValueAt(int row, int column)
    {
//        if(databaseMode == false)
            return super.getValueAt(row, column);

//        return contents [row][column];
    }

    public Class getColumnClass (int col)
    {
        if(getRowCount() == 0)
            return String.class;
        
//        if(databaseMode == false)
//        {
            if(getValueAt(0, col) == null)
                return String.class;

            return getValueAt(0, col).getClass();
//        }
        
//        return columnClasses [col];
    }

    public String getColumnName (int col) { 
//        if(databaseMode == false)
            return super.getColumnName(col);

//        return columnNames [col]; 
    } 
    // END: For the database mode //
    
    public String getName()
    {
	return name;
    }
    
    public void setName(String nm)
    {
	name = nm;
    }

    public String getLangEnc()
    {
    	return langEnc;
    }

    public void setLangEnc(String langEnc)
    {
    	this.langEnc = langEnc;
    }

    public String getFilePath()
    {
        return filePath;
    }
    
    public void setFilePath(String fp)
    {
        filePath = fp;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset(String cs)
    {
        charset = cs;
    }
    
    public String getSchemaFilePath()
    {
        return schemaFilePath;
    }
    
    public void setSchemaFilePath(String f)
    {
        schemaFilePath = f;
    }
    
    public String getSchemaCharset()
    {
        return schemaCharset;
    }
    
    public void setSchemaCharset(String cs)
    {
        schemaCharset = cs;
    }
    
    public SanchayTableModel getSchema()
    {
        return schema;
    }
    
    public void setSchema(SanchayTableModel s)
    {
//        if(getColumnCount() == 0)
//        {
            schema = s;
            
            schemaFilePath = s.getFilePath();
            schemaCharset = s.getCharset();
            setColumnCount(s.getRowCount());
            
            String colNames[] = new String[getColumnCount()];
            
            for(int i = 0; i < s.getRowCount(); i++)
            {
                for(int j = 0; j < s.getColumnCount(); j++)
                {
                    if(s.getColumnName(j).equals(GlobalProperties.getIntlString("ColumnName")))
                    {
                        colNames[i] = (String) s.getValueAt(i, j);
                    }
                }
            }

            setColumnIdentifiers(colNames);
//        }
    }
    
    public boolean getEditable()
    {
        return tableEditable;
    }
    
    public void setEditable(boolean e)
    {
        tableEditable = e;
    }
    
    public DBConnection getDBConnection()
    {
        return connection;
    }
    
    public void setDBConnection(DBConnection c)
    {
        connection = c;
    }
    
    public boolean isToBeSaved()
    {
        return save;
    }
    
    public void isToBeSaved(boolean save)
    {
        this.save = save;
    }
    
    public int[] getEditableRows()
    {
        return editableRows;
    }
    
    public void setEditableRows(int[] r)
    {
        editableRows = r;
    }
    
    public int[] getEditableColumns()
    {
        return editableCols;
    }
    
    public void setEditableColumns(int[] c)
    {
        editableCols = c;
    }
    
    public boolean isCellEditable(int row, int column)
    {
        if(getEditable() == true)
            return true;
        
        boolean red = false;
        boolean ced = false;
	
        if(getEditableRows() != null)
	{
	    for(int i = 0; i < editableRows.length; i++)
	    {
		if(row == editableRows[i])
		{
		    red = true;
		    break;
		}
	    }
	}

	if(getEditableColumns() != null)
	{
	    for(int i = 0; i < editableCols.length; i++)
	    {
		if(column == editableCols[i])
		{
		    ced = true;
		    break;
		}
	    }
	}
        
        if(
	    (red && ced)
	    || (getEditableRows() == null && ced)
	    || (getEditableColumns() == null && red)
	)
            return true;
        
        return false;
    }

    public void addRows(int count)
    {
        for (int i = 0; i < count; i++)
        {
            addRow();
        }
    }

    public void addColumns(int count, String headerPrefix)
    {
        if(headerPrefix == null)
            headerPrefix = "";

        int prevCount = getColumnCount();

        for (int i = 0; i < count; i++)
        {
            addColumn(headerPrefix + (prevCount + i + 1));
        }
    }
    
    public boolean addRow()
    {
        int colCount = getColumnCount();
            
        Vector rowData = new Vector(colCount);
        
        for(int i = 0; i < colCount; i++)
        {
            Class cls = getColumnClass(i);
            Object object = null;
                    
            try {
//                object = cls.newInstance();
                if(cls.getCanonicalName().equals(Boolean.class.getCanonicalName()))
                    object = Boolean.FALSE;
                else if(cls.getCanonicalName().equals(Byte.class.getCanonicalName()))
                    object = Byte.valueOf("0");
                else if(cls.getCanonicalName().equals(Short.class.getCanonicalName()))
                    object = Short.valueOf("0");
                else if(cls.getCanonicalName().equals(Integer.class.getCanonicalName()))
                    object = Integer.valueOf(0);
                else if(cls.getCanonicalName().equals(Long.class.getCanonicalName()))
                    object = Long.valueOf(0L);
                else if(cls.getCanonicalName().equals(Float.class.getCanonicalName()))
                    object = Float.valueOf(0.0F);
                else if(cls.getCanonicalName().equals(Double.class.getCanonicalName()))
                    object = Double.valueOf(0.0);
                else if(cls.getCanonicalName().equals(String.class.getCanonicalName()))
                    object = "";
                else
//                    object = cls.getConstructor().newInstance();
                    object = Class.forName(object.getClass().getName()).newInstance();
            } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException  ex) {
                Logger.getLogger(SanchayTableModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            rowData.add(object);
//            rowData.add(new String(""));
        }
        
        addRow(rowData);
        
        return true;
    }
    
    public boolean insertRow(int atIndex)
    {
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        
        if(rowCount == 0 || atIndex > rowCount || atIndex < 0)
            return false;
        
        Vector rowData = new Vector(colCount);
        
        for(int i = 0; i < colCount; i++)
            rowData.add("");
        
        insertRow(atIndex, rowData);
        
        return true;
    }
    
    public boolean addRowUnique(String colName, Object rowData[])
    {
        int colIndex = getColumnIndex(colName);
        
        return addRowUnique(colIndex, rowData);
    }
    
    public boolean addRowUnique(int colIndex, Object rowData[])
    {
        Object colVal = rowData[colIndex];
        
        if(getRows(colIndex, colVal).size() > 0)
            return false;
        
        addRow(rowData);
        
        return true;
    }
    
    public boolean addRowUnique(String colName[], Object rowData[]) throws Exception
    {
        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
        
        return addRowUnique(colIndex, rowData);
    }
    
    public boolean addRowUnique(int colIndex[], Object rowData[]) throws Exception
    {
        Vector colVec = new Vector(colIndex.length);
        
        for(int i = 0; i < colIndex.length; i++)
            colVec.add(rowData[i]);
        
        Object colVal[] = colVec.toArray();
        
        if(getRowsAnd(colIndex, colVal).size() > 0)
            return false;
        
        addRow(rowData);
        
        return true;
    }
    
    public Vector getRow(int rowIndex)
    {
        return (Vector) dataVector.get(rowIndex);
    }

    // Returns a Vector containing matching row indices as Integer objects
    public Vector getRowIndices(String colName, Object val)
    {
        int colIndex = getColumnIndex(colName);
        
        return getRowIndices(colIndex, val);
    }

    public Vector<Integer> getRowIndices(int colIndex, Object val)
    {
        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, colIndex).equals(val))
                ret.add(Integer.valueOf(i));
        }

        return ret;
    }

    public Vector<Integer> getRowIndicesOr(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        

        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getRowIndicesOr(colIndex, vals);
    }

    public Vector getRowIndicesOr(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < colIndex.length; i++)
        {
            Vector temp = getRowIndices(colIndex[i], vals[i]);
            ret.addAll(temp);
        }
        
        return ret;
    }

    public Vector getRowIndicesAnd(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getRowIndicesAnd(colIndex, vals);
    }

    public Vector getRowIndicesAnd(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            boolean match = true;

            for(int j = 0; j < colIndex.length; j++)
            {
                if(getValueAt(i, j).equals(vals[j]) == false)
                    match = false;
            }
            
            if(match)
                ret.add(Integer.valueOf((i)));
        }
        
        return ret;
    }

    // Returns Vector of Vectors containing matching rows
    public Vector getRows(String colName, Object val)
    {
        int colIndex = getColumnIndex(colName);
        
        return getRows(colIndex, val);
    }

    public Vector getRows(int colIndex, Object val)
    {
        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, colIndex).equals(val))
                ret.add(dataVector.get(i));
        }

        return ret;
    }

    public Vector getRowsOr(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getRowsOr(colIndex, vals);
    }

    public Vector getRowsOr(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < colIndex.length; i++)
        {
            Vector temp = getRows(colIndex[i], vals[i]);
            ret.addAll(temp);
        }
        
        return ret;
    }

    public Vector getRowsAnd(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getRowsAnd(colIndex, vals);
    }

    public Vector getRowsAnd(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            boolean match = true;

            for(int j = 0; j < colIndex.length; j++)
            {
                if(getValueAt(i, j).equals(vals[j]) == false)
                    match = false;
            }
            
            if(match)
                ret.add(dataVector.get(i));
        }
        
        return ret;
    }
    
    // Returns the first matching value
    public Object getValue(String colName, Object val, String outColName /* whose value needed */)
    {
        Vector ret = new Vector(0, 5);
        int colIndex = getColumnIndex(colName);
        
        return getValue(colIndex, val, outColName);
    }

    // Returns the first matching value
    public Object getValue(int colIndex, Object val, String outColName)
    {
        Vector ret = getValues(colIndex, val, new String[]{outColName});
        
        if(ret.size() > 0 && ((Vector) ret.get(0)).size() > 0)
            return ((Vector) ret.get(0)).get(0);
        
        return null;
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValues(String colName, Object val, String outColNames[] /* whose values needed */)
    {
        Vector ret = new Vector(0, 5);
        int colIndex = getColumnIndex(colName);
        
        return getValues(colIndex, val, outColNames);
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValues(int colIndex, Object val, String outColNames[])
    {
        Vector ret = new Vector(0, 5);
        int outColIndex[] = new int[outColNames.length];
        
        for(int i = 0; i < outColNames.length; i++)
            outColIndex[i] = getColumnIndex(outColNames[i]);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, colIndex).equals(val))
            {
                Vector retvals = new Vector(outColNames.length);
                
                for(int j = 0; j < outColNames.length; j++)
                {
                    retvals.add(getValueAt(i, outColIndex[j]));
                }
                
                ret.add(retvals);
            }
        }

        return ret;
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValuesOr(String colName[], Object vals[], String outColNames[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getValuesOr(colIndex, vals, outColNames);
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValuesOr(int colIndex[], Object vals[], String outColNames[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < colIndex.length; i++)
        {
            Vector temp = getValues(colIndex[i], vals[i], outColNames);
            ret.addAll(temp);
        }
        
        return ret;
    }

    // Returns the first matching value
    public Object getValueAnd(String colName[], Object vals[], String outColName /* whose value needed */) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getValueAnd(colIndex, vals, outColName);
    }

    // Returns the first matching value
    public Object getValueAnd(int colIndex[], Object vals[], String outColName) throws Exception
    {
        Vector ret = getValuesAnd(colIndex, vals, new String[]{outColName});
        
        if(ret.size() > 0 && ((Vector) ret.get(0)).size() > 0)
            return ((Vector) ret.get(0)).get(0);
        
        return null;
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValuesAnd(String colName[], Object vals[], String outColNames[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return getValuesAnd(colIndex, vals, outColNames);
    }

    // Returns Vector of Vectors containing matching values
    public Vector getValuesAnd(int colIndex[], Object vals[], String outColNames[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        int outColIndex[] = new int[outColNames.length];
        
        for(int i = 0; i < outColNames.length; i++)
            outColIndex[i] = getColumnIndex(outColNames[i]);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            boolean match = true;

            for(int j = 0; j < colIndex.length; j++)
            {
                if(getValueAt(i, j).equals(vals[j]) == false)
                    match = false;
            }
            
            if(match)
            {
                Vector retvals = new Vector(outColNames.length);
                
                for(int j = 0; j < outColNames.length; j++)
                {
                    retvals.add(getValueAt(i, outColIndex[j]));
                }

                ret.add(retvals);
            }
        }
        
        return ret;
    }

    // Returns the number of values changed
    public int modifyValues(String colName, Object val, String outColNames[], Object outVals[]) throws Exception
    {
        Vector ret = new Vector(0, 5);
        int colIndex = getColumnIndex(colName);
        
        return modifyValues(colIndex, val, outColNames, outVals);
    }

    public int modifyValues(int colIndex, Object val, String outColNames[], Object outVals[]) throws Exception
    {
        if(outColNames.length != outVals.length)
        {
            throw new Exception();
        }

        int ret = 0;
        int outColIndex[] = new int[outColNames.length];
        
        for(int i = 0; i < outColNames.length; i++)
            outColIndex[i] = getColumnIndex(outColNames[i]);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, colIndex).equals(val))
            {
                for(int j = 0; j < outColNames.length; j++)
                    setValueAt(outVals[j], i, outColIndex[j]);
                
                ret++;
            }
        }

        return ret;
    }

    public int modifyValuesOr(String colName[], Object vals[], String outColNames[], Object outVals[]) throws Exception
    {
        if(colName.length != vals.length || outColNames.length != outVals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return modifyValuesOr(colIndex, vals, outColNames, outVals);
    }

    public int modifyValuesOr(int colIndex[], Object vals[], String outColNames[], Object outVals[]) throws Exception
    {
        if(colIndex.length != vals.length || outColNames.length != outVals.length)
            throw new Exception();

        int ret = 0;
        
        for(int i = 0; i < colIndex.length; i++)
        {
            ret += modifyValues(colIndex[i], vals[i], outColNames, outVals);
        }
        
        return ret;
    }

    public int modifyValuesAnd(String colName[], Object vals[], String outColNames[], Object outVals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return modifyValuesAnd(colIndex, vals, outColNames, outVals);
    }

    public int modifyValuesAnd(int colIndex[], Object vals[], String outColNames[], Object outVals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        int ret = 0;
        int outColIndex[] = new int[outColNames.length];
        
        for(int i = 0; i < outColNames.length; i++)
            outColIndex[i] = getColumnIndex(outColNames[i]);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            boolean match = true;

            for(int j = 0; j < colIndex.length; j++)
	        {
                if(getValueAt(i, j).equals(vals[j]) == false)
                    match = false;
 	        }
            
            if(match)
            {
                for(int j = 0; j < outColNames.length; j++)
                    setValueAt(outVals[j], i, outColIndex[j]);

                ret++;
            }
        }
        
        return ret;
    }

    // Returns Vector of Vectors containing matching rows
    public Vector removeRows(String colName, Object val)
    {
        Vector ret = new Vector(0, 5);
        int colIndex = getColumnIndex(colName);
        
        return removeRows(colIndex, val);
    }

    public Vector removeRows(int colIndex, Object val)
    {
        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            if(getValueAt(i, colIndex).equals(val))
            {
                ret.add(dataVector.get(i));
                removeRow(i);
            }
        }

        return ret;
    }

    public Vector removeRowsOr(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return removeRowsOr(colIndex, vals);
    }

    public Vector removeRowsOr(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < colIndex.length; i++)
        {
            Vector temp = removeRows(colIndex[i], vals[i]);
            ret.addAll(temp);
        }
        
        return ret;
    }

    public Vector removeRowsAnd(String colName[], Object vals[]) throws Exception
    {
        if(colName.length != vals.length)
            throw new Exception();

        int colIndex[] = new int[colName.length];
        
        for(int i = 0; i < colName.length; i++)
            colIndex[i] = getColumnIndex(colName[i]);
            
        return removeRowsAnd(colIndex, vals);
    }

    public Vector removeRowsAnd(int colIndex[], Object vals[]) throws Exception
    {
        if(colIndex.length != vals.length)
            throw new Exception();

        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
        {
            boolean match = true;

            for(int j = 0; j < colIndex.length; j++)
	        {
                if(getValueAt(i, j).equals(vals[j]) == false)
                    match = false;
 	        }
            
            if(match)
            {
                ret.add(dataVector.get(i));
	            removeRow(i);
	        }
        }
        
        return ret;
    }
    
    public Vector insertColumn(String colName, Object colId)
    {
        int colIndex = getColumnIndex(colName);
        
        return insertColumn(colIndex, colId);
    }
    
    public Vector insertColumn(int colIndex, Object colId)
    {
        Vector ret = getColumn(colIndex);

        columnIdentifiers.insertElementAt(colId, colIndex);

        int rcount = getRowCount();
        for(int i = 0; i < rcount; i++)
        {
            ((Vector) dataVector.get(i)).insertElementAt("", colIndex);
        }

        fireTableStructureChanged();
        
        return ret;
    }
    
    public Vector removeColumn(String colName)
    {
        int colIndex = getColumnIndex(colName);
        
        return removeColumn(colIndex);
    }
    
    public Vector removeColumn(int colIndex)
    {
        Vector ret = getColumn(colIndex);

        columnIdentifiers.remove(colIndex);

        int rcount = getRowCount();
        for(int i = 0; i < rcount; i++)
        {
            ((Vector) dataVector.get(i)).remove(colIndex);
        }

        fireTableStructureChanged();
        
        return ret;
    }
    
    public void setColumnIdentifier(int colIndex, Object colId)
    {
        if(columnIdentifiers != null && colIndex < columnIdentifiers.size() && colIndex >= 0)
            columnIdentifiers.set(colIndex, colId);
    }
    
    public Vector getColumn(String colName)
    {
        int colIndex = -1;
        
        for(int i = 0; i < getColumnCount(); i++)
        {
        	if(getColumnName(i).equals(colName))
            {
                colIndex = i;
                i = getColumnCount();
            }
        }
        
        return getColumn(colIndex);
    }

    public Vector getColumn(int colIndex)
    {
        Vector ret = new Vector(0, 5);
        
        for(int i = 0; i < getRowCount(); i++)
            ret.add(getValueAt(i, colIndex));
        
        return ret;
    }

    public int getColumnIndex(String colName)
    {
        for(int i = 0; i < getColumnCount(); i++)
        {
            if(getColumnName(i).equals(colName))
                return i;
        }
        
        return -1;
    }

    public int read() throws FileNotFoundException, IOException
    {
        return read(filePath, charset);
    }

    public int read(String f, String charset) throws FileNotFoundException, IOException
    {
        clear();

        BufferedReader lnReader = null;
        
        if(charset.equals("") == false)
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = new String[2];

        int colCount = 0;
        int maxColCount = 0;
        
        while((line = lnReader.readLine()) != null)
        {
            if((!line.startsWith("#")) && line.equals("") == false)
            {
                // Schema::<file>::<charset>
                if(line.startsWith(GlobalProperties.getIntlString("Schema::")))
                {
                    splitstr = line.split("::");
                    splitstr = splitstr[1].split("\t");
                    
                    setSchema(new SanchayTableModel(splitstr[1], splitstr[2]));
                }
                else if(line.startsWith(GlobalProperties.getIntlString("Column_Names::")))
                {
                    splitstr = line.split("::", 2);
                    splitstr = splitstr[1].split("\t");
                    setColumnIdentifiers(splitstr);
                    
                    maxColCount = splitstr.length;
                }
                else if(line.startsWith(GlobalProperties.getIntlString("Column_Count::")))
                {
                    splitstr = line.split("::");
                    
                    colCount = Integer.parseInt(splitstr[1]);
                }
                else
                {
                    splitstr = line.split("[\t]");
                    
                    int ccount = getColumnCount();
                    
                    if(ccount < splitstr.length)
                    {
                        for (int i = ccount; i < splitstr.length; i++)
                        {
                            addColumn("C" + (i + 1));
                        }
                        
                        colCount = splitstr.length;
                    }

                    addRow(splitstr);
                    
//                    if(splitstr.length > 0)
//                    {
//        //                    addRow(splitstr);
//                        addRow();
//                        int rindex = getRowCount() - 1;
//
//                        for (int i = 0; i < splitstr.length; i++)
//                        {
//                            setValueAt(splitstr[i], rindex, i);                        
//                        }
//
//                        if(maxColCount < splitstr.length)
//                            maxColCount = splitstr.length;
//                    }
                }
            }
        }
        
        if(colCount == 0)
            colCount = maxColCount;
        
        setColumnCount(colCount);
        
        filePath = f;
        this.charset = charset;
        
        lnReader.close();
        
        return 0;
    }
    
    public int loadTableSchema(String tableName) throws SQLException
    {        
        TreeMap<String,DatabaseTableColumnSpec> columnSpecs  = DBConnection.getTableMetaData(connection.getConnection(), getName());

        int i = 1; // Because there is already one dummy row
        for (Map.Entry<String,DatabaseTableColumnSpec> entry : columnSpecs.entrySet()) 
        {
            DatabaseTableColumnSpec columnSpec = entry.getValue();
            
            addRow();

            setValueAt(columnSpec.columnName, i, 0);
            setValueAt(columnSpec.dataType, i, 1);
            setValueAt(Boolean.valueOf(columnSpec.isPrimaryKey), i, 2);
            setValueAt(Boolean.valueOf(columnSpec.isNotNULL), i, 3);
            setValueAt(Boolean.valueOf(columnSpec.isUnique), i, 4);
            setValueAt(Boolean.valueOf(columnSpec.defaultValue), i, 5);
            setValueAt(Boolean.valueOf(true), i, 6);
            i++;
        }
        
        removeRow(0);
        
        fireTableDataChanged();
        
        return 0;
    }
    
    public static LinkedHashMap readMany(String f, String charset) throws FileNotFoundException, IOException
    {
        LinkedHashMap ht = new LinkedHashMap(0, 5);
        
        BufferedReader lnReader = null;
        
        if(!charset.equals(""))
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), charset));
        else
            lnReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), GlobalProperties.getIntlString("UTF-8")));

        String line;
        String splitstr[] = null;

        SanchayTableModel tbl = null;
        String tblName = "";

        int colCount = 0;
        int maxColCount = 0;

        while((line = lnReader.readLine()) != null)
        {
            if((!line.startsWith("#")) && line.equals("") == false)
            {
                if(line.startsWith(GlobalProperties.getIntlString("TableBegin::")))
                {
                    splitstr = line.split("::");
                    tblName = splitstr[1];
                    colCount = 0;
                    maxColCount = 0;

                    tbl = new SanchayTableModel();
                }
                else if(line.startsWith(GlobalProperties.getIntlString("TableEnd")))
                {
                    if(tblName.equals("") == false && tbl != null)
                    {
                        ht.put(tblName, tbl);
        
                        if(colCount == 0)
                            colCount = maxColCount;
        
                        tbl.setColumnCount(colCount);

                        tblName = "";
                        tbl = null;
                    }
                    else
                        throw new IOException();
                }
                else if(line.startsWith(GlobalProperties.getIntlString("Schema::")))
                {
                    splitstr = line.split("::");
                    splitstr = splitstr[1].split("\t");
                    
                    tbl.setSchema(new SanchayTableModel(splitstr[1], splitstr[2]));
                }
                else if(line.startsWith(GlobalProperties.getIntlString("Column_Names::")))
                {
                    splitstr = line.split("::");
                    splitstr = splitstr[1].split("\t");
                    tbl.setColumnIdentifiers(splitstr);
                    
                    maxColCount = splitstr.length;
        
                    tbl.setColumnCount(maxColCount);
                }
                else if(line.startsWith(GlobalProperties.getIntlString("Column_Count::")))
                {
                    splitstr = line.split("::");
                    
                    colCount = Integer.parseInt(splitstr[1]);
        
                    tbl.setColumnCount(colCount);
                }
                else
                {
                    splitstr = line.split("\t");
                    tbl.addRow(splitstr);
                    
                    if(maxColCount < splitstr.length)
                    {
                        maxColCount = splitstr.length;
                        tbl.setColumnCount(maxColCount);
                    }
                }
            }
        }
        
        return ht;
    }

    public int save() throws FileNotFoundException, UnsupportedEncodingException
    {
        if(databaseMode || databaseSchemaMode)
        {
            if(name == null || name.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Please enter the table name on the left.", "Error", JOptionPane.ERROR_MESSAGE);            
                return -1;            
            }
        }
        
        if(databaseSchemaMode)
        {
            try {
                boolean tableExists = DBConnection.doesTableExist(connection.getConnection(), name);
                
                if(tableExists)
                {
                    JOptionPane.showMessageDialog(null, "A table with name" + name + " already exists. Please enter another name.", "Error", JOptionPane.ERROR_MESSAGE);            
                    return -1;                                
                }

                TreeMap<String, DatabaseTableColumnSpec> columnSpecs = new TreeMap<>();

                int rowCount = getRowCount();

                for (int i = 0; i < rowCount; i++) {

                    DatabaseTableColumnSpec columnSpec = new DatabaseTableColumnSpec();

                    String columnName = (String) getValueAt(i, 0);
                    columnSpec.columnName = columnName;

                    String dataType = (String) getValueAt(i, 1);
                    columnSpec.dataType = dataType;

                    boolean isPrimaryKey = Boolean.parseBoolean(getValueAt(i, 2).toString());

                    if(isPrimaryKey)
                    {
                        columnSpec.isPrimaryKey = true;
                    }

                    boolean isNotNULL = Boolean.parseBoolean(getValueAt(i, 3).toString());

                    if(isNotNULL)
                    {
                        columnSpec.isNotNULL = true;
                    }

                    boolean isUnique = Boolean.parseBoolean(getValueAt(i, 4).toString());

                    if(isUnique)
                    {
                        columnSpec.isUnique = true;
                    }

                    String defaultValue = getValueAt(i, 5).toString();

                    if(defaultValue.equals("NULL") == false)
                    {
                        columnSpec.defaultValue = defaultValue;
                    }

                    columnSpecs.put(columnName, columnSpec);
                }

                PreparedStatement createTableStmt = DBConnection.createTable(connection.getConnection(), columnSpecs, name);

                createTableStmt.executeUpdate();
                
            } catch (SQLException ex) {
                Logger.getLogger(SanchayTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        else if(databaseMode)
        {
            int result = JOptionPane.showConfirmDialog(null,"Sure? This will replace all previously\nstored data in the table with\nthe current data?", "Swing Tester",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE);
            
            if(result == JOptionPane.NO_OPTION)
            {
                return -1;
            }
            
            try {
                PreparedStatement stmt = DBConnection.deleteAllRows(connection.getConnection(), name);
                
                stmt.executeUpdate();
                
                ArrayList<String> columnNames = new ArrayList<>();
                
                int colCount = getColumnCount();
                int rowCount = getRowCount();

                Object dataValues[][] = new Object[rowCount][colCount];
                        
                for (int i = 0; i < colCount; i++) {
                    
                    String colName = getColumnName(i);
                    
                    columnNames.add(colName);
                }

                for (int i = 0; i < rowCount; i++) {
                                        
                    for (int j = 0; j < colCount; j++) {
                        dataValues[i][j] = getValueAt(i, j);
                    }
                }
                        
                stmt = DBConnection.insertIntoTableMultipleRows(connection.getConnection(), columnNames, dataValues, name);
                
                stmt.executeUpdate();
                
            }
            catch (SQLException ex) {
                Logger.getLogger(SanchayTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//            try 
//            {
//                DatabaseMetaData meta = connection.getConnection().getMetaData();
//
//                ResultSet tables = meta.getTables(null, null, "%", new String[] { "TABLE" });
//                
////                while (tables.next()) {
//                    String catalog = tables.getString("TABLE_CAT");
//                    String schema = tables.getString("TABLE_SCHEM");
//                    String tableName = tables.getString("TABLE_NAME");
//                    System.out.println("Table: " + tableName);
//                    try (ResultSet primaryKeys = meta.getPrimaryKeys(catalog, schema, tableName)) {
//                        while (primaryKeys.next()) {
//                            System.out.println("Primary key: " + primaryKeys.getString("COLUMN_NAME"));
//                        }
//                    }
//                    // similar for exportedKeys
////                }
//            }
//            catch (SQLException ex) {
//                Logger.getLogger(SanchayTableModel.class.getName()).log(Level.SEVERE, null, ex);
//                ex.printStackTrace();
//            }    
//            
            return 0;            
        }
        else 
        {
            return save(filePath, charset);
        }
        
        return -1;
    }

    public int save(String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        print(ps);
        
        filePath = f;
        this.charset = charset;

        ps.close();
        
        return 0;
    }

    public static void saveMany(LinkedHashMap tables, String f, String charset) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintStream ps = new PrintStream(f, charset);
        printMany(tables, ps);
    }
    
    public String rowToString(int r, String separator)
    {
        String s = "";
        
        Vector rvec = (Vector) dataVector.get(r);
        
        int count = rvec.size();
        
        for(int i = 0; i < count; i++)
        {
            if(i < count - 1)
                s += rvec.get(i) + separator;
            else
                s += rvec.get(i);
        }
        
        return s;
    }
    
    public boolean stringToRow(int r, String str, String separator)
    {
        if(r >= getRowCount() || r < 0)
            return false;
            
        String cells[] = str.split(separator);
        
        Vector rvec = (Vector) dataVector.get(r);
        
        int count = getColumnCount();
        
        for(int i = 0; i < count && i < cells.length; i++)
        {
            setValueAt(cells[i], r, i);
        }
        
        return true;
    }

    public void print(PrintStream ps)
    {
        if(schema == null)
        {
            if(getColumnCount() > 0 && getColumnName(0).equals("") == false)
            {
                int colCount = getColumnCount();
                ps.println(GlobalProperties.getIntlString("Column_Count::") + colCount);
                
                ps.print(GlobalProperties.getIntlString("Column_Names::"));

                for(int i = 0; i < colCount; i++)
                {
                    ps.print(getColumnName(i));

                    if(i == getColumnCount() - 1)
                        ps.print("\n");
                    else
                        ps.print("\t");
                }
            }
        }
        else
        {
            ps.print(GlobalProperties.getIntlString("Schema::") + schemaFilePath+ "::" + schemaCharset);
        }
        
        for(int i = 0; i < getRowCount(); i++)
        {
            for(int j = 0; j < getColumnCount(); j++)
            {
		Object val = getValueAt(i, j);
		
		if(val == null)
		    ps.print("");
		else
		    ps.print(getValueAt(i, j));

                if(j == getColumnCount() - 1)
                    ps.print("\n");
                else
                    ps.print("\t");
            }
        }
    }

    public static void printMany(LinkedHashMap tables, PrintStream ps)
    {
        Vector kvec = new Vector(tables.keySet());
        Collections.sort(kvec);
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String tblName = (String) kvec.get(i);
            SanchayTableModel tbl = (SanchayTableModel) tables.get(tblName);
            
            ps.println(GlobalProperties.getIntlString("TableBegin::") + tblName);
	    tbl.print(ps);
            ps.println(GlobalProperties.getIntlString("TableEnd"));
        }
    }

    public static void printManyXML(LinkedHashMap tables, PrintStream ps)
    {
        DOMElement domElementMany = new DOMElement(GlobalProperties.getIntlString("MultiTable"));

        Vector kvec = new Vector(tables.keySet());
        Collections.sort(kvec);
        
        int count = kvec.size();
        
        for(int i = 0; i < count; i++)
        {
            String tblName = (String) kvec.get(i);
            SanchayTableModel tbl = (SanchayTableModel) tables.get(tblName);
            
            DOMElement domElement = tbl.getDOMElement();
            DOMAttribute attribTbl = new DOMAttribute(domElement, new org.dom4j.QName(GlobalProperties.getIntlString("name")), tblName);
            
            domElementMany.add(domElement);
        }
        
        ps.print(domElementMany.asXML());
    }

    public Object clone() throws CloneNotSupportedException// copyFS($fs)
    {
        SanchayTableModel obj = null;

        String sfname = GlobalProperties.getHomeDirectory() + "/" + "SanchayTable-tmp.tmp";
        
        try {
            FileOutputStream out = new FileOutputStream(sfname);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(this);
            os.flush();

            FileInputStream in = new FileInputStream(sfname);
            ObjectInputStream is = new ObjectInputStream(in);
            obj = (SanchayTableModel) is.readObject();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }

    public void clear()
    {
        while(getRowCount() > 0)
            removeRow(0);
        
        setColumnIdentifiers(new Vector(0));
    }

    public void clearData()
    {
        int rcount = getRowCount();
        int ccount = getColumnCount();
        
        for(int i = 0; i < rcount; i++)
        {
            clearRowData(i);
        }
    }

    public void clearRowData(int r)
    {
        int ccount = getColumnCount();
        
        for(int i = 0; i < ccount; i++)
        {
            setValueAt("", r, i);
        }
    }

    public void clearColData(int c)
    {
        int rcount = getRowCount();
        
        for(int i = 0; i < rcount; i++)
        {
            setValueAt("", i, c);
        }
    }

    public boolean isRowEmpty(int row, boolean exceptFirst)
    {
        int ccount = getColumnCount();

        int i = 0;

        if(exceptFirst)
            i = 1;

        for(; i < ccount; i++)
        {
            Object val = getValueAt(row, i);
            
            if((val != null && val instanceof String && val.equals("") == false) && val != null)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isColumnEmpty(int col, boolean exceptFirst)
    {
        int rcount = getRowCount();

        int i = 0;

        if(exceptFirst)
            i = 1;

        for(; i < rcount; i++)
        {
            Object val = getValueAt(i, col);

            if(val != null && (val instanceof String && val.equals("") == false))
                return false;
        }

        return true;
    }

    public void trimRows(boolean exceptFirst)
    {
        int rcount = getRowCount();

        for(int i = 0; i < rcount; i++)
        {
            if(isRowEmpty(i, exceptFirst))
            {
                removeRow(i);
                i--;
                rcount--;
            }
        }        
    }

    public void trimColumns(boolean exceptFirst)
    {
        int ccount = getColumnCount();

        for(int i = 0; i < ccount; i++)
        {
            if(isColumnEmpty(i, exceptFirst))
            {
                removeColumn(i);
                i--;
                ccount--;
            }
        }
    }
    
    /*
     * Returns the index of the row with the longest value (say, String) for a particular column.
     * May be useful for resizing columns to fit content.
     */
    public int getLongestValueRowIndex(int colInd)
    {
	int count = getRowCount();
	int max = 0;
	int maxRowInd = 0;
	
	for (int i = 0; i < count; i++)
	{
            Object val = getValueAt(i, colInd);
            
            if(val != null && val.toString() != null)
            {
                String valStr = val.toString();
                
                int len = valStr.length();

                if(len > max)
                {
                    max = len;
                    maxRowInd = i;
                }
            }
	}
	
	return maxRowInd;
    }
    
    public int copyRow(int row1, int colOffset1, SanchayTableModel toTable, int row2, int colOffset2)
    {
	if(row1 >= getRowCount() || row2 > toTable.getRowCount())
	    return -1;

	int maxColCount = Math.max(getColumnCount(), toTable.getColumnCount());
	
	for (int i = colOffset1; i < maxColCount; i++)
	{
	    if(i + colOffset1 < getColumnCount() && i + colOffset2 < toTable.getColumnCount())
    		toTable.setValueAt(getValueAt(row1, i + colOffset1), row2, i + colOffset2);
	    else if(i + colOffset2 < toTable.getColumnCount())
    		toTable.setValueAt("", row2, i + colOffset2);
	}
	
	return 0;
    }
    
    public int copyColumn(int col1, int rowOffset1, SanchayTableModel toTable, int col2, int rowOffset2)
    {
	if(col1 >= getColumnCount() || col2 >= toTable.getColumnCount())
	    return -1;

	int maxRowCount = Math.max(getRowCount(), toTable.getRowCount());
	
	for (int i = rowOffset1; i < maxRowCount; i++)
	{
	    if(i + rowOffset1 < getRowCount() && i + rowOffset2 < toTable.getRowCount())
		toTable.setValueAt(getValueAt(i + rowOffset1, col1), i + rowOffset2, col2);
	    else if(i + rowOffset2 < toTable.getRowCount())
		toTable.setValueAt("", i + rowOffset2, col2);
	}
	
	return 0;
    }
     
    public static SanchayTableModel mergeRows(Vector tables, boolean addTableNumbers)
    {
	int maxColCount = 0;
	int rowCount = 0;
	
	int add = 0;
	
	if(addTableNumbers)
	    add = 1;
	
	for (int i = 0; i < tables.size(); i++)
	{
	    int ccount = ((SanchayTableModel) tables.get(i)).getColumnCount();
	    rowCount += ((SanchayTableModel) tables.get(i)).getRowCount();
	    
	    if(maxColCount < ccount)
		maxColCount = ccount;
	}
	
	SanchayTableModel mergedTable = new SanchayTableModel(rowCount, maxColCount + add);

    rowCount = 0;

	for (int i = 0; i < tables.size(); i++)
	{
	    SanchayTableModel tbl = ((SanchayTableModel) tables.get(i));
	    
	    int rcount = tbl.getRowCount();
	    
	    for (int j = 0; j < rcount; j++)
	    {
            if(addTableNumbers)
                mergedTable.setValueAt("" + (i + 1), rowCount, 0);
            
            tbl.copyRow(j, 0, mergedTable, rowCount++, add);
	    }
	}
	
	return mergedTable;
    }
   
    public static SanchayTableModel mergeRows(Vector tables)
    {
	return mergeRows(tables, false);
    }
    
    public static SanchayTableModel mergeColumns(Vector tables)
    {
	int maxRowCount = 0;
	int colCount = 0;
	
	for (int i = 0; i < tables.size(); i++)
	{
	    int rcount = ((SanchayTableModel) tables.get(i)).getRowCount();
	    colCount += ((SanchayTableModel) tables.get(i)).getColumnCount();
	    
	    if(maxRowCount < rcount)
		maxRowCount = rcount;
	}
	
	SanchayTableModel mergedTable = new SanchayTableModel(maxRowCount, colCount);

	for (int i = 0; i < tables.size(); i++)
	{
	    SanchayTableModel tbl = ((SanchayTableModel) tables.get(i));
	    
	    int ccount = tbl.getColumnCount();
	    
	    for (int j = 0; j < ccount; j++)
	    {
		tbl.copyColumn(j, 0, mergedTable, mergedTable.getColumnCount() + j, 0);
	    }
	}
	
	return mergedTable;
    }

    public void stringsToLowerCase()
    {
        int rcount = getRowCount();
        int ccount = getColumnCount();

        for (int i = 0; i < rcount; i++)
        {
            for (int j = 0; j < ccount; j++)
            {
                Object val = getValueAt(i, j);

                if(val instanceof String)
                {
                    String valStr = (String) val;
                    valStr = valStr.toLowerCase();

                    setValueAt(valStr, i, j);
                }
            }
        }
    }

    public void stringsToUpperCase()
    {
        int rcount = getRowCount();
        int ccount = getRowCount();

        for (int i = 0; i < rcount; i++)
        {
            for (int j = 0; j < ccount; j++)
            {
                Object val = getValueAt(i, j);

                if(val instanceof String)
                {
                    String valStr = (String) val;
                    valStr = valStr.toUpperCase();

                    setValueAt(valStr, i, j);
                }
            }
        }
    }

    public DOMElement getDOMElement() {
        DOMElement domElement = new DOMElement("KeyValueProperties");
        
        if(schema == null)
        {
            if(getColumnCount() > 0 && getColumnName(0).equals("") == false)
            {
                DOMElement domElementSchema = new DOMElement("SanchayTableSchema");

                int colCount = getColumnCount();
                DOMAttribute attribColCount = new DOMAttribute(domElementSchema, new org.dom4j.QName("colCount"), ("" + colCount) );
                
                DOMElement domElementColNames = new DOMElement("ColumnsNames");

                for(int i = 0; i < colCount; i++)
                {
                    DOMElement domElementColName = new DOMElement("ColumnsName");
                    DOMAttribute attribColName = new DOMAttribute(domElementColName, new org.dom4j.QName(GlobalProperties.getIntlString("name")), getColumnName(i));
                    domElementColNames.add(domElementColName);
                }

                domElementSchema.add(domElementColNames);
                domElement.add(domElementSchema);
            }
        }
        else
        {
            DOMElement domElementSchema = new DOMElement("SanchayTableSchema");
            DOMAttribute attribSchemaFile = new DOMAttribute(domElementSchema, new org.dom4j.QName("schemaFilePath"), schemaFilePath);
            DOMAttribute attribSchemaCharset = new DOMAttribute(domElementSchema, new org.dom4j.QName("schemaFilePath"), schemaCharset);
            domElement.add(domElementSchema);
        }
        
        for(int i = 0; i < getRowCount(); i++)
        {
            DOMElement domElementRow = new DOMElement(GlobalProperties.getIntlString("Row"));
            
            for(int j = 0; j < getColumnCount(); j++)
            {
                DOMElement domElementCell = new DOMElement(GlobalProperties.getIntlString("Cell"));

                Object val = getValueAt(i, j);
                DOMAttribute attribCell = new DOMAttribute(domElementCell, new org.dom4j.QName(GlobalProperties.getIntlString("value")), val.toString());
                		
                domElementRow.add(domElementCell);
            }
            
            domElement.add(domElementRow);
        }
        
        return domElement;
    }

    public String getXML() {
        org.dom4j.dom.DOMElement element = getDOMElement();
        return element.asXML();
    }

    public void printXML(PrintStream ps) {
        ps.println(getXML());
    }

    public void readXML(Element domElement) {
    }
      
    public String toString()
    {
        return name;
    }

    public static void mapToTable(Map map, SanchayTableModel table, boolean reverse, boolean clear)
    {
        if(table.getColumnCount() != 2)
        {
            System.err.println("Error: Number of columns should be two for map to table conversion.");
            return;
        }

        if(clear)
        {
            while(table.getRowCount() > 0)
                table.removeRow(0);
        }

        Iterator itr = map.keySet().iterator();

        int rowIndex = 0;

        while(itr.hasNext())
        {
            Object key = itr.next();
            Object val = map.get(key);

            table.addRow();

            if(reverse)
            {
                table.setValueAt(key, rowIndex, 1);
                table.setValueAt(val, rowIndex++, 0);
            }
            else
            {
                table.setValueAt(key, rowIndex, 0);
                table.setValueAt(val, rowIndex++, 1);
            }
        }
    }

    public static void mapToTable(Map map, SanchayTableModel table, boolean clear)
    {
        mapToTable(map, table, false, clear);
    }

    public Cell findValue(Object v)
    {
        int rcount = getRowCount();
        int ccount = getRowCount();

        for (int i = 0; i < rcount; i++)
        {
            for (int j = 0; j < ccount; j++)
            {
                Object val = getValueAt(i, j);

                if(val instanceof String)
                {
                    if(val.equals(v))
                        return new Cell(i, j);
                }
                else
                {
                    if(val == v)
                        return new Cell(i, j);
                }
            }
        }

        return null;
    }
    
    public static void main(String[] args) {
        SanchayTableModel st = null;
        
        try {
//            st = new SanchayTableModel(GlobalProperties.getHomeDirectory() + "/" + "userData/userdata1.txt", GlobalProperties.getIntlString("UTF-8"));
            st = new SanchayTableModel("/extra/work/questimate/tmp/fst/doc147b.GRAPH-1.post", "UTF-8");
            st.print(System.out);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
