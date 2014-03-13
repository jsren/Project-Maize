package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;

public abstract class CompilerError extends Exception
{
	private static final long serialVersionUID = 1L;
		
	public abstract ErrorType getType();
	
	public CompilerError() { }
	public CompilerError(String message) { super(message); }
	
	public Token getToken() { return null; }
}
