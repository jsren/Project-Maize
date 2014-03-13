package uk.ac.ed.maize;

import uk.ac.ed.maize.exceptions.ObjectFrozenException;

public abstract interface MetaReference<T> extends Freezable
{	
	public abstract boolean getIsResolved();
	public abstract void resolveReference(T value) throws ObjectFrozenException;
}
