package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;


public final class EvaluationError extends CompilerError
{
	private static final long serialVersionUID = 1L;

	private Token badToken;
	
	public EvaluationError(String msg) {
		super(msg);
	}
	public EvaluationError(String msg, Token token)
	{
		super(msg);
		this.badToken = token;
	}
	
	@Override
	public Token getToken() { return this.badToken; }
	@Override
	public ErrorType getType() { return ErrorType.EvaluationError; }
}
