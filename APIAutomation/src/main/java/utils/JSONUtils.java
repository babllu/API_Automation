package utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JSONUtils {

	
	/**
	 *  This method goes to the jsonPath and reads the value of the json file
	 *
	 *@author bghosh
	 *@param jsonFile
	 *@param jsonPath
	 *@return
	 */
	public static String readJson(String jsonFile, String jsonPath)
	{
		String value = JsonPath.read(jsonFile, jsonPath).toString();
		return value;
	}
	
	/**
	*   This method updates the value of the key present  in  'jsonPath' to  'newValue'
	*
	*   @author mtilotiya
	*   @param response
	*   @param jsonpath
	*   @param newValue
	*   @return
	*/
	public  static String updateJsonValue(String response,  String jsonpath,  String newValue) 
	{ 
		try
		{
			String updatedjson  =  JsonPath.parse(response).set(jsonpath,  newValue).jsonString(); 
			return updatedjson;
	    }  
		catch  (PathNotFoundException  pe) 
		{ 
			return response;
	    }
	}	
	
	/**
	*   This method deletes the value of the key present  in  'jsonPath' to  'newValue'
	*
	*   @author mtilotiya
	*   @param response
	*   @param jsonpath
	*   @param newValue
	*   @return
	*/
	public  static String deleteJsonValue(String response,  String jsonpath) 
	{ 
		try
		{
			String updatedjson  =  JsonPath.parse(response).delete(jsonpath).jsonString(); 
			return updatedjson;
	    }  
		catch  (PathNotFoundException  pe) 
		{ 
			return response;
	    }
	}
	
	
}
