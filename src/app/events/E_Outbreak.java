package app.events;

import app.game.City;
import app.game.Virus;

public class E_Outbreak extends Event{

	private final int sinceRound;
	
	private final Virus virus;
	
	private final double prevalence;
	
	private final City city;
	
	public E_Outbreak(City city, int sinceRound, Virus virus, double prevalence) {
		super(EventType.outbreak);
		this.sinceRound = sinceRound;
		this.virus = virus;
		this.prevalence = prevalence;
		this.city = city;
	}

	public Virus getVirus() {
		return virus;
	}

	public double getPrevalence() {
		return prevalence;
	}

	public City getCity() {
		return city;
	}
	
	@Override
	public String toString() {
		return super.toString()+". Since: "+getSinceRound()+", City:  "+getCity().getName()+
				", prevalence: "+getPrevalence()+", Virus:"+getVirus().toString();
	}

	public int getSinceRound() {
		return sinceRound;
	}
	
}
