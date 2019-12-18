package app.game.events;

import app.game.City;
import app.game.Pathogen;

/**
 * Class to hold information about the ingame VaccineDeployed Event
 */
public class E_VaccineDeployed extends Event{

	private final int round;
	
	private final Pathogen pathogen;
	
	private final City city;
	
	/**
	 * 
	 * @param round the current round of the game
	 * @param pathogen the pathogen for which vaccination is being deployed
	 * @param city the city in which the vaccination is being deployed
	 */
	public E_VaccineDeployed(int round, Pathogen pathogen, City city) {
		super(EventType.vaccineDeployed);
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
	 * @return the pathogen for which vaccination is being deployed
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * 
	 * @return the city in which the vaccination is being deployed
	 */
	public City getCity() {
		return city;
	}
	
}
