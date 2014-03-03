package uk.ac.ed.kl.exceptions;

public class LexerError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	private int line;
	private int column;
	private String unitName;
	private ErrorType type;
	
	public LexerError(String message, String unitName, int line, int column, ErrorType type)
	{
		super(message);
		this.line     = line;
		this.column   = column;
		this.type     = type;
		this.unitName = unitName;
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

	@Override
	public String getUnitName() {
		return this.unitName;
	}
}
