package uk.ac.ed.kl.parser;

import java.io.File;
import java.io.InputStreamReader;

import uk.ac.ed.kl.lexer.Lexer;

public class SecondPassParser
{
	private File  file;
	private Lexer lexer;
	
	public SecondPassParser(InputStreamReader reader, File file)
	{
		this.lexer = new Lexer(reader, file.toString());
		this.file  = file;
	}

	public Object parse(ParserContext globalContext)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
