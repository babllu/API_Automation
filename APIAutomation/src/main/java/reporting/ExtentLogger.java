package reporting;

import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public final class ExtentLogger {

	private ExtentLogger()
	{
		
	}
	
	public static void pass(String message)
	{
		ExtentManager.getExtentTest().pass(message);
	}
	
	public static void fail(String message)
	{
		ExtentManager.getExtentTest().fail(message);
	}
	
	public static void skip(String message)
	{
		ExtentManager.getExtentTest().skip(message);
	}
	
	public static void loginfo(String message)
	{
		ExtentManager.getExtentTest().info(message);
	}
	
	public static void jsonInfo(String message, String nodeName)
	{
		ExtentManager.getExtentTest().createNode(nodeName).info(MarkupHelper.createCodeBlock(message, CodeLanguage.JSON));
	}
	
	public static void xmlInfo(String message, String nodeName)
	{
		ExtentManager.getExtentTest().createNode(nodeName).info(MarkupHelper.createCodeBlock(message, CodeLanguage.XML));
	}
	
	public static void info(String message, String nodeName)
	{
		ExtentManager.getExtentTest().createNode(nodeName).info(nodeName);
	}
	
	
	public static void headersInfo(String nodeName, String[][] headerDetail)
	{
		Markup m = MarkupHelper.createTable(headerDetail);
		ExtentManager.getExtentTest().createNode(nodeName).info(m);
	}
	
	
}
