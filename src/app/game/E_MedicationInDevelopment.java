package app.game;

public class E_MedicationInDevelopment extends Event{

	private final int untilRound, sinceRound;
	
	private final Virus virus;
	
	public E_MedicationInDevelopment(int sinceRound, int untilRound, Virus virus) {
		super(EventType.medicationInDevelopment);
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
		return super.toString()+", Since: "+getSinceRound()+",Until: "+getUntilRound()+", Virus: "+getVirus().toString();
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
}
