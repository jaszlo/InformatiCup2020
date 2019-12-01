package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame ConnectionClosed Event
 */
public class E_ConnectionClosed extends Event{

	private final int untilRound,sinceRound;
	
	private final City from, to;
	
	/**
	 * 
	 * @param sinceRound the round in which this event started
	 * @param untilRound the round until this event remains active
	 * @param from Starting city of the connection
	 * @param to target city of the connection
	 */
	public E_ConnectionClosed(int sinceRound, int untilRound, City from, City to) {
		super(EventType.connectionClosed);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.from = from;
		this.to = to;
	}

	/**
	 * 
	 * @return the round until this event remains active
	 */
	public int getUntilRound() {
		return untilRound;
	}

	/**
	 * 
	 * @return Starting city of the connection
	 */
	public City getFrom() {
		return from;
	}

	/**
	 * 
	 * @return target city of the connection
	 */
	public City getTo() {
		return to;
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}
	
	/**
	 * @return general information about this event as String
	 */
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", From: "+getFrom().getName()+", To: "+getTo().getName();
	}
	
	
	@Override
	public String getName () {
		return "connectionClosed";
	}
}
