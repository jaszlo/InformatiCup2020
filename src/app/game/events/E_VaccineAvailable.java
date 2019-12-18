package app.game.events;

import app.game.Pathogen;

/**
 * Class to hold information about the ingame VaccineAvailable Event
 */
public class E_VaccineAvailable extends Event{

	private final int sinceRound;
	
	private final Pathogen pathogen;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started 
	 * @param pathogen the pathogen for which vaccination is available
	 */
	public E_VaccineAvailable(int sinceRound, Pathogen pathogen) {
		super(EventType.vaccineAvailable);
		this.sinceRound = sinceRound;
		this.pathogen = pathogen;
	}

	/**
	 * 
	 * @return the pathogen for which vaccination is available
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+",Since: "+getSinceRound()+" ,Pathogen: "+getPathogen().getName();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
