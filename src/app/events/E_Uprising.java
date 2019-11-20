package app.events;

import app.game.City;

public class E_Uprising extends Event{

	private final int sinceRound;
	
	private final int participants;
	
	private final City city;
	
	public E_Uprising(City city, int sinceRound, int participants) {
		super(EventType.uprising);
		this.sinceRound = sinceRound;
		this.participants = participants;
		this.city = city;
	}

	public int getParticipants() {
		return participants;
	}

	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()
				+", participants: "+getParticipants();
	}

	public City getCity() {
		return city;
	}

	public int getSinceRound() {
		return sinceRound;
	}

}
