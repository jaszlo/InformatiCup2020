package app.game.events;

import app.game.City;
import app.game.Pathogen;

/**
 * Class to hold information about the ingame BioTerrorism Event
 */
public class E_BioTerror extends Event{

	private final Pathogen pathogen;
	
	private final City city;
	
	private final int sinceRound;
	
	/**
	 * 
	 * @param city the City in which this event takes place
	 * @param sinceRound the round in which this event started
	 * @param pathogen The pathogen being spread 
	 */
	public E_BioTerror(City city, int sinceRound, Pathogen pathogen) {
		super(EventType.bioTerrorism);
		this.sinceRound = sinceRound;
		this.pathogen = pathogen;
		this.city = city;
	}

	/**
	 * 
	 * @return the pathogen being spread by this event
	 */
	public Pathogen getPathogen() {
		return pathogen;
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
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()+", Pathogen: "+getPathogen().toString();
	}
}
