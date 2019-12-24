package app.game.events;

import app.game.City;

/**
 *  Class to hold information about the ingame InfluenceExerted Event
 */
public class E_InfluenceExerted extends Event{

	private final City city;
	
	private final int round;
	
	/**
	 * 
	 * @param round the round in which this event occoured
	 * @param city the city in which the event took place
	 */
	public E_InfluenceExerted(City city, int round) {
		super(EventType.influenceExerted);
		this.city = city;
		this.round = round;
	}

	/**
	 * 
	 * @return city the city in which the event took place
	 */
	public City getCity() {
		return city;
	}

	/**
	 * 
	 * @return round the round in which this event occoured
	 */
	public int getRound() {
		return round;
	}

}