package uk.ac.ed.kl;

import java.util.ArrayList;

import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;

public class TokenChain extends ArrayList<Token>
{
	private static final long serialVersionUID = 1L;
	
	private boolean isNameToken(TokenType token)
	{
		return (token.isLiteral() || token == TokenType.Directive 
				|| token == TokenType.Identifier || token == TokenType.Keyword);
	}
	
	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		TokenType prevType = TokenType.Delimiter;
		
		for (Token t : this)
		{
			if (this.isNameToken(prevType) && this.isNameToken(prevType = t.getType()))
			{
				output.append(' ');
			}
			output.append(t.getLexeme());
		}
		return output.toString();
	}
	
	@Override
	public Token[] toArray()
	{
		Token[] output = new Token[this.size()];
		return this.toArray(output);
	}

}
