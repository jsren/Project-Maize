package uk.ac.ed.kl.meta;

import java.util.Arrays;

import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.base.BaseType;
import uk.ac.ed.kl.base.BooleanType;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.parser.UnitContext;

public class TypeRef implements Freezable
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
	
	public TypeRef(UnitContext context, Type type, boolean array, boolean _const, int ptrLvl) throws ParserError
	{
		this.typeName     = type.getName();
		this.isArray      = array;
		this.isConst      = _const;
		this.pointerLevel = ptrLvl;
		this.resolvedType = type;
		this.frozen       = true;
		this.isResolved   = true;
		this.token        = Token.empty;
		
		if (ptrLvl < 0) 
			throw new ParserError("Negative pointer level", context, ErrorType.Internal);
		
		String[] nschain = this.typeName.split("::");
		
		this.types      = nschain[nschain.length - 1].split("\\.") ;
		this.namespaces = Arrays.copyOfRange(nschain, 0, nschain.length - 1);
	}
	
	public TypeRef(UnitContext context, Token token, boolean array, boolean _const, int ptrLvl) throws ParserError
	{
		this.token       = token;
		this.typeName    = token.getLexeme();
		this.isArray     = array;
		this.isConst     = _const;
		
		this.pointerLevel = ptrLvl;
		
		if (ptrLvl < 0) 
			throw new ParserError("Negative pointer level", context, ErrorType.Internal);
		
		this.isBaseType = BaseType.getIsBaseType(this);
		
		String[] nschain = this.typeName.split("::");
		
		this.types      = nschain[nschain.length - 1].split("\\.") ;
		this.namespaces = Arrays.copyOfRange(nschain, 0, nschain.length - 1);
	}
	
	public void resolve(Type type)
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
	
	public static TypeRef fromLiteral(UnitContext context, Token token) throws ParserError
	{
		TokenType type   = token.getType();
		String    lexeme = token.getLexeme();
		
		if (type == TokenType.IntegerLiteral)
		{
			// Get a suffix, if one exists, and we're not 
			String suffix = "";
			for (int i = lexeme.length() - 1; i != -1; i--)
			{
				char c = lexeme.charAt(i);
				
				if (!Character.isDigit(c)) suffix += c;
				else break;
			}
			if (suffix.length() == 0) return BooleanType.getBasicTypeRef();
			else throw new ParserError("Not yet implemented!", context, null, null);
		}
		else if (type == TokenType.HexLiteral)
		{
			return BooleanType.getBasicTypeRef();
		}
		else throw new ParserError("Not yet implemented!", context, null, null);
	}
	
	
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
}
