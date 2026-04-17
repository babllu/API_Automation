package utils;

import java.util.Map;

public class SampleGetTC_01_1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		APIComponent execution = new APIComponent();
		Map<String, Object> response = execution.executeSingleAPI();
		boolean testCaseStatus=execution.assertAllValidation(response);
		System.out.println(testCaseStatus);
	}

}
