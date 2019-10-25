package app.game;

public class E_MedicationDeployed extends Event{

	private final int round;
	
	private final Virus virus;
	
	private final City city;
	
	public E_MedicationDeployed(int round, Virus virus, City city) {
		super(EventType.medicationDeployed);
		this.round = round;
		this.virus = virus;
		this.city = city;
	}

	public int getRound() {
		return round;
	}

	public Virus getVirus() {
		return virus;
	}

	public City getCity() {
		return city;
	}
	
}
