package app.game;

public class Virus {

	private final String name;
	
	private final Scale infectivity, mobility, duration, lethality;
	
	/**
	 * 
	 * @param name The name of the virus
	 * @param infectivity The infectivity of virus 
	 * @param mobility The mobility of virus
	 * @param duration The duration of virus
	 * @param lethality The deadliness of virus
	 */
	public Virus(String name, Scale infectivity, Scale mobility, Scale duration, Scale lethality) {
		this.name = name;
		this.infectivity = infectivity;
		this.mobility = mobility;
		this.duration = duration;
		this.lethality = lethality;
	}

	/**
	 * 
	 * @return Name of virus
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the infectivity of virus
	 */
	public Scale getInfectivity() {
		return infectivity;
	}

	/**
	 * 
	 * @return the mobility of virus.
	 */
	public Scale getMobility() {
		return mobility;
	}

	/**
	 * 
	 * @return the duration of virus.
	 */
	public Scale getDuration() {
		return duration;
	}

	/**
	 * 
	 * @return the lethality of virus.
	 */
	public Scale getLethality() {
		return lethality;
	}
	
	/**
	 * @return the basic information of virus as String, in the following format
	 * [%name,%infectivity,%mobility,%duration,%lethality]
	 */
	@Override
	public String toString() {
		return "["+getName()+",Infectivity:"+getInfectivity().toString()+","
				+ "Mobility:"+getMobility().toString()+","
				+ "Duration:"+getDuration().toString()+","
				+ "Lethality:"+getLethality().toString()+"]";
	}
	
}
