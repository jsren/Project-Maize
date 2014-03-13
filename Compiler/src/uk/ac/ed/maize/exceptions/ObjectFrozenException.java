package uk.ac.ed.maize.exceptions;

public class ObjectFrozenException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private Object object;
	
	public Object getObject() { return this.object; }
	
	public ObjectFrozenException(Object obj) {
		this.object = obj;
	}
}
