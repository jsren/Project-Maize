package uk.ac.ed.maize.linker;

import uk.ac.ed.maize.ParameterMap;
import uk.ac.ed.maize.base.BaseType;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.MemberType;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;

public final class Sizer
{
	private static int ptr_size = BaseType.getPointerSize();
	
	private ParameterMap params;
	
	public Sizer(ParameterMap params) { this.params = params; }
	
	
	public int sizeType(Type type) throws ObjectFrozenException
	{
		// If it already has a size, just return that
		if (type.getSize() != null) return type.getSize();
		
		// Otherwise compute it
		int ignoreEnd;
		int totalSize = 0;
		
		Member[] members = type.getMembers();
		
		// Get a size for each member
		for (Member m : members) sizeOf(m);
		
		boolean orderBySize = this.params.get("auto-member-order") && 
				!type.getHasCustomPadding() && !type.getIsPODType();
		
		// Scan through each member determining
		// packing from alignment
		
		// Bubble sort in descending order of size
		// with instance variables at the front
		boolean finished = true;
		
		while (true)
		{
			int invalidCount = 0;
			for (int i = 0; i < members.length - 1; i++)
			{
				int sz0 = isValidMember(members[i])   ? members[i].getSize()   : 0;
				int sz1 = isValidMember(members[i+1]) ? members[i+1].getSize() : 0;
				
				if ((sz1 > sz0 && (orderBySize || sz0 == 0)))
				{
					Member temp  = members[i];
					members[i]   = members[i+1];
					members[i+1] = temp;
					finished     = false;
				}
				if (sz0 == 0) invalidCount++;
				if (i == members.length - 2 && sz1 == 0) invalidCount++;
			}
			if (finished) { ignoreEnd = invalidCount; break; }
		}
		
		if (members.length - ignoreEnd != 0)
		{
			// First member alignment will determine type alignment
			// If one has already been set, but is too small, override
			int size  = sizeOf(members[0]);
			int align = getDefaultAlignment(size);
			Integer req   = type.getAlignment();
			if (req == null || type.getAlignment() < align) type.setAlignment(align);
			
			// Now align by member size
			int offset = size;
			for (int i = 1; i < members.length - ignoreEnd; i++)
			{
				int mbSize  = members[i].getSize();
				int mbAlign = getDefaultAlignment(mbSize);
				
				offset += mbAlign - ((align + offset) % mbAlign);
				members[i].setOffset(offset);
				offset += mbSize;
			}
			totalSize = offset;
		}
		type.setSize(totalSize);
		return totalSize;
	}
	
	private static boolean isValidMember(Member member)
	{
		return member.getIsInstance() && member.getType() == MemberType.Variable;
	}
	
	public int sizeOf(Member member) throws ObjectFrozenException
	{
		// Don't change existing size - could be set manually
		if (member.getSize() == null)
		{
			TypeRef typeRef   = member.getValueType();
			Type    valueType = typeRef.getResolvedType();
			
			if (typeRef.getIsReferenceType()) { member.setSize(ptr_size); }
			else
			{
				Integer size = valueType.getSize();
				if (size == null) sizeType(valueType);
				
				member.setSize(valueType.getSize());
			}
		}
		return member.getSize();
	}
	
	public static int getDefaultAlignment(int size)
	{
		if (size % 2 != 0) size += 1;
		return size > 8 ? 8 : size;
	}

	/* ========================================================================== */
	
	public static void performSizing(CodeUnit unit, ParameterMap parameters)
	{
		
	}
}
