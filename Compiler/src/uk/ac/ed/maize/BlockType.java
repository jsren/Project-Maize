package uk.ac.ed.maize;

public enum BlockType
{
	File,
	Checked,
	Class,
	Else,
	Elseif,
	Enum,
	Function,
	If,
	Interface,
	Namespace,
	Operator,
	Scope,
	Struct,
	While;
	
	public static final boolean isTypeBlock(BlockType type)
	{
		return type == Class || type == Enum || type == Interface
				|| type == Struct;
	}
	public static final boolean isExecutable(BlockType type) {
		return type == Function || type == Operator;
	}
}
