package app.game.events;

import app.game.City;
import app.game.Virus;

/**
 * Class to hold information about the ingame VaccineDeployed Event
 */
public class E_VaccineDeployed extends Event{

	private final int round;
	
	private final Virus virus;
	
	private final City city;
	
	/**
	 * 
	 * @param round the current round of the game
	 * @param virus the virus for which vaccination is being deployed
	 * @param city the city in which the vaccination is being deployed
	 */
	public E_VaccineDeployed(int round, Virus virus, City city) {
		super(EventType.vaccineDeployed);
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
	 * @return the virus for which vaccination is being deployed
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * 
	 * @return the city in which the vaccination is being deployed
	 */
	public City getCity() {
		return city;
	}
	
}
