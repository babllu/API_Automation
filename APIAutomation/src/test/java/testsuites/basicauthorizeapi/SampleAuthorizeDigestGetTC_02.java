package testsuites.basicauthorizeapi;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import utils.APIComponent;

public class SampleAuthorizeDigestGetTC_02 {

	@Test
	private void validateApiResponseSampleAuthorizeDigestGetTC_02() throws ClassNotFoundException
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
