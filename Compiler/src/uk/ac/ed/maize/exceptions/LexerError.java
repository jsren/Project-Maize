package uk.ac.ed.maize.exceptions;

public class LexerError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	private int line;
	private int column;
	private ErrorType type;
	
	public LexerError(String message, int line, int column, ErrorType type)
	{
		super(message);
		this.line     = line;
		this.column   = column;
		this.type     = type;
	}
	
	public int getLineNumber() {
		return this.line;
	}
	public int getColumnNumber() {
		return this.column;
	}

	@Override
	public ErrorType getType() {
		return this.type;
	}
}
