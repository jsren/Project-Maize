package uk.ac.ed.kl.meta;

import java.io.Serializable;
import java.util.EnumSet;

import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.Visibility;

public abstract class Member implements Serializable, Freezable
{
	private static final long serialVersionUID = 1L;
	
	private boolean frozen;
	
	private int        line;
	private String     name;
	private MemberType type;
	private TypeRef    valueType;
	private boolean    instance;
	private String     docstring;
	private Integer    offset;
	private Integer    size;
	private Scope      scope;
	
	private Visibility         visbility;
	private EnumSet<Attribute> attributes;
	
	public int        getLine()   { return this.line; }
	public MemberType getType()   { return this.type; }
	public String     getName()   { return this.name; }
	public Integer    getSize()   { return this.size; }
	public Integer    getOffset() { return this.offset; }
	public Scope      getScope()  { return this.scope; }
	
	public boolean            getIsInstance() { return this.instance; }
	public TypeRef            getValueType()  { return this.valueType; }
	public Visibility         getVisibility() { return this.visbility; }
	public EnumSet<Attribute> getAttributes() { return this.attributes; }
	
	
	public Member(int line, Scope scope, String name, TypeRef type, Visibility vis,
			EnumSet<Attribute> atts, MemberType member)
	{
		this.line       = line;
		this.name       = name;
		this.valueType  = type;
		this.visbility  = vis;
		this.type       = member;
		this.attributes = atts;
		this.scope      = scope;
		
		// If static and/or const (also implies static), then it's not an
		// instance member
		this.instance = !atts.contains(Attribute.Static) && !atts.contains(Attribute.Const);
	}
	
	public abstract boolean matchesSignature(Member member);
	
	public void setSize(int size) throws ObjectFrozenException
	{
		this.assertFrozen();
		this.size = size;
	}
	public void setOffset(int offset) throws ObjectFrozenException
	{
		this.assertFrozen();
		this.offset = offset;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Member)
		{
			Member member = (Member)obj;
			return member.instance == this.instance && member.name.equals(this.name);
		}
		else return super.equals(obj);
	}
	
	/* == FROZEN IMPLEMENTATION == */
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
	
	protected void assertFrozen() throws ObjectFrozenException { 
		if (this.frozen) throw new ObjectFrozenException(this); 
	}
}
