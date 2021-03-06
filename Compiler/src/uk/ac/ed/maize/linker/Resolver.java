package uk.ac.ed.maize.linker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.ed.maize.base.BaseType;
import uk.ac.ed.maize.code.Expression;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.NameError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.CodeUnitRef;
import uk.ac.ed.maize.meta.Field;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.MemberType;
import uk.ac.ed.maize.meta.Namespace;
import uk.ac.ed.maize.meta.Parameter;
import uk.ac.ed.maize.meta.Parameterised;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.CompilerContext;
import uk.ac.ed.maize.parser.ParserContext;

// NOTE: Trivial to introduce caching here if necessary
// (Cache previous namespace lookup, previous results, etc.)

public final class Resolver
{
	Scope[]     scopes;
	ParserContext context;
	
	public Resolver(ParserContext context) throws CompilerError
	{
		CodeUnitRef[] refs = context.getCodeUnit().getReferences();
		
		// Copy all applicable scopes to a single array
		this.scopes    = new Scope[refs.length + 2];
		this.scopes[0] = context.getCurrentScope();
		this.scopes[1] = context.getCodeUnit();
		
		this.context = context;
		this.context.freeze();
		
		for (int i = 0, n = 2; i < refs.length; i++, n++)
		{
			if (refs[i].getIsResolved()) {
				this.scopes[n] = refs[i].getResolvedUnit();
			}
			else throw new InternalError("CodeUnitRef not resolved", refs[i].getToken());
		}
	}
	
	public void resolveTypeRef(TypeRef ref) throws CompilerError
	{
		// Already resolved
		if (ref.getIsResolved()) return;
		// Resolve base types separately
		if (ref.getIsBaseType()) { ref.resolveReference(BaseType.getBaseType(ref)); return; }
		
		String[] typechain = ref.getTypeNameChain();
		String[] naspchain = ref.getNamespaceChain();
		
		/* == RESOLVE NAMESPACES == */
		boolean resolveUp = true;
		Scope[] scopes    = this.scopes;
		// If there are qualifying namespaces, then we resolve at these
		// and no deeper.
		if (naspchain.length != 0)
		{
			resolveUp = false;
			ArrayList<Scope> newBases = new ArrayList<Scope>(5);
			
			for (int i = 0; i < scopes.length; i++)
			{
				Scope scope = scopes[i];
				// Skip local if we're actually evaluating in global
				if (i == 0 && scopes[0] == scopes[1]) continue;
				if (!(scope instanceof Namespace)) continue;
				
				while (true)
				{
					// Resolve down the chain
					boolean   found = true;
					Namespace base  = (Namespace)scope;
					for (int n = 0; n < naspchain.length; n++)
					{
						boolean partFound = false;
						for (Namespace ns : base.getSubspaces())
						{
							if (ns.getName().equals(naspchain[n]))
							{
								base      = ns;
								partFound = true;
								break;
							}
						}
						if (!(found = partFound)) break;
					}
					if (found == true) newBases.add(base);
					// Now iterate UP the chain as appropriate
					// This is not a do-while as some consider them unreadable and outmoded
					if ((scope = scope.getParentScope()) != null) continue;
					else break;
				}
			}
			// No bases found - unable to locate
			if (newBases.size() == 0)
			{
				throw new NameError("Unable to locate the type " + ref.getTypeName() + 
						" - possible missing reference", ref.getToken());
			}
			else scopes = newBases.toArray(scopes);
		}
		
		/* == RESOLVE TYPES == */
		ArrayList<Type> foundTypes = new ArrayList<Type>(1);
		for (int i = 0; i < scopes.length; i++)
		{
			Scope scope = scopes[i];
			
			if (i == 0 && scopes[0] == scopes[1]) continue;
			while (true)
			{
				// Resolve down the chain
				for (Type base : scope.getSubtypes())
				{
					if (!typechain[0].equals(base.getName())) continue;
					
					boolean found = true;
					for (int n = 1; n < typechain.length; n++)
					{
						boolean partFound = false;
						for (Type t : base.getSubtypes())
						{
							if (t.getName().equals(typechain[n]))
							{
								base      = t;
								partFound = true;
								break;
							}
						}
						if (!(found = partFound)) break; 
					}
					if (found == true) foundTypes.add(base);
				}
				// Now iterate UP the chain as appropriate
				// This is not a do-while as some consider them unreadable and outmoded
				if ((scope = scope.getParentScope()) != null && resolveUp) continue;
				else break;
			}
		}
		
		/* == OUTPUT == */
		if (foundTypes.size() == 0)
		{
			throw new NameError("Unable to locate the type " + ref.getTypeName() + 
					" - possible missing reference", ref.getToken());
		}
		else if (foundTypes.size() > 1)
		{
			throw new NameError("Type name abiguity (" + ref.getTypeName() + " - " + foundTypes.size() 
					+ " possible types) - consider qualifying with namespaces", ref.getToken());
		}
		else ref.resolveReference(foundTypes.get(0));
	}
	
	public void resolveMemberTypes() throws CompilerError
	{
		for (Member m : this.scopes[0].getMembers())
		{
			MemberType type = m.getType();
			
			if (type == MemberType.Constant)
			{
				Field f = (Field)m;
				// Resolve initial expression
				if (f.getIsInitialised()) {
					this.resolveExpression(f.getInitialValue());
				}
				this.resolveTypeRef(f.getValueType());
			}
			else if (type == MemberType.EnumConst || type == MemberType.Variable)
			{
				this.resolveTypeRef(m.getValueType());
			}
			
			else if (type == MemberType.Function || type == MemberType.Operator)
			{
				Parameterised p = (Parameterised)m;
				
				TypeRef returnType = m.getValueType();
				if (returnType != null) this.resolveTypeRef(returnType);
				
				for (Parameter param : p.getParameters()) {
					this.resolveTypeRef(param.getTypeRef());
				}
			}
			else throw new InternalError("Unknown member type : "+type.toString());
		}
	}
	
	public void resolveExpression(Expression expr)
	{
		
	}
	
	public static void resolveUnitReferences(ParserContext context) throws CompilerError, IOException, ObjectFrozenException
	{
		CompilerContext globalContext = context.getGlobalContext();
		for (CodeUnit unit : globalContext.getLoadedCodeUnits())
		{
			// First resolve CodeUnit references
			for (CodeUnitRef ref : unit.getReferences())
			{
				// If we already have one, just skip
				if (ref.getIsResolved()) continue;
				
				// Get the unit reference's path
				Path refPath  = Paths.get(ref.getCodeUnitPath());
				boolean isAbs = refPath.isAbsolute();
				
				if (isAbs && !Files.exists(refPath, LinkOption.NOFOLLOW_LINKS))
					throw new NameError("Cannot locate referenced file \"" + refPath.toString()+'"', ref.getToken());
				
				if (isAbs && Files.isSameFile(unit.getFile().toPath(), refPath))
					throw new NameError("Invalid using statement - reference is to the current code unit", ref.getToken());
				
				// Get the absolute path of the reference
				if (!isAbs) unit.getFile().toPath().getParent().resolve(refPath);
				
				for (CodeUnit u : globalContext.getLoadedCodeUnits())
				{
					if (u == unit) continue; // Skip the current unit
					
					// If we've found the referenced unit
					if (Files.isSameFile(refPath, u.getFile().toPath()))
					{
						ref.resolveReference(u);
						ref.freeze(); // Freeze to prevent further changes
						break;
					}
				}
				// Throw if not resolved
				if (!ref.getIsResolved()) 
					throw new NameError("Referenced code unit not loaded: \"" + refPath.toString() + 
							"\" - it may not be included in compilation", ref.getToken());
			}
			
			// Now resolve alias references
			Resolver resolver = new Resolver(context);
			for (TypeRef ref : unit.getAliasedTypes()) {
				resolver.resolveTypeRef(ref);
			}
		}
		// Freeze the context to prevent unresolved types/members
		globalContext.freeze();
	}
	
	public static void performResolution(CodeUnit unit) throws CompilerError
	{
		Resolver unitResolver = new Resolver(new ParserContext(null, unit));
		
		// Resolve type aliases
		for (TypeRef ref : unit.getAliasedTypes()) {
			unitResolver.resolveTypeRef(ref);
		}
		
		// Resolve the rest
		Resolver.performResolution(unit, (Namespace)unit);
	}
	
	public static void performResolution(CodeUnit unit, Namespace namespace) throws CompilerError
	{
		ParserContext context = new ParserContext(null, unit);
		context.setCurrentScope(namespace);
		
		Resolver resolver = new Resolver(context);
		
		// Resolve members
		resolver.resolveMemberTypes();
		
		// Resolve namespaces
		for (Namespace ns : namespace.getSubspaces()) {
			Resolver.performResolution(unit, ns);
		}
		// Resolve types
		for (Type t : namespace.getSubtypes()) {
			Resolver.performResolution(unit, t);
		}
	}
	
	public static void performResolution(CodeUnit unit, Type type) throws CompilerError
	{
		ParserContext context = new ParserContext(null, unit);
		context.setCurrentType (type);
		context.setCurrentScope(type);
		
		Resolver resolver = new Resolver(context);
		// Resolve base types
		for (TypeRef baseType : type.getBaseTypes()) {
			resolver.resolveTypeRef(baseType);
		}
		// Resolve members
		resolver.resolveMemberTypes();
		// Resolve subtypes
		for (Type t : type.getSubtypes()) {
			Resolver.performResolution(unit, t);
		}
	}
}
