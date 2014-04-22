package uk.ac.ed.maize.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import uk.ac.ed.maize.code.Variable;

public class VirtualStack
{
	private int esp;
	private Stack<Integer> frames;
	private List<Integer>  indices;
	private List<Variable> variables;
	
	public VirtualStack()
	{
		this.esp       = 0;
		this.frames    = new Stack<Integer>();
		this.indices   = new ArrayList<Integer>();
		this.variables = new ArrayList<Variable>();
		
		this.frames.push(0);
		this.indices.add(-1);
		this.variables.add(null);
	}
	
	public void newFrame() {
		frames.push(this.esp);
	}
	
	public Integer unwind() {
		return frames.pop();
	}
	
	public Integer getStackIndex(Variable var)
	{
		int mapping = variables.indexOf(var);
		if (mapping == -1) return null;
		
		return (this.indices.get(mapping) - frames.peek());
	}
	
	public void setStackIndex(int index, Variable var)
	{
		index += frames.peek();
		
		int mapping = indices.indexOf(index);
		if (mapping == -1)
		{
			indices.add(index);
			variables.add(var);
		}
		else variables.set(index, var);		
	}
	
	public Integer pushVariable(Variable var)
	{
		indices.add(esp);
		variables.add(var);
		return (esp++ - frames.peek());
	}
	
	public Variable popVariable()
	{
		int mapping = indices.indexOf(--esp);
		if (mapping == -1) return null;
		
		indices.remove(mapping);
		return variables.remove(mapping);
	}
}
