package uk.ac.ed.kl.exceptions;

import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.parser.UnitContext;

public class ParserError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	int line;
	int column;
	
	UnitContext context;
	ErrorType   type;
	
	@Override
	public int getLineNumber()   { return this.line; }
	@Override
	public int getColumnNumber() { return this.column; }
	@Override
	public ErrorType getType()   { return this.type; }
	
	@Override
	public String getUnitName()
	{
		if (this.context.getCodeUnit() != null)
			return this.context.getCodeUnit().getName();
		else return null;
	}
	
	public ParserError(String message, UnitContext context, ErrorType type)
	{
		super(message);
		this.type    = type;
		this.context = context;
	}
	public ParserError(String message, UnitContext context, Token badToken, ErrorType type)
	{
		super(message);
		this.line    = badToken.getLineIndex();
		this.column  = badToken.getCharIndex();
		this.type    = type;
		this.context = context;
	}
}
