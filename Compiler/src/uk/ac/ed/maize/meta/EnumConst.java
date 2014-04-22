package uk.ac.ed.maize.meta;

import java.util.EnumSet;

import uk.ac.ed.maize.code.Expression;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.EvaluationError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.parser.Attribute;
import uk.ac.ed.maize.parser.ParserContext;

public class EnumConst extends Member
{
	private static final long serialVersionUID = 1L;
	
	private Expression value;
	private long       longValue;
	
	public long   getLongValue() { return longValue; }

	public EnumConst(ParserContext context, TypeRef type, Token name, Expression value, Visibility vis) 
			throws CompilerError
	{
		super(name.getLineIndex(), context.getCurrentScope(), name.getLexeme(), type, 
				vis, EnumSet.of(Attribute.Const), MemberType.EnumConst);
		
		this.value = value;
		
		if (value.getHasValue())
		{
			try
			{ 
				this.longValue = Long.parseLong(this.getValue().getLexeme());
			}
			catch (Exception e) {
				throw new EvaluationError("Invaid enumeration constant - expected number");
			}
		}
	}

	public Token getValue() throws CompilerError { 
		return value.getTokenValue(); 
	}
	
	@Override
	public boolean matchesSignature(Member member)
	{
		return member.getType() == MemberType.EnumConst && 
				member.getName() == this.getName();
	}
}
