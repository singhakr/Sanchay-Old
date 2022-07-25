package sanchay.filters;

import java.util.*;
import sanchay.GlobalProperties;

/**
*/
public class Unit implements Cloneable
{
	String quantity;
	String qtype;
	
	// Names for the base unit, the first one being the 'main'
	Vector base_unit;
	
	// The key will be the 'main' name of the unit and the value
	// will be a Vector of other units
	Hashtable unit_names;
	
	// The key will be the 'main' name of the unit and the value
	// will be a String denoting the formula to be used for conversion
	Hashtable conversions;

	public Unit()
	{
	}
	
	public String getQuantity() 
	{
		return quantity;
	}
	
	public void setQuantity(String q) 
	{
		quantity = q;
	}
	
	public String getQuantityType() 
	{
		return qtype;
	}
	
	public void setQuantityType(String t)
	{
		qtype = t;
	}
	
	public Vector getBaseUnit() 
	{
		return base_unit;
	}
	
	public void setBaseUnit(Vector b)
	{
		base_unit = b;
	}
	
	public Enumeration getOtherUnitMainNames()
	{
		return unit_names.keys();
	}
	
	public int countOtherUnits()
	{
		return unit_names.size();
	}
	
	public void addOtherUnit(String main_name, Vector other_names, String coversion_formula)
	{
		unit_names.put(main_name, other_names);
		conversions.put(main_name, coversion_formula);
	}
	
	public void removeOtherUnit(String main_name)
	{
		unit_names.remove(main_name);
		conversions.remove(main_name);
	}
	
	public Object clone()
	{
		try
		{
			Unit obj = (Unit) super.clone();

			obj.quantity = quantity;
			obj.qtype = qtype;
			obj.base_unit = (Vector) base_unit.clone();
			obj.unit_names = (Hashtable) unit_names.clone();
			obj.conversions = (Hashtable) conversions.clone();

			return obj;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
		}
	}
}
