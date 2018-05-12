package com.hit.algorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MFUAlgoCacheImpl<K,V> implements IAlgoCache<K, V> {
	
	private Map<K, V> virtualMemory;
	private Map<K, Integer> usageCount;
	private Integer capacity;
	private static final Integer VIRTUAL_MEMORY_DEFAULT_SIZE = 5 ;

	// Default constructor with default virtual memory size.
	public MFUAlgoCacheImpl() 
	{
		this(VIRTUAL_MEMORY_DEFAULT_SIZE);
	}

	// Constructor which gets the virtual memory size as an argument.
	public MFUAlgoCacheImpl(Integer size) 
	{
		if(size > 0) 
		{
			virtualMemory = new HashMap<K, V>(size);
			usageCount = new LinkedHashMap<K, Integer>(size);
			capacity = size;
		}
		else 
		{
			virtualMemory = new HashMap<K, V>(VIRTUAL_MEMORY_DEFAULT_SIZE);
			usageCount = new LinkedHashMap<K, Integer>(VIRTUAL_MEMORY_DEFAULT_SIZE);
			capacity = VIRTUAL_MEMORY_DEFAULT_SIZE;
		}
	}
	
	/*
	 * Helping method to find the key with the maximum use counter.
	 * Returns:
	 * key with maximum use counter.
	 */
	private K findMax() 
	{
		Integer maxValueInMap = (Collections.max(usageCount.values()));  	// This will return max value in the Hash map
        
		for (Entry<K, Integer> entry : usageCount.entrySet()) 
		{  		// Iterate through hash map
            if (entry.getValue() == maxValueInMap) 
            {                
                return entry.getKey();									// Return the key with max value
            }
        }
		
        return null;
	}

	/*
	 * Associates the new specified value with the existing specified key in memory.
	 * Parameters:
	 * key - with which the specified value is to be associated.
	 * value - to be associated with the specified key.
	 */
	private void updateValue(K key, V newValue) 
	{
		Integer newCounter = usageCount.get(key) + 1;
		virtualMemory.put(key, newValue);							// Update value of key and use counter.
		usageCount.remove(key);
		usageCount.put(key, newCounter);
	}

	/*
	 * Associates the specified value with the  specified key in memory.
	 * Parameters:
	 * key - with which the specified value is to be associated.
	 * value - to be associated with the specified key.
	 */
	private void registerNewKey(K key, V newValue) 
	{
		virtualMemory.put(key, newValue);					
		usageCount.put(key, 1);
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
	public List<V> getElement(List<K> key) 
	{
		List<V> valuesToReturn = new LinkedList<V>();
		
		for(K k: key) 
		{
			if(virtualMemory.containsKey(k)) {
				valuesToReturn.add(virtualMemory.get(k));
			    usageCount.put(k, usageCount.get(k) + 1);				// Updating use counter.
			}
			else
				valuesToReturn.add(null);
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
	public List<V> putElement(List<K> key, List<V> value) 
	{
		List<V> valuesToReturn = new LinkedList<V>();
		K keyToRemove, currentKey;
		V currentValueToReplace, currentValue;
		Iterator<K> keyIterator = key.iterator();
		Iterator<V> valueIterator = value.iterator();
		
		while(keyIterator.hasNext() && valueIterator.hasNext()) 
		{
			currentValueToReplace = null;
			currentKey = keyIterator.next();
			currentValue = valueIterator.next();
			
			if(virtualMemory.size() == capacity) 
			{ 								
				if(!virtualMemory.containsKey(currentKey)) 						// If virtual memory is full and key does not exist in memory:
				{    					 
					keyToRemove = findMax();									// Get the key with the maximum use counter.	
					currentValueToReplace = virtualMemory.get(keyToRemove);
					removeElement(Arrays.asList(keyToRemove));					// Remove this key and insert the new values.
					registerNewKey(currentKey, currentValue);
				}
				else 															// If virtual memory is full and key exist in memory:
					updateValue(currentKey, currentValue);						// Update value of key and use counter.
			}
			else 
			{
				if(virtualMemory.containsKey(currentKey)) 						// If memory is not full yet and key exist in memory:
					updateValue(currentKey, currentValue);						// Update value of key and use counter.
				else 
				{
					registerNewKey(currentKey, currentValue);					// If memory is not full yet and key does no exist in memory:	
					usageCount.put(currentKey, 1);								// Insert new key and value and create use counter for that key which set to 1.
				}
			}
			
			valuesToReturn.add(currentValueToReplace);
		}
		
		return valuesToReturn;
	}

	/*
	 * Removes the mapping for the specified key from this map if present.
	 * Parameters:
	 * key - whose mapping is to be removed from the cache according to the current algorithm
	 */
	@Override
	public void removeElement(List<K> key) 									
	{									
		for(K k: key) 
		{
			virtualMemory.remove(k);
			usageCount.remove(k);
		}
	}

	@Override
	public String toString() 
	{
		return "MFUAlgoCacheImpl [cache = " + virtualMemory + ", capacity=" + capacity + "]    [items counter: " + usageCount + "]";
	}
}
