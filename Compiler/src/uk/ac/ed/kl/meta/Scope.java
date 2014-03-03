package uk.ac.ed.kl.meta;

import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;

public interface Scope extends Freezable
{
	Scope getParentScope();
	
	void addSubtype(Type child) throws CompilerError, ObjectFrozenException;
	void addMember(Member child) throws CompilerError, ObjectFrozenException;
	
	Member[] getMembers();
	Type  [] getSubtypes();
	
}
