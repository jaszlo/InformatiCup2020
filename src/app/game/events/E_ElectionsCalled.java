package app.game.events;

import app.game.City;

public class E_ElectionsCalled extends Event{

	private final City city;
	
	private final int round;
	
	public E_ElectionsCalled(City city, int round) {
		super(EventType.electionsCalled);
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