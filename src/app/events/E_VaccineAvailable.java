package app.events;

import app.game.Virus;

public class E_VaccineAvailable extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	public E_VaccineAvailable(int sinceRound, Virus virus) {
		super(EventType.vaccineAvailable);
		this.sinceRound = sinceRound;
		this.virus = virus;
	}

	public Virus getVirus() {
		return virus;
	}

	public String toString() {
		return super.toString()+",Since: "+getSinceRound()+" ,Virus: "+getVirus().getName();
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
}
