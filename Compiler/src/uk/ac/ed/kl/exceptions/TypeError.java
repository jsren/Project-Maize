package uk.ac.ed.kl.exceptions;

import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.meta.Member;
import uk.ac.ed.kl.meta.Type;
import uk.ac.ed.kl.parser.UnitContext;

public class TypeError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	private int line;
	private int column;
	private Type        type;
	private Member      member;
	private UnitContext context;
	
	@Override
	public int getLineNumber()   { return this.line; }
	@Override
	public int getColumnNumber() { return this.column; }
	@Override
	public ErrorType getType()   { return ErrorType.TypeError; }
	@Override
	public String getUnitName()  { return this.context.getCodeUnit().getName(); }
	
	public Member getMember()    { return this.member; }
	
	public TypeError(String message, UnitContext context, Token token)
	{
		super(message);
		this.line     = token.getLineIndex();
		this.column   = token.getCharIndex();
	}
	public TypeError(String message, Type type) 
	{
		super(message);
		this.type     = type;
		this.line     = 0;
		this.column   = 0;
	}
	public TypeError(String message, Member member) 
	{
		super(message);
		this.line     = member.getLine();
		this.column   = 0;
		this.member   = member;
	}
	public TypeError(String message, Type type, Member member) 
	{
		super(message);
		this.line    = member.getLine();
		this.column   = 0;
		this.member   = member;
		this.type     = type;
	}
	
	public Type getObjectType()
	{
		if (this.type == null) return this.context.getCurrentType();
		else return this.type;
	}

	
}
