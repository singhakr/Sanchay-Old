/*
 * DBConnection.java
 *
 * Created on July 23, 2008, 10:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.db;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import sanchay.GlobalProperties;
import sanchay.common.types.DBDriverType;

/**
 *
 * @author Anil Kumar Singh
 */
public class DBConnection {
    
    DBDriverType dbType;
    
    String url;
    String username;
    String password;
//    static String driver = DatabaseUtils.DERBY_DRIVER_NAME;
    Connection connection;
    String status = GlobalProperties.getIntlString("Not_Connected");
    
    private PreparedStatementStorage preparedStatementStorage;
    
    public DBConnection(DBDriverType type) {
        dbType = type;
        
        preparedStatementStorage = new PreparedStatementStorage();
    }
    
    public void connect()
    {
        try {
            Class.forName(dbType.DRIVER);
            status = GlobalProperties.getIntlString("Found_driver,_starting_to_connect...");
        } catch (java.lang.ClassNotFoundException e) {
            status = GlobalProperties.getIntlString("Driver_not_found");
            return;
        }
        try {
            connection = DriverManager.getConnection(dbType.PROTOCOL + ":" + url, username, password);
            status = GlobalProperties.getIntlString("Connected_to_the_database");
        } catch(java.sql.SQLException e) {
            status = GlobalProperties.getIntlString("Could_not_connect");
        }        
    }
    
    public boolean closeConnection()
    {
        dbType = DBDriverType.JDBS_SQLITE;
        
        url = null;
        username = null;
        password = null;
        
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            
            return false;
        }
        
        connection = null;
        
        return true;
    }
    
    public String getURL()
    {
        return url;
    }
    
    public void setURL(String u)
    {
        url = u;
    }
    
    public void setUser(String user)
    {
        this.username = user;
    }
    
    public void setPassword(String pwd)
    {
        this.password = pwd;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getPass() {
        return password;
    }
    
    public String getUser() {
        return username;
    }
    
    public ResultSet executeQuery(String s) throws java.sql.SQLException {
        Statement stmt = connection.createStatement();
        System.out.println(s);
        return stmt.executeQuery(s);
    }
    
    public int executeUpdate(String s) throws java.sql.SQLException {
        Statement stmt = connection.createStatement();
        System.out.println(s);
        return stmt.executeUpdate(s);
    }
    
    public Connection getConnection()
    {
        return connection;
    }
    
    public static boolean isColumnUnique(Connection connection, String tableName, String columnName) throws SQLException
    {   
        String queryDCount = "select count(distinct " + columnName + ")"
                + " from " + tableName + ";";
        System.out.println("Query prepared: " + queryDCount);

        String queryCount = "select count(" + columnName + ")"
                + " from " + tableName + ";";
        System.out.println("Query prepared: " + queryCount);
        
        PreparedStatement statement = connection.prepareStatement(queryDCount);        
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int distinctCount = resultSet.getInt(1);        
        
        statement = connection.prepareStatement(queryCount);        
        resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt(1);        
        
        if(distinctCount == count)
        {
            return true;
        }
        
        return false;
    }
    
    public static TreeMap<String,DatabaseTableColumnSpec> getTableMetaData(Connection connection, String tableName) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();

        int colCount = rsMetaData.getColumnCount();
        
        TreeMap<String,DatabaseTableColumnSpec> columnSpecs = new TreeMap<>();
        
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        
        ResultSet columns = databaseMetaData.getColumns(null, null,tableName, null);
        while(columns.next())
        {
            DatabaseTableColumnSpec columnSpec = new DatabaseTableColumnSpec();
            columnSpec.columnName = columns.getString("COLUMN_NAME");
            columnSpec.dataType = columns.getString("TYPE_NAME");
//            columnSpec.dataType = columns.getString("DATA_TYPE");
            columnSpec.isNotNULL = !columns.getBoolean("IS_NULLABLE");            
            columnSpec.defaultValue = columns.getString("COLUMN_DEF");
            columnSpec.isUnique = isColumnUnique(connection, tableName, columnSpec.columnName);
//            String columnsize = columns.getString("COLUMN_SIZE");
//            String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");

            columnSpecs.put(columnSpec.columnName, columnSpec);
        }
                
        ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, tableName);

        while(primaryKeys.next())
        {
            String primaryKey = primaryKeys.getString("COLUMN_NAME");
            
            columnSpecs.get(primaryKey).isPrimaryKey = true;
        }
        
        return columnSpecs;
    }
    
    /** Operations for accessing metadata.**/
    public static Set<String> getTableNames(Connection connection) throws SQLException {
        
        try(ResultSet tableNames = connection.getMetaData().getTables(null, null,"%", null);) {

            SortedSet<String> tableNameSet = new TreeSet<>();

            while(tableNames.next()) {
                String tableName = tableNames.getString(3);
                out.println(""+tableName+" is the name of the tale.");
                tableNameSet.add(tableName);
            }

            return tableNameSet;
        }
    }
    
    public static boolean doesTableExist(Connection connection, String tableName) throws SQLException {
    
        Set<String> tables = getTableNames(connection);
        
        return tables.contains(tableName);
    }
    
    public static Set<String> getPrimaryKeyNamesForTable(Connection connection, String tableName) throws SQLException {
        
        try(ResultSet pkColumns= connection.getMetaData().getPrimaryKeys(null, null, tableName);) {

            SortedSet<String> pkColumnSet = new TreeSet<>();

            while(pkColumns.next()) {
                String pkColumnName = pkColumns.getString("COLUMN_NAME");
                Integer pkPosition = pkColumns.getInt("KEY_SEQ");
                out.println(""+pkColumnName+" is the "+pkPosition+". column of the primary key of the table "+tableName);
                pkColumnSet.add(pkColumnName);
            }

            return pkColumnSet;
        }
    }
    
    public static Set<String> getForeignKeyNamesForTable(Connection connection, String tableName) throws SQLException {
        
        try(ResultSet fkColumns= connection.getMetaData().getImportedKeys(null, null, tableName);) {

            SortedSet<String> fkColumnSet = new TreeSet<>();

            while(fkColumns.next()) {
                String fkColumnName = fkColumns.getString("COLUMN_NAME");
                Integer fkPosition = fkColumns.getInt("KEY_SEQ");
                out.println(""+fkColumnName+" is the "+fkPosition+". column of the foreign key of the table "+tableName);
                fkColumnSet.add(fkColumnName);
            }

            return fkColumnSet;
        }
    }
    
    public static Set<String> getColumnNamesForTable(Connection connection, String tableName) throws SQLException {
        
        try(ResultSet columns= connection.getMetaData().getColumns(null, null, tableName, null);) {

            SortedSet<String> columnSet = new TreeSet<>();

            while(columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                Integer keyPosition = columns.getInt("KEY_SEQ");
                out.println(""+columnName+" is the "+keyPosition+". column of the table "+tableName);
                columnSet.add(columnName);
            }

            return columnSet;
        }
    }
    
    /** Implementations of generic query statements as defined in the SQLDataQueryType class.
     * For data queries with SQL queries, rather than operators provided by the Connection class.
     * All these return a PreparedStatement.
    **/
    
    public static PreparedStatement getAllTables(Connection connection) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement("SHOW TABLES");
        
        return statement;
    }
    
    public static PreparedStatement getAllColumnsDataFromTable(Connection connection, String tableName) throws SQLException
    {
        String query = "SELECT * FROM " + tableName + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }
    
    public static PreparedStatement getAllColumnsDataFromTable(Connection connection, String[] columnNames, String tableName) throws SQLException
    {
        String query = "SELECT ";
        
        for (int i = 0; i < columnNames.length; i++) {
            
            if(i == columnNames.length - 1)
            {
                query = query + columnNames[i] + " ";            
            }
            else
            {
                query = query + columnNames[i] + ", ";
            }
        }

        query = query + " FROM " + tableName;
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }
    
    public static PreparedStatement getGenericQueryDataFromTable(Connection connection, String[] columnNames, TreeMap<String, String> conditions, String tableName, boolean andConditions) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
            
        String query = "SELECT ";
        
        for (int i = 0; i < columnNames.length; i++) {
            
            if(i == columnNames.length - 1)
            {
                query = query + columnNames[i] + " ";            
            }
            else
            {
                query = query + columnNames[i] + ", ";
            }
        }

        query = query + " FROM " + tableName + " ";

        if(conditions != null && conditions.isEmpty() == false)
        {
            query = query + " WHERE ";

            String lastKey = conditions.lastKey();

            for (Map.Entry<String,String> entry : conditions.entrySet()) 
            {
                int columnIndex = resultSet.findColumn(entry.getKey());
                int dataType = rsMetaData.getColumnType(columnIndex + 1);

                if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                        || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                        || dataType == Types.TIMESTAMP_WITH_TIMEZONE)
                {
                    query = query + entry.getKey() + " = '" + entry.getValue() + "'";
                }
                else
                {
                    query = query + entry.getKey() + " = " + entry.getValue();                
                }

                if(entry.getKey().equals(lastKey) == false)
                {
                    if(andConditions)
                        query = query + " AND ";
                    else
                        query = query + " OR ";                        
                }
            }
            
        }
        
        query = query + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }

    public static PreparedStatement createDatabase(Connection connection, String databaseName) throws SQLException
    {
        String query = "CREATE DATABASE " + databaseName + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;        
    }
    
    public static PreparedStatement createTable(Connection connection, TreeMap<String, DatabaseTableColumnSpec> columnSpecs, String tableName) throws SQLException
    {
        if(doesTableExist(connection, tableName))
        {
            dropTable(connection, tableName);
        }
        
        String query = "CREATE TABLE " + tableName + " ( ";

        String lastKey = columnSpecs.lastKey();
        
        for (Map.Entry<String,DatabaseTableColumnSpec> entry : columnSpecs.entrySet()) 
        {
            String columnName = entry.getKey();
            DatabaseTableColumnSpec dbColSpec = entry.getValue();

            query = query + columnName + " ";
            query = query + dbColSpec.dataType + " ";
             
            if(dbColSpec.defaultValue != null && dbColSpec.defaultValue.equals("") == false)
            {
                if(dbColSpec.dataType.equals("CHAR") || dbColSpec.dataType.equals("LONGNVARCHAR") || dbColSpec.dataType.equals("LONGVARCHAR") || dbColSpec.dataType.equals("NCHAR") || dbColSpec.dataType.equals("VARCHAR")
                        || dbColSpec.dataType.equals("DATE") || dbColSpec.dataType.equals("TIME") || dbColSpec.dataType.equals("TIMESTAMP") || dbColSpec.dataType.equals("TIME_WITH_TIMEZONE")
                        || dbColSpec.dataType.equals("TIMESTAMP_WITH_TIMEZONE"))
                {
                    query = query + " DEFAULT " + "'" + dbColSpec.defaultValue + "'";
                }
                else
                {
                    query = query + " DEFAULT " + dbColSpec.defaultValue;
                }
            }
           
            if(dbColSpec.isPrimaryKey)
            {
                query = query + " PRIMARY KEY ";
            }

            if(dbColSpec.isNotNULL)
            {
                query = query + " NOT NULL ";
            }

            if(dbColSpec.isPrimaryKey == false && dbColSpec.isUnique)
            {
                query = query + " UNIQUE ";
            }            

            if(entry.getKey().equals(lastKey) == false)
            {
                query = query + ", ";
            }

        }
        
        query = query + ");";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }
    
    public static PreparedStatement addColumnToTable(Connection connection, DatabaseTableColumnSpec columnSpec, String tableName) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
            
        String query = "ALTER TABLE " + tableName + " ADD ";
        
        query = query + columnSpec.columnName + " ";
        query = query + columnSpec.dataType + " ";
             
        int columnIndex = resultSet.findColumn(columnSpec.columnName);
        int dataType = rsMetaData.getColumnType(columnIndex);

        if(columnSpec.defaultValue != null && columnSpec.equals("") == false)
        {
            if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                    || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                    || dataType == Types.TIMESTAMP_WITH_TIMEZONE || dataType == Types.NULL)
            {
                query = query + " DEFAULT " + "'" + columnSpec.defaultValue + "'";
            }
            else
            {
                query = query + " DEFAULT " + columnSpec.defaultValue;
            }
        }
           
        if(columnSpec.isPrimaryKey)
        {
            query = query + " PRIMARY KEY ";
        }

        if(columnSpec.isNotNULL)
        {
            query = query + " NOT NULL ";
        }

        if(columnSpec.isPrimaryKey == false && columnSpec.isUnique)
        {
            query = query + " UNIQUE ";
        }            
        
        query = query + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }

    public static PreparedStatement dropTable(Connection connection, String tableName) throws SQLException
    {
        String query = "DROP TABLE " + tableName + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;        
    }
    
    public static PreparedStatement updateTableWithGenericQuery(Connection connection, TreeMap<String, Object> columnValues, TreeMap<String, String> conditions, String tableName, boolean andConditions) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
            
        String query = "UPDATE " + tableName + " SET ";

        String lastKey = columnValues.lastKey();

        for (Map.Entry<String,Object> entry : columnValues.entrySet()) 
        {
            int columnIndex = resultSet.findColumn(entry.getKey());
            int dataType = rsMetaData.getColumnType(columnIndex);

            if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                    || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                    || dataType == Types.TIMESTAMP_WITH_TIMEZONE)
            {
                query = query + entry.getKey() + " = '" + entry.getValue() + "'";
            }
            else
            {
                query = query + entry.getKey() + " = " + entry.getValue();                
            }

            if(entry.getKey().equals(lastKey) == false)
            {
                query = query + ", ";        
            }
        }
        
        if(conditions != null && conditions.isEmpty() == false)
        {
            query = query + " WHERE ";

            lastKey = conditions.lastKey();

            for (Map.Entry<String,String> entry : conditions.entrySet()) 
            {
                int columnIndex = resultSet.findColumn(entry.getKey());
                int dataType = rsMetaData.getColumnType(columnIndex);

                if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                        || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                        || dataType == Types.TIMESTAMP_WITH_TIMEZONE)
                {
                    query = query + entry.getKey() + " = '" + entry.getValue() + "'";
                }
                else
                {
                    query = query + entry.getKey() + " = " + entry.getValue();                
                }

                if(entry.getKey().equals(lastKey) == false)
                {
                    if(andConditions)
                        query = query + " AND ";
                    else
                        query = query + " OR ";                        
                }
            }
            
        }
        
        query = query + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }

    public static PreparedStatement deleteAllRows(Connection connection, String tableName) throws SQLException
    {
        if(doesTableExist(connection, tableName))
        {
            String query = "DELETE FROM " + tableName + ";";

            PreparedStatement stmt = connection.prepareStatement(query);

            return stmt;
        }
        
        return null;
    }
    
    public static PreparedStatement insertIntoTableMultipleRows(Connection connection, ArrayList<String> columnNames, Object[][] dataValues, String tableName) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
            
        String query = "INSERT INTO " + tableName + " ( ";

        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);

            if(i == columnNames.size() - 1)
            {
                query = query + columnName + ") ";                        
            }            
            else
            {
                query = query + columnName + ", ";        
            }
            
        }

        query = query + " VALUES ";                        

        for (int i = 0; i < dataValues.length; i++)
        {
            query = query + " ( ";

            for (int j = 0; j < dataValues[i].length; j++)
            {
                int columnIndex = j;
                int dataType = rsMetaData.getColumnType(columnIndex + 1);
                
                Object value = dataValues[i][j];

                if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                        || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                        || dataType == Types.TIMESTAMP_WITH_TIMEZONE || dataType == Types.NULL)
                {
                    query = query + "'" + value + "'";
                }
                else
                {
                    query = query + value;                
                }

                if(j == dataValues[i].length - 1)
                {
                    if(i == dataValues.length - 1)
                    {
                        query = query + ") ";
                    }
                    else
                    {
                        query = query + "),\n ";                        
                    }
                }
                else
                {
                    query = query + ", ";        
                }
            }
        }
        
//        query = query.substring(0, query.length() - 2);
        
        query = query + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }
    
    public static PreparedStatement deleteFromTable(Connection connection, TreeMap<String, String> conditions, String tableName, boolean andConditions) throws SQLException
    {
        PreparedStatement stmt = getAllColumnsDataFromTable(connection, tableName);
        
        ResultSet resultSet = stmt.executeQuery();
        
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
            
        String query = "DELETE FROM " + tableName + " ";

        String lastKey = conditions.lastKey();
        
        query = query + " WHERE ";

        lastKey = conditions.lastKey();

        for (Map.Entry<String,String> entry : conditions.entrySet()) 
        {
            int columnIndex = resultSet.findColumn(entry.getKey());
            int dataType = rsMetaData.getColumnType(columnIndex);

            if(dataType == Types.CHAR || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR || dataType == Types.NCHAR || dataType == Types.VARCHAR
                    || dataType == Types.DATE || dataType == Types.TIME || dataType == Types.TIMESTAMP || dataType == Types.TIME_WITH_TIMEZONE
                    || dataType == Types.TIMESTAMP_WITH_TIMEZONE)
            {
                query = query + entry.getKey() + " = '" + entry.getValue() + "'";
            }
            else
            {
                query = query + entry.getKey() + " = " + entry.getValue();                
            }

            if(entry.getKey().equals(lastKey) == false)
            {
                if(andConditions)
                    query = query + " AND ";
                else
                    query = query + " OR ";                        
            }
        }
        
        query = query + ";";
        
        System.out.println("Query prepared:" + query);
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        return statement;
    }
}
