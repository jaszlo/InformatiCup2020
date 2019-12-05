package app.game.events;

import app.game.City;

/**
 * Class to hold information about the ingame Uprising Event
 */
public class E_Uprising extends Event{

	private final int sinceRound;
	
	private final int participants;
	
	private final City city;
	
	/**
	 * 
	 * @param city the city in which uprising takes place
	 * @param sinceRound the round in which this event started
	 * @param participants the amount of citizens taking place in the uprising in 10^3.
	 */
	public E_Uprising(City city, int sinceRound, int participants) {
		super(EventType.uprising);
		this.sinceRound = sinceRound;
		this.participants = participants;
		this.city = city;
	}

	/**
	 * 
	 * @return the amount of citizens taking place in the uprising in 10^3.
	 */
	public int getParticipants() {
		return participants;
	}

	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()
				+", participants: "+getParticipants();
	}

	/**
	 * 
	 * @return the city in which uprising takes place
	 */
	public City getCity() {
		return city;
	}

	/**
	 * 
	 * @return the round in which this event started
	 */
	public int getSinceRound() {
		return sinceRound;
	}

}
