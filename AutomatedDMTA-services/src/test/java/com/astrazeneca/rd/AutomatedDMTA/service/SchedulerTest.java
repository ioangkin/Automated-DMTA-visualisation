package com.astrazeneca.rd.AutomatedDMTA.service;

import static org.junit.Assert.*;
import org.junit.Test;

public class SchedulerTest
{

	@Test
	public void testPaths()
	{
		String path = "C:\\dev\\workspace\\AutomatedDMTA\\AutomatedDMTA-services\\src\\test\\resources\\Cycle\\Bioassay\\";
		assertEquals("design_File_Path is not as expected", Scheduler.getDesignFilePath(), path);
	}

}
