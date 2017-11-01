package com.astrazeneca.rd.AutomatedDMTA.model;

import com.astrazeneca.rd.AutomatedDMTA.model.StageType;

/**
 * @author klfl423
 * 
 * Testing the Enum class
 *
 */

public class TestEnum {

	public static void main(String[] args) {
		
		for (StageType n : StageType.values()) {

			testEnumBacklog(n);

			}
	}
	
	public static void testEnumBacklog(StageType stage){
	
		System.out.println(stage + "'s order is " + stage.ordinal());
		
		if (stage.ordinal() >= 1) {
			
			System.out.println("compound has started the cycle and is in " + stage + " stage");
			
			}
		
		else {
			
			System.out.println("compound is still in Backlog");
		
			}
		
		System.out.println();
		
	}
}
