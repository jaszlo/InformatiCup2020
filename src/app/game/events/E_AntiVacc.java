package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame AntiVaccionationism Event
 */
public class E_AntiVacc extends Event{

	private final City city;
	
	private final int sinceRound;
	
	/**
	 * 
	 * @param city the City in which the antivaccionationism takes place
	 * @param sinceRound the round in which this event started
	 */
	public E_AntiVacc(City city, int sinceRound) {
		super(EventType.antiVaccinationism);
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
	 * @return the City in which this event takes place
	 */
	public City getCity() {
		return city;
	}
	
	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City: "+getCity().getName();
	}
	
}
