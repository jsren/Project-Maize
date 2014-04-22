package uk.ac.ed.maize.linker;

public final class ManglerFactory
{
	public static final int MANGLER_MAIZE = 0;
	public static final int MANGLER_GCC3  = 1;
	
	public Mangler createInstance(int manglerTypeID)
	{
		switch (manglerTypeID)
		{
		case MANGLER_MAIZE:
			return new MaizeMangler();
		}
		return null;
	}
	
}
