package app.game;

public class E_Quarantine extends Event{

	private final int untilRound, sinceRound;
	
	private final City city;
	
	public E_Quarantine(int untilRound, int sinceRound, City city) {
		super(EventType.quarantine);
		this.untilRound = untilRound;
		this.sinceRound = sinceRound;
		this.city = city;
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
	public int getUntilRound() {
		return untilRound;
	}
	
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", Until: "+getUntilRound()+" , City: "+getCity().getName();
	}

	public City getCity() {
		return city;
	}
	
}
