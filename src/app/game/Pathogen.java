package app.game;

public class Pathogen {

	private final String name;
	
	private final Scale infectivity, mobility, duration, lethality;
	
	/**
	 * 
	 * @param name The name of the pathogen
	 * @param infectivity The infectivity of pathogen 
	 * @param mobility The mobility of pathogen
	 * @param duration The duration of pathogen
	 * @param lethality The deadliness of pathogen
	 */
	public Pathogen(String name, Scale infectivity, Scale mobility, Scale duration, Scale lethality) {
		this.name = name;
		this.infectivity = infectivity;
		this.mobility = mobility;
		this.duration = duration;
		this.lethality = lethality;
	}

	/**
	 * 
	 * @return Name of pathogen
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the infectivity of pathogen
	 */
	public Scale getInfectivity() {
		return infectivity;
	}

	/**
	 * 
	 * @return the mobility of pathogen.
	 */
	public Scale getMobility() {
		return mobility;
	}

	/**
	 * 
	 * @return the duration of pathogen.
	 */
	public Scale getDuration() {
		return duration;
	}

	/**
	 * 
	 * @return the lethality of pathogen.
	 */
	public Scale getLethality() {
		return lethality;
	}
	
	/**
	 * @return the basic information of pathogen as String, in the following format
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
