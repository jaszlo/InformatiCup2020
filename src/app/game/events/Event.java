package app.game.events;


import app.game.City;
import app.game.Pathogen;

/**
 * Superclass for all event-classes. Superclass holds information about a
 * specific in game event.
 */
public class Event {

	private final EventType type;

	private final int roundUntil, round;

	private final double magnitude; // not be confused with magnetism

	private final Pathogen pathogen;

	private final City city, cityTo;

	/**
	 * Creates a generic event.
	 * 
	 * @param type       The type of the event.
	 * @param round      The round in which the event occurred or since it has been
	 *                   active.
	 * @param roundUntil The round until the event will be active.
	 * @param pathogen   The pathogen of the event.
	 * @param city       The city of the event.
	 * @param cityTo     The second city to which the connection will be closed from
	 *                   the first city.
	 * @param magnitude  Prevalence or participants of an event.
	 */
	public Event(EventType type, int round, int roundUntil, Pathogen pathogen, City city, City cityTo,
			double magnitude) {

		this.type = type;
		this.round = round;
		this.roundUntil = roundUntil;
		this.pathogen = pathogen;
		this.city = city;
		this.cityTo = cityTo;
		this.magnitude = magnitude;
	}

	/**
	 * Creates an airport closed or quarantine event.
	 * 
	 * @param type       The type of the event.
	 * @param round      The round in which the event occurred or since it has been
	 *                   active.
	 * @param roundUntil The round until the event will be active.
	 * @param city       The city of the event.
	 */
	public Event(EventType type, int round, int roundUntil, City city) {
		this(type, round, roundUntil, null, city, null, 0);
	}

	/**
	 * Creates anti-vaccinationism, campaign launched, elections called, influence
	 * exerted or hygienic measures applied event.
	 * 
	 * @param type  The type of the event.
	 * @param round The round in which the event occurred or since it has been
	 *              active.
	 * @param city  The city of the event.
	 */
	public Event(EventType type, int round, City city) {
		this(type, round, 0, null, city, null, 0);
	}

	/**
	 * Creates a bio terrorism or vaccine or medication deployed event.
	 * 
	 * @param type     The type of the event.
	 * @param round    The round in which the event occurred or since it has been
	 *                 active.
	 * @param pathogen The pathogen of the event.
	 * @param city     The city of the event.
	 */
	public Event(EventType type, int round, Pathogen pathogen, City city) {
		this(type, round, 0, pathogen, city, null, 0);
	}

	/**
	 * Creates a connection closed event.
	 * 
	 * @param round      The round in which the event occurred or since it has been
	 *                   active.
	 * @param roundUntil The round until the event will be active.
	 * @param city       The first city.
	 * @param cityTo     The second city to which the connection will be closed from
	 *                   the first city.
	 */
	public Event(int round, int roundUntil, City city, City cityTo) {
		this(EventType.connectionClosed, round, roundUntil, null, city, cityTo, 0);
	}

	/**
	 * Creates a medication or vaccines available or pathogen encountered event.
	 * 
	 * @param type     The type of the event.
	 * @param round    The round in which the event occurred or since it has been
	 *                 active.
	 * @param pathogen The pathogen of the event.
	 */
	public Event(EventType type, int round, Pathogen pathogen) {
		this(type, round, 0, pathogen, null, null, 0);
	}

	/**
	 * Creates a medication or vaccine in development event.
	 * 
	 * @param type       The type of the event.
	 * @param round      The round in which the event occurred or since it has been
	 *                   active.
	 * @param roundUntil The round until the event will be active.
	 * @param pathogen   The pathogen of the event.
	 */
	public Event(EventType type, int round, int roundUntil, Pathogen pathogen) {
		this(type, round, roundUntil, pathogen, null, null, 0);
	}

	/**
	 * Creates an outbreak event.
	 * 
	 * @param round     The round in which the event occurred or since it has been
	 *                  active.
	 * @param pathogen  The pathogen of the event.
	 * @param city     The city of the event.
	 * @param magnitude Prevalence or participants of an event.
	 */
	public Event(int round, Pathogen pathogen, City city, double prevalence) {
		this(EventType.outbreak, round, 0, pathogen, city, null, prevalence);
	}

	/**
	 * Creates an uprising event.
	 * 
	 * @param round     The round in which the event occurred or since it has been
	 *                  active.
	 * @param city      The city of the event.
	 * @param magnitude Prevalence or participants of an event.
	 */
	public Event(int round, City city, double participants) {
		this(EventType.uprising, round, 0, null, city, null, participants);
	}

	/**
	 * @return The round in which the event occurred or since it has been active.
	 */
	public int getRound() {
		return this.round;
	}

	/**
	 * @return The pathogen of the event.
	 */
	public Pathogen getPathogen() {
		return this.pathogen;
	}

	/**
	 * @return The city of the event.
	 */
	public City getCity() {
		return this.city;
	}

	/**
	 * @return The second city to which the connection will be closed from the first
	 *         city.
	 */
	public City getCityTo() {
		return this.cityTo;
	}

	/**
	 * @return The type of the event.
	 */
	public EventType getType() {
		return this.type;
	}

	/**
	 * @return The name of the event.
	 */
	public String getName() {
		return this.type.name();
	}

	/**
	 * @return The round until the event will be active.
	 */
	public int getRoundUntil() {
		return this.roundUntil;
	}

	/** 
	 * @return The prevalence of the event.
	 */
	public double getPrevalence() {
		return this.magnitude;
	}

	/**
	 * @return The participants of the event.
	 */
	public double getParticipants() {
		return this.magnitude;
	}
	
}
