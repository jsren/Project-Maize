package uk.ac.ed.kl.meta;

import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.TypeError;
import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.Visibility;

public class Method extends Member implements Parameterised
{
	private static final long serialVersionUID = 1L;

	private boolean hasBody;
	private boolean isConstructor;
	private Parameter[] params;
	
	public Parameter[] getParameters()    { return this.params; }
	public boolean     getReturnsVoid()   { return this.getValueType() == null; }
	public boolean     getIsConstructor() { return this.isConstructor; }
	public boolean     getHasBody()       { return this.hasBody; }
	
	public Method(int line, Scope scope, String name, TypeRef type, Visibility vis,
			EnumSet<Attribute> atts, boolean constructor, boolean hasBody,
			Parameter[] params) throws CompilerError
	{
		super(line, scope, name, type, vis, atts, MemberType.Function);
		
		this.params        = params;
		this.hasBody       = hasBody;
		this.isConstructor = constructor;
	}
	
	public void validate() throws CompilerError
	{
		EnumSet<Attribute> atts = this.getAttributes();
		
		// This is horrible, but readable (-ish) and
		// efficient (-ish). Java enums, everyone...
		boolean abs = atts.contains(Attribute.Abstract);
		boolean asm = atts.contains(Attribute.Asm);
		boolean cst = atts.contains(Attribute.Const);
		boolean xpl = atts.contains(Attribute.Explicit);
		boolean exp = atts.contains(Attribute.Export);
		boolean ext = atts.contains(Attribute.Extern);
		boolean inl = atts.contains(Attribute.Inline);
		boolean ovr = atts.contains(Attribute.Override);
		boolean sea = atts.contains(Attribute.Sealed);
		boolean sta = atts.contains(Attribute.Static);
		boolean vrt = atts.contains(Attribute.Virtual);
		
		// Whole set of rules for constructors
		if (this.isConstructor)
		{
			if (abs || exp || ext || ovr || sea || vrt)
					throw new TypeError("Construtors cannot be marked abstract, export, " +
							"extern, override, sealed or virtual", this);
			if (!this.hasBody)
				throw new TypeError("Constructors must provide a method body", this);
		}
		else if (xpl) throw new TypeError("Only constructors can be marked explicit", this);
		
		// Check for method body
		if (this.hasBody)
		{
			if (abs) throw new TypeError("Abstract methods cannot provide a method body", this);
			if (ext) throw new TypeError("External methods cannot provide a method body", this);
		}
		else if (!(abs || ext)) throw new TypeError("Method must define a method body", this);
		
		// Check for inheritance stuff
		if (abs)
		{
			if (vrt) throw new TypeError("Only one of abstract and virtual can be applied", this); 
			if (inl) throw new TypeError("Abstract methods cannot be marked inline",        this);
			if (sta) throw new TypeError("Abstract methods cannot be marked static",        this);
		}
		if (sta)
		{
			// Static virtual members may be included in later releases
			if (vrt || abs) throw new TypeError("Static methods cannot be marked abstract or virtual", this);
			
		}
	}
	
	@Override
	public String toString()
	{
		// Format attribute list
		String atstr = "";
		for (Attribute a : this.getAttributes()) { atstr += a.toString() + ' '; }
		
		// Format parameter list
		int p = 0; String paramstr = "";
		for (; p < params.length - 1; p++) { paramstr += params[p].toString() + ", "; }
		if (params.length != 0) paramstr += params[p].toString();
		
		// Add braces if body given
		return String.format("%s %s%s %s(%s) %s", this.getVisibility().toString().toLowerCase(), atstr, 
				this.getValueType(), this.getName(), paramstr, this.hasBody ? "{ ... }" : "");
	}
	
	@Override
	public boolean matchesSignature(Member member)
	{
		if (member.getType() == MemberType.Function)
		{
			// Compare name and return type
			if (!member.getName().equals(this.getName()) ||
					!member.getValueType().equals(this.getValueType()))
			{
				return false;
			}
			// If it's a function, it should be an instance of Parameterised
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
