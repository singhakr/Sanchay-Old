package sanchay.filters;

import sanchay.GlobalProperties;

/**
*/
public class SanchayNumber implements Cloneable
{
	String data_type;
	String value_string;
	Object value_obj; // Integer or Long etc.
	boolean is_in_words;
	
	public SanchayNumber()
	{
	}
	
	public String getDataType() 
	{
		return data_type;
	}
	
	public void setDataType(String t) 
	{
		data_type = t;
	}
	
	public String getValue() 
	{
		return value_string;
	}
	
	public void setValue(String v) 
	{
		value_string = v;
	}
	
	public Object getValueObject() 
	{
		return value_obj;
	}
	
	public void setValueObject(Object o)
	{
		value_obj = o;
	}
	
	public boolean isInWords() 
	{
		return is_in_words;
	}
	
	public Object clone()
	{
		try
		{
			SanchayNumber obj = (SanchayNumber) super.clone();

			obj.data_type = data_type;
			obj.value_string = value_string;
//			obj.value_obj = value_obj.clone(); // Integer or Long etc.
			obj.is_in_words = is_in_words;

			return obj;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(GlobalProperties.getIntlString("But_the_class_is_Cloneable!!!"));
		}
	}
}
