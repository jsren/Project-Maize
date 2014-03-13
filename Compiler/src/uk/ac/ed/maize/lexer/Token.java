package uk.ac.ed.maize.lexer;

/**
 * Object representing a single token
 * from a code file.
 * 
 * @author JSR
 */
public class Token
{
	private TokenType type;
	private String    lexeme;
	
	private int lineIndex;
	private int charIndex;
	
	/**
	 * Gets the type of the token.
	 * @return The token's type.
	 */
	public TokenType getType() { return this.type; }
	/**
	 * Gets the lexeme (value) of the token.
	 * @return The token's lexeme.
	 */
	public String getLexeme() { return this.lexeme;	}
	/**
	 * Gets the line index at which the current token is found.
	 * @return The line number at which the token is located in the source.
	 */
	public int getLineIndex() { return this.lineIndex; }
	/**
	 * Gets the character index at which the current token is found.
	 * @return The column number at which the token is located in the source.
	 */
	public int getCharIndex() { return this.charIndex; }
	
	@Override
	public String toString() {
		return String.format("(%s:%s)  '%s'  [%s]", lineIndex, charIndex, lexeme, type);
	}
	
	/**
	 * Creates a new token object with the given
	 * type and lexeme (value).
	 * 
	 * @param type
	 * 		The type of the token.
	 * @param lexeme
	 * 		The lexeme of the token.
	 * @param lineIndex
	 * 		The line number at which the token is located in the source.
	 * @param charIndex
	 *		The column number at which the token is located in the source.
	 */
	public Token(TokenType type, String lexeme, int lineIndex, int charIndex)
	{
		this.type      = type;
		this.lexeme    = lexeme;
		this.lineIndex = lineIndex;
		this.charIndex = charIndex;
	}
	
	/** The empty token. */
	public static final Token empty = new Token(TokenType.None, "", -1, -1);
}
