package uk.ac.ed.maize.code;

import uk.ac.ed.maize.MetaReference;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.Method;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class FunctionCall extends Expression implements MetaReference<Method>
{
	private boolean frozen;
	
	private Method method;
	private Token  identifier;
	
	private Expression[] parameters;
	
	public FunctionCall(ParserContext context, Token identifier, Expression[] parameters)
	{
		super(context);
		
		this.identifier = identifier;
		this.parameters = parameters;
	}

	@Override
	public boolean getHasValue() { return false; }
	@Override
	public Token getTokenValue() { return null; }
	
	public Token        getMethodName()    { return this.identifier; }
	public Expression[] getParameters()    { return this.parameters; }
	public Method       getReolvedMethod() { return this.method; }

	@Override
	public TypeRef getValueType()
	{
		if (this.method == null) return null;
		else return this.method.getValueType();
	}

	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }

	@Override
	public boolean getIsResolved() { return this.method == null; }

	@Override
	public void resolveReference(Method value) throws ObjectFrozenException
	{
		if (this.frozen) throw new ObjectFrozenException(this);
		else             this.method = value;		
	}

	@Override
	public ExpressionType getType() { return ExpressionType.FunctionCall; }
}
