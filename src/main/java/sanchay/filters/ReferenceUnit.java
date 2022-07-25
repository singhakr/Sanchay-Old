package sanchay.filters;

import sanchay.GlobalProperties;

/**
*/
public class ReferenceUnit implements Cloneable
{
	String quantity;
	String qtype;
	String name;

	public ReferenceUnit()
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
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public Object clone()
	{
		try
		{
			ReferenceUnit obj = (ReferenceUnit) super.clone();

			obj.quantity = quantity;
			obj.qtype = qtype;
			obj.name = name;

			return obj;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
		}
	}
}
