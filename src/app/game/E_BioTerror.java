package app.game;

public class E_BioTerror extends Event{

	private final Virus virus;
	
	private final City city;
	
	private final int sinceRound;
	
	public E_BioTerror(City city, int sinceRound, Virus virus) {
		super(EventType.bioTerrorism);
		this.sinceRound = sinceRound;
		this.virus = virus;
		this.city = city;
	}

	public Virus getVirus() {
		return virus;
	}

	public City getCity() {
		return city;
	}
	
	public int getSinceRound() {
		return sinceRound;
	}
	
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()+", Virus: "+getVirus().toString();
	}
}
