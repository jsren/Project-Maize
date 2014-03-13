package uk.ac.ed.maize.meta;

public class Parameter
{
	private TypeRef type;
	private String  name;
	private boolean params;
	private String  docstring;
	
	public TypeRef getTypeRef()  { return this.type; }
	public String  getName()     { return this.name; }
	public boolean getIsParams() { return this.params; }
	
	public Parameter(String name, TypeRef type, boolean params)
	{
		this.name   = name;
		this.type   = type;
		this.params = params;
	}
	
	@Override
	public String toString()
	{
		return (this.params ? "params " : "") + this.type.toString() + ' ' + this.name;
	}
	
	public boolean equals(Parameter param)
	{
		return param.getName().equals(this.getName()) 
				&& param.getTypeRef().equals(this.getTypeRef());
	}
}
