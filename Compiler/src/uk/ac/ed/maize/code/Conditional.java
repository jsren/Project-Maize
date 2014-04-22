package uk.ac.ed.maize.code;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.EvaluationError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.parser.ParserContext;

public class Conditional extends Expression
{
	Expression condition;
	Expression trueValue;
	Expression falseValue;
	
	@Override
	public boolean getHasValue() { return false; }
	
	public Expression getCondition()         { return this.condition; }
	public Expression getExpressionIfTrue()  { return this.trueValue; }
	public Expression getExpressionIfFalse() { return this.falseValue; }
	
	public Conditional(ParserContext context, Expression condition, 
			Expression trueExpr, Expression falseExpr) throws CompilerError
	{
		super(context);
		
		this.condition  = condition;
		this.trueValue  = trueExpr;
		this.falseValue = falseExpr;
		
		if (!this.trueValue.getValueType().isEquivalent(this.falseValue.getValueType())) {
			throw new EvaluationError("Invalid conditional staement: value types do not match");
		}
	}
	
	public boolean getCanCompileTimeEval() {
		return this.condition.getHasValue();
	}
	
	public Expression compileTimeEval() throws CompilerError
	{
		if (condition.getHasValue())
		{
			Token lhs = condition.getTokenValue();
			if (lhs.getType() == TokenType.BooleanLiteral)
			{
				// Couldn't resist...
				return lhs.getLexeme().equals("true") ? trueValue : falseValue;
			}
			else 
			{
				throw new EvaluationError("Invalid condition expression for" +
					" conditional statement: expecting boolean value");
			}
		}
		else return null;
	}

	@Override
	public TypeRef getValueType() throws CompilerError {
		return this.trueValue.getValueType();
	}

	@Override
	public Token getTokenValue() throws CompilerError {
		return this.compileTimeEval().getTokenValue();
	}

	@Override
	public ExpressionType getType() {
		return ExpressionType.Conditional;
	}

}
