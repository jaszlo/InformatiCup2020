package app.game.events;

import app.game.City;
import app.game.Virus;

/**
 * Class to hold information about the ingame Outbreak Event
 */
public class E_Outbreak extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	private final double prevalence;
	
	private final City city;
	
	/**
	 * 
	 * @param city the city in which the outbreak takes place
	 * @param sinceRound the round in which this event started
	 * @param virus the virus which broke out
	 * @param prevalence the percentage of citizens infected. prevalence lies in the interval [0,1]
	 */
	public E_Outbreak(City city, int sinceRound, Virus virus, double prevalence) {
		super(EventType.outbreak);
		this.sinceRound = sinceRound;
		this.virus = virus;
		this.prevalence = prevalence;
		this.city = city;
	}

	/**
	 * 
	 * @return the virus which broke out
	 */
	public Virus getVirus() {
		return virus;
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
				", prevalence: "+getPrevalence()+", Virus:"+getVirus().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
