package uk.ac.ed.kl;

import uk.ac.ed.kl.exceptions.CompilerError;

public class Log
{	
	public void warn(String message)
	{
		System.out.println("[WARN ] <stdout> "+message);
	}
	
	public void warn(String filename, String message)
	{
		System.out.println(String.format("[WARN ] <%s> %s", filename, message));
	}
	
	public void warn(CompilerError warning)
	{
		System.out.println(String.format("[WARN ] (%s) <%s:%s:%s> %s", 
				warning.getType(),         warning.getUnitName(), warning.getLineNumber(), 
				warning.getColumnNumber(), warning.getMessage()
		));
	}
	
	public void error(String message)
	{
		System.out.println("[ERROR] <stderr> "+message);
		System.exit(-1);
	}
	
	public void error(CompilerError error)
	{
		System.out.println(String.format("[ERROR] (%s) <%s:%s:%s> %s", 
				error.getType(),         error.getUnitName(), error.getLineNumber(), 
				error.getColumnNumber(), error.getMessage()
		));
		System.exit(-1);
	}
}
