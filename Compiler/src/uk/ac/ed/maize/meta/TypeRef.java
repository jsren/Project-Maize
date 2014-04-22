package uk.ac.ed.maize.meta;

import java.util.Arrays;

import uk.ac.ed.maize.MetaReference;
import uk.ac.ed.maize.base.BaseType;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;

public class TypeRef implements MetaReference<Type>
{
	private boolean frozen;
	
	private boolean isBaseType;
	private String  typeName;
	private Token   token;
	private Type    resolvedType;
	private boolean isArray;
	private boolean isConst;
	private boolean isResolved;
	
	private String[] namespaces;
	private String[] types;
	
	private int pointerLevel;
	
	public boolean getIsBaseType()   { return this.isBaseType;   }
	public String  getTypeName()     { return this.typeName;     }
	public Type    getResolvedType() { return this.resolvedType; }
	public boolean getIsResolved()   { return this.isResolved;   }
	
	public Token   getToken()   { return this.token;   }
	public boolean getIsArray() { return this.isArray; }
	public boolean getIsConst() { return this.isConst; }
	
	public int getPointerLevel() { return this.pointerLevel; }
	
	public String[] getNamespaceChain() { return this.namespaces; }
	public String[] getTypeNameChain()  { return this.types; }
	
	public boolean getIsReferenceType() { return this.isArray || this.pointerLevel != 0; }
	
	public TypeRef(Type type, boolean array, boolean _const, int ptrLvl) throws CompilerError
	{
		this.typeName     = type.getName();
		this.isArray      = array;
		this.isConst      = _const;
		this.pointerLevel = ptrLvl;
		this.resolvedType = type;
		this.frozen       = true;
		this.isResolved   = true;
		this.token        = Token.empty;
		
		if (ptrLvl < 0) throw new InternalError("Negative pointer level");
		
		String[] nschain = this.typeName.split("::");
		
		this.types      = nschain[nschain.length - 1].split("\\.") ;
		this.namespaces = Arrays.copyOfRange(nschain, 0, nschain.length - 1);
	}
	
	public TypeRef(Token token, boolean array, boolean _const, int ptrLvl) throws CompilerError
	{
		this.token       = token;
		this.typeName    = token.getLexeme();
		this.isArray     = array;
		this.isConst     = _const;
		
		this.pointerLevel = ptrLvl;
		
		if (ptrLvl < 0) throw new InternalError("Negative pointer level", token);
		
		this.isBaseType = BaseType.getIsBaseType(this);
		
		String[] nschain = this.typeName.split("::");
		
		this.types      = nschain[nschain.length - 1].split("\\.") ;
		this.namespaces = Arrays.copyOfRange(nschain, 0, nschain.length - 1);
	}
	
	public void resolveReference(Type type)
	{
		this.resolvedType = type;
		this.isResolved   = true;
		this.freeze();
	}
	
	@Override
	public String toString()
	{
		String prefix = this.isConst ? "const " : "";
		
		String suffix = "";
		for (int i = 0; i < this.pointerLevel; i++) suffix += '*';
		if (this.isArray) suffix += "[]";
		
		return prefix + this.typeName + suffix;
	}
	
	public boolean isEquivalent(TypeRef ref) throws CompilerError
	{
		if (ref.getIsResolved() && this.getIsResolved())
		{
			// First check ptr and array status
			if (this.pointerLevel != ref.pointerLevel || this.isArray != ref.isArray) 
			{
				return false;
			}
			if (this.resolvedType != ref.resolvedType)
			{
				TypeRef[] theseBases = this.resolvedType.getBaseTypes();
				TypeRef[] thoseBases = ref.resolvedType.getBaseTypes();
				
				// Check for implicit base type conversion
				for (int i = 0; i < theseBases.length; i++)
				{
					TypeRef thisBase = theseBases[i];
					
					for (int n = 0; n < thoseBases.length; n++)
					{
						TypeRef thatBase = thoseBases[n];
						
						if (!thisBase.getIsResolved() || !thatBase.getIsResolved()) {
							throw new InternalError("Unresolved base type reference in 'isEquivalent'");
						}
						// Check if base classes are equivalent
						if (thisBase.isEquivalent(thatBase))
						{
							return true;
						}
					}
				}
				// None found in base class(es)
				return false;
			}
			// They're equal
			else return true;
		}
		else throw new InternalError("Unresolved type reference in 'isEquivalent'");
	}	
	
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
}
