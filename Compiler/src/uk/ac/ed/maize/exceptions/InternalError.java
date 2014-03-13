package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;

public class InternalError extends CompilerError
{
	private static final long serialVersionUID = 1L;

	private Token token;
	
	public InternalError(String msg) {
		super(msg);
	}
	public InternalError(String msg, Token token)
	{
		super(msg);
		this.token = token;
	}
	
	@Override
	public ErrorType getType() { return ErrorType.Internal; }
	@Override
	public Token getToken() { return this.token; }
	
}
