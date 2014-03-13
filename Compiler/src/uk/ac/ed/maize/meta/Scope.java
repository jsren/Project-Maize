package uk.ac.ed.maize.meta;

import uk.ac.ed.maize.Freezable;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;

public interface Scope extends Freezable
{
	Scope getParentScope();
	
	void addSubtype(Type child) throws CompilerError, ObjectFrozenException;
	void addMember(Member child) throws CompilerError, ObjectFrozenException;
	
	Member[] getMembers();
	Type  [] getSubtypes();
	
}
