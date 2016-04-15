package air.model;

import java.util.List;

public class AirOperation {

	String id;
	List<AirOperationType> airOperationTypes; //you don't need to provide AirOperationType.ANY to the list
	String airplaneType;
	String flightRoute; // format : "EPWA EPSC EPGD"
	int airOperationPrice;

	public AirOperation() {}

	public AirOperation(String id, List<AirOperationType> airOperationTypes, String airplaneType, String flightRoute) {
		this.id = id;
		this.airOperationTypes = airOperationTypes;
		this.airplaneType = airplaneType;
		this.flightRoute = flightRoute;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<AirOperationType> getAirOperationTypes() {
		return airOperationTypes;
	}

	public void setAirOperationTypes(List<AirOperationType> airOperationTypes) {
		this.airOperationTypes = airOperationTypes;
	}

	public String getAirplaneType() {
		return airplaneType;
	}

	public void setAirplaneType(String airplaneType) {
		this.airplaneType = airplaneType;
	}

	public String getFlightRoute() {
		return flightRoute;
	}

	public void setFlightRoute(String flightRoute) {
		this.flightRoute = flightRoute;
	}

	public int getAirOperationPrice() {
		return airOperationPrice;
	}

	public void setAirOperationPrice(int airOperationPrice) {
		this.airOperationPrice = airOperationPrice;
	}

	@Override
	public String toString() {
		return "AirOperation [id=" + id + ", airOperationTypes=" + airOperationTypes + ", airplaneType=" + airplaneType
				+ ", flightRoute=" + flightRoute + ", airOperationPrice=" + airOperationPrice + "]";
	}
	
	
}
