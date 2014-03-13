package uk.ac.ed.maize.exceptions;

import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.parser.ParserContext;

public class CompilerException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private ParserContext context;
	private CompilerError error;
	
	public CompilerException(ParserContext context, CompilerError error)
	{
		this.context = context;
		this.error   = error;
	}
	
	public Integer getLineNumber()
	{
		Token t = this.error.getToken();
		if (t != null) return t.getLineIndex();
		
		Member m = this.context.getCurrentMember();
		if (m != null) return m.getLine();
		
		Type y = this.context.getCurrentType();
		if (y != null) return y.getDeclarationLine();
		
		return null;
	}
	
	public Integer getColumnNumber()
	{
		Token t = this.error.getToken();
		if (t != null) return t.getCharIndex();
		
		return null;
	}
	
	public CodeUnit getCodeUnit() {
		return this.context.getCodeUnit();
	}
	
	public ErrorType getType() {
		return this.error.getType();
	}
	
}
