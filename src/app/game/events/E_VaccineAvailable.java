package app.game.events;

import app.game.Virus;

/**
 * Class to hold information about the ingame VaccineAvailable Event
 */
public class E_VaccineAvailable extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started 
	 * @param virus the virus for which vaccination is available
	 */
	public E_VaccineAvailable(int sinceRound, Virus virus) {
		super(EventType.vaccineAvailable);
		this.sinceRound = sinceRound;
		this.virus = virus;
	}

	/**
	 * 
	 * @return the virus for which vaccination is available
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+",Since: "+getSinceRound()+" ,Virus: "+getVirus().getName();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
