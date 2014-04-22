package uk.ac.ed.maize.code;

public class Variable
{
	private String  identifier;
	private int     size;
	private boolean isReference;
	private boolean isTemp;
	private boolean isIndex;
	private boolean isBusy;
	private boolean isLarge;
	
	public String getIdentifier() {
		return this.identifier;
	}
	public int getSize() { 
		return this.size;
	}
	public boolean getIsReference() {
		return this.isReference;
	}
	public void setIsReference(boolean value) {
		this.isReference = value;
	}
	public boolean getIsTemporary() {
		return this.isTemp;
	}
	public boolean getIsIndex() {
		return this.isIndex;
	}
	public boolean getIsBusy() {
		return this.isBusy;
	}
	public boolean getIsLarge() {
		return this.isLarge;
	}
	
	public Variable(String identifier, int size, boolean isRef)
	{
		this.identifier = identifier;
		this.size       = size;
	}
	
	public Variable(String identifier, int size, boolean isRef,
			boolean isTemp, boolean isIndex, boolean isBusy, boolean isLarge)
	{
		this.identifier  = identifier;
		this.size        = size;
		this.isReference = isRef;
		this.isTemp      = isTemp;
		this.isIndex     = isIndex;
		this.isBusy      = isBusy;
		this.isLarge     = isLarge;
	}
}
