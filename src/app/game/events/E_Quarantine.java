package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame Quarantine Event
 */
public class E_Quarantine extends Event{

	private final int untilRound, sinceRound;
	
	private final City city;
	
	/**
	 * 
	 * @param untilRound the round until this event remains active
	 * @param sinceRound the round in which this event started
	 * @param city the city being put under quarantine
	 */
	public E_Quarantine(int untilRound, int sinceRound, City city) {
		super(EventType.quarantine);
		this.untilRound = untilRound;
		this.sinceRound = sinceRound;
		this.city = city;
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
	/**
	 * 
	 * @return the round until this event remains active
	 */
	public int getUntilRound() {
		return untilRound;
	}
	
	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", Until: "+getUntilRound()+" , City: "+getCity().getName();
	}

	/**
	 * 
	 * @return the city being put under quarantine
	 */
	public City getCity() {
		return city;
	}
	
}
