package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import listeners.ListenerClass;
import logger.Logger;
import reporting.ExtentLogger;

public class APIComponent {

	
	
	/**
	 * This method is used to execute a single api
	 *
	 * @author bghosh
	 * @return Map containing headers and response body
	 */
	public Map<String, Object> executeSingleAPI() {
		ExcelUtility dataTable=new ExcelUtility();
		String testCaseName = dataTable.getTestcasename();
		Logger logger = new Logger(testCaseName);
		Map<String, Object> responseDetails = new HashMap<String,Object>();
		Map<String, String> header = APIUtils.getRequestHeaders(dataTable);
		RequestSpecification requestSpecification = APIUtils.authorize(dataTable);
		String uri1 = dataTable.getData("General", "URI1").trim();
		String method = dataTable.getData("General", "SERVICE_METHOD").trim();
		System.out.println("API TYPE : " + method);
		String resCode = dataTable.getData("General", "RESPONSE_CODE").trim();
		header = APIUtils.updatedHeader(header, "APIEnvFile");
		uri1 = APIUtils.processEnvVariable(uri1, "APIEnvFile");
		if (uri1.contains("((")) {
			uri1 = APIUtils.processTestData(uri1);
		}
			logger.captureLog("================Test Case:" + testCaseName+"=====================================");
			logger.captureLog("URI1:" + uri1);
			ExtentLogger.loginfo("URI1:" + uri1);
			logger.captureLog("Request Header:" + header);
			APIUtils.requestHeaderDetails(header);
			Response response1 = null;
			if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE"))
			{
				response1 = APIServices.sendNReceive(uri1, method.toUpperCase(), header , requestSpecification);
				APIUtils.generatePostmanCollection(method.toUpperCase(), "", header, uri1, testCaseName, resCode, ListenerClass.obj);
			}
			else if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH") || method.equalsIgnoreCase("PUT"))
			{
				String bodyFile = dataTable.getData("General", "BODY").trim();
				// String payloadPath = System.getProperty("user.dir")+"/src/test/java/payloads/"+bodyFile;
				String payloadBody = "";
				APIUtils.updatePayloadWithEnvFile(bodyFile, "APIEnvFile", "URI");
				APIUtils.updatePayloadBodyByTestData(bodyFile, "URI");
				try {
					payloadBody = new String(Files.readAllBytes(
							Paths.get(System.getProperty("user.dir") + "/src/test/java/payloads/" + "URI" + bodyFile)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				    logger.captureLog("Payload Body: "+payloadBody);
				    System.out.println("Request payload   "+payloadBody);
					ExtentLogger.jsonInfo(payloadBody,"Request Payload");
					response1 = APIServices.sendNReceive(uri1, method.toUpperCase(), "JSON", payloadBody,header,requestSpecification);
					response1.prettyPrint();
					APIUtils.generatePostmanCollection(method.toUpperCase(), payloadBody, header, uri1, testCaseName, resCode, ListenerClass.obj);
			}
			responseDetails=APIUtils.captureResponseDetails(response1);
			logger.captureLog("Response Headers: " + responseDetails.get("headers"));
			logger.captureLog("Response Body: " + responseDetails.get("response"));
			logger.captureLog("");
			APIUtils.validateStatusCode(resCode, response1);
			APIUtils.getResponseTime(response1,dataTable);	
			logger.flushLogger();
		    return responseDetails;
	}		
	

	/**
	 * This method is used to execute a orchestrated single api
	 *
	 * @author bghosh
	 * @return Map containing headers and response body
	 */
	public Map<String, Object> executeOrchestratedSingleAPI() {
		ExcelUtility dataTable=new ExcelUtility();
		String testCaseName = dataTable.getTestcasename();
		Logger logger = new Logger(testCaseName);
		Map<String, Object> responseDetails = new HashMap<String,Object>();
		Map<String, String> header =APIUtils.getRequestHeaders(dataTable);
		RequestSpecification requestSpecification = APIUtils.authorize(dataTable);
		String uri1 = dataTable.getData("General", "URI1").trim();
		String method = dataTable.getData("General", "SERVICE_METHOD").trim();
		String resCode = dataTable.getData("General", "RESPONSE_CODE").trim();
		//APIUtils.executePrequisite(dataTable);
		header = APIUtils.updatedHeader(header, "APIEnvFile");
		uri1 = APIUtils.processEnvVariable(uri1, "APIEnvFile");
		if (uri1.contains("((")) 
		{
			uri1 = APIUtils.processTestData(uri1);
		}
		uri1=APIUtils.updateOrchValue(uri1,dataTable);
		logger.captureLog("================Test Case:" + testCaseName+"=====================================");
		logger.captureLog("URI1:" + uri1);
		ExtentLogger.loginfo("URI1:" + uri1);
		logger.captureLog("Request Header:" + header);
		APIUtils.requestHeaderDetails(header);
		Response response1 = null;
		if (method.equalsIgnoreCase("GET")|| method.equalsIgnoreCase("DELETE")) 
		{
			response1 = APIServices.sendNReceive(uri1, method.toUpperCase(), header,requestSpecification);
			APIUtils.generatePostmanCollection(method.toUpperCase(), "", header, uri1, testCaseName, resCode, ListenerClass.obj);
		}
	    else if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH") ) 
	    {
			
	    	String bodyFile = dataTable.getData("General", "BODY").trim();
			// String payloadPath = System.getProperty("user.dir")+"/src/test/java/payloads/"+bodyFile;
			String payloadBody = "";
			APIUtils.updatePayloadWithEnvFile(bodyFile, "APIEnvFile", "URI");
			APIUtils.updatePayloadBodyByTestData(bodyFile, "URI");
			try {
				payloadBody = new String(Files.readAllBytes(
						Paths.get(System.getProperty("user.dir") + "/src/test/java/payloads/" + "URI" + bodyFile)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			    logger.captureLog("Payload Body: "+payloadBody);
				ExtentLogger.jsonInfo(payloadBody,"Request Payload");
				response1 = APIServices.sendNReceive(uri1, method.toUpperCase(), "JSON", payloadBody,header,requestSpecification);
			    APIUtils.generatePostmanCollection(method.toUpperCase(), payloadBody, header, uri1, testCaseName, resCode,ListenerClass.obj);
		}
		responseDetails=APIUtils.captureResponseDetails(response1);
		logger.captureLog("Response Headers: " + responseDetails.get("headers"));
		logger.captureLog("Response Body: " + responseDetails.get("response"));
		logger.captureLog("");
		APIUtils.validateStatusCode(resCode, response1);
		APIUtils.getResponseTime(response1,dataTable);	
		logger.flushLogger();
		return responseDetails;
		
	}

	/**
	 * This method checks the assertions in a response body
	 *
	 * @author bghosh
	 */
	public boolean assertAllValidation(Map<String, Object> responseDetails) {
		ExcelUtility dataTable=new ExcelUtility();
		List<String> attributePathList = new ArrayList<String>();
		List<String> assertionTypeList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		boolean status=true;
		int i = 1;
		do {
			String attributePath = dataTable.getData("Assertion", "AttributePath." + i).trim();
			String assertionType = dataTable.getData("Assertion", "AssertionType." + i).trim();
			String value = dataTable.getData("Assertion", "Value." + i).trim();
			if (attributePath.contentEquals("")) {
				break;
			}
			attributePathList.add(attributePath);
			assertionTypeList.add(assertionType);
			valueList.add(value);
			i++;
		} while (true);
		for (int j = 0; j < attributePathList.size(); j++) {
			String attributePath = attributePathList.get(j);
			String assertionType = assertionTypeList.get(j);
			String expectedValue = valueList.get(j);
			if (expectedValue.startsWith("$:")) {
				String propertyKey = expectedValue.split("\\$:")[1];
				expectedValue = APIUtils.readPropertyFile(propertyKey, "TestData.properties");
			}
			String response = (String) responseDetails.get("response");
			Headers headers = (Headers) responseDetails.get("headers");
			String actualValue = "";
			// String actualValue = APIUtils.getResponseValues(response, attributePath);
			if (!attributePath.equalsIgnoreCase("headers") && !assertionType.equalsIgnoreCase("Schema")) 
			{
				if (headers.getValue("Content-Type").contains("xml"))
				{
					actualValue = XMLUtils.readXml(response, attributePath);
				} 
				else 
				{
					actualValue = JSONUtils.readJson(response, attributePath);
				}
			}
			switch (assertionType) {
			case "NumberOnly":
				if (actualValue.matches("^[0-9]*$")) {
					ExtentLogger
					.pass("NumberOnly validation of "+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				} else {
					status=false;
					ExtentLogger
					.fail("NumberOnly validation of "+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				}
				break;
			case "Equals":
				if (actualValue.equals(expectedValue)) {
					ExtentLogger.pass("Equal validation of "+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				} else {
					status=false;
					ExtentLogger.fail("Equal validation of "+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				}
				break;
			case "isHeadersPresent":
				String[] expectedHeader = expectedValue.split(",");
				for (String head : expectedHeader) {
					if (!headers.hasHeaderWithName(head.trim())) {
						status=false;
						ExtentLogger.fail("Header validation" + head + "Not present");
					}
				}
				ExtentLogger.pass("Header validation" + expectedHeader + "Headers present");
				break;
			case "Contains":
				if (actualValue.contains(expectedValue)) {
					ExtentLogger
							.pass("Contains validation of "+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				} else {
					status=false;
					ExtentLogger
							.fail("Contains validation of"+attributePathList.get(j)+" || "+ "Expected: " + expectedValue + " Actual: " + actualValue);
				}
				break;
			case "Schema":
				String result = APIServices.validateSchema(response, attributePath);
				if (result.equalsIgnoreCase("Matched")) {
					ExtentLogger.pass("Schema validation||" + "Expected: Matched" + " Actual: Matched");
				} else {
					status=false;
					ExtentLogger.fail("Schema validation||" + "Expected: Matched" + " Actual: " + result);
				}
				break;
			default:
				status=false;
				ExtentLogger.fail("validation||" + "Assertion type: " + assertionType + " not present");
			}
		}
		
		return status;
	}

	/**
	 * This method compares API response header and body and compare in two
	 * different environments
	 *
	 * @author bghosh
	 *//*
		 * public void validateReponseHeaderAndBody() { Headers headers1 = (Headers)
		 * scriptHelper.getcontext().get("headers1"); Headers headers2 = (Headers)
		 * scriptHelper.getContext().get("headers2"); String ResponseBody1 =
		 * scriptHelper.getContext().get("ResponseBody1").toString(); String
		 * ResponseBody2 = scriptHelper.getContext().get("ResponseBody2").toString(); if
		 * (headers1.equals(headers2)) {
		 * ExtentLogger.pass("Response Header||"+"Expected: Matched"+" Actual: Matched"
		 * ); //report.updateTestLog("Response Header", "Matched", "Matched",
		 * Status.PASS); } else { report.updateTestLog("Response Header", headers1,
		 * headers2, Status.FAIL);
		 * ExtentLogger.fail("Response Header||"+"Expected: "+headers1+" Actual: "
		 * +headers2); } if (ResponseBody1.equals(ResponseBody2)) {
		 * ExtentLogger.pass("Response Body||"+"Expected: Matched"+" Actual: Matched");
		 * //report.updateTestLog("Response Body", "Matched", "Matched", Status.PASS); }
		 * else {
		 * ExtentLogger.pass("Response Body||"+"Expected: "+ResponseBody1+" Actual: "
		 * +ResponseBody2); //report.updateTestLog("Response Body", ResponseBody1,
		 * ResponseBody2, Status.FAIL); } }
		 */

}
