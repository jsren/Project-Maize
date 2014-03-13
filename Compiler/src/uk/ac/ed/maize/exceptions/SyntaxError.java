package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;

public class SyntaxError extends CompilerError
{
	private static final long serialVersionUID = 1L;

	private Token token;
	
	public SyntaxError(Token token)
	{
		super("Invalid syntax");
		this.token = token;
	}
	public SyntaxError(String message, Token token)
	{
		super(message);
		this.token = token;
	}
	
	@Override
	public ErrorType getType() { return ErrorType.SyntaxError; }
	@Override
	public Token getToken() { return this.token; }
}
