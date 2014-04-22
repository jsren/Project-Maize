package uk.ac.ed.maize.linker;

import java.util.Stack;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.NameError;
import uk.ac.ed.maize.meta.Method;
import uk.ac.ed.maize.meta.Namespace;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Type;

public class MaizeMangler implements Mangler
{
	private final static int nasmMax = 4095; 
	
	final static String[] basenames = new String[]
	{
		"bool",  "byte",   "char",  "double", "float", "int",
		"int32", "long",   "sbyte", "short",  "uint",  "uint32",
		"ulong", "ushort", "void"
	};
	
	private static String mangleBaseType(String basetype)
	{
		for (int i = 0; i < basenames.length; i++) {
			if (basenames.equals(basetype)) return Integer.toString(i);
		}
		return null;
	}
	
	@Override
	public String demangle(String identifier)
	{
		return null;
	}

	@Override
	public String mangle(Method method) throws CompilerError
	{
		StringBuilder builder = new StringBuilder();
		
		// Prevents keyword conflicts with NASM
		builder.append("$M");
		
		// Add prefixes as appropriate
		if (method.getIsConstructor()) {
			builder.append('C');
		}
		else if (!method.getIsInstance()) {
			builder.append('S');
		}
		builder.append('_');
		
		// Now append namespace path
		Stack<String> path = new Stack<String>();
		Scope scope = method.getScope();
		
		while (scope != null)
		{
			if (scope instanceof Namespace)
			{
				Namespace ns = (Namespace)scope;
				path.push(ns.getName());
			}
			else if (scope instanceof Type)
			{
				Type t = (Type)scope;
				path.push(t.getName());
			}
			scope = scope.getParentScope();
		}
		while (path.size() != 0)
		{
			builder.append(path.pop());
			builder.append('@');
		}
		
		// Append method name
		builder.append(method.getName());
		
		// TODO: Append return type & param types
		
		String output = builder.toString();
		
		if (output.length() > MaizeMangler.nasmMax) {
			throw new NameError("Mangled identifier too long", method.getBodyStart());
		}
		else return output;
	}

}
