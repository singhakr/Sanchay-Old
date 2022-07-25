package sanchay.text.diff;

public class SSString
{
	public static String[] stringToArray(String str)
	{
		str = str.replaceFirst("/", "");
		str = str.substring(0, str.lastIndexOf('/'));
		String parts[] = str.split("/");

		return parts;
	}

	public static String arrayToString(String[] array)
	{
		String str = "/";

		for(int i = 0; i < array.length; i++)
		{
			str += array[i] + "/";
		}

		return str;
	}
}
