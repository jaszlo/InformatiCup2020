package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame CampaignLaunched Event
 */
public class E_CampaignLaunched extends Event{

	private final City city;
	
	private final int round;
	
	/**
	 * 
	 * @param round the round in which this event occoured
	 * @param city the city in which the event took place
	 */
	public E_CampaignLaunched(City city, int round) {
		super(EventType.campaignLaunched);
		this.city = city;
		this.round = round;
	}

	/**
	 * 
	 * @return the city in which the event took place
	 */
	public City getCity() {
		return city;
	}

	/**
	 * 
	 * @return the round in which this event occoured
	 */
	public int getRound() {
		return round;
	}

}
