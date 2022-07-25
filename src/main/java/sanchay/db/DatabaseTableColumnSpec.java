/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sanchay.db;

/**
 *
 * @author User
 */
public class DatabaseTableColumnSpec {
    
    public String columnName;
    public String dataType;
    public int dataTypeSize;
    public boolean isPrimaryKey = false;
    public boolean isNotNULL = false;
    public boolean isUnique = false;
    public String defaultValue = "NULL";
}
