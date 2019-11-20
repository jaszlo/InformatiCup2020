package app.events;

import app.game.City;
import app.game.Virus;

public class E_VaccineDeployed extends Event{

	private final int round;
	
	private final Virus virus;
	
	private final City city;
	
	public E_VaccineDeployed(int round, Virus virus, City city) {
		super(EventType.vaccineDeployed);
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
