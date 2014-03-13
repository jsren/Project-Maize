package uk.ac.ed.maize.parser;

import java.io.File;
import java.io.InputStreamReader;

import uk.ac.ed.maize.exceptions.CompilerException;
import uk.ac.ed.maize.lexer.Lexer;

public class SecondPassParser
{
	private File  file;
	private Lexer lexer;
	
	public SecondPassParser(InputStreamReader reader, File file)
	{
		this.lexer = new Lexer(reader);
		this.file  = file;
	}

	public Object parse(CompilerContext globalContext) throws CompilerException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
