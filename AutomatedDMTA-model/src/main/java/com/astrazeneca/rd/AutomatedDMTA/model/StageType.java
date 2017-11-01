package com.astrazeneca.rd.AutomatedDMTA.model;

/**
 * @author klfl423
 * 
 * Stage enumerated type class
 *
 */

/*
* Maintain Stages' order for comparison. 
* stage.ordinal() returns stage's order int value based on their order in this declaration
*/
public enum StageType {
	BACKLOG,
	DESIGN,
	SYNTHESIS,
	PURIFICATION,
	TESTING;
	
}
