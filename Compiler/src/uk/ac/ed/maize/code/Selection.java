package uk.ac.ed.maize.code;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class Selection extends Expression
{
	Expression parent;
	Expression child;	
	
	public Selection(ParserContext context, Expression parent, Expression child)
	{
		super(context);
		
		this.parent = parent;
		this.child  = child;
	}
	
	public Expression getParent() {
		return this.parent;
	}
	public Expression getChild() {
		return this.child;
	}
	
	@Override
	public boolean getHasValue() {
		return false;
	}
	@Override
	public TypeRef getValueType() throws CompilerError {
		return null;
	}
	@Override
	public Token getTokenValue() throws CompilerError {
		return null;
	}
	@Override
	public ExpressionType getType() {
		return ExpressionType.Selection;
	}

}
