package air.model;

public enum AirOperationType {

	ANY(10), 
	ARRIVAL(20), DEPARTURE(15), WEEKEND_OPERATION(30), 
	FRIDAY_THE_13TH_OPERATION_AT_1_PM(1000), EPRA_OPERATION(10000);

	private int airOperationTypePrice;

	private AirOperationType(int priceInEuro) {
		this.airOperationTypePrice = priceInEuro;
	}

	public int getAirOperationTypePrice() {
		return airOperationTypePrice;
	}
}
