package app.game.events;

import app.game.City;
import app.game.Pathogen;

/**
 * Class to hold information about the ingame Outbreak Event
 */
public class E_Outbreak extends Event{

	private final int sinceRound;
	
	private final Pathogen pathogen;
	
	private final double prevalence;
	
	private final City city;
	
	/**
	 * 
	 * @param city the city in which the outbreak takes place
	 * @param sinceRound the round in which this event started
	 * @param pathogen the pathogen which broke out
	 * @param prevalence the percentage of citizens infected. prevalence lies in the interval [0,1]
	 */
	public E_Outbreak(City city, int sinceRound, Pathogen pathogen, double prevalence) {
		super(EventType.outbreak);
		this.sinceRound = sinceRound;
		this.pathogen = pathogen;
		this.prevalence = prevalence;
		this.city = city;
	}

	/**
	 * 
	 * @return the pathogen which broke out
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * 
	 * @return prevalence the percentage of citizens infected. prevalence lies in the interval [0,1]
	 */
	public double getPrevalence() {
		return prevalence;
	}

	/**
	 * 
	 * @return the city in which the outbreak takes place
	 */
	public City getCity() {
		return city;
	}
	
	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()+
				", prevalence: "+getPrevalence()+", Pathogen:"+getPathogen().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
