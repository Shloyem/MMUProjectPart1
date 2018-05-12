package com.hit.algorithm;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class IAlgoCacheTest
{
	private List<Integer> initialKeys = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
	private List<String> initialValues = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh");
	private List<String> receivedValues = new ArrayList<String>();
	private IAlgoCache<Integer, String> algorithmTested;
	private List<Integer> keysToGetOrPut;
	private List<String> valuesToEnter;
	private List<String> expectedValues;
	
	//Perform PUT command, and print the input, output and states of before and after
	private void putElementsInAlgorithm()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		//Printing keys and values the PUT command will use
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Before PUT command:").append(System.lineSeparator());						
		stringBuilder.append(algorithmTested.toString()).append(System.lineSeparator());			
		stringBuilder.append("*** Perofrming PUT command for -");  
		
		stringBuilder.append(generateKeysString());
		stringBuilder.append(generateValuesToEnterString());
		stringBuilder.append(generateExpectedValuesString());
		
		//Performing the PUT command and testing expected result is as actual result
		receivedValues = algorithmTested.putElement(keysToGetOrPut, valuesToEnter);
		assertThat(receivedValues, is(expectedValues));
		
		//Print state after PUT command
		stringBuilder.append("After PUT command:").append(System.lineSeparator());						
		stringBuilder.append(algorithmTested.toString()).append(System.lineSeparator());
		
		System.out.println(stringBuilder.toString());
	}
	
	private String generateExpectedValuesString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(System.lineSeparator()).append("Expecting the values of:[ ");
		
		for (String expectedValue : expectedValues)
		{
			stringBuilder.append(expectedValue);
			stringBuilder.append(" ");
		}
		
		stringBuilder.append("]").append(System.lineSeparator());		
		
		return stringBuilder.toString();
	}
	
	private String generateValuesToEnterString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("    Values: [");
		
		for (String value : valuesToEnter)
		{
			stringBuilder.append(value).append(" ");
		}
		
		stringBuilder.append("]");
		
		return stringBuilder.toString();
	}

	private String generateKeysString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("keys: [");
		
		for (Integer key : keysToGetOrPut)
		{
			stringBuilder.append(key).append(" ");
		}
		
		stringBuilder.append("]");
		
		return stringBuilder.toString();
	}

	//Perform GETcommand, and print the input, output and states of before and after
	private void getElementsInAlgorithm()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Before GET command:").append(System.lineSeparator());						
		stringBuilder.append(algorithmTested.toString()).append(System.lineSeparator());			
		stringBuilder.append("*** Perofrming GET command for -");
		
		stringBuilder.append(generateKeysString());
		stringBuilder.append(generateExpectedValuesString());
		
		receivedValues = algorithmTested.getElement(keysToGetOrPut);
		assertThat(receivedValues, is(expectedValues));
		
		stringBuilder.append("After GET command:").append(System.lineSeparator());						
		stringBuilder.append(algorithmTested.toString()).append(System.lineSeparator());
		
		System.out.println(stringBuilder.toString());
	}
	
	//Perform a test for LRU algorithm
	@Test
	public void testLRU()
	{
		System.out.println("\nLRU Algorithm test:");
		algorithmTested = new LRUAlgoCacheImpl<Integer,String>(5);
		
		keysToGetOrPut = initialKeys;
		valuesToEnter = initialValues;
		expectedValues = Arrays.asList(null, null, null, null, null, "First", "Second");
		putElementsInAlgorithm();
		
		keysToGetOrPut = Arrays.asList(3, 10, 11, 6, 7);
		expectedValues = Arrays.asList("Third", null, null, "Sixth", "Seventh");
		getElementsInAlgorithm();
		
		keysToGetOrPut = Arrays.asList(5, 4, 1, 2, 8, 9, 10);
		valuesToEnter = Arrays.asList("Fifth","Fourth","First","Second","Eighth","Ninth","Tenth");
		expectedValues = Arrays.asList(null,null,"Third","Sixth","Seventh","Fifth","Fourth");
		putElementsInAlgorithm();
	}
	
	//Perform a test for Second Chance algorithm
	@Test	
	public void testSecondChance()
	{
		System.out.println("\nSecond Chance Algorithm test:");
		algorithmTested = new SecondChanceAlgoCacheImpl<Integer,String>(5);

		keysToGetOrPut = initialKeys;
		valuesToEnter = initialValues;
		expectedValues = Arrays.asList(null, null, null, null, null, "First", "Second");
		putElementsInAlgorithm();	//3-7 in memory
		
		keysToGetOrPut = Arrays.asList(3, 10, 11, 6, 7);
		expectedValues = Arrays.asList("Third",null,null,"Sixth","Seventh");
		getElementsInAlgorithm();
		
		keysToGetOrPut = Arrays.asList(1, 2, 8, 9, 10, 5, 4);
		valuesToEnter = Arrays.asList("First","Second","Eighth","Ninth","Tenth","Fifth","Fourth");
		expectedValues = Arrays.asList("Fourth","Fifth","Third","First","Second","Sixth","Seventh");
		putElementsInAlgorithm();
	}
	
	//Perform a test for MFU algorithm
	@Test
	public void testMFU()
	{
		System.out.println("\nMFU Algorithm test:");
		algorithmTested = new MFUAlgoCacheImpl<Integer,String>(5);
		
		
		keysToGetOrPut = initialKeys;
		valuesToEnter = initialValues;
		expectedValues = Arrays.asList(null, null, null, null, null, "First", "Second");
		putElementsInAlgorithm();	//3-7 in memory
		
		
		keysToGetOrPut = Arrays.asList(3, 10, 11, 5, 4);
		expectedValues = Arrays.asList("Third",null,null,"Fifth","Fourth");
		getElementsInAlgorithm();	//3,5,4 are present
		
		keysToGetOrPut = Arrays.asList(5, 4, 4, 1, 2, 8, 9);
		valuesToEnter = Arrays.asList("Fifth","Fourth","Fourth","First","Second","Eighth","Ninth");
		expectedValues = Arrays.asList(null, null, null,"Fourth","Fifth","Third","Sixth");
		putElementsInAlgorithm();	//replacement order: [4 with counter = 3] , [5 with counter = 2] , [3 with counter = 1] , and then the rest	
	}
}
