package app.game.events;

import app.game.Pathogen;

/**
 * Class to hold information about the ingame MedicationAvailable Event
 */
public class E_MedicationAvailable extends Event{

	private final int sinceRound;
	
	private final Pathogen pathogen;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param pathogen The pathogen for which medication is available 
	 */
	public E_MedicationAvailable(int sinceRound, Pathogen pathogen) {
		super(EventType.medicationAvailable);
		this.sinceRound = sinceRound;
		this.pathogen = pathogen;
	}

	/**
	 * 
	 * @return The pathogen for which medication is available
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}
	
	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Pathogen: "+getPathogen().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}

}
