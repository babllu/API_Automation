package testsuites.orchsampleapi;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import utils.APIComponent;

public class SampleOrchGetTC_03 {

	@Test
	private void validateApiResponseSampleOrchGetTC_03() throws ClassNotFoundException
	{
		APIComponent execution = new APIComponent();
		Map<String, Object> response = execution.executeSingleAPI();
		boolean testCaseStatus=execution.assertAllValidation(response);
		if(testCaseStatus==false)
		{ 
			Assert.fail();
		}
	
}
	
}
