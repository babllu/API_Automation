package base;

import java.io.IOException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import reporting.ExtentReport;

public class PSM {
	
	@BeforeSuite
	public void startReport() throws IOException {
		ExtentReport.initReports();
	}

	@BeforeClass
	public void startTest() {
		ExtentReport.createTest("Sample testcase");
	}
	
	@AfterSuite
	public void tearDown() throws IOException {
		ExtentReport.flushReport();
	}
	
}
