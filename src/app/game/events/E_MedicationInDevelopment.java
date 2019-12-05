package app.game.events;

import app.game.Virus;


/**
 * Class to hold information about the ingame MedicationInDevelopment Event
 */
public class E_MedicationInDevelopment extends Event{

	private final int untilRound, sinceRound;
	
	private final Virus virus;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param untilRound the round until this event remains active
	 * @param virus the virus for which medication is being developed
	 */
	public E_MedicationInDevelopment(int sinceRound, int untilRound, Virus virus) {
		super(EventType.medicationInDevelopment);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.virus = virus;
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
	 * @return the virus for which medication is being developed
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", Virus: "+getVirus().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
