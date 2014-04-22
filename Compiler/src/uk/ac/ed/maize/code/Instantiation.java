package uk.ac.ed.maize.code;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.linker.Resolver;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class Instantiation extends Expression
{
	private TypeRef    type;
	private Expression address;
	
	public Expression getAddress() { return this.address; }
	
	public Instantiation(ParserContext context, Token type) throws CompilerError
	{
		super(context);
		this.type = new TypeRef(type, false, false, 1);
		new Resolver(context).resolveTypeRef(this.type);
	}
	public Instantiation(ParserContext context, Token type, Expression address) throws CompilerError
	{
		super(context);
		this.address = address;
		this.type    = new TypeRef(type, false, false, 1);
		new Resolver(context).resolveTypeRef(this.type);
	}

	@Override
	public boolean getHasValue() {
		return address.getHasValue();
	}
	@Override
	public TypeRef getValueType() throws CompilerError {
		return this.type;
	}
	@Override
	public Token getTokenValue() throws CompilerError {
		return address.getTokenValue();
	}
	@Override
	public ExpressionType getType() {
		return ExpressionType.Instantiation;
	}
}
