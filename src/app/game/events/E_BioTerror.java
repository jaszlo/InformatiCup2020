package app.game.events;

import app.game.City;
import app.game.Virus;

/**
 * Class to hold information about the ingame BioTerrorism Event
 */
public class E_BioTerror extends Event{

	private final Virus virus;
	
	private final City city;
	
	private final int sinceRound;
	
	/**
	 * 
	 * @param city the City in which this event takes place
	 * @param sinceRound the round in which this event started
	 * @param virus The virus being spread 
	 */
	public E_BioTerror(City city, int sinceRound, Virus virus) {
		super(EventType.bioTerrorism);
		this.sinceRound = sinceRound;
		this.virus = virus;
		this.city = city;
	}

	/**
	 * 
	 * @return the virus being spread by this event
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * 
	 * @return the City in which this event takes place
	 */
	public City getCity() {
		return city;
	}
	
	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()+", Virus: "+getVirus().toString();
	}
}
