package uk.ac.ed.kl.exceptions;

import uk.ac.ed.kl.lexer.Token;

public class NameError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	int line;
	int column;
	
	String name;
	String filename;
	
	public String getInvalidName() { return this.name; }
	
	public NameError(String filename, Token token)
	{
		super("Invalid reference");
		this.line     = token.getLineIndex();
		this.column   = token.getCharIndex();
		this.name     = token.getLexeme();
		this.filename = filename;
	}
	public NameError(String message, String filename, Token token)
	{
		super(message);
		this.line     = token.getLineIndex();
		this.column   = token.getCharIndex();
		this.name     = token.getLexeme();
		this.filename = filename;
	}
	
	@Override
	public int getLineNumber() { return this.line; }
	@Override
	public int getColumnNumber() { return this.column; }
	@Override
	public ErrorType getType() { return ErrorType.NameError; }
	@Override
	public String getUnitName() { return this.filename; }

}
