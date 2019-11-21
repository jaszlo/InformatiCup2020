package app.game.events;

import app.game.Virus;

public class E_MedicationAvailable extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	public E_MedicationAvailable(int sinceRound, Virus virus) {
		super(EventType.medicationAvailable);
		this.sinceRound = sinceRound;
		this.virus = virus;
	}

	public Virus getVirus() {
		return virus;
	}
	
	public String toString() {
		return super.toString()+", Since: "+getSinceRound()+",Virus: "+getVirus().toString();
	}

	public int getSinceRound() {
		return sinceRound;
	}

}
