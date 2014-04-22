package uk.ac.ed.maize.code;

import uk.ac.ed.maize.Freezable;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class TypeCast extends Expression implements Freezable
{
	TypeRef    newType;
	Expression value;
	boolean    frozen;
	
	public TypeCast(ParserContext context, TypeRef type)
	{
		super(context);
		
		this.newType = type;
	}
	public TypeCast(ParserContext context, TypeRef type, Expression value)
	{
		super(context);
		
		this.newType = type;
		this.value   = value;
		this.freeze();
	}
	
	public boolean getHasExpression() {
		return this.value != null;
	}
	
	public void setExpression(Expression e) throws ObjectFrozenException
	{
		if (this.frozen) throw new ObjectFrozenException(this);
		this.value = e;
	}
	
	@Override
	public boolean getHasValue() {
		return this.value != null && this.value.getHasValue(); 
	}

	@Override
	public TypeRef getValueType() throws CompilerError { 
		return this.newType; 
	}

	@Override
	public Token getTokenValue() throws CompilerError {
		return value.getTokenValue();
	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.Cast;
	}

	@Override
	public void freeze() {
		this.frozen = true;
	}

	@Override
	public boolean getIsFrozen() {
		return this.frozen;
	}

}
