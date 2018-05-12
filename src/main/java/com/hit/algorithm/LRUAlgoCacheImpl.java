package com.hit.algorithm;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class LRUAlgoCacheImpl<K,V> implements IAlgoCache<K, V>
{
	private Map<K, V> virtualMemory;
	private int capacity;
	private static final Integer VIRTUAL_MEMORY_DEFAULT_SIZE = 5 ;

	public LRUAlgoCacheImpl()
	{
		this(VIRTUAL_MEMORY_DEFAULT_SIZE);
	}
	
	public LRUAlgoCacheImpl(int size)
	{
		if(size>0)
		{
			virtualMemory= new LinkedHashMap<>(size);
			this.capacity=size;
		}
		else
		{
			virtualMemory= new LinkedHashMap<>(VIRTUAL_MEMORY_DEFAULT_SIZE);
			this.capacity=VIRTUAL_MEMORY_DEFAULT_SIZE;
		}
	}
	
	/*
	 * Returns the value to which the specified key is mapped, or null if this cache contains no mapping for the key. 
	 * In addition performs the relevant cache algorithm
	 * Parameters:
	 * key - with which the specified value is to be associated
	 * Returns:
	 * the value to which the specified key is mapped, or null if this cache contains no mapping for the key
	 */
	@Override
	public List<V> getElement(List<K> keys)
	{
		List<V> valuesToReturn= new LinkedList<>();
		
		for(K key:keys)									//For each key on the given list
		{
			if(virtualMemory.containsKey(key))			//If the key is in the memory already
			{
				V valueOfKey = virtualMemory.get(key);	
				valuesToReturn.add(valueOfKey);				//Add the value of this key to the returned values list
				virtualMemory.remove(key);				
				virtualMemory.put(key, valueOfKey);		//Remove it and re-insert it, to keep track of the least recently used
			}
			else
				valuesToReturn.add(null);					//In case the key is not in the memory, add null to the returned list
		}
		
		return valuesToReturn;
	}

	/*
	 * Associates the specified value with the specified key in this cache according to the current algorithm
	 * Parameters:
	 * key - with which the specified value is to be associated
	 * value - to be associated with the specified key
	 * Returns:
	 * return the value of the element which need to be replaced
	 */
	public List<V> putElement(List<K> keys, List<V> values)
	{
		List<V> valuesToReturn = new LinkedList<>();						//Values which have been eliminated
		K currentKey,	removedKey;
		V currentValue,	removedValue;
		Iterator<K> keysIterator = keys.iterator();
		Iterator<V> valuesIterator = values.iterator();
		
		while (keysIterator.hasNext() && valuesIterator.hasNext())	//We will continue as long as both keys and values list are not over
		{
				removedValue = null;
				currentKey = keysIterator.next();
				currentValue = valuesIterator.next();
				
				if (virtualMemory.size() == capacity)						//Memory is full and a page needs to be replaced
				{
					if (virtualMemory.containsKey(currentKey))
					{
						virtualMemory.remove(currentKey);				//Remove and add the current key at top
						
					}
					else
					{
						removedKey = virtualMemory.keySet().iterator().next();	//Find the first(and therefore the least recently used) key 			
						removedValue = virtualMemory.get(removedKey);			//Use it to find it's value
						virtualMemory.remove(removedKey);						//removing the entry using the key to be removed
					}
				}
				
				valuesToReturn.add(removedValue);					//Add the value to be remove or null if none was removed
				virtualMemory.put(currentKey, currentValue);		//Insert the current key to the memory
		}
		
		return valuesToReturn;
	}
	
	/*
	 * Removes the mapping for the specified key from this map if present.
	 * Parameters:
	 * key - whose mapping is to be removed from the cache according to the current algorithm
	 */
	@Override
	public void removeElement(List<K> keys)
	{
		for(K key:keys)
		{
			virtualMemory.remove(key);
		}
	}
	
	public String toString() 
	{
		return "LRUAlgoCacheImpl [cache = " + virtualMemory + ", capacity = " + capacity + "]";
	}
}