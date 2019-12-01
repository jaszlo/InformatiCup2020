package app.game.events;

import app.game.Virus;

/**
 * Class to hold information about the ingame MedicationAvailable Event
 */
public class E_MedicationAvailable extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param virus The virus for which medication is available 
	 */
	public E_MedicationAvailable(int sinceRound, Virus virus) {
		super(EventType.medicationAvailable);
		this.sinceRound = sinceRound;
		this.virus = virus;
	}

	/**
	 * 
	 * @return The virus for which medication is available
	 */
	public Virus getVirus() {
		return virus;
	}
	
	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Virus: "+getVirus().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}

}
