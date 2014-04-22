package uk.ac.ed.maize.parser;

import java.util.ArrayList;

import uk.ac.ed.maize.CachedArrayList;
import uk.ac.ed.maize.code.Expression;
import uk.ac.ed.maize.code.FunctionCall;
import uk.ac.ed.maize.code.Literal;
import uk.ac.ed.maize.code.Operation;
import uk.ac.ed.maize.code.TypeCast;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.EvaluationError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;

public abstract class ExpressionParser
{
	public static final int findClosingParen(Token[] tokens, int start)
	{
		int parenIndex = 1;
		for (int i = start + 1; i < tokens.length; i++)
		{
			String lexeme = tokens[i].getLexeme();
			
			     if ("(".equals(lexeme)) parenIndex++;
			else if (")".equals(lexeme)) parenIndex--;
			
			if (parenIndex == 0) return i;
		}
		return -1;
	}
	
	private static final Expression[] parseParams(ParserContext context, Token[] tokens, int start) throws CompilerError, ObjectFrozenException
	{
		ArrayList<Expression> output = new ArrayList<Expression>(2);
		ArrayList<Token>      buffer = new ArrayList<Token>();
		
		int endParent = findClosingParen(tokens, start);
		
		for (int i = start; i < endParent; i++)
		{
			Token t = tokens[i];
			if (t.getLexeme().equals(","))
			{
				if (buffer.size() == 0) {
					throw new SyntaxError("Method calling missing parameter", t);
				}
				output.add(ExpressionParser.tryParse(context, buffer.toArray(new Token[0])));
				buffer.clear();
			}
			else buffer.add(t);
		}
		if (buffer.size() != 0) {
			output.add(ExpressionParser.tryParse(context, buffer.toArray(new Token[0])));
		}
		return output.toArray(new Expression[0]);
	}
	
	public static final Expression parse(ParserContext context, Token[] tokens) 
			throws CompilerError, ObjectFrozenException
	{
		try // Wrap in a try-catch to handle StackOverflows in expression parsing
		{
			return tryParse(context, tokens);
		}
		catch (StackOverflowError e) {
			throw new EvaluationError("Expression too complex", tokens[0]);
		}
	}
	private static final Expression tryParse(ParserContext context, Token[] tokens) 
			throws CompilerError, ObjectFrozenException
	{
		if (tokens.length == 0) return null;
		
		Token lastToken = null; // Used to check for function calls
		CachedArrayList<Object> parts = new CachedArrayList<>(Object.class);
		
		for (int i = 0; i < tokens.length; i++)
		{
			Token     t    = tokens[i];
			TokenType type = t.getType();
			
			if (type.isLiteral()) {
				parts.add(new Literal(t));
			}
			else if (type == TokenType.Operator) {				
				parts.add(t);
			}
			else if (type == TokenType.Identifier) {
				//parts.add(new )
			}
			else if (type == TokenType.Delimiter)
			{
				if ("(".equals(t.getLexeme()))
				{
					// Check if previous token was an identifier, as then it's a function call
					if (lastToken != null && lastToken.getType() == TokenType.Identifier)
					{
						// Split & parse parameters
						// This would be a good place to use a 'ref' param for i,
						// If Java supported them, that is...
						Expression[] params = ExpressionParser.parseParams(context, tokens, i);
						parts.add(new FunctionCall(context, lastToken, params));
						// Skip past parameters
						i = ExpressionParser.findClosingParen(tokens, i);
						lastToken = null; // Reset lastToken
					}
					// Otherwise it's either an expression scope or cast
					// Treat these the same for now
					else
					{
						int end = ExpressionParser.findClosingParen(tokens, i);
						if (end == -1) 
						{
							throw new SyntaxError("Unexpected end of expression - " +
									"missing closing parenthesis", tokens[i]);
						}
						else
						{
							Token[] buffer = new Token[end - i];
							for (int n = 0, e = i; e < end; n++, e++) { buffer[n] = tokens[e]; }
							
							// Determine if the current expression is a cast
							if (end + 1 != tokens.length)
							{
								Token next = tokens[end + 1];
								
								if (next.getType() != TokenType.Operator && !")".equals(next.getLexeme())) {
									parts.add(TypeCastParser.parse(context, buffer));
								}
								// Otherwise, parse as normal
								else parts.add(ExpressionParser.parse(context, buffer));
							}
							// Otherwise, parse as normal
							else parts.add(ExpressionParser.parse(context, buffer));
						}
					}
				}
				else if (":".equals(t.getLexeme())) {
					parts.add(':');
				}
			}
			// Update lastToken
			lastToken = t;
		}
		
		// Process casts
		for (int i = 0; i < parts.size(); i++)
		{
			Object part = parts.get(i);
			
			if (part instanceof TypeCast)
			{
				// Shouldn't need to check these
				TypeCast op = (TypeCast)part;
				op.setExpression((Expression)parts.remove(i+1));
				op.freeze();
			}
			// Skip non-casts
			else continue;
		}
		
		// Process 
		
		for (int i = 1; i < Operation.getMaxLevel(); i++)
		{
			for (int n = 0; n < parts.size(); n++)
			{
				Object part = parts.get(n);
				
				// Operator
				if (part instanceof Token && ((Token)part).getType() == TokenType.Operator)
				{
					Token op = (Token)part;
					Expression lhs, rhs, p3;
					
					Integer opArity = Operation.getOperatorArity(op);
					
					// Look for first parameter
					if (n == 0 || !(parts.get(n-1) instanceof Expression)) {
						lhs = null;
					} else lhs = (Expression)parts.get(n-1);
					
					// Look for second parameter
					if (n + 1 == parts.size() || !(parts.get(n+1) instanceof Expression)) {
						rhs = null;
					} else rhs = (Expression)parts.get(n+1);
					
					if (opArity == 3)
					{
						// Look for third parameter (if ':' present)
						if (n + 2 == parts.size() || !(parts.get(n+2) instanceof Token)) {
							p3 = null;
						}
						else if (!((Token)parts.get(n+2)).getLexeme().equals(":")) {
							p3 = null;
						}
						else if (n + 3 == parts.size() || !(parts.get(n+3) instanceof Expression)) {
							throw new SyntaxError("Invalid token - missing expression", (Token)parts.get(n+2));
						}
						else p3 = (Expression)parts.get(n+3);
						
						// Now parse & add
						parts.set(n - 1, OperationParser.parse(context, op, lhs, rhs, p3));
						// Remove component parts
						for (int t = n; t < n + 4; t++) { parts.remove(t); }
					}
					else
					{
						Operation o = OperationParser.parse(context, op, lhs, rhs);
						
						int removeStart, removeEnd;
						
						if (o.getLHS() == null)
						{
							parts.set(n, 0);
							removeStart = n + 1;
							removeEnd   = n + 2;
						}
						else if (o.getRHS() == null)
						{
							parts.set(n - 1, o);
							removeStart = n;
							removeEnd   = n + 1;
						}
						else
						{
							parts.set(n - 1, o);
							removeStart = n;
							removeEnd   = n + 2;
						}
						for (int t = removeStart; t < removeEnd; t++) { 
							parts.remove(t); 
						}
						n = removeStart - 1;
					}
				}
				else if (part instanceof TypeCast)
				{
					// Shouldn't need to check these
					TypeCast op = (TypeCast)part;
					op.setExpression((Expression)parts.remove(i+1));
					op.freeze();
				}
				// Skip non-casts
				else continue;
			}
		}
		if (parts.size() != 1 || !(parts.get(0) instanceof Expression)) {
			throw new SyntaxError("Invalid expression", tokens[0]);
		}
		return (Expression)parts.get(0);
	}
}
