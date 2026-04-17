package testsuites.orchsampleapi;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import base.PSM;
import listeners.ListenerClass;
import utils.APIComponent;

@Listeners(ListenerClass.class)
public class SampleOrchPostTC_01 {

	@Test
	private void validateApiResponseSampleOrchPostTC_01() throws ClassNotFoundException
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
