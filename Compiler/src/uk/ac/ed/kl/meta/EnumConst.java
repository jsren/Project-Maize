package uk.ac.ed.kl.meta;

import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.UnitContext;
import uk.ac.ed.kl.parser.Visibility;

public class EnumConst extends Member
{
	private static final long serialVersionUID = 1L;
	
	private Expression value;
	private long       longValue;
	
	public String getValue()     { return value.getValue(); }
	public long   getLongValue() { return longValue; }

	public EnumConst(UnitContext context, TypeRef type, Token name, Expression value, Visibility vis) throws ParserError
	{
		super(name.getLineIndex(), context.getCurrentScope(), name.getLexeme(), type, 
				vis, EnumSet.of(Attribute.Const), MemberType.EnumConst);
		
		this.value = value;
		
		if (value.getHasValue())
		{
			try { this.longValue = Long.parseLong(value.getValue()); }
			catch (Exception e)
			{				
				throw new ParserError("Invaid enumeration constant - expected number", context, 
						name, ErrorType.ValueError);
			}
		}
	}

	@Override
	public boolean matchesSignature(Member member)
	{
		return member.getType() == MemberType.EnumConst && 
				member.getName() == this.getName();
	}
}
