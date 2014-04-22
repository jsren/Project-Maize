package uk.ac.ed.maize.meta;

import java.util.EnumSet;

import uk.ac.ed.maize.code.Expression;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.parser.Attribute;

public class Field extends Member
{
	private static final long serialVersionUID = 1L;
	
	private Expression value;
	private Integer    offset;
	private boolean    initialised;
	private Integer    manualSize;
	
	public Integer getOffset()          { return this.offset; }
	public boolean getIsInitialised()   { return this.initialised; }
	public Expression getInitialValue() { return this.value; }
		
	public void setOffset(int offset) throws ObjectFrozenException
	{
		assertFrozen();
		this.offset = offset;
	}
	
	@Override
	public Integer getSize()
	{
		if (this.manualSize != null) return this.manualSize;
		else return super.getSize();
	}
	@Override
	public void setSize(int size) throws ObjectFrozenException
	{
		super.setSize(this.manualSize == null ? size : this.manualSize);
	}
	
	public Field(int line, Scope scope, String name, TypeRef type, Visibility vis, 
			EnumSet<Attribute> atts, Expression initValue)
	{
		super(line, scope, name, type, vis, atts, atts.contains(Attribute.Const) 
				? MemberType.Constant : MemberType.Variable);
		
		this.value       = initValue;
		this.initialised = initValue != null;
	}
	
	public Field(int line, Scope scope, String name, TypeRef type, Visibility vis, 
			EnumSet<Attribute> atts, int manualSize)
	{
		super(line, scope, name, type, vis, atts, atts.contains(Attribute.Const) 
				? MemberType.Constant : MemberType.Variable);
		
		this.value       = null;
		this.initialised = false;
		this.manualSize  = manualSize;
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
