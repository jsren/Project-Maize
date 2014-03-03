package uk.ac.ed.kl.parser;

public enum Visibility
{
	Public,
	Internal,
	Protected,
	Private;
	
	public static final Visibility parse(String name)
	{
		for (Visibility vis : Visibility.values()) {
			if (vis.name().toLowerCase().equals(name)) return vis;
		}
		return null;
	}
}
