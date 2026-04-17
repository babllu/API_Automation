package testsuites.sampleapi;

import java.util.Map;

import org.apache.poi.util.SystemOutLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.APIComponent;

public class SamplePutTC_3R {
	
	@Test
	private void validateApiResponseSamplePutTC_3R() throws ClassNotFoundException
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
