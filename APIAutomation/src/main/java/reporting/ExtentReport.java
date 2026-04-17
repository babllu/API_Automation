package reporting;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import utils.APIUtils;

public final class ExtentReport {

	private static ExtentReports extent;
	
	private ExtentReport()
	{
		
	}
	
	/**
	 * This method is used to intialize the Extent Report and set up 
	 * the reporting configuration
	 *
	 * @author arpan
	 * @throws IOException 
	 */
	public static void initReports() throws IOException
	{
		if(Objects.isNull(extent)) {
		extent = new ExtentReports();
		ExtentSparkReporter spark = new ExtentSparkReporter(APIUtils.getReportPath()+"index.html");
		//spark.loadXMLConfig(new File("extentconfig.xml"));
		extent.attachReporter(spark);
		extent.setSystemInfo("OS : ", System.getProperty("os.name"));
		extent.setSystemInfo("User Name : ", System.getProperty("user.name"));
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		extent.setSystemInfo("IP Address: " , inetAddress.getHostAddress());
		extent.setSystemInfo("Host Name: " , inetAddress.getHostName());
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle("Demo API Report");
		spark.config().setReportName("API Automation Report");
		spark.config().setTimelineEnabled(false);
		spark.config().enableOfflineMode(true);
	  }
	}
	
	/**
	 * This method is used to flush the Extent Report 
	 *
	 * @author arpan
	 */
	public static void flushReport() throws IOException
	{
		if(Objects.nonNull(extent))
		{
			extent.flush();
		}
		
		Desktop.getDesktop().browse(new File (APIUtils.getReportPath()+"index.html").toURI());
	}
	
	
	/**
	 * This method is used to create a test for Extent Report 
	 *
	 * @author arpan
	 */
	public static void createTest(String testcasename)
	{
		ExtentTest test=extent.createTest(testcasename);
		ExtentManager.setExtentTest(test);
	}
	
}
