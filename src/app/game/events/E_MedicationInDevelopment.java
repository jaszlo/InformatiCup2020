package app.game.events;

import app.game.Pathogen;


/**
 * Class to hold information about the ingame MedicationInDevelopment Event
 */
public class E_MedicationInDevelopment extends Event{

	private final int untilRound, sinceRound;
	
	private final Pathogen pathogen;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param untilRound the round until this event remains active
	 * @param pathogen the pathogen for which medication is being developed
	 */
	public E_MedicationInDevelopment(int sinceRound, int untilRound, Pathogen pathogen) {
		super(EventType.medicationInDevelopment);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.pathogen = pathogen;
	}

	/**
	 * 
	 * @return the round until this event remains active
	 */
	public int getUntilRound() {
		return untilRound;
	}

	/**
	 * 
	 * @return the pathogen for which medication is being developed
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", Pathogen: "+getPathogen().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
