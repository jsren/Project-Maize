package uk.ac.ed.kl.meta;

import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.Visibility;

public class Field extends Member
{
	private static final long serialVersionUID = 1L;
	
	private Expression value;
	private Integer    offset;
	private boolean    initialised;
	
	public Integer getOffset()          { return this.offset; }
	public boolean getIsInitialised()   { return this.initialised; }
	public Expression getInitialValue() { return this.value; }
		
	public void setOffset(int offset) throws ObjectFrozenException
	{
		assertFrozen();
		this.offset = offset;
	}
	
	public Field(int line, Scope scope, String name, TypeRef type, Visibility vis, 
			EnumSet<Attribute> atts, Expression initValue)
	{
		super(line, scope, name, type, vis, atts, atts.contains(Attribute.Const) 
				? MemberType.Constant : MemberType.Variable);
		
		this.value       = initValue;
		this.initialised = initValue != null;
	}
	
	@Override
	public boolean matchesSignature(Member member)
	{
		if (member.getType() == MemberType.Constant 
				|| member.getType() == MemberType.Variable)
		{
			return member.getName().equals(this.getName()) && 
					member.getValueType().equals(this.getValueType());
		}
		return false;
	}
}
