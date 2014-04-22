/*
 * Application.java - (c) James S Renwick 2014
 * -------------------------------------------
 * Version 1.0.0
 * 
 * Contains the logic governing the processing of
 * input from the CLI. Program entry point.
 */
package uk.ac.ed.maize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ed.maize.exceptions.CompilerException;
import uk.ac.ed.maize.exceptions.FormatException;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.generator.Generator;
import uk.ac.ed.maize.linker.Resolver;
import uk.ac.ed.maize.linker.Sizer;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.parser.CompilerContext;
import uk.ac.ed.maize.parser.FirstPassParser;
import uk.ac.ed.maize.parser.SecondPassParser;

public final class Application
{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * @throws IOException
	 * @throws FormatException
	 * @throws ObjectFrozenException
	 */
	public static void main(String[] args) 
			throws Exception, IOException, FormatException, ObjectFrozenException
	{		
		// Create the global parser context
		CompilerContext globalContext = new CompilerContext();
		
		// Handle no inputs
		if (args.length == 0) globalContext.log.error("No input files specified");
		
		Path outputPath = Paths.get("./kernel.bin");
		File outputDir  = new File("./");
		
		ArrayList<File> inputFiles  = new ArrayList<File>();
		ArrayList<File> objFileRefs = new ArrayList<File>();
		ParameterMap    parameters  = new ParameterMap();
		
		// === HANDLE CLI INPUTS ===
		
		boolean outFile  = false; // Check for more than one -o flag
		boolean isInfile = false;
		
		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			
			if (arg.startsWith("-") && !isInfile)
			{
				if (arg.equals("-o")) // output filepath
				{
					if (outFile)              globalContext.log.error("Output filepath specified more than once");
					if (i + 1 == args.length) globalContext.log.error("Missing parameter for -o");
					else
					{
						outputPath = Paths.get(args[i+1]);
						outputDir  = outputPath.getParent().toFile();
						outFile    = true;
						
						if (!outputDir.exists()) globalContext.log.error("Specififed output directory does not exist");
					}
					i++; // Skip arg
				}
				else if (arg.equals("-l"))
				{
					if (i + 1 == args.length) globalContext.log.error("Missing parameter for -l");
					else
					{
						File linkFile = new File(args[i+1]);
						if (!linkFile.exists()) globalContext.log.error("Object file '"+args[i+1]+"' not found");
						else objFileRefs.add(linkFile);
						
					}
					i++; // Skip arg
				}
				else if (arg.equals("-sse2"))
				{
					if (i + 1 != args.length)
					{
						if (args[i+1].equals("on")) { parameters.set(arg.substring(1), true); i++; }
						else if (args[i+1].equals("off")){ i++; }
						// For now, the default is to true
						else parameters.set(arg.substring(1), true);
					}
					// For now, the default is to true
					else parameters.set(arg.substring(1), true);
				}
				else
				{
					parameters.set(arg.substring(1), true);
				}
			}
			// Otherwise, treat as input files from here
			else
			{
				isInfile = true;
				
				File infile = new File(arg);
				if (infile.exists() && !infile.isDirectory())
				{
					inputFiles.add(infile);
				}
				else globalContext.log.error("Invalid input file specified");
			}
		}
		
		// Now that parameters have been loaded, assign to the compiler context
		globalContext.setParameters(parameters);
		
		// === BEGIN COMPILATION ===
		
		// Start first pass of parsing
		// TODO: This could be easily done in parallel. NOTE: you'll have to change
		//       to thread-safe collections. However profiling suggests that this might
		//       actually be detrimental to performance.
		for (File file : inputFiles)
		{
			try
			{
				globalContext.addCodeUnit(
						new FirstPassParser(new InputStreamReader(new FileInputStream(file)), file)
							.parse(globalContext)
				);
			} catch (CompilerException e) { globalContext.log.error(e); }
		}
		
		// *SYNCHRONISE HERE*
		
		// TODO: Now is the time for any pre-generation plugins to act. 
		
		
		
		// Now attempt to resolve all TypeRefs and CodeUnitRefs, generate member offsets 
		// and type sizes, and to evaluate compile-time expressions.
		for (CodeUnit unit : globalContext.getLoadedCodeUnits()) {
			Resolver.performResolution(unit);
		}
		for (CodeUnit unit : globalContext.getLoadedCodeUnits()) {
			Sizer.performSizing(unit, parameters);
		}
		// TODO compile-time expressions
		
		// Create/replace output file
		File outputFile = outputPath.toFile();
		if (!outputFile.createNewFile())
		{
			     if (!parameters.get("fo")) globalContext.log.error("Output file already exists; use the -fo flag to enable overwriting");
			else if (!outputFile.delete())  globalContext.log.error("Error overwriting file - ensure write/delete access");
			outputFile.createNewFile();
		}
		
		OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
		
		// Perform second pass of parsing & generate assembly for each file
		Generator generator = new Generator(outputWriter, parameters);
		for (File file : inputFiles)
		{
			try
			{
				generator.generate(
						new SecondPassParser(new InputStreamReader(new FileInputStream(file)), file).parse(globalContext)
				);
				outputWriter.flush();
			}
			catch (CompilerException e) { globalContext.log.error(e); }
		}
		outputWriter.close();
		
		// For now, just exit - we should have some nice assembly
		// ready for ... assembly
		System.exit(0);
	}

}
