package API_Basics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TC_04 {
	
	public static void main (String[] arg) {
	
	File jsonDataInFile = new File(System.getProperty("Users/2164624/eclipse/API_Workspace\\API_Testing_Samples\\Resources\\payload.json"));
	Map <String, String> headers_map = new HashMap<>();
	headers_map.put("Content-type", "appliaction/json");
	headers_map.put("Accept", "appliaction/json");
	headers_map.put("Authroization", "Basic  YWRtaW46cGFzc3dvcmQxMjM=");
	RestAssured.baseURI="https://restful-broker.herokuapp.com/booking";
	
	Response response = RestAssured.given().headers(headers_map).body(jsonDataInFile).when().patch("/1").then().statusCode(200).extract().response();
	
	System.out.println(response.asString());
	}

}
