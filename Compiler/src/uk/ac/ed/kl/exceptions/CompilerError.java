package uk.ac.ed.kl.exceptions;

public abstract class CompilerError extends Exception
{
	private static final long serialVersionUID = 1L;
		
	public abstract int getLineNumber();
	public abstract int getColumnNumber();
	public abstract ErrorType getType();
	public abstract String getUnitName();
	
	public CompilerError() { }
	public CompilerError(String message) { super(message); }
}
