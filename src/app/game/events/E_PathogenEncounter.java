package app.game.events;

import app.game.Virus;

/**
 * Class to hold information about the ingame PathogenEncounter Event
 */
public class E_PathogenEncounter extends Event{

	private final Virus virus;
	
	private final int round;
	
	/**
	 * 
	 * @param round the round the pathogen was first encountered
	 * @param virus the virus that is encountered
	 */
	public E_PathogenEncounter(int round, Virus virus) {	
		super(EventType.pathogenEncountered);
		this.round = round;
		this.virus = virus;
	}

	/**
	 * 
	 * @return the virus that is encountered
	 */
	public Virus getVirus() {
		return virus;
	}

	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Round: "+getRound()+
				", Virus:"+getVirus().toString();
	}

	/**
	 * 
	 * @return the round the pathogen was first encountered
	 */
	public int getRound() {
		return round;
	}
	
}
