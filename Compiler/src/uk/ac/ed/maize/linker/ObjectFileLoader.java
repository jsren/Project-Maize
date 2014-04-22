package uk.ac.ed.maize.linker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import uk.ac.ed.maize.elf.ELFHeader;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.FormatException;
import uk.ac.ed.maize.meta.CodeUnit;

public class ObjectFileLoader
{
	private File filepath;
	private FileInputStream file;
	
	public ObjectFileLoader(File file) throws FileNotFoundException
	{
		this.file     = new FileInputStream(file);
		this.filepath = file;
	}
	
	public CodeUnit loadUnit() throws FormatException, CompilerError
	{
		// Read the ELF header
		try
		{
			byte[] buffer = new byte[ELFHeader.size64];
			file.read(buffer);
			
			ELFHeader fileHeader = ELFHeader.fromBuffer(ByteBuffer.wrap(buffer));
			
			// We have a valid header, so attempt to load a symbol table
			// I'm not entirely sure how the de-mangling is going to work,
			// or if I should even attempt it...
			// Although I remember seeing more formal return types somewhere
			// in some section data...
			throw new FormatException("Why does Java not have a NotImplemented annotation?!?!?");
		}
		catch (IOException e)
		{
			
		}
		// TODO
		return new CodeUnit(filepath);
	}
}
