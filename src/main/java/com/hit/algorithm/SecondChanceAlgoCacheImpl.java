package com.hit.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SecondChanceAlgoCacheImpl<K,V> implements IAlgoCache<K, V>
{
	private Map<K,V> virtualMemory;
	private Map<K,Boolean> referenceBit;
	private int capacity;
	private static final Integer VIRTUAL_MEMORY_DEFAULT_SIZE = 5;
	
	public SecondChanceAlgoCacheImpl()
	{
		this(VIRTUAL_MEMORY_DEFAULT_SIZE);
	}
	
	public SecondChanceAlgoCacheImpl(int size)
	{
		if(size>0)
		{
			this.capacity = size;
			virtualMemory = new LinkedHashMap<>(size);
			referenceBit = new HashMap<K, Boolean>(size);
		}
		else
			virtualMemory = new LinkedHashMap<>(VIRTUAL_MEMORY_DEFAULT_SIZE);
			referenceBit = new HashMap<K, Boolean>(VIRTUAL_MEMORY_DEFAULT_SIZE);
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
		List<V> valuesToReturn = new LinkedList<>();
		
		for(K key:keys)									//For each key on the given list
		{
			if(virtualMemory.containsKey(key))			//If the key is in the memory already
			{
				V valueOfKey = virtualMemory.get(key);
				valuesToReturn.add(valueOfKey);				//Add the value of this key to the returned values list	
				referenceBit.put(key, true);			//change it's reference bit into true(1) because we addressed it
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
	@Override
	public List<V> putElement(List<K> keys, List<V> values)
	{
		List<V> valuesToReturn = new LinkedList<>();			//Values which have been eliminated
		K currentKey,	currentKeyToReplace;
		V currentValue,	currentValueToReplace;
		Iterator<K> keysIterator = keys.iterator();
		Iterator<V> valuesIterator = values.iterator();
		
		while (keysIterator.hasNext() && valuesIterator.hasNext())
		{
				currentValueToReplace = null;
				currentKey = keysIterator.next();
				currentValue = valuesIterator.next();
				
				//If the key is present in the memory, make it's reference true because we addressed it
				if(virtualMemory.containsKey(currentKey))
				{
					referenceBit.put(currentKey, true);
				}
				else
				{
					if(virtualMemory.size() == capacity)		//Memory is full and a page needs to be replaced
					{
						currentKeyToReplace = findKeyToReplace();
						currentValueToReplace = virtualMemory.get(currentKeyToReplace);
						virtualMemory.remove(currentKeyToReplace);
						referenceBit.remove(currentKeyToReplace);
					}
					
					virtualMemory.put(currentKey, currentValue);
					referenceBit.put(currentKey, false);
				}
				
				valuesToReturn.add(currentValueToReplace); 		//Adding a value that was replaced' or null if there was no need to replace
		}
		
		return valuesToReturn;
	}
	
	/*
	 * Find the key to be replaced according to second chance paging algorithm - if a page has been addressed he gets a second chance before replaced
	 * Returns:
	 * key to be replaced after all the addressed ones got a second chance
	 */
	private K findKeyToReplace()
	{
		V currentValue;
		K currentKey;
		List<K> keysToBeReinserted = new LinkedList<>();
		List<V> valuesToBeReinserted = new LinkedList<>();
		Iterator<K> keysIterator = virtualMemory.keySet().iterator();

		while(keysIterator.hasNext())
		{
			currentKey=keysIterator.next();
			
			if(referenceBit.get(currentKey) == true)		//If the reference bit is true
			{
				referenceBit.put(currentKey, false);		//Make it false (as a second chance for the entry)
				currentValue = virtualMemory.get(currentKey);
				keysToBeReinserted.add(currentKey);			//Can't insert or omit while iterating on a Virtual Memory map
				valuesToBeReinserted.add(currentValue);		//So they will be added with the 'false' Reference 
			}
			else											//If the reference bit is false
			{
				reInsertToMemory(keysToBeReinserted, valuesToBeReinserted);	//Re-insert the pages which were changed from true to false before
				return currentKey;
			}
		}									//Given we've iterated over all the list, they were all true's, we 
											//changed them to false and now will take the first one
		reInsertToMemory(keysToBeReinserted, valuesToBeReinserted);
		
		return virtualMemory.keySet().iterator().next();
	}
	
	//Can't insert or omit while iterating on a Virtual Memory map, so re-adding objects to the map after finished iterating
	private void reInsertToMemory(List<K> keys,List<V> values)
	{
		K currentKey;
		V currentValue;
		
		for(int i=0; i < keys.size(); i++)
		{
			currentKey = keys.get(i);
			currentValue = values.get(i);
			virtualMemory.remove(currentKey,currentValue);
			virtualMemory.put(currentKey,currentValue);
		}
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
			referenceBit.remove(key);
		}
	}
	
	@Override
	public String toString() 
	{
		return "SecondChanceAlgoCacheImpl [cache = " + virtualMemory + ", capacity=" + capacity + "]    [items status: " + referenceBit + "]";
	}
}
