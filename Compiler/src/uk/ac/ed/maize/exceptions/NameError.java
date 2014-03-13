package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;

public class NameError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	Token name;
	
	public NameError(Token token)
	{
		super("Invalid reference");
		this.name = token;
	}
	public NameError(String message, Token token)
	{
		super(message);
		this.name = token;
	}
	
	@Override
	public ErrorType getType() { return ErrorType.NameError; }
	
	@Override
	public Token getToken() { return this.name; }

}
