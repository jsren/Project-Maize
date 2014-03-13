package uk.ac.ed.maize.lexer;

import java.io.IOException;
import java.io.InputStreamReader;

import uk.ac.ed.maize.exceptions.ErrorType;
import uk.ac.ed.maize.exceptions.LexerError;


/**
 * A lexical analyzer for JPP. Scans through the 
 * source breaking it down into individual tokens.
 * 
 * @author JSR
 * */
public class Lexer
{
	private char lastChar  = 0; // Holds the last scanned character for carrying.
	private int  lineIndex = 1; // Indices used for symbol/line locations
	private int  colIndex  = 0; // for emission in debugging sections.
	
	private boolean carry = false;
	
	private InputStreamReader reader;
	
	// Gets the number of chars in the system line separator
	private final int lineSepLen = System.lineSeparator().length();
	
	// Helper function for throwing a lexer error.
	private void throwError(String message, ErrorType type) throws LexerError {
		throw new LexerError(message, this.lineIndex, this.colIndex, type);
	}
	
	/**
	 * Creates a new lexer from a given source code stream.
	 * @param streamReader
	 * 		A reader from which to scan for tokens.
	 */
	public Lexer(InputStreamReader streamReader) {
		this.reader = streamReader;
	}
	
	/**
	 * Gets whether the end of the stream has been reached.
	 * @returns True if at EOS/EOF 
	 * @throws IOException
	 */
	public boolean isEndOfStream() throws IOException {
		return !this.reader.ready();
	}
	
	public static boolean isNewline(char c)
	{
		final char[] newlines = { '\r', '\n' };
		
		for (int i = 0; i < newlines.length; ++i) {
			if (newlines[i] == c) return true;
		}
		return false;
	}
	
	/**
	 * Gets whether the character is a valid integer literal suffix.
	 * I.e. one of b, B, L, S, U.
	 */
	public static boolean isLiteralSuffix(char c)
	{
		return  (c == 'U' || c == 'b' || c == 'B' || c == 'L' || c == 'S');
	}
	
	public static boolean isNameChar(char c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}
	
	public static char getEscapeChar(char c)
	{
		if      (c == 'n')  return '\n';
		else if (c == 't')  return '\t';
		else if (c == 'v')  return (char)0xB;
		else if (c == 'b')  return '\b';
		else if (c == 'r')  return '\r';
		else if (c == 'f')  return '\f';
		else if (c == '\\') return '\\';
		else if (c == '?')  return '?';
		else if (c == '\'') return '\'';
		else if (c == '"')  return '\"';
		else if (c == '0')  return '\0';
		
		else return (char)255;
	}
	
	
	// Note that this is all inlined for performance;
	// we are, after all, doing potentially unbuffered 
	// (I hope not) char-by-char processing.
	/**
	 * Scans the next token from the input stream.
	 * @throws LexerError 
	 */
	public Token nextToken() throws IOException, LexerError
	{		
		// Holds the temporary in-progress token fields
		TokenType     type   = TokenType.None;
		StringBuilder output = new StringBuilder();
		
		/*
		 * NB. The while (true) is necessary to allow 
		 * carrying of chars from previous iterations.
		 */
		boolean escaped  = false;
		char    nextChar = lastChar;
		
		int startLine = -1;
		int startCol  = -1;
		
		this.lastChar = '\0'; // Reset lastChar
		
		// Read & parse while available chars
		while (true)
		{
			// Carry mechanism, where CARRY is set
			// to allow a single iteration to be run with
			// the previous character
			boolean carried = this.carry;
			if (!this.carry) 
			{
				int ccode = this.reader.read();
				if (ccode == -1) break;
				nextChar  = (char)ccode;
			}
			else this.carry = false;
			
			// === Parsing Start ===
			if (nextChar == 0) continue; // Skip NULL chars
			boolean isNewline = isNewline(nextChar);
			
			// Update indices
			if (!carried)
			{
				if (isNewline) { ++this.lineIndex; this.colIndex = 0; }
				else           { ++this.colIndex; }
			}
			
			// If the start of the next token
			if (output.length() == 0 && type == TokenType.None)
			{
				// Skip space, line and control chars
				if (Character.isWhitespace(nextChar) || isNewline || nextChar < 0x20)
					continue;
				
				startLine = this.lineIndex;
				startCol  = this.colIndex;
				
				// Look for comments (do this before operators)
				if (this.lastChar == '/') // Note that lastChar still == '/'. This is intentional.
				{
					if      (nextChar == '/') { type = TokenType.LineComment;  continue; }
					else if (nextChar == '*') { type = TokenType.BlockComment; continue; }
				}
				else if (nextChar == '/') { this.lastChar = '/'; continue; }
				
				// Look for hex digits (do this before integers)
				if (this.lastChar == '0')
				{
					if (nextChar == 'x') { type = TokenType.HexLiteral; continue; }
					else { output.append(lastChar); type = TokenType.IntegerLiteral; this.carry = true; continue; }
				}
				else if (nextChar == '0') { this.lastChar = '0'; continue; }
				
				// Look for delimiters
				if      (TokenType.isDelimiter(nextChar)) type = TokenType.Delimiter;
				else if (TokenType.isOperator(nextChar))  type = TokenType.Operator;
				
				// If numeric, we have a numeric literal
				else if (Character.isDigit(nextChar)) type = TokenType.IntegerLiteral;
				
				else if (nextChar == '"')  { type = TokenType.StringLiteral;    continue; }
				else if (nextChar == '\'') { type = TokenType.CharacterLiteral; continue; }
				else if (nextChar == '@')  { type = TokenType.Directive;        continue; }
				
				// Anything left is hopefully an identifier or keyword (checked at end)
				else if (isNameChar(nextChar)) type = TokenType.Identifier;
				
				else throwError("Unknown expression", ErrorType.SyntaxError);
			}
			else if (type == TokenType.Delimiter)
			{
				this.carry = true;
				break;
			}
			else if (type == TokenType.Operator)
			{
				if (!TokenType.isOperator(nextChar))
				{
					this.carry = true;
					break;
				}
			}
			else if (type == TokenType.StringLiteral)
			{				
				// Process escape char
				if (this.lastChar == '\\')
				{
					nextChar = getEscapeChar(nextChar);
					if (nextChar == 0xFF) throwError("Invalid escape character", ErrorType.SyntaxError);
					
					this.lastChar = '\0';
					continue;
				}
				
				// Only terminate string if newline or unescaped &quot;
				else if (isNewline || (nextChar == '"' && !escaped)) break;
				
				// Don't add to output 
				else if (nextChar == '\\') 
				{
					this.lastChar = '\\';
					continue;
				}
			}
			else if (type == TokenType.LineComment)
			{
				// Only terminate line comment on newline
				if (isNewline) break;
			}
			else if (type == TokenType.BlockComment)
			{
				// Only terminate on */ sequence. Withhold previous char so
				// we don't have to trim spurious * chars when parsing.
				if (this.lastChar == '*')
				{
					if (nextChar == '/') break;
					else                 output.append(this.lastChar);
				}
				if (nextChar == '*')
				{
					this.lastChar = nextChar;
					continue;
				}
				/* A consequence of this is that given a document ending with "/* ... *",
				 * the final asterisk will not be reported. Don't see this as a big deal, though.
				 */
			}
			else if (type == TokenType.IntegerLiteral)
			{
				// U can be the start of a two-part suffix
				// So check whether we've had it
				boolean wasU = this.lastChar == 'U';
				// If the number contains a decimal point, 
				if (nextChar == '.' && !wasU) type = TokenType.FloatLiteral;
				// Any non-digit (excepting '.') should terminate
				// Throw an error if it's unexpected
				else if (!Character.isDigit(nextChar))
				{
					// Append if a valid suffix
					if (isLiteralSuffix(nextChar))
					{
						output.append(nextChar);
						if (nextChar == 'U')
						{
							// Could be a compound (e.g. UL)
							if (!wasU) continue; 
							// Suffix "UU" not permitted
							else throwError("Duplicate 'unsigned' suffix in numeric literal", ErrorType.SyntaxError);
						}
						else break; // Finished the literal
					}
					else if (Character.isLetter(nextChar))
						throwError("Invalid character in numeric literal", ErrorType.SyntaxError);
					
					this.carry = true;
					break;
				}
			}
			else if (type == TokenType.FloatLiteral)
			{
				// Any non-digit (excepting '.') should terminate
				// Throw an error if it's unexpected
				if (!Character.isDigit(nextChar))
				{
					if (Character.isLetter(nextChar))
						throwError("Invalid character in numeric literal", ErrorType.SyntaxError);
					
					this.carry = true;
					break;
				}
			}
			else if (type == TokenType.HexLiteral)
			{				
				// Any non-digit (including '.') should terminate
				// Throw an error if it's unexpected				
				if (!Character.isDigit(nextChar) && !TokenType.isHexDigit(nextChar))
				{
					if (Character.isLetter(nextChar))
						throwError("Invalid character in numeric literal", ErrorType.SyntaxError);
					
					this.carry = true;
					break;
				}
			}
			else if (type == TokenType.Identifier)
			{
				// Process the namespace delimiters (::) as part
				// of the identifier
				if (nextChar == ':')
				{
					this.lastChar = ':';
					continue;
				}
				else if (lastChar == ':') // Last char was ':', nextChar not a ':'
				{
					this.lastChar = nextChar;
					this.carry    = true;
					// TODO: This is HORRIBLE. But the alternative seems to be a char buffer.
					return new Token(TokenType.Delimiter, ":", this.lineIndex, this.colIndex);
				}
				
				// Break on anything but identifier chars (alphanumeric and '_')
				if (!Character.isLetterOrDigit(nextChar) && nextChar != '_')
				{
					this.carry = true;
					break;
				}
			}
			else if (type == TokenType.CharacterLiteral)
			{
				// Process escape char
				if (this.lastChar == '\\')
				{
					nextChar = getEscapeChar(nextChar);
					if (nextChar == 0xFF) throwError("Invalid escape character", ErrorType.SyntaxError);
					
					this.lastChar = '\0';
					output.append(nextChar);
					continue;
				}
				// If end of literal, break
				else if (nextChar == '\'') break;
				
				// If we've consumed one
				else if (output.length() != 0)
					throwError("Invalid character literal", ErrorType.SyntaxError);
				
				// Escape char - don't add to output yet
				else if (nextChar == '\\') 
				{
					this.lastChar = '\\';
					continue;
				}
			}
			
		// ======================================
			output.append(nextChar);
			this.lastChar = nextChar;
			continue;
		// ======================================
		}
		
		this.lastChar = nextChar;
		String lexeme = output.toString();
		
		// Keywords and boolean values are  reserved identifiers, 
		// so check if completed symbol matches one:
		if (type == TokenType.Identifier)
		{
			if (TokenType.isKeyword(lexeme)) {
				type = TokenType.Keyword;
			}
			else if (lexeme.equals("true") || lexeme.equals("false")) {
				type = TokenType.BooleanLiteral;
			}
		}
		
		// Return complete token
		return new Token(type, lexeme, ++startLine/lineSepLen, startCol);
	}
}