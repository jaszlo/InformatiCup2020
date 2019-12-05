package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame AirportClosed Event
 */
public class E_AirportClosed extends Event{

	private final int untilRound, sinceRound;
	
	private final City city;
	
	/**
	 * 
	 * @param sinceRound the round at which the airport got initially closed
	 * @param untilRound the round until the airport remains closed
	 * @param city the City in which the airport is closed
	 */
	public E_AirportClosed(int sinceRound, int untilRound, City city) {
		super(EventType.airportClosed);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.city = city;
	}

	/**
	 * 
	 * @return the round until the airport remains closed
	 */
	public int getUntilRound() {
		return untilRound;
	}

	/**
	 * 
	 * @return the City in which the airport is closed
	 */ 
	public City getCity() {
		return city;
	}
	
	/**
	 * @return General information about the event as a String 
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", City: "+getCity().getName();
	}

	/**
	 * 
	 * @return the round at which the airport got initially closed
	 */
	public int getSinceRound() {
		return sinceRound;
	}

}
