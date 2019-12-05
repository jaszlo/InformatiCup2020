package app.game.events;

import app.game.City;
import app.game.Virus;


/**
 * Class to hold information about the ingame MedicationDeployed Event
 */
public class E_MedicationDeployed extends Event{

	private final int round;
	
	private final Virus virus;
	
	private final City city;
	
	/**
	 * 
	 * @param round the current round of the game
	 * @param virus the virus for which medication is deployed
	 * @param city the city in which the medication is deployed
	 */
	public E_MedicationDeployed(int round, Virus virus, City city) {
		super(EventType.medicationDeployed);
		this.round = round;
		this.virus = virus;
		this.city = city;
	}

	/**
	 * 
	 * @return the current round of the game
	 */
	public int getRound() {
		return round;
	}

	/**
	 * 
	 * @return the virus for which medication is deployed
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * 
	 * @return the city in which the medication is deployed
	 */
	public City getCity() {
		return city;
	}
	
}
