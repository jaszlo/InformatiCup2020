package app.game;

public class E_AirportClosed extends Event{

	private final int untilRound, sinceRound;
	
	private final City city;
	
	public E_AirportClosed(int sinceRound, int untilRound, City city) {
		super(EventType.airportClosed);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.city = city;
	}

	public int getUntilRound() {
		return untilRound;
	}

	public City getCity() {
		return city;
	}
	
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", City: "+getCity().getName();
	}

	public int getSinceRound() {
		return sinceRound;
	}

}
