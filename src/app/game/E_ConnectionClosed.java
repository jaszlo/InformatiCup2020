package app.game;

public class E_ConnectionClosed extends Event{

	private final int untilRound,sinceRound;
	
	private final City from, to;
	
	public E_ConnectionClosed(int sinceRound, int untilRound, City from, City to) {
		super(EventType.connectionClosed);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.from = from;
		this.to = to;
	}

	public int getUntilRound() {
		return untilRound;
	}

	public City getFrom() {
		return from;
	}

	public City getTo() {
		return to;
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", From: "+getFrom().getName()+", To: "+getTo().getName();
	}
	
	@Override
	public String getName () {
		return "closedConnection";
	}
}
