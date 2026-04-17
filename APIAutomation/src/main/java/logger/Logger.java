package logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

public class Logger {

    private PrintWriter writer;
	
	public Logger(String testCaseName)
	{
		try {
			createTestLog(testCaseName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public  static void generateLogFile() throws IOException  
	{
		File file = new File(System.getProperty("user.dir")+"/target/ExtentReport/temp");
	    File[] fileList = file.listFiles();
		File f = new File(System.getProperty("user.dir")+"/target/ExtentReport/testlog.txt");
		if(!f.getParentFile().exists()) {
	          f.getParentFile().mkdirs();
	      } 
		if(!(f.exists())) 
		{ 
			f.createNewFile();
		}
		else 
		{
			f.delete();
			f.createNewFile();
		}
		PrintWriter writer = new PrintWriter(f);
		writer.println("------------------------------------------------------------------------------------------------------");
		writer.println("               TEST Execution Log");
		writer.println("------------------------------------------------------------------------------------------------------");
		for(int i =0;i<fileList.length;i++)
		{
			    BufferedReader br = new BufferedReader(new FileReader(fileList[i].getAbsolutePath()));
	            String line = br.readLine();
	            while (line != null) 
	            {
	            	writer.println(line);
	                line = br.readLine();
		         }
	            br.close();
		}
		writer.flush();
		writer.close();
		FileUtils.deleteDirectory(file);
	
	}
	
	private void createTestLog(String testcaseName) throws IOException
	{
		File f = new File(System.getProperty("user.dir")+"/target/ExtentReport/temp/"+testcaseName+".txt");
		if(!f.getParentFile().exists()) {
	          f.getParentFile().mkdirs();
	      } 
		if(!(f.exists())) 
		{ 
			f.createNewFile();
		}
		else 
		{
			f.delete();
			f.createNewFile();
		}
		writer = new PrintWriter(f);
	}
	
	public void flushLogger() 
	{
			writer.flush();
			writer.close();
	}
	
	public void captureLog(String value)
	{
		writer.println(value);
	}

}
