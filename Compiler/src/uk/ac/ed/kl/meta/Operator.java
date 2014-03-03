package uk.ac.ed.kl.meta;

import java.util.EnumSet;

import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.Visibility;


public class Operator extends Member implements Parameterised
{
	private static final long serialVersionUID = 1L;
	
	private String operator;
	private Parameter[] params;
	
	public String      getOperator()    { return this.operator; }
	public Parameter[] getParameters()  { return this.params; }
	public boolean     getReturnsVoid() { return this.getValueType() == null; }

	public Operator(int line, Scope scope, String name, TypeRef type, Visibility vis,
			EnumSet<Attribute> atts)
	{
		super(line, scope, name, type, vis, atts, MemberType.Operator);
	}
	
	@Override
	public boolean matchesSignature(Member member)
	{
		if (member.getType() != MemberType.Operator)
		{
			// Compare name and return type
			if (!member.getName().equals(this.getName()) ||
					!member.getValueType().equals(this.getValueType()))
			{
				return false;
			}
			// If it's an operator, it should be an instance of Parameterised
			Parameter[] params = ((Parameterised)member).getParameters();
			if (params.length != this.params.length) return false;
			
			// Compare parameters
			for (int i = 0; i < params.length; i++) {
				if (!params[i].equals(this.params[i])) return false;
			}
		}
		return false;
	}
}
