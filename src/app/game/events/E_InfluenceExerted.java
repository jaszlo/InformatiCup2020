package app.game.events;

import app.game.City;

public class E_InfluenceExerted extends Event{

	private final City city;
	
	private final int round;
	
	public E_InfluenceExerted(City city, int round) {
		super(EventType.influenceExerted);
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