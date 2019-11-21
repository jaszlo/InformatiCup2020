package app.game.events;

import app.game.Virus;

public class E_PathogenEncounter extends Event{

	private final Virus virus;
	
	private final int round;
	
	public E_PathogenEncounter(int round, Virus virus) {	
		super(EventType.pathogenEncountered);
		this.round = round;
		this.virus = virus;
	}

	public Virus getVirus() {
		return virus;
	}

	@Override
	public String toString() {
		return super.toString()+". Round: "+getRound()+
				", Virus:"+getVirus().toString();
	}

	public int getRound() {
		return round;
	}
	
}
