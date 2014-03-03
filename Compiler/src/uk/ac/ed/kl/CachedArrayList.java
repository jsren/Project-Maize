package uk.ac.ed.kl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class CachedArrayList<T> extends ArrayList<T>
{
	private static final long serialVersionUID = 1L;

	T[] cachedArray;
	Class<T> type;
	
	public CachedArrayList(Class<T> t)
	{
		this.type = t;
	}
	
	@Override
	public void add(int index, T element)
	{
		this.cachedArray = null;
		super.add(index, element);
	}
	
	@Override
	public boolean add(T e)
	{
		this.cachedArray = null;
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		this.cachedArray = null;
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		this.cachedArray = null;
		return super.addAll(index, c);
	}
	
	@Override
	public void clear()
	{
		this.cachedArray = null;
		super.clear();
	}
	
	@Override
	public T remove(int index)
	{
		this.cachedArray = null;
		return super.remove(index);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) 
	{
		this.cachedArray = null;
		return super.removeAll(c);
	}
	
	@Override
	public boolean remove(Object o)
	{
		this.cachedArray = null;
		return super.remove(o);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		this.cachedArray = null;
		return super.retainAll(c);
	}
	
	@Override
	public T set(int index, T element)
	{
		T out = super.set(index, element);
		this.cachedArray[index] = element;
		return out;
	}
	
	@Override
	public void trimToSize()
	{
		this.cachedArray = null;
		super.trimToSize();
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray()
	{
		if (this.cachedArray == null)
		{
			this.cachedArray = (T[])Array.newInstance(type, this.size());
			this.toArray(cachedArray);
		}
		return this.cachedArray;
	}
}
