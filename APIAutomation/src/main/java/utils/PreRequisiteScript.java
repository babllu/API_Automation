package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PreRequisiteScript {

	
	/**
	 *  This method returns date value of input value
	 *  
	 *@author bghosh
	 *@param d
	 *@return newDate
	 */	
	public String futureDate(String d) {
		String oldDate= APIUtils.readPropertyFile("CurrentDate", "TestData.properties");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try
		{
			c.setTime(sdf.parse(oldDate));
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		int i = Integer.parseInt(d);
		c.add(Calendar.DAY_OF_MONTH, i);
		String newDate = sdf.format(c.getTime());
		return newDate;
	}
}
