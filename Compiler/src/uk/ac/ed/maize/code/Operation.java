package uk.ac.ed.maize.code;

import java.util.HashMap;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class Operation extends Expression
{
	private Token operator;
	private Expression lhs;
	private Expression rhs;
	private Expression value;
	
	public Operation(ParserContext context, Token operator,
			Expression lhs, Expression rhs) 
	{
		super(context);
		this.operator = operator;
		this.rhs = rhs;
		this.lhs = lhs;

		// Attempt to eval compile-time value
		
	}
	
	public Expression getLHS() {
		return this.lhs;
	}
	public Expression getRHS() {
		return this.rhs;
	}

	@Override
	public boolean getHasValue() {
		return this.value != null;
	}

	@Override
	public TypeRef getValueType() throws CompilerError {
		return this.value.getValueType();
	}

	@Override
	public Token getTokenValue() throws CompilerError {
		return this.value.getTokenValue();
	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.Operation;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		
		if (lhs != null) builder.append(lhs);
		builder.append(' ' + operator.getLexeme() + ' ');
		if (rhs != null) builder.append(rhs);
		
		builder.append(')');
		return builder.toString();
	}
	
	/* === STATIC OPERATOR PRECEDENCE === */
	private static final int maxLevel = 14;
	private static HashMap<String, Integer> operators;
	static
	{
		Operation.operators = new HashMap<String, Integer>();
		
		operators.put("++",   1); operators.put("--",   1);
		operators.put("~",    2); operators.put("!",    2);
		operators.put("<<",   3); operators.put(">>",   3);
		operators.put("&",    4);
		operators.put("^",    5);
		operators.put("|",    6);
		operators.put("*",    7); operators.put("/",    7); operators.put("%",    7);
		operators.put("+",    8); operators.put("-",    8);
		operators.put(">",    9); operators.put("<",    9); operators.put(">=",   9); operators.put("<=",   9);
		operators.put("==",  10); operators.put("!=",  10);
		operators.put("&&",  11);
		operators.put("||",  12);
		operators.put("?",   13);
		operators.put("+=",  14); operators.put("-=",  14); operators.put("*=",  14); operators.put("/=",  14);
		operators.put("|=",  14); operators.put("&=",  14); operators.put("^=",  14); operators.put("<<=", 14);
		operators.put(">>=", 14); operators.put("%=",  14); operators.put("=",   14);
	}
	
	public static Integer getOperatorPower(Token op) {
		return Operation.operators.get(op.getLexeme());
	}
	
	public static boolean isValidOperator(Token op)	{
		return Operation.operators.containsKey(op);
	}
	
	public static int getMaxLevel() { 
		return Operation.maxLevel;
	}
	
	public static Integer getOperatorArity(Token op)
	{
		if (op.getLexeme().equals("?")) {
			return 3;
		}
		
		Integer level = getOperatorPower(op);
		
		if (level == null) { 
			return null;
		}
		else return (level < 3) ? 1 : 2;
	}
}


