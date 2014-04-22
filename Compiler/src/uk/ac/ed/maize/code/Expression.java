package uk.ac.ed.maize.code;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public abstract class Expression
{
	protected ParserContext context;
	
	public abstract boolean getHasValue();
	public abstract TypeRef getValueType() throws CompilerError;
	public abstract Token   getTokenValue() throws CompilerError;
	
	public abstract ExpressionType getType();
	
	
	public Expression(ParserContext context)
	{
		this.context = context;
		context.freeze();
	}
	
	public Expression[] getSubParts()
	{
		final Expression[] empty = new Expression[0];
		return empty;
	}
}
