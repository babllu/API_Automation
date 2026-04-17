package utils;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import reporting.ExtentLogger;


public class ExcelUtility {
	
	  private  HashMap<String, HashMap<String, HashMap<String, String>> > datatable;
	  private  String testcasename;
	
	  
	  public ExcelUtility()  
	  {
		  setTestcasename();
		  setDatatable();
	  }
	  
	  /**
		* This method is used to retreive data from testdata sheet based 
		* on sheetname and columnname 
		*
		* @author bghosh
		* @param  sheetname
		* @param  columnname
		* @return String value of column content
	  */
	  public  String getData(String sheetname, String columnname) 
	  {		
		  return datatable.get(sheetname).get(testcasename).get(columnname);
	  }
	  
	  /**
		* This method is used to store content of testdata sheet in a hashmap
		*
		* @author arpan
	  */ 
	   private void setDatatable()   
	   {
		   this.datatable = readData();
	   }

	   /**
		* This method is used to set testcasename variable with calling class name 
		*
		* @author arpan
	  */
	   private void setTestcasename()
	   {
			try {
				this.testcasename = GetCallerClass.getCallerClass().getSimpleName();
			} catch (ClassNotFoundException e) {
				ExtentLogger.loginfo("Test Case Class not present");
			}
	   }

	   /**
		* This method is used to return a hashmap of testdata sheet contents
		*
		* @author arpan
		* @return hashmap 
	  */
	   public HashMap<String, HashMap<String, HashMap<String, String>>> getDataTable() {
			return datatable;
		}

	   /**
		* This method is used to fetch testcasename variable
		*
		* @author arpan
		* @return String value of testcasename variable
	  */
	public String getTestcasename() {
		return testcasename;
	}

  /**
	* This method is used to read testdata sheet and stores all contents
	* in a hashmap
	*
	* @author arpan
	* @return hashmap 
    */
	public static HashMap<String, HashMap<String, HashMap<String, String>> > readData() {
	 String[] parts = null;
			try {
				  // parts = GetCallerClass.getCallerClass().getPackageName().split(Pattern.quote("."));
				   parts = GetCallerClass.getCallerClass().getPackage().getName().split(Pattern.quote("."));
				   for (String i : parts) {
					   System.out.println(i);
				   }
			    } 
			catch (ClassNotFoundException e1) 
			{
				ExtentLogger.loginfo("Issue in getting datasheet name");
			}
		   String filePathName = parts[1];
		   FileInputStream file;
		   HashMap<String, HashMap<String, HashMap<String, String>> > datatable = null ;
			try 
			{
				System.out.println("src/test/resources/DataSheets/"+filePathName+".xlsx");
				file = new FileInputStream(new File("src/test/resources/DataSheets/"+filePathName+".xlsx"));
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				Iterator<Sheet> sheetIterator = workbook.iterator();
				datatable = new HashMap<>();
				 
				while (sheetIterator.hasNext())
				{
				    Sheet sheet = sheetIterator.next();
				    String sheetName = sheet.getSheetName();
				    HashMap<String, HashMap<String, String>> sheetData = new HashMap<>();
				    int rowcount = sheet.getPhysicalNumberOfRows();
				    int columncount = sheet.getRow(0).getPhysicalNumberOfCells();
				    for(int i=1;i<rowcount;i++)
				    {
				    	String TC_ID = sheet.getRow(i).getCell(0).getStringCellValue();
				    	HashMap<String, String> data = new HashMap<>();
				    	for(int j = 1;j<columncount;j++)
				    	{
				    		
				    		String fieldName = sheet.getRow(0).getCell(j).getStringCellValue();
				    		String fieldValue = "";
				    		Cell cell = sheet.getRow(i).getCell(j);
				    		if(cell.getCellType()==CellType.STRING)
				    		{
				    			fieldValue = cell.getStringCellValue().trim();
				    		}
				    		else if(cell.getCellType()==CellType.NUMERIC)
				    		{
				    			 if (DateUtil.isCellDateFormatted(cell))
			                      {
			                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			                            fieldValue=dateFormat.format(cell.getDateCellValue()).trim();
			                       }
			                      else
			                      {
			                          int temp = (int) cell.getNumericCellValue();
			                          fieldValue=String.valueOf(temp).trim();
			                      }
				    		}
				    		else if(cell.getCellType()==CellType.BOOLEAN)
				    		{
				    			fieldValue = String.valueOf(cell.getBooleanCellValue()).trim();
				    		}
				    		else if(cell.getCellType()==CellType.BLANK)
				    		{
				    			fieldValue = "";
				    		}
				    		data.put(fieldName, fieldValue) ;
				    	}
				    	sheetData.put(TC_ID, data);
				    }
				    
				    datatable.put(sheetName, sheetData);
				}
				
				workbook.close();
				file.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return datatable;
	   }
	 
	

	
}
