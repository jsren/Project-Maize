package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.code.Conditional;
import uk.ac.ed.maize.code.Expression;
import uk.ac.ed.maize.code.ExpressionType;
import uk.ac.ed.maize.code.Operation;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;

public class OperationParser
{
	public static final Operation parse(ParserContext context, Token operator, Expression arg) 
			throws InternalError
	{
		if (Operation.getOperatorArity(operator) != 1) {
			throw new InternalError("Invalid operator arity - expected 1", operator);
		}
		return new Operation(context, operator, null, arg);
	}
	
	public static final Operation parse(ParserContext context, Token operator, 
			Expression lhs, Expression rhs) throws InternalError
	{
		Integer operationArity = Operation.getOperatorArity(operator);
		
		if (operationArity == 1)
		{
			// If a simple prefix operator, parse & return
			if (operator.getLexeme().length() == 1) {
				return new Operation(context, operator, null, rhs);
			}
			
			// Otherwise we have ourselves an increment or decrement
			// This can be a prefix or a suffix.
			
			// Valid suffix syntax takes precedence
			boolean isPrefix = false;
			ExpressionType exprType;
			
			// Test for valid suffix syntax
			if (lhs == null) {
				isPrefix = true;
			}
			else if ((exprType = lhs.getType()) != ExpressionType.Variable)
			{
				if (exprType == ExpressionType.Conditional)
				{
					Conditional cond = (Conditional)lhs;
					if (cond.getExpressionIfFalse().getType() != ExpressionType.Variable
							|| cond.getExpressionIfTrue().getType() != ExpressionType.Variable)
					{
						isPrefix = true;
					}
				}
				else isPrefix = true;
			}
			
			// Now parse as required
			if (isPrefix) {
				return new Operation(context, operator, null, rhs);
			}
			else return new Operation(context, operator, lhs, null);
			
		}
		else if (operationArity == 2) {
			return new Operation(context, operator, lhs, rhs);
		}
		else throw new InternalError("Invalid operator arity - expected 1 or 2", operator);
	}
	
	public static final Expression parse(ParserContext context, Token operator, Expression lhs, 
			Expression rhs, Expression p3) throws CompilerError
	{
		if (Operation.getOperatorArity(operator) == 3) {
			return new Conditional(context, lhs, rhs, p3);
		}
		else throw new InternalError("Invalid operator arity", operator);
	}
}
