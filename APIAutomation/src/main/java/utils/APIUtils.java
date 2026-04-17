package utils; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import reporting.ExtentLogger;

public class APIUtils {

	public final static String propertiesFileName = "GlobalSettings.properties";
	
	
	
	/**This method returns request based on authorization type.
	 * @param dataTable
	 * @return
	 */
	public static RequestSpecification authorize(ExcelUtility dataTable)
	{
		String authType = dataTable.getData("General", "AUTHORIZATION_TYPE").trim();
		RequestSpecification request=RestAssured.given();
		if(authType.equalsIgnoreCase("Basic"))
		{
			String username = dataTable.getData("Authorization", "USERID").trim();
			String password = dataTable.getData("Authorization", "PASSWORD").trim();
			request = request.auth().basic(username, password);
		}
		else if(authType.equalsIgnoreCase("Digest"))
		{
			String username = dataTable.getData("Authorization", "USERID").trim();
			String password = dataTable.getData("Authorization", "PASSWORD").trim();
			request = request.auth().digest(username, password);
		}
		
		return request;
	}
	
	/** This method updates the orchestration value.
	 * @param uri1
	 * @param dataTable
	 * @return
	 */
	public static String  updateOrchValue(String uri1,ExcelUtility dataTable)
	{
		int k = 1;
		do {
			String updateTo = dataTable.getData("Orchestration", "updateTo." + k);
			String updateFrom = dataTable.getData("Orchestration", "updateFrom." + k);
			if (updateTo.contentEquals("")) {
				break;
			}
			// String newValue = APIUtiles.getData(updateFrom)
			String[] updateFromArray = updateFrom.split("::");
			String updateFromData = APIUtils.getDataFromResponseFile(updateFromArray[0], updateFromArray[1]);
			String[] updateToArray = updateTo.split("::");
			if (updateToArray[0].toLowerCase().startsWith("url")) {
				uri1 = uri1.replace("[[" + updateToArray[1] + "]]", updateFromData).trim();
			}
			//orchvalue.put(updateTo, updateFrom);
			k++;
		} while (true);
		return uri1;
	}
	
	/**This method adds response time to report
	 * @param response1
	 * @param dataTable
	 */
	public static void getResponseTime(Response response1,ExcelUtility dataTable)
	{
		String testCaseName = dataTable.getTestcasename();
		String res1_body = response1.body().asString();
		APIUtils.saveResponse(testCaseName + "_URI1", res1_body);
		String responseTime1 = String.valueOf((float) response1.getTime() / 1000);
		ExtentLogger.loginfo("Response Time:" + responseTime1 + " Seconds");
		
	}
	
	/**This method validates status codes.
	 * @param resCode
	 * @param response1
	 */
	public static void validateStatusCode(String resCode,Response response1)
	{
		if (!resCode.isEmpty())
		{
			if (resCode.contains(String.valueOf(response1.getStatusCode())))
				ExtentLogger.pass("Expected Status Code: " + resCode + " Actual status code:"
						+ response1.getStatusCode());
			else {
				ExtentLogger.fail("Expected Status Code: " + resCode + " Actual status code:"
						+ response1.getStatusCode());
			    Assert.fail();
			}
		}
	}
	
	 /** This method returns the response details
	 * @param response1
	 * @return
	 */
	public static Map<String, Object> captureResponseDetails(Response response1)
	{   
		Map<String, Object> responseDetails = new HashMap<String,Object>();
		String res1_body = response1.body().asString();
		Headers respHeaders = response1.headers();
		APIUtils.responseHeaderDetails(response1);
		if(respHeaders.get("Content-Type")==null)
		{
			ExtentLogger.info(res1_body, "Response Body");
		}
		else if(respHeaders.get("Content-Type").toString().contains("json")) 
		{
			ExtentLogger.jsonInfo(res1_body, "Response Body");
		}
		else if(respHeaders.get("Content-Type").toString().contains("xml"))
		{
			ExtentLogger.xmlInfo(res1_body, "Response Body");
		}
		responseDetails.put("response", res1_body);
		responseDetails.put("headers", respHeaders);
		return responseDetails;
	}
	
	/**This method executes prerequisite steps from dataTable
	 * @param dataTable
	 */
	public static void executePrequisite(ExcelUtility dataTable)
	{
		String preReqMethod = dataTable.getData("General", "PreRequisite_Script").trim();
		boolean flag = true;
		if (!preReqMethod.isEmpty()) 
		{
			flag = APIUtils.executePreReqMethod(preReqMethod, "APIEnvFile");
			if (!flag)
				ExtentLogger.fail("Method not found in Prerequisite script");
		}
	}
	
	/**This method gets all request headers from dataTable.
	 * @param dataTable
	 * @return
	 */
	public static Map<String, String> getRequestHeaders(ExcelUtility dataTable)
	{
		Map<String, String> header = new HashMap<String, String>();
		int i = 1;
		do {
			String key = dataTable.getData("Header", "Key" + i);
			String value = dataTable.getData("Header", "Value" + i);

			// replace value with properties file
			if (value.startsWith("$:")) {
				String propertyKey = value.split("\\$:")[1];
				value = APIUtils.readPropertyFile(propertyKey, "TestData.properties");
			} else if (value.startsWith("Prescript:")) {
				value = APIUtils.preScript(value, "Prescript:");
			}
			if (key.contentEquals("")) {
				break;
			}
			header.put(key, value);
			i++;
		} while (true);
		header.replace("X-ConversationId", "PM-" + UUID.randomUUID());
		return header;
	}
	
	/** This method updates payload as per testdata
	 * @param bodyFile
	 * @param newFileName
	 */
	public static void updatePayloadBodyByTestData(String bodyFile, String newFileName) {
		try {
			File f1 = new File(System.getProperty("user.dir") + "/src/test/java/payloads/" + "URI" + bodyFile);
			BufferedReader br = new BufferedReader(new FileReader(f1));
			String s = br.readLine();
			File dir = new File(System.getProperty("user.dir") + "/src/test/java/payloads/");
			dir.mkdirs();
			File file1 = new File(dir, newFileName + bodyFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
			while (s != null) {
				s = processTestData(s);
				writer.write(s);
				writer.newLine();
				s = br.readLine();
			}
			writer.flush();
			writer.close();
			br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** This method processes test data from datasheet
	 * @param data
	 * @return
	 */
	public static String processTestData(String data) {
		ExcelUtility dataTable = new ExcelUtility();
		List<String> variable = new ArrayList<String>();
		String[] arr = data.split("\\(\\(");
		for (String s1 : arr) {
			if (s1.contains("))")) {
				String toReplace = s1.split("\\)\\)")[0];
				variable.add(toReplace);
			}
		}
		for (String vari : variable) {
			String value = dataTable.getData("TestData", vari);
			if (value.startsWith("$:")) {
				String propertyKey = value.split("\\$:")[1];
				value = APIUtils.readPropertyFile(propertyKey, "TestData.properties");
			}
			if (value.startsWith("PreScript:")) {
				value = APIUtils.preScript(value, "PreScript:");
			}
			data = data.replace("((" + vari + "))", value);
		}
		return data;
	}

	
	
	/** This method updates response header details in Report.
	 * @param response
	 */
	public static void responseHeaderDetails(Response response)
	{
		Headers respHeaders = response.headers();
		List<Header> heads = respHeaders.asList();
		String [][] val = new String [heads.size()][2] ;
		for(int k=0;k<heads.size();k++)
		{
			val[k][0]=heads.get(k).getName();
			val[k][1]=heads.get(k).getValue();
		}
		ExtentLogger.headersInfo("Response Headers", val);
	}
	
	/** This method updates request header details in Report.
	 * @param headers
	 */
	public static void requestHeaderDetails(Map<String, String> headers)
	{
		String [][] val = new String [headers.size()][2] ;
		int k=0;
		for(Map.Entry<String,String> entry : headers.entrySet())
		{
			val[k][0] = entry.getKey().toString();
			val[k][1] = entry.getValue().toString();
	        k++;
	    }
		ExtentLogger.headersInfo("Request Headers", val);
	}


	/**
	 *    This method  replaces  the  value of the  variable  being passed  in  "data"  with
	 *    the value "filevariable"
	 *
	 *    @author  bgbosh
	 *    @param data
	 *    @param fileVariable
	 *    @return
	 */ 

	public static String processEnvVariable(String data, String fileVariable) 
	{ 
		List<String> variable = new ArrayList<String>();
		String[] arr = data.split("\\{\\{");
		for (String s1:arr)
		{
			if (s1.contains("}}")) 
			{
				variable.add(s1.split("\\}\\}")[0]);
			}
		}
		for (String vari:variable) 
		{
			String value = getEnvProperties(vari,fileVariable); 
			data = data.replace("{{" + vari + "}}",  value);
	    }
		return  data;
	}
	
	
	/**
	 *    This method  returns  the  value  of the  key  being  passed 
	 *
	 *    @author mtilotiya,
	 *    @param  key
	 *    @param  variablename
	 *    @return 
	 */
	
	  public static String getEnvProperties(String key, String variablename) {
	  //final Properties PROPERTIES = getInstance(); 
	  //String envFile =PROPERTIES.getProperty(variablename);
	  String envFile = readPropertyFile(variablename, propertiesFileName) ;
	  String value ="Key not present In json"; 
	  String filePath = System.getProperty("user.dir") +"/src/test/resources/Environment/" + envFile;
	  try { 
		  String resBody = new String(Files.readAllBytes(Paths.get(filePath))); 
		  value =JsonPath.read(resBody, "values[?(@.key ==  '" + key +
	  "' && @.enabled == true)].value").toString(); 
	  if(value.contains("[")) 
		value = value.replaceAll("\\[\"", "").replaceAll("\"\\]", ""); 
	  } 
	  catch (IOException e) 
	  { 
		  e.printStackTrace(); 
	  } 
	  return value;
	  
	  }
	 

	/**
	 *    This method is for saving the response in the report path
	 *   @author bghosh
	 *   @param fileName  
	 *   @param response 
	 */
	 public static void saveResponse(String fileName, String response)
	{ 
		//final Properties PROPERTIES = getInstance(); 
		//String responsePath = PROPERTIES.getProperty("ResponsePath");
		String responsePath = readPropertyFile("ResponsePath", propertiesFileName);
		File dir = new File(System.getProperty("user.dir") + responsePath);
		dir.mkdirs(); 
		File file = new File(dir, fileName + ".txt");
	try {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(response); 
		writer.close();
	} catch (IOException e) {
		e.printStackTrace();
	}}

	/** 
	 *    This method  is  for  validating if the value being passed  is  an  integer  or  not
	 * 
	 *   @author bghosh,
	 *   @param value
	 *   @return
	 */ 
	public  static  boolean validateNumberOnly(String value)
	{ 
		try  {
			int  x  =  Integer.parseInt(value); 
			return true;
		}  catch  (Exception  ex)  { 
			return false;
		} }


	/**
	 *    This method  is  for  getting the value of a  particular  node in the response
	 *
	 *   @author bghosh
	 *   @param response
	 *   @param node
	 *   @return ]
	 */
	public  static  String getResponseValues(String response,  String node)  { 
		String node1  =  node.replaceAll("/",   "  ");
		String keys[]  =  node1.split("  "); 
		JSONParser  parser  =  new JSONParser();
		JSONObject  object  =  null; 
		try  {
			object  =  (JSONObject)  parser.parse(response); 
		}  catch  (ParseException  e)  {
			//  TODO Auto-generated  catch  block
			e.printStackTrace();
		}
		for  (int  i  =  0;  i  < keys.length;  i++)  { if  (i  ==  (keys.length)   -  1)
			node = object.get(keys[i]).toString();
		else {
			object = (JSONObject) object.get(keys[i]);
		}
		}
		return node;
	}


	

	/**
	 * This method is for reading the response file
	 *
	 * @author bghosh
	 * @param fileName
	 * @param jsonPath
	 * @return
	 */
	public static String getDataFromResponseFile(String fileName, String jsonPath) {
		//String filePath = System.getProperty("user.dir") + "/target/CRAFTReports/HTML Results/responses/" + fileName
		 String filePath = System.getProperty("user.dir") + "/target/ExtentReport/responses/" + fileName
				+ "_URI1.txt";
		String value = "";
		try   {
			String resBody = new String(Files.readAllBytes(Paths.get(filePath)));
			value =  JSONUtils.readJson(resBody,   jsonPath); } 
		catch (IOException e)   {
			//  TODO Auto-generated catch  block 
			e.printStackTrace();
		}
		return value;
	}	

	/**This method  is for  updating the "filevariable"  in header with null  value
	 *   @author bghosh 
	 *   @param header
	 *   @param fileVariable
	 *   @return 
	 */
	public  static Map<String,  String> updatedHeader(Map<String,  String> header, String fileVariable )   { 
		String keyValue =  null; 
		for  (Map.Entry<String,  String>  entry   :   header.entrySet())   {
			keyValue = processEnvVariable(entry.getValue(),fileVariable);
			header.replace(entry.getKey(),  keyValue);
		}
		return header;
	}
	
	/**
	*   This method  updates the value of the key  present  in   'pathList'  to blank  in
	*   the response body
	*   
	*   @author mtilotiya
	*   @param resl_body
	*   @param pathList
	*   @return
	*/
	public  static  String updateResponseBody(String res1_body,  List<String>  pathList) {
	 for  (String path   :  pathList) 
		{
		    //  res1_body  =  updateJsonValue(res1_body,path,"");
			if   (path.contains("("))
			{
				res1_body=replaceString(path,res1_body);
		    }
			else
			{
				res1_body = JSONUtils.deleteJsonValue(res1_body,path);
			}
		}
			return res1_body;
}
	
	/** This method replaces string value.
	 * @param path
	 * @param res1_body
	 * @return
	 */
	public static String replaceString(String path, String res1_body) 
	{
		String s1 = path.split("\\(")[1].split("\\)")[0].split(",")[0].trim();
	    String s2  =  path.split("\\(")[1].split("\\)")[0].split(",")[0].trim(); 
	    res1_body = res1_body.replace(s1,s2); 
	    return res1_body;
	}
	
	/** This method removes the values of all headers present in the response
	*   @author mtilotiya
	*   @param headers1
	*   @param headermap
	*   @return
	*/
	public static HashMap<String, String> updateResponseHeader(Headers headers1, Map<String,  String> headerMap)
	{
		HashMap<String,  String> headerMap1 = new HashMap<>();
		for (Header h  : headers1)
			headerMap1.put(h.getName(), h.getValue());
			headerMap1.keySet().removeAll(headerMap.keySet());
			return headerMap1; 
	}
	
	
	/** In This method dynamic payloads is handled and replaces with its corresponding
	 * value from the env file
	*   @author mtilotiya
	*   @param bodyFile
	*   @param envVariable
	*   @param newFileName
	*   @return
	*/
	public static void updatePayloadWithEnvFile(String bodyFile, String envVariable, String newFileName)
	{
		try {
			File f1= new File(System.getProperty("user.dir")+"/src/test/java/payloads/"+ bodyFile);
			BufferedReader br = new BufferedReader(new FileReader(f1));
			String s = br.readLine();
			File dir = new File(System.getProperty("user.dir")+"/src/test/java/payloads/");
			dir.mkdirs();
			File file1 = new File(dir, newFileName+bodyFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
			while(s!=null)
			{
				s = processEnvVariable(s,envVariable);
				writer.write(s);
				writer.newLine();
				s=br.readLine();
			}
			writer.flush();
			writer.close();
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/** This method updates environment JSON
	 * @param variablename
	 * @param key
	 * @param value
	 */
	public static void updateEnvJSON(String variablename,String key, String value)
	{ 
		//final Properties PROPERTIES = getInstance(); 
		//String envFile = PROPERTIES.getProperty(variablename);
		String envFile = readPropertyFile(variablename,propertiesFileName);
		String filePath = System.getProperty("user.dir") + "/src/test/resources/Environment/" +envFile;
		String resBody="";
		try {
			resBody = new String(Files.readAllBytes(Paths.get(filePath)));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		String updatedJSON="";
		if((JsonPath.read(resBody, "values[?(@.key== '"+ key + "')]".toString()).equals("[]")))
		{
			org.json.JSONObject obj = new org.json.JSONObject(resBody);
			org.json.JSONArray array = obj.getJSONArray("values");
			JSONObject newValue =  new JSONObject();
			newValue.put("key:", key);
			newValue.put("value:", value);
			newValue.put("enabled:", true);
			array.put(newValue);
			updatedJSON = obj.toString();
			//FileWriter file = new FileWriter(filePath);
			//file.write(obj.toString());
		}
		else
		{
			updatedJSON = JSONUtils.updateJsonValue(resBody,"values[?@.key == '"+key+"' && @.enabled == true)].value", value);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = JsonParser.parseString(updatedJSON);
			String beautifiedJson = gson.toJson(je);
			//file.write(beautifiedJson);
			//value=JsonPath.read(resBody, "values[?@.key == '"+key+"' && @.enabled == true)].value".toString());
			FileWriter file = null;
			try {
				file = new FileWriter(filePath);
				file.write(beautifiedJson);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					file.flush();
					file.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**  This method executes Pre-requisite script, if the value for pre-requisite is
	*   set as Yes in excel
	*
	*  @author mtilotiya
	*  @param envVariable
	*  @param newFileName
	*  @return
	*/   
	public static boolean executePreReqMethod(String preReqMethod, String envirorment){
	 try{
		 Method methodname =   PreRequisiteScript.class.getMethod(preReqMethod, String.class);
		 PreRequisiteScript testPreReuqist = new PreRequisiteScript();
	     methodname.invoke(testPreReuqist, envirorment);
	  } 
	 catch (NoSuchMethodException e) {
		 return false; 
	  } 
	 catch (Exception e) {
		 e.printStackTrace();
	 }
	return true;
	}
	
	
	public static JSONObject returnJsonSchema() throws FileNotFoundException, IOException, ParseException
	{
		JSONParser jsonparser = new JSONParser();
		JSONObject obj = null;
		obj = (JSONObject) jsonparser.parse(new FileReader(
				System.getProperty("user.dir")+"/src/test/resources/CCS-Sample.postman_collection.json"));
		return obj;
		
	}
	
	
	/**This method generates postman collection.
	 * @param servicesMethod
	 * @param payloadBody
	 * @param headersArg
	 * @param uri
	 * @param name
	 * @param expStatusCode
	 */
	public static void generatePostmanCollection(String servicesMethod, String payloadBody, Map<String,String> headersArg,
			String uri , String name, String expStatusCode,JSONObject obj )
	{
		
		System.out.println(servicesMethod);
		System.out.println(payloadBody);
		System.out.println(headersArg);
		System.out.println(uri);
		System.out.println(name);
		System.out.println(expStatusCode);
		//final Properties PROPERTIES = getInstance();
		//String createCollection = PROPERTIES.getProperty("CreatePostmanCollection");
		
		/*
		 * String createCollection =
		 * readPropertyFile("CreatePostmanCollection",propertiesFileName);
		 * if(createCollection.equalsIgnoreCase("No")) return; JSONParser jsonparser =
		 * new JSONParser(); JSONObject obj = null;
		 */
		try
		{
			/*
			 * File postmanCol = new File( System.getProperty("user.dir")+
			 * "/src/test/resources/Created_postman_collection.json");
			 * if(!postmanCol.exists()) { obj = (JSONObject) jsonparser.parse(new
			 * FileReader( System.getProperty("user.dir")+
			 * "/src/test/resources/CCS-Sample.postman_collection.json")); } else { obj =
			 * (JSONObject) jsonparser.parse(new FileReader( System.getProperty("user.dir")+
			 * "/src/test/resources/Created_postman_collection.json")); }
			 */
			JSONObject root = (JSONObject) obj;
			//System.out.println(root);
			
			List<JSONObject> header = new ArrayList<JSONObject>();
			int i=0;
			for(Map.Entry<String,String> head : headersArg.entrySet()) {
				JSONObject newHeader = new JSONObject();
				newHeader.put("key", head.getKey());
				newHeader.put("value", head.getValue());
				header.add(newHeader);
			}
			
			JSONArray headers = new JSONArray();
			for(JSONObject heade: header) {
				headers.add(heade);
			}
			
			JSONObject body = new JSONObject();
			if(payloadBody.length()>0) {
				body.put("mode", "raw");
				body.put("raw", payloadBody);
			}
			
			String protocol = uri.split(":")[0];
			String[] hosturl = uri.split("//")[1].split("/");
			JSONArray host = new JSONArray();
			
			host.add(hosturl[0]);
			JSONArray path = new JSONArray();
			for(int j=1;j<hosturl.length; j++)
			{
				path.add(hosturl[j]);
			}
			JSONObject url = new JSONObject();
			url.put("raw", uri);
			url.put("protocol", protocol);
			url.put("host", host);
			url.put("path", path);
			
			JSONObject request = new JSONObject();
			request.put("method", servicesMethod);
			request.put("header", headers);
			request.put("body", body);
			request.put("url", url);
			
			
			JSONArray exec = new JSONArray();
			exec.add("\"tests[\\\"Status code is "+ expStatusCode + "\\\"] = responseCode.code == "+ expStatusCode
					+";\"");
			
			JSONObject script = new JSONObject();
			script.put("exec", exec);
			script.put("type", "text/javascript");
			
			JSONObject checkResCode = new JSONObject();
			checkResCode.put("listen", "test");
			checkResCode.put("script", script);
			
			JSONArray event = new JSONArray();
			event.add(checkResCode);
			
			JSONObject apidetails = new JSONObject();
			apidetails.put("name", name);
			apidetails.put("request", request);
			apidetails.put("event", event);
			
			ArrayList<JSONObject> items = (ArrayList<JSONObject>) obj.get("item");
			//System.out.println(items);
			items.add(apidetails);
			
			obj.put("items", items);
			//System.err.println(obj);
			
			String collection = obj.toJSONString();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = JsonParser.parseString(collection);
			String beautifiedjson = gson.toJson(je);
			
			FileWriter fileWriter = new FileWriter(
					System.getProperty("user.dir")+"/src/test/resources/Created_postman_collection.json");
			fileWriter.write(beautifiedjson);
			fileWriter.flush();
			fileWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**This method reads property file.
	 * @param key
	 * @param fileName
	 * @return
	 */
	public static String readPropertyFile(String key, String fileName)
	{
		String value="";
		try {
			FileReader reader =  new FileReader(System.getProperty("user.dir")+"/src/test/resources/"+fileName);
			Properties pro = new Properties();
			pro.load(reader);
			value = pro.getProperty(key);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
	//check this
	/** This method returns value for preScript
	 * @param value
	 * @param script
	 * @return
	 */
	public  static String preScript(String value,String script)
	{ 
		String methodName = value.split(script)[1].split(",")[0]; 
		int index =value.split(script)[1].indexOf(','); 
		String arg =  value.split(script)[1].substring(index + 1); 
		String value1 = null;
		PreRequisiteScript p = new PreRequisiteScript();
		Method[] methods = p.getClass().getDeclaredMethods();
		for (Method method   : methods) {
			if  (method.getName().contentEquals(methodName))  { 
				try {
					    Object o=null; 
					    if(!arg.isEmpty())
						 o=method.invoke(p,arg);
					    else
						 o=method.invoke(p); 
					     value1 = (String)o;
					     return value1; 
					     }
				catch (IllegalAccessException   |   IllegalArgumentException   |   InvocationTargetException e)
				{
						e.printStackTrace(); 
				}
			}
	    }
		return value;
	}
	
	
	
	
	
	
	/** This method updates payload with data
	 * @param fileName
	 * @param newFileName
	 */
	public  static  void updatePayloadUithData(String fileName,String newFileName)   {
	try {
		File f1  = new File(System.getProperty("user.dir")  + "/src/test/java/payloads/" + fileName);
		BufferedReader  br  = new BufferedReader(new FileReader(f1));
		String s = br.readLine();
		File dir = new File(System.getProperty("user.dir") + "/src/test/java/payloads/");
		dir.mkdirs();
		File file1 =  new File(dir,  newFileName + fileName);
		BufferedWriter  writer  =  new BufferedWriter(new FileWriter(file1));
		while  (s!=null)  {
			s = processTestDataWithProperties(s);
			writer.write(s);
			writer.newLine();
			s  =  br.readLine();
		}
		writer.flush();
		writer.close(); 
		br.close(); 
	   } 
	  catch  (IOException  e)  
	  {
		e. printStackTrace();
	  } 
	}
	
	/** This method processes testdata with properties. 
	 * @param line
	 * @return
	 */
	public  static  String processTestDataWithProperties(String line)
	{
		if(line.contains("("))
	{
			String valuel=line.split("\\(")[1].split("\\)")[0];
			String value="";
		if(valuel.startsWith("$"))  {
			String propertyKey=valuel.split("\\$:")[1];
			value=readPropertyFile(propertyKey,   "TestData.properties");
			line = line.replace("("+valuel+")",  value);
		}
     }
		return line;
	}
	
	
	
	
	/** This method returns report path.
	 * @return
	 */
	public static String getReportPath()
	{
		return readPropertyFile("ReportPath",propertiesFileName);
	}
		
}