package uk.ac.ed.maize.linker;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.meta.Method;

public interface Mangler
{
	String demangle(String identifier) throws CompilerError;
	String mangle(Method method) throws CompilerError;
}
