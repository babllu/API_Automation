package API_Basics;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class API_Sample1 {

	
	public void samplejson1() {

		RestAssured.baseURI = "https:simple-tool-rental-api.glitch.mo/tools";
		Response response = RestAssured.given().header("Content-Type", "application/json").get();
	}
	
	@Test
	public void putmethod() {
		RestAssured.baseURI = "https://regres.in/";
		String requestBody ="{\r\n" + "\"name\":\"morpheus\",\r\n" +"  \"job\": \"zion resident\"r\n" +"}";
		
		Response response =RestAssured.given().header("Content-type", "application/json").and().body(requestBody).when().put("/api/users/2")
				           .then().assertThat().statusCode(200).log().all().extract().response();
		
		
	}

}