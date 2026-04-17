package testsuites.sampleapi;

import java.util.Map;

import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.APIComponent;

public class SampleGetTC_01 {
	
	
	@Test
	private void validateApiResponseSampleGetTC_01() throws ClassNotFoundException
	{
		APIComponent execution = new APIComponent();
		Map<String, Object> response = execution.executeSingleAPI();
		boolean testCaseStatus=execution.assertAllValidation(response);
		if(testCaseStatus==false)
		{
			Assert.fail();
		}
		System.out.println("Get API executed");
		
	}

}
