package app.game.events;

import app.game.City;

public class E_HygienicMeasuresApplied extends Event{

	private final City city;
	
	private final int round;
	
	public E_HygienicMeasuresApplied(City city, int round) {
		super(EventType.hygienicMeasuresApplied);
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