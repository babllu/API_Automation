package API_Basics;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TC_03 {

	@Test	
	
	public void delete_Api() {
		
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
		
		Response res = RestAssured.when().delete("posts/1").then().extract().response();
		
		Assert.assertEquals(200, res.statusCode());
		
	}
}
