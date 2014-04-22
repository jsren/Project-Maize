package uk.ac.ed.maize.lexer;

/*
 * Other keywords:
 * -------------------------------------
 * bool   byte char  double float false 
 * global int  int32 long   sbyte short 
 * string true uint  uint32 ulong ushort 
 * void 
 */

/**
 * Delimiters are lexemes which stand 
 * 
 * Operators are a specific subset of delimiters
 * 
 * @author JSR
 *
 */
public enum TokenType
{
	None,
	Directive,
	Identifier,
	Operator,
	Keyword,
	BooleanLiteral,
	StringLiteral,
	IntegerLiteral,
	HexLiteral,
	FloatLiteral,
	CharacterLiteral,
	Delimiter,
	LineComment,
	BlockComment;
	
	public final boolean isLiteral()
	{
		return this == BooleanLiteral || this == CharacterLiteral 
			|| this == FloatLiteral   || this == HexLiteral 
			|| this == IntegerLiteral || this == StringLiteral;
	}
	
	private static final char[] hexDigits = {'A','B','C','D','E','F'};
	
	private static final String[] keywords = 
	{ 
		"abstract", "alias",   "align",     "asm",      "at",        "base",
		"break",    "checked", "class",     "const",    "continue",  "delete",
		"else",		"enum",    "explicit",  "export",   "extern",    "for",       
		"goto",		"if",      "include",   "inline",   "interface", "internal",
		"is",       "link",    "namespace", "new",      "operator",  "override",
		"params",   "private", "protected", "public",   "register",  "return",  
		"sealed",   "sizeof",  "static",    "struct",   "template",  "this",    
		"union",    "using",   "virtual",   "volatile", "while"
	};
	private static final char[] delimiters = { '{', '}', '#', '(', ')', ':', ';', '@', '[', ']', '`', '$', ',' };
	private static final char[] operators  = { '=', '?', '<', '>', '^', '~', '&', '|', '!', '/', '*', '+', '-', '%' };
	
	public static final boolean isHexDigit(char c)
	{
		for (int i = 0; i < hexDigits.length; ++i) {
			if (hexDigits[i] == Character.toUpperCase(c)) return true;
		}
		return false;
	}
	public static final boolean isDelimiter(char c)
	{
		for (int i = 0; i < delimiters.length; ++i) {
			if (delimiters[i] == c) return true;
		}
		return false;
	}
	public static final boolean isOperator(char c)
	{
		for (int i = 0; i < operators.length; ++i) {
			if (operators[i] == c) return true;
		}
		return false;
	}
	public static final boolean isKeyword(String s)
	{
		for (int i = 0; i < keywords.length; ++i) {
			if (keywords[i].equals(s)) return true;
		}
		return false;
	}	
	public static final boolean isComment(TokenType type) {	
		return type == BlockComment || type == LineComment;
	}	
}
