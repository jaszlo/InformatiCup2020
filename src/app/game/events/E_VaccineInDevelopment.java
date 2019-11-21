package app.game.events;

import app.game.Virus;

public class E_VaccineInDevelopment extends Event{

	private final int untilRound, sinceRound;
	
	private final Virus virus;
	
	public E_VaccineInDevelopment(int sinceRound, int untilRound, Virus virus) {
		super(EventType.vaccineInDevelopment);
		this.sinceRound = sinceRound;
		this.untilRound = untilRound;
		this.virus = virus;
	}

	public int getUntilRound() {
		return untilRound;
	}

	public Virus getVirus() {
		return virus;
	}

	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+" ,Until: "+getUntilRound()+", Virus: "+getVirus().toString();
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
}
