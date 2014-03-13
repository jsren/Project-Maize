package uk.ac.ed.maize;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.CompilerException;
import uk.ac.ed.maize.meta.CodeUnit;

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
		// TODO
	}
	
	public void error(String message)
	{
		System.out.println("[ERROR] <stderr> "+message);
		System.exit(-1);
	}
	
	public void error(CompilerException error)
	{
		CodeUnit unit     = error.getCodeUnit();
		String   unitName = unit != null ? unit.getName() : "unknown";
		
		System.out.println(String.format("[ERROR] (%s) <%s:%s:%s> %s", 
				error.getType(),         unitName,
				error.getLineNumber(),   error.getColumnNumber(), error.getMessage()
		));
		System.exit(-1);
	}
}
