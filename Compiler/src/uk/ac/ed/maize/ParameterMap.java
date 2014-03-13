package uk.ac.ed.maize;

import java.util.HashMap;

public class ParameterMap
{
	private HashMap<String, Boolean> map;
	
	public ParameterMap()
	{
		this.map = new HashMap<String, Boolean>();
	}
	
	public void set(String key, Boolean value)
	{
		this.map.put(key, value);
	}
	
	public boolean get(String key)
	{
		if (!map.containsKey(key))
		{
			return false;
		}
		else return map.get(key);
	}
}
