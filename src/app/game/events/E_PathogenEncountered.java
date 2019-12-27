package app.game.events;

import app.game.Pathogen;

/**
 * Class to hold information about the ingame PathogenEncounter Event
 */
public class E_PathogenEncounter extends Event{

	private final Pathogen pathogen;
	
	private final int round;
	
	/**
	 * 
	 * @param round the round the pathogen was first encountered
	 * @param pathogen the pathogen that is encountered
	 */
	public E_PathogenEncounter(int round, Pathogen pathogen) {	
		super(EventType.pathogenEncountered);
		this.round = round;
		this.pathogen = pathogen;
	}

	/**
	 * 
	 * @return the pathogen that is encountered
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * @return general information about this event as String
	 */
	@Override
	public String toString() {
		return super.toString()+". Round: "+getRound()+
				", Pathogen:"+getPathogen().toString();
	}

	/**
	 * 
	 * @return the round the pathogen was first encountered
	 */
	public int getRound() {
		return round;
	}
	
}
