package sanchay.filters;

import sanchay.GlobalProperties;

/**
*/
public class Quantity implements Cloneable
{
	SanchayNumber number;
	Unit unit;
	
	public Quantity()
	{
	}
	
	public SanchayNumber getNumber() 
	{
		return number;
	}
	
	public void setNumber(SanchayNumber n) 
	{
		number = n;
	}
	
	public Unit getUnit() 
	{
		return unit;
	}
	
	public void setUnit(Unit u) 
	{
		unit = u;
	}
	
	public Object clone()
	{
		try
		{
			Quantity obj = (Quantity) super.clone();

			obj.number = (SanchayNumber) number.clone();
			obj.unit = (Unit) unit.clone();

			return obj;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
		}
	}
}
