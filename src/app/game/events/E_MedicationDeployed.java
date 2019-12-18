package app.game.events;

import app.game.City;
import app.game.Pathogen;


/**
 * Class to hold information about the ingame MedicationDeployed Event
 */
public class E_MedicationDeployed extends Event{

	private final int round;
	
	private final Pathogen pathogen;
	
	private final City city;
	
	/**
	 * 
	 * @param round the current round of the game
	 * @param pathogen the pathogen for which medication is deployed
	 * @param city the city in which the medication is deployed
	 */
	public E_MedicationDeployed(int round, Pathogen pathogen, City city) {
		super(EventType.medicationDeployed);
		this.round = round;
		this.pathogen = pathogen;
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
	 * @return the pathogen for which medication is deployed
	 */
	public Pathogen getVirus() {
		return pathogen;
	}

	/**
	 * 
	 * @return the city in which the medication is deployed
	 */
	public City getCity() {
		return city;
	}
	
}
