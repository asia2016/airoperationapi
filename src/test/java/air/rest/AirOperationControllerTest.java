package air.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import air.config.Application;
import air.model.AirOperation;
import air.model.AirOperationType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class AirOperationControllerTest {

	// Required to generate JSON content from Java objects
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new TestRestTemplate();
	
	private static final String REST_URL = "http://localhost:8080/air";
	
	@Test
	public void noAirOperationTypesInRequestBodyTest() throws JsonProcessingException {
		
		AirOperation requestBody = new AirOperation();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		// Invoking the API
		ResponseEntity<AirOperation> re = restTemplate.postForEntity(REST_URL, httpEntity, AirOperation.class);
		assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
	}

	@Test
	public void ordinaryFlightWithTransitTest() throws JsonProcessingException {
		AirOperation requestBody = new AirOperation();
		requestBody.setId("Test1");
		requestBody.setAirplaneType("B747");
		requestBody.setFlightRoute("EPWA EPSC EPGD");

		List<AirOperationType> airOperationTypes = new ArrayList<>();
		Collections.addAll(airOperationTypes, AirOperationType.DEPARTURE, // EPWA
				AirOperationType.ARRIVAL, // EPSC
				AirOperationType.DEPARTURE, // EPSC
				AirOperationType.ARRIVAL);// EPGD
		
		requestBody.setAirOperationTypes(airOperationTypes);
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		AirOperation apiResponse = restTemplate.postForObject(REST_URL, httpEntity,
				AirOperation.class);
		
		 assertNotNull(apiResponse);
		 assertEquals(requestBody.getId(), apiResponse.getId());
		 assertEquals(requestBody.getFlightRoute(), apiResponse.getFlightRoute());
		 assertEquals(requestBody.getAirplaneType(), apiResponse.getAirplaneType());
		 assertEquals(requestBody.getAirOperationTypes(), apiResponse.getAirOperationTypes());
		 assertTrue(apiResponse.getAirOperationPrice()==110);
	}
	
	@Test
	public void notValidAirOperationTypesBecauseArrivalsAndDeparturesNotEqual() throws JsonProcessingException {
		AirOperation requestBody = new AirOperation();
		requestBody.setId("Test1");
		requestBody.setAirplaneType("B747");
		requestBody.setFlightRoute("EPWA EPSC EPGD");

		List<AirOperationType> airOperationTypes = new ArrayList<>();
		Collections.addAll(airOperationTypes, AirOperationType.DEPARTURE, // EPWA
				AirOperationType.ARRIVAL, // EPSC
				AirOperationType.DEPARTURE); // EPSC
		
		requestBody.setAirOperationTypes(airOperationTypes);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		ResponseEntity<RestError> re = restTemplate.postForEntity(REST_URL, httpEntity, RestError.class);
		assertEquals(HttpStatus.NOT_ACCEPTABLE, re.getStatusCode());
		assertEquals("Number of arrivals and departures are not equal in operationTypes", re.getBody().getMessage());
	}
	
	@Test
	public void notValidNumberOfArrivalsOrDeparturesVsAirportsNumber() throws JsonProcessingException {
		AirOperation requestBody = new AirOperation();
		requestBody.setId("Test1");
		requestBody.setAirplaneType("B747");
		requestBody.setFlightRoute("EPWA EPGD");//only two airports, should be three

		List<AirOperationType> airOperationTypes = new ArrayList<>();
		Collections.addAll(airOperationTypes, AirOperationType.DEPARTURE, // EPWA
				AirOperationType.ARRIVAL, // EPSC
				AirOperationType.DEPARTURE, // EPSC
				AirOperationType.ARRIVAL);// EPGD
		
		requestBody.setAirOperationTypes(airOperationTypes);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		ResponseEntity<RestError> re = restTemplate.postForEntity(REST_URL, httpEntity, RestError.class);
		assertEquals(HttpStatus.NOT_ACCEPTABLE, re.getStatusCode());
		assertEquals("Number of pairs arrivals and depratures in flightRoute not equal to those in operationTypes", re.getBody().getMessage());
	}
	
	@Test
	public void notValidBecauseEPRAinFlightRouteOnly() throws JsonProcessingException {
		AirOperation requestBody = new AirOperation();
		requestBody.setId("Test1");
		requestBody.setAirplaneType("B747");
		requestBody.setFlightRoute("EPWA EPRA EPGD");//only two airports, should be three

		List<AirOperationType> airOperationTypes = new ArrayList<>();
		Collections.addAll(airOperationTypes, AirOperationType.DEPARTURE, // EPWA
				AirOperationType.ARRIVAL, // EPSC
				AirOperationType.DEPARTURE, // EPSC
				AirOperationType.ARRIVAL);// EPGD
		
		requestBody.setAirOperationTypes(airOperationTypes);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		ResponseEntity<RestError> re = restTemplate.postForEntity(REST_URL, httpEntity, RestError.class);
		assertEquals(HttpStatus.NOT_ACCEPTABLE, re.getStatusCode());
		assertEquals("Number of EPRA arrivals in flightRoute not equal to number of EPRA arrivals in airOperationTypes", re.getBody().getMessage());
	}
	
	@Test
	public void validManyEPRAairports() throws JsonProcessingException {
		AirOperation requestBody = new AirOperation();
		requestBody.setId("Test1");
		requestBody.setAirplaneType("B747");
		requestBody.setFlightRoute("EPRA EPWA EPRA EPSC EPGD EPRA");

		List<AirOperationType> airOperationTypes = new ArrayList<>();
		Collections.addAll(airOperationTypes, AirOperationType.DEPARTURE, // EPRA
				AirOperationType.ARRIVAL, // EPWA
				AirOperationType.DEPARTURE, // EPWA
				AirOperationType.ARRIVAL, // EPRA
				AirOperationType.EPRA_OPERATION,
				AirOperationType.DEPARTURE, // EPRA
				AirOperationType.ARRIVAL, // EPSC
				AirOperationType.DEPARTURE, // EPSC
				AirOperationType.ARRIVAL, // EPGD
				AirOperationType.DEPARTURE, // EPGD
				AirOperationType.ARRIVAL, // EPRA
				AirOperationType.EPRA_OPERATION,
				AirOperationType.FRIDAY_THE_13TH_OPERATION_AT_1_PM,
				AirOperationType.WEEKEND_OPERATION);
				
		requestBody.setAirOperationTypes(airOperationTypes);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> httpEntity = new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody),
				requestHeaders);

		ResponseEntity<AirOperation> re = restTemplate.postForEntity(REST_URL, httpEntity, AirOperation.class);
		assertEquals(HttpStatus.OK, re.getStatusCode());
		assertEquals(21345, re.getBody().getAirOperationPrice());
	}
}
