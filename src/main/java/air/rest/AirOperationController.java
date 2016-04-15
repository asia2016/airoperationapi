package air.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import air.model.AirOperation;
import air.model.AirOperationType;
import air.rest.exception.AirOperationNotFoundException;
import air.rest.exception.AirOperationNotValidException;

@RestController
public class AirOperationController {

	@RequestMapping(value = "/air", produces = "application/json")
	public AirOperation validateAndCalculate(@RequestBody AirOperation airOperation) {

		validateAirOperation(airOperation);
		calculateFullPrice(airOperation);

		return airOperation;
	}

	private void validateAirOperation(AirOperation airOperation){
		
		if(airOperation.getFlightRoute()==null
				|| airOperation.getAirOperationTypes()==null){
			throw new AirOperationNotFoundException();
		}
		
		List<AirOperationType> airOperationTypes = airOperation.getAirOperationTypes();
		
		//arrivals and departures should equal
		long arrivalsInOperationTypes = 
						airOperationTypes.stream()
							.filter(op -> op.equals(AirOperationType.ARRIVAL))
							.count();
		
		long departuresInOperationTypes = 
						airOperationTypes.stream()
							.filter(op -> op.equals(AirOperationType.DEPARTURE))
							.count();

		if(arrivalsInOperationTypes!=departuresInOperationTypes)
			throw new AirOperationNotValidException("Number of arrivals and departures are not equal in operationTypes");

		
		/*number of arrivals == number of departures = number of airports - 1
		 * (first airport only departure, last airport only arrival,
		 * every other airport arrival and departure) */
		String route = airOperation.getFlightRoute();
		List<String> airports = Arrays.asList(route.split(" "));
		int numberOfPairsArrivalsAndDeparturesFromFlightRoute = airports.size() - 1;
		
		long arrivarsAndDeparturesPairsInOperationTypes = 
				arrivalsInOperationTypes + departuresInOperationTypes;
		
		if(numberOfPairsArrivalsAndDeparturesFromFlightRoute!=arrivarsAndDeparturesPairsInOperationTypes/2)
			throw new AirOperationNotValidException("Number of pairs arrivals and depratures in flightRoute not equal to those in operationTypes");

		validateEPRA(airports, airOperationTypes);
	}
	
	private void validateEPRA(List<String> airports, List<AirOperationType> airOperationTypes){
		/*if ARRIVAL to EPRA in flightRoute, AirOperationType EPRA_OPERATION should be in 
		 * airOperationTypes and vice versa*/
		long numberOfArrivalsToEPRAinFlightRoute 
			= airports.stream().filter(airport->airport.equals("EPRA")).count();
		//if EPRA first airport, there is no ARRIVAL to EPRA
		if(airports.get(0).equals("EPRA"))
			numberOfArrivalsToEPRAinFlightRoute--;
		
		long numberOfArrivalsToEPRAinAirOperationTypes 
			= airOperationTypes.stream().filter(
					operation->operation.equals(AirOperationType.EPRA_OPERATION))
										.count();
		
		if(numberOfArrivalsToEPRAinFlightRoute!=numberOfArrivalsToEPRAinAirOperationTypes){
			throw new AirOperationNotValidException("Number of EPRA arrivals in flightRoute not equal to number of EPRA arrivals in airOperationTypes");
		}
	}

	private void calculateFullPrice(AirOperation airOperation) {
		int fullPrice = 0;
		if (airOperation.getAirOperationTypes() == null) {
			throw new AirOperationNotFoundException();
		}
		
		for (AirOperationType aot : airOperation.getAirOperationTypes()) {
			if(!aot.equals(AirOperationType.ANY))
				fullPrice += aot.getAirOperationTypePrice() + AirOperationType.ANY.getAirOperationTypePrice();
		}
		airOperation.setAirOperationPrice(fullPrice);
	}

	@ExceptionHandler(AirOperationNotValidException.class)
	@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
	public RestError airOperationNotValid(AirOperationNotValidException exception) {
		
		return new RestError(406, exception.getMessage());
	}

	@ExceptionHandler(AirOperationNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "AirOperation not provided")
	public void airOperationNotProvided(AirOperationNotFoundException exception) {
	}
}
