package uk.ac.ed.maize.parser;

import java.io.InputStreamReader;

import uk.ac.ed.maize.code.Statement;
import uk.ac.ed.maize.exceptions.CompilerException;
import uk.ac.ed.maize.lexer.Lexer;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Method;
import uk.ac.ed.maize.meta.Namespace;
import uk.ac.ed.maize.meta.Type;

public class SecondPassParser
{
	private CodeUnit unit;
	private Lexer    lexer;
	
	public SecondPassParser(InputStreamReader reader, CodeUnit unit)
	{
		this.lexer = new Lexer(reader);
		this.unit  = unit;
	}

	public Statement[] parse(CompilerContext globalContext) throws CompilerException
	{
		ParserContext context = new ParserContext(globalContext, this.unit);
		
	}
	
	
	
	private void parseNamespace(ParserContext context, Namespace ns)
	{
		context.setCurrentScope(ns);
		
		for (Member m : ns.getMembers())
		{
			context.setCurrentMember(m);
			
			if (m instanceof Method)
			{
				Method func = (Method)m;
				this.parseMethod(context, func);
			}
		}
		for (Type type : ns.getSubtypes()) {
			this.parseType(context, type);
		}
		for (Namespace subspace : ns.getSubspaces()) {
			this.parseNamespace(context, subspace);
		}
	}
	
	private void parseType(ParserContext context, Type t)
	{
		context.setCurrentType(t);
		
		for (Member m : t.getMembers())
		{
			context.setCurrentMember(m);
			
			if (m instanceof Method)
			{
				Method func = (Method)m;
				this.parseMethod(context, func);
			}
		}
		for (Type subtype : t.getSubtypes()) {
			this.parseType(context, subtype);
		}
	}
	
	private void parseMethod(ParserContext context, Method m)
	{
		
	}
}
