package testsuites.orchsampleapi;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.PSM;
import utils.APIComponent;

public class SampleOrchGetTC_02 extends PSM{

	@Test
	private void validateApiResponseSampleOrchGetTC_02() throws ClassNotFoundException
	{
		APIComponent execution = new APIComponent();
		Map<String, Object> response = execution.executeOrchestratedSingleAPI();
		boolean testCaseStatus=execution.assertAllValidation(response);
		if(testCaseStatus==false)
		{ 
			Assert.fail();
		}
		 
		
	}
}
