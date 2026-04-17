package API_Basics;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TC02 {
	
	@Test
	public void test_prod() {
		
		RestAssured.baseURI="https://reqres.in";
//		Payload
		String request_body ="{\r\n"
				+ "  \"name\": \"Abhek\",\r\n"
				+ "  \"midde_name\": \"deti\" \r\n"
				+ "  \"id\": \"21624\"\r\n"
				+ "  \"emailId\" : \"guddeti.abhhek@cognizant.com\"\r\n"
				+ "}";
		
		Response response = RestAssured.given().header("Content-Type","appliaction/json").
				when().post("api/user").then().statusCode(200).extract().response();
		
		System.out.println(response.asString());
		
		
				
				
	}

}
