package uk.ac.ed.maize.meta;

import java.io.Serializable;

import uk.ac.ed.maize.CachedArrayList;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.TypeError;

public class Namespace implements Scope, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private boolean frozen;
	private String  name;
	private Scope   parent;
	
	protected CachedArrayList<Type>      types;
	protected CachedArrayList<Member>    members;
	protected CachedArrayList<Namespace> subspaces;
	
	public Namespace(String name, Scope parentScope)
	{
		this.name   = name;
		this.parent = parentScope;
		
		this.types     = new CachedArrayList<>(Type.class);
		this.members   = new CachedArrayList<>(Member.class);
		this.subspaces = new CachedArrayList<>(Namespace.class);
		
		// Find the code unit
		Scope scope = parentScope;
		while (true)
		{
			Scope parent = scope.getParentScope();
			if (parent == null) break;
			else scope = parent;
		}
	}
	
	public String getName() { return this.name; }
	
	@Override
	public Scope getParentScope() { return this.parent; }
	
	protected void assertFrozen() throws ObjectFrozenException {
		if (this.frozen) throw new ObjectFrozenException(this);
	}
	
	@Override
	public void addMember(Member child) throws CompilerError, ObjectFrozenException
	{
		this.assertFrozen();		
		// Accepts all but enum const
		if (child.getType() == MemberType.EnumConst)
		{
			// If you are here, something has gone horribly wrong
			throw new TypeError("Namespace cannot contain enum constant", child);
		}
		// else
		members.add(child);
	}
	
	@Override
	public void addSubtype(Type child) throws CompilerError, ObjectFrozenException
	{
		this.assertFrozen();
		if (this.types.contains(child))
		{
			throw new TypeError("Namespace contains class name already wut", child);
		}
		// else
		types.add(child);
	}
	
	public void addNamespace(Namespace child) throws CompilerError, ObjectFrozenException
	{
		this.assertFrozen();
		// Check if a namespace with the same name already exists
		int index = this.subspaces.indexOf(child);
		if (index != -1)
		{
			// Combine members into a single namespace
			this.subspaces.get(index).merge(child);
		}
		else this.subspaces.add(child);
	}
	
	public void merge(Namespace ns) throws ObjectFrozenException, CompilerError
	{
		this.assertFrozen(); ns.assertFrozen();
		
		for (Member    m : ns.members)   { this.addMember(m); }
		for (Type      t : ns.types)     { this.addSubtype(t); }
		for (Namespace n : ns.subspaces) { this.addNamespace(n); }
	}
	
	@Override
	public String toString()
	{
		String content = this.subspaces.size() == 0 && this.members.size() == 0 && this.types.size() == 0 ? "" : "... ";
		return "namespace " + this.name + " { " + content + "}";
	}
	
	@Override
	public Member[] getMembers() { return this.members.toArray(); }

	@Override
	public void freeze() { this.frozen = true; }

	@Override
	public boolean getIsFrozen() { return this.frozen; }

	@Override
	public Type[] getSubtypes() { return this.types.toArray(); }
	
	public Namespace[] getSubspaces() { return this.subspaces.toArray(); }

}
