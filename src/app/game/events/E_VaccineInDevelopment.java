package app.game.events;

import app.game.Virus;

/**
 * Class to hold information about the ingame VaccineInDevelopment Event
 */
public class E_VaccineInDevelopment extends Event{

	private final int untilRound, sinceRound;
	
	private final Virus virus;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param untilRound the round until this event remains active
	 * @param virus the virus for which a vaccine is being developed
	 */
	public E_VaccineInDevelopment(int sinceRound, int untilRound, Virus virus) {
		super(EventType.vaccineInDevelopment);
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
	 * @return the virus for which a vaccine is being developed
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+" ,Until: "+getUntilRound()+", Virus: "+getVirus().toString();
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
}
