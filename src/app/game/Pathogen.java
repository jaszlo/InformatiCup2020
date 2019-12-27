package app.game;

/**
 * Class to represent a pathogen and to store all necessary information.
 */
public class Pathogen {

	private final String name;

	private final Scale infectivity, mobility, duration, lethality;

	/**
	 * @param name        The name of this pathogen.
	 * @param infectivity The infectivity of this pathogen.
	 * @param mobility    The mobility of this pathogen.
	 * @param duration    The duration of this pathogen.
	 * @param lethality   The deadliness of this pathogen.
	 */
	public Pathogen(String name, Scale infectivity, Scale mobility, Scale duration, Scale lethality) {
		this.name = name;
		this.infectivity = infectivity;
		this.mobility = mobility;
		this.duration = duration;
		this.lethality = lethality;
	}

	/**
	 * @return The Name of this pathogen.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return The infectivity of this pathogen.
	 */
	public Scale getInfectivity() {
		return this.infectivity;
	}

	/**
	 * 
	 * @return The mobility of this pathogen.
	 */
	public Scale getMobility() {
		return this.mobility;
	}

	/**
	 * 
	 * @return The duration of this pathogen.
	 */
	public Scale getDuration() {
		return this.duration;
	}

	/**
	 * @return The lethality of this pathogen.
	 */
	public Scale getLethality() {
		return this.lethality;
	}

	/**
	 * @return The basic information of pathogen as string, in the following format
	 *         [%name,%infectivity,%mobility,%duration,%lethality].
	 */
	@Override
	public String toString() {
		return "[" + getName() + ",Infectivity:" + getInfectivity().toString() + "," + "Mobility:"
				+ getMobility().toString() + "," + "Duration:" + getDuration().toString() + "," + "Lethality:"
				+ getLethality().toString() + "]";
	}

}
