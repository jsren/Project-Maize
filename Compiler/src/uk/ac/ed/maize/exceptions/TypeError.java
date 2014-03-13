package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Type;

public class TypeError extends CompilerError
{
	private static final long serialVersionUID = 1L;
	
	private Type type;
	private Member member;
	
	@Override
	public ErrorType getType() { return ErrorType.TypeError; }
	
	public TypeError(String message, Type type) 
	{
		super(message);
		this.type = type;
	}
	public TypeError(String message, Member member) 
	{
		super(message);
		this.member = member;
	}
	public TypeError(String message, Type type, Member member) 
	{
		super(message);
		this.member = member;
		this.type   = type;
	}
	
	public Member getMember() { 
		return this.member; 
	}
	
	public Type getObjectType() {
		return this.type;
	}
}
