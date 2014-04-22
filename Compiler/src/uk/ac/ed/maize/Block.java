package uk.ac.ed.maize;

import java.util.EnumSet;

import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Visibility;
import uk.ac.ed.maize.parser.Attribute;

public class Block
{
	private BlockType type;
	protected Block  parent;
	protected Scope  scope;
	protected String name;
	protected Token  startToken;
	
	protected Visibility         visibility;
	protected EnumSet<Attribute> attributes;
	
	public Block(BlockType type, Token startToken)
	{
		this.type       = type;
		this.startToken = startToken;
		this.visibility = Visibility.Private;
		this.attributes = EnumSet.noneOf(Attribute.class);
	}
	public Block(BlockType type, Token startToken, Block parent)
	{
		this.type       = type;
		this.parent     = parent;
		this.startToken = startToken;
		this.visibility = Visibility.Private;
		this.attributes = EnumSet.noneOf(Attribute.class);
	}
	
	
	public BlockType getType() { return this.type; }
	
	public Block  getParent()     { return this.parent; }
	public Scope  getScope()      { return this.scope; }
	public String getName()       { return this.name; }
	public Token  getStartToken() { return this.startToken; }
	
	public Visibility         getVisibility() { return this.visibility; }
	public EnumSet<Attribute> getAttributes() { return this.attributes; }
	
	
	public String getFullNamespace()
	{
		String output = "";
		
		if (this.parent != null) 
			output = this.parent.getFullNamespace();
		if (this.name != null)
			output = (output.length() == 0 ? "" : "::") + this.name;
		
		return output;
	}
}
