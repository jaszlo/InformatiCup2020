package app.game.events;

import app.game.City;

public class E_AntiVacc extends Event{

	private final City city;
	
	private final int sinceRound;
	
	public E_AntiVacc(City city, int sinceRound) {
		super(EventType.antiVaccinationism);
		this.sinceRound = sinceRound;
		this.city = city;
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
	public City getCity() {
		return city;
	}
	
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City: "+getCity().getName();
	}
	
}
