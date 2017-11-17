package com.astrazeneca.rd.AutomatedDMTA.repository;

import com.astrazeneca.rd.AutomatedDMTA.model.StageType;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

/**
 * @author klfl423
 * 
 * Testing the Enum class
 *
 */

public class StageTypeTest {

	public static void main(String[] args) {
		for (StageType n : StageType.values()) {
	    	testEnumBacklog(n);
		}

	}
	
	public static void testEnumBacklog(StageType stage){		
		
		System.out.println(stage + "'s order is " + stage.ordinal());
		
		if (stage.compareTo(StageType.BACKLOG) > 0) {
			System.out.println("compound has moved to " + stage + " stage"); 
			}

		if (stage.equals(StageType.BACKLOG)) {
			System.out.println("compound has not started the cycle yet"); 
		}
		
		System.out.println();
		
	}
}
