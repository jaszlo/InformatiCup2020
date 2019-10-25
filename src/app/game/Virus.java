package app.game;

public class Virus {

	private final String name;
	
	private final Scale infectivity, mobility, duration, lethality;
	
	public Virus(String name, Scale infectivity, Scale mobility, Scale duration, Scale lethality) {
		this.name = name;
		this.infectivity = infectivity;
		this.mobility = mobility;
		this.duration = duration;
		this.lethality = lethality;
	}

	public String getName() {
		return name;
	}

	public Scale getInfectivity() {
		return infectivity;
	}

	public Scale getMobility() {
		return mobility;
	}

	public Scale getDuration() {
		return duration;
	}

	public Scale getLethality() {
		return lethality;
	}
	
	@Override
	public String toString() {
		return "["+getName()+",Infectivity:"+getInfectivity().toString()+","
				+ "Mobility:"+getMobility().toString()+","
				+ "Duration:"+getDuration().toString()+","
				+ "Lethality:"+getLethality().toString()+"]";
	}
	
}
