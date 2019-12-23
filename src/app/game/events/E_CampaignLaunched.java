package app.game.events;

import app.game.City;

public class E_CampaignLaunched extends Event{

	private final City city;
	
	private final int round;
	
	public E_CampaignLaunched(City city, int round) {
		super(EventType.campaignLaunched);
		this.city = city;
		this.round = round;
	}

	public City getCity() {
		return city;
	}

	public int getRound() {
		return round;
	}

}
