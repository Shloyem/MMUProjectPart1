package com.hit.algorithm;

import java.util.List;

public interface IAlgoCache<K,V>
{
	//Returns the value to which the specified key is mapped, or null if this cache contains no mapping for the key.
	public List<V> getElement(List<K> keys);
	
	//Associates the specified value with the specified key in this cache according to the current algorithm
	public List<V> putElement(List<K> keys,List<V> values);
	
	//Removes the mapping for the specified key from this map if present.
	public void removeElement(List<K> key);
}