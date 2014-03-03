package uk.ac.ed.kl.meta;

import java.io.Serializable;
import java.util.EnumSet;

import uk.ac.ed.kl.CachedArrayList;
import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.exceptions.TypeError;
import uk.ac.ed.kl.parser.Attribute;
import uk.ac.ed.kl.parser.Visibility;

public class Type implements Scope, Serializable, Freezable
{
	private static final long serialVersionUID = 1L;
	
	private boolean frozen;
	
	private Integer   size;
	private Scope     parent;
	private boolean   isAbstract;
	private boolean   isSealed;
	private boolean   isStatic;
	private TypeRef[] baseTypes;
	private String    name;
	private Integer   alignment;
	private boolean   customPadding;
	
	private Visibility visibility;
	private CachedArrayList<Type>   subtypes;
	private CachedArrayList<Member> members;
	
	@Override
	public Scope getParentScope() { return this.parent; }
	
	public boolean   getIsAbstract() { return this.isAbstract; }
	public boolean   getIsSealed()   { return this.isSealed; }
	public boolean   getIsStatic()   { return this.isStatic; }
	public TypeRef[] getBaseTypes()  { return this.baseTypes; }
	public Integer   getAlignment()  { return this.alignment; }
	
	public Visibility getVisibility() { return this.visibility; }
	
	public boolean getHasCustomPadding() { return this.customPadding; }
	
	
	public Integer getSize() { return this.size; }
	public String  getName() { return this.name; }
	
	public void setSize(int size) throws ObjectFrozenException
	{ 
		this.assertFrozen();
		this.size = size;
	}
	
	public void setAlignment(int size) throws ObjectFrozenException
	{
		this.assertFrozen();
		this.alignment = size;
	}
	
	public void setHasCustomPadding(boolean value) throws ObjectFrozenException
	{
		this.assertFrozen();
		this.customPadding = value;
	}
	
	public boolean getIsPODType()
	{
		try { this.assertIsPODType(); return true; }
		catch (TypeError e) { return false; }
	}
	
	public void assertIsPODType() throws TypeError
	{
		if (this.baseTypes.length != 0)
			throw new TypeError("POD types cannot inherit from other types", this);
		if (this.isAbstract || this.isStatic)
			throw new TypeError("POD types cannot be marked 'abstract' or 'static'", this);
		if (this.subtypes.size() != 0)
			throw new TypeError("POD types cannot contain subtypes", this);
		
		for (Member member : this.members)
		{
			if (member.getType() == MemberType.Variable)
			{
				if (member.getAttributes().contains(
						EnumSet.of(Attribute.Abstract, Attribute.Virtual)))
					throw new TypeError("Members of POD types cannot be marked 'abstract' or 'virtual'", this, member);
				
				if (!member.getValueType().getResolvedType().getIsPODType())
					throw new TypeError("Members of POD types must themselves be POD types", this, member);
			}
		}
	}
	
	public Type(Scope parentScope, String name, boolean abs, boolean sea, boolean sta,
			Visibility vis, TypeRef[] bases)
	{
		this.subtypes    = new CachedArrayList<>(Type.class);
		this.members     = new CachedArrayList<>(Member.class);
		this.parent      = parentScope;
		this.frozen      = false;
		this.isAbstract  = abs;
		this.isSealed    = sea;
		this.isStatic    = sta;
		this.visibility  = vis;
		this.name        = name;
		this.baseTypes   = bases;
	}

	@Override
	public void addMember(Member child) throws CompilerError, ObjectFrozenException
	{
		this.assertFrozen();
		if (this.members.contains(child))
		{
			throw new TypeError("Duplicate member", this, child);
		}
		else
		{
			this.members.add(child);
		}
	}
	@Override
	public void addSubtype(Type subtype) throws CompilerError, ObjectFrozenException
	{
		this.assertFrozen();
		if (this.subtypes.contains(subtype))
		{
			throw new TypeError("Duplicate type", subtype);
		}
		else
		{
			this.subtypes.add(subtype);
		}
	}
	
	@Override
	public Member[] getMembers() { return this.members.toArray(); }
	
	@Override
	public Type[] getSubtypes() { return this.subtypes.toArray(); }

	/* === FROZEN IMPLEMENTAION === */
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
	
	private void assertFrozen() throws ObjectFrozenException {
		if (this.frozen) throw new ObjectFrozenException(this);
	}
}
