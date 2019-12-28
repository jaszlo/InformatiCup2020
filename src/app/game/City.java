package app.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.game.events.Event;
import app.game.events.EventType;
import app.game.Pathogen;

/**
 * Class to represent a city and to store all necessary information.
 */
public class City {

	private final String name;

	private final double x, y;

	private final Set<City> connections;
	private final Map<EventType, Event> singleEvents;
	private final Map<EventType, Set<Event>> multipleEvents;

	private int population;

	private Scale economy, government, hygiene, awareness;

	/**
	 * Creates City object with specified parameters.
	 * 
	 * @param name        Name of the city
	 * @param x           The x-coordinate of city in cartesian. Equivalent to
	 *                    longitude.
	 * @param y           The y-coordinate of city in cartesian. Equivalent to
	 *                    latitude.
	 * @param connections Set of all Cities this city is connected to.
	 * @param population  The number of citizens in 1000.
	 * @param economy     The strength of the economy.
	 * @param government  The stability of government.
	 * @param hygiene     Hygiene standards of city.
	 * @param awareness   The awareness towards pathogens of the citizens.
	 */
	public City(String name, double x, double y, Set<City> connections, int population, Scale economy,
			Scale government, Scale hygiene, Scale awareness) {

		this.name = name;
		this.x = x;
		this.y = y;
		this.connections = connections;
		this.setPopulation(population);
		this.setEconomy(economy);
		this.setGovernment(government);
		this.setHygiene(hygiene);
		this.setAwareness(awareness);
		this.singleEvents = new HashMap<>();
		this.multipleEvents = new HashMap<>();
		this.initMultipleEvents();
	}

	/**
	 * @return Name of the city.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The y-coordinate of city in cartesian. Equivalent to latitude.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The x-coordinate of city in cartesian. Equivalent to longitude.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return Set of all cities this one is connected to.
	 */
	public Set<City> getConnections() {
		return connections;
	}

	/**
	 * @return The number of citizens in 1000.
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * @param population The number of citizens in 1000.
	 */
	public void setPopulation(int population) {
		this.population = population;
	}

	/**
	 * @return The awareness towards pathogens of the citizens.
	 */
	public Scale getAwareness() {
		return awareness;
	}

	/**
	 * @param awareness The awareness towards pathogens of the citizens.
	 */
	public void setAwareness(Scale awareness) {
		this.awareness = awareness;
	}

	/**
	 * @return The stability of government.
	 */
	public Scale getGovernment() {
		return government;
	}

	/**
	 * @param government The stability of government.
	 */
	public void setGovernment(Scale government) {
		this.government = government;
	}

	/**
	 * @return The strength of the economy.
	 */
	public Scale getEconomy() {
		return economy;
	}

	/**
	 * @param economy The strength of the economy.
	 */
	public void setEconomy(Scale economy) {
		this.economy = economy;
	}

	/**
	 * @return Hygiene standards of city.
	 */
	public Scale getHygiene() {
		return hygiene;
	}

	/**
	 * @param hygiene Hygiene standards of city.
	 */
	public void setHygiene(Scale hygiene) {
		this.hygiene = hygiene;
	}

	/**
	 * Adds an event to the city that takes place in this city.
	 * 
	 * @param event Event that takes places in the city and should be added.
	 */
	public void addEvent(Event event) {

		if (!event.getType().isMultipleEventType()) {
			this.singleEvents.put(event.getType(), event);
			
		} else {
			this.multipleEvents.get(event.getType()).add(event);
		}
	}

	/**
	 * Initializes the event-map.
	 */
	public void initMultipleEvents() {

		for (EventType type : EventType.values()) {
			if (!this.multipleEvents.containsKey(type) && type.isMultipleEventType()) {
				this.multipleEvents.put(type, new HashSet<>());
			}
		}
	}

	/**
	 * @return The prevalence of the outbreak in the city. If no outbreak exists in
	 *         the city, 0 is returned.
	 */
	public double getPrevalance() {

		Event outbreak = this.getOutbreak();
		return outbreak == null ? 0 : outbreak.getPrevalence();
	}

	/// Getters for events in the city.

	/**
	 * @return Airport closed event. If the airport is not closed, null is returned.
	 */
	public Event getAirportClosed() {
		return this.singleEvents.get(EventType.airportClosed);
	}

	/**
	 * @return Anti-vaccionationism event. If no anti-vaccinationism does not take
	 *         place in the city, null is returned.
	 */
	public Event getAntiVacc() {
		return this.singleEvents.get(EventType.antiVaccinationism);
	}

	/**
	 * @return Bio terrorism event. If no bio terrorism takes place in the city,
	 *         null is returned.
	 */
	public Event getBioTerror() {
		return this.singleEvents.get(EventType.bioTerrorism);
	}

	/**
	 * @return Set of all connections that are closed as connection closed events.
	 */
	public Set<Event>  getConnectionClosed() {
		return this.multipleEvents.get(EventType.connectionClosed);
	}

	/**
	 * @return Set of all medication that is deployed in the city.
	 */
	public Set<Event>  getMedicationDeployed() {
		return this.multipleEvents.get(EventType.medicationDeployed);
	}

	/**
	 * @return Outbreak event. If no outbreak is present in the city, null is returned.
	 */
	public Event getOutbreak() {
		return this.singleEvents.get(EventType.outbreak);
	}

	/**
	 * @return Quarantine event. If city is not under quarantine, null is returned.
	 */
	public Event getQuarantine() {
		return this.singleEvents.get(EventType.quarantine);
	}

	/**
	 * @return Uprising event. If no uprising takes place in the city, null is returned.
	 */
	public Event getUprising() {
		return this.singleEvents.get(EventType.uprising);
	}

	/**
	 * @return Set of all vaccines that were deployed in the city.
	 */
	public Set<Event>  getVaccineDeployed() {
		return this.multipleEvents.get(EventType.vaccineDeployed);
	}

	/**
	 * @return Get the pathogen in this city. If the city is not infected null is returned.
	 */
	public Pathogen getPathogen() {
		return this.getOutbreak() != null ? this.getOutbreak().getPathogen() : null;
	}

	/**
	 * @return Returns true if the city is infected by any pathogen and false if it is
	 *         not infected at all.
	 */
	public boolean isInfected() {
		return this.getOutbreak() != null;
	}

	/**
	 * Returns true if the city is infected by the given pathogen. If the pathogen
	 * is null true is returned if the city is uninfected. 
	 * 
	 * @param pathogen Pathogen to check against.
	 * @return True if city is infected by pathogen.
	 */
	public boolean isInfected(Pathogen pathogen) {
		return pathogen != null && this.getPathogen() == pathogen;
	}
}
