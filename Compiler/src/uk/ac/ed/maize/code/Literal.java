package uk.ac.ed.maize.code;

import uk.ac.ed.maize.base.BooleanType;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.TypeRef;

public class Literal extends Expression
{
	Token value;
	
	public Literal(Token value)
	{
		super(null);
		this.value = value;
	}
	
	@Override
	public Token getTokenValue() { return value; }

	@Override
	public boolean getHasValue() { return true; }

	@Override
	public TypeRef getValueType() throws CompilerError
	{
		TokenType type = value.getType();
		
		switch(type)
		{
		case BooleanLiteral: 
			return BooleanType.getBasicTypeRef();
		case CharacterLiteral:
			return BooleanType.getBasicTypeRef();
		case FloatLiteral:
			return BooleanType.getBasicTypeRef();
		case HexLiteral:
			return BooleanType.getBasicTypeRef();
		case IntegerLiteral:
			return BooleanType.getBasicTypeRef();
		case StringLiteral:
			return BooleanType.getBasicTypeRef();			
		}
		
		throw new InternalError("Unknown literal type", value);
	}

	@Override
	public ExpressionType getType()	{
		return ExpressionType.Literal;
	}

}
