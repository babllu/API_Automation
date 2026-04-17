package listeners;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import logger.Logger;
import reporting.ExtentLogger;
import reporting.ExtentReport;
import utils.APIUtils;

public class ListenerClass implements ITestListener,ISuiteListener{

	public final static String propertiesFileName = "GlobalSettings.properties";
	public static JSONObject obj;
	
	@Override
	public void onStart(ISuite suite) {
		try {
			ExtentReport.initReports();
			String createCollection =APIUtils.readPropertyFile("CreatePostmanCollection",propertiesFileName);
			if(createCollection.equalsIgnoreCase("Yes"))
			{
				obj=APIUtils.returnJsonSchema();
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		try {
			ExtentReport.flushReport();
			Logger.generateLogFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentReport.createTest(result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentLogger.pass(result.getMethod().getMethodName()+" is passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentLogger.fail(result.getMethod().getMethodName()+" is failed");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		ExtentLogger.skip(result.getMethod().getMethodName()+" is skipped");
	}

	
}
