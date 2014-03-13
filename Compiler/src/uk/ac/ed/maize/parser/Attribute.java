package uk.ac.ed.maize.parser;

public enum Attribute
{
	Abstract,
	Asm,
	Builtin,
	Const,
	Explicit,
	Export,
	Extern,
	Inline,
	Override,
	Sealed,
	Static,
	Virtual,
	Volatile;
	
	public static final Attribute parse(String name)
	{
		for (Attribute a : Attribute.values()) {
			if (a.name().toLowerCase().equals(name)) return a;
		}
		return null;
	}
}