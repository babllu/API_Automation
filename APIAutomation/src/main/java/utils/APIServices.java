package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APIServices {
	
	/**
	 * This method is used to make service request for GET and DELETE Methods and stores the 
	 * response received
	 * 
	 * @author bghosh
	 * @param  url
	 * @param  methodType
	 * @param  headersMap
	 * @param  requestSpecification
	 * @return Response of request invoked
	 */
	public static Response sendNReceive(String url,String methodType, Map<String,String> headersMap , RequestSpecification requestSpecification)
	{
		Response response = null;
		switch(methodType.toUpperCase()) {
		case "GET":
			if(headersMap!=null)
			{
				response = RestAssured.given(requestSpecification)
						.relaxedHTTPSValidation()
						.headers(headersMap)
						.get(url);
			}
			else
			{
				response = RestAssured.given(requestSpecification).relaxedHTTPSValidation().get(url);
			}
			break;
		case "DELETE":
			if(headersMap!=null)
			{
				response = RestAssured.given(requestSpecification).relaxedHTTPSValidation().headers(headersMap).delete(url);
			}
			else
			{
				response = RestAssured.given(requestSpecification).relaxedHTTPSValidation().delete(url);
			}
			break;
		default:
		}
		return response;
	}
	
	
	
	/**
	 * This method is used to make service call for POST,PATCH and PUT Methods and stores
	 * response received
	 *
	 * @author bghosh
	 * @param  url
	 * @param  methodType
	 * @param  postBodyType
	 * @param  postBodyContent
	 * @param  headersMap
	 * @param  requestSpecification
	 * @return Response of request invoked
	 */
	public static Response sendNReceive(String url,String methodType,String postBodyType, String postBodyContent,
			Map<String,String> headersMap,RequestSpecification requestSpecification)
	{
		Response response = null;
		String contentType = "ContentType." + postBodyType.toUpperCase();
		switch(methodType.toUpperCase())
		{
		case "POST":
			if(headersMap!=null)
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.headers(headersMap).post(url);
			}
			else
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.post(url);
			}
			break;
		case "PATCH":
			if(headersMap!=null)
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.headers(headersMap).patch(url);
			}
			else
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.patch(url);
			}
			break;
		case "PUT":
			if(headersMap!=null)
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.headers(headersMap).put(url);
			}
			else
			{
				response = RestAssured.given(requestSpecification).contentType(contentType).relaxedHTTPSValidation().body(postBodyContent)
						.put(url);
			}
			break;
		default:
		}
		return response;
	}

	/**
	 * This method is used to validate schema of the response
	 *
	 * @author bghosh
	 * @param  responseFile
	 * @param  schema
	 */
	public static String validateSchema(String responseFile, String schema)
	{
		String schemaPath = System.getProperty("user.dir")+"/src/test/resources/Schema/"+schema;
		File schemaFile = new File(schemaPath);
		JSONTokener schemaData = null;
		try {
			schemaData = new JSONTokener(new FileInputStream(schemaFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JSONObject jsonSchema = new JSONObject(schemaData);
		
		JSONTokener jsonDataFile = null;
		jsonDataFile = new JSONTokener(responseFile);
		JSONObject jsonObject = new JSONObject(jsonDataFile);
		
		Schema schemaValidator = SchemaLoader.load(jsonSchema);
		try {
			schemaValidator.validate(jsonObject);
			return "Matched";
		} catch (ValidationException e) {
			return e.getMessage();
		}
		
		
	}
}
