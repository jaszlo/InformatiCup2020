package app.game;

import java.util.HashMap;

import java.util.HashSet;

import app.game.events.E_AirportClosed;
import app.game.events.E_AntiVacc;
import app.game.events.E_BioTerror;
import app.game.events.E_ConnectionClosed;
import app.game.events.E_MedicationDeployed;
import app.game.events.E_Outbreak;
import app.game.events.E_Quarantine;
import app.game.events.E_Uprising;
import app.game.events.E_VaccineDeployed;
import app.game.events.Event;
import app.game.events.EventType;

public class City {

	private final String name;
	
	private final double x,y;
	
	private final HashSet<City> connections;
	private final HashMap<EventType, Event> singleEvents;
	private final HashMap<EventType, HashSet<? extends Event>> multipleEvents;
	
	private int population;
	
	private Scale economy, government, hygiene, awareness;
	
	/**
	 * Creates City object with specified parameters.
	 * @param name Name of the city
	 * @param x The x-coordinate of city in cartesian. equivalent to longitude
	 * @param y The y-coordinate of city in cartesian. equivalent to latitude
	 * @param connections Set of all Cities this city is connected to
	 * @param population Num of citizens in 10^3
	 * @param economy The strength of the economy
	 * @param government The stability of government
	 * @param hygiene Hygiene standards of city
	 * @param awareness The awareness towards viruses of the citizens
	 */
	public City(String name, double x, double y, HashSet<City> connections, int population,
			Scale economy, Scale government, Scale hygiene, Scale awareness) {
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
	}

	/**
	 * 
	 * @return Name of the city
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The y-coordinate of city in cartesian. equivalent to latitude
	 */
	public double getY() {
		return y;
	}

	/**
	 * 
	 * @return The x-coordinate of city in cartesian. equivalent to longitude
	 */
	public double getX() {
		return x;
	}

	/**
	 * 
	 * @return Set of all cities this one is connected to.
	 */
	public HashSet<City> getConnections() {
		return connections;
	}

	/**
	 * 
	 * @return Num of citizens in 10^3
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * 
	 * @param population Num of citizens in 10^3
	 */
	public void setPopulation(int population) {
		this.population = population;
	}

	/**
	 * 
	 * @return The awareness towards viruses of the citizens
	 */
	public Scale getAwareness() {
		return awareness;
	}

	/**
	 * 
	 * @param awareness The awareness towards viruses of the citizens
	 */
	public void setAwareness(Scale awareness) {
		this.awareness = awareness;
	}

	/**
	 * 
	 * @return The stability of government
	 */
	public Scale getGovernment() {
		return government;
	}

	/**
	 * 
	 * @param government The stability of government
	 */
	public void setGovernment(Scale government) {
		this.government = government;
	}

	/**
	 * 
	 * @return The strength of the economy
	 */
	public Scale getEconomy() {
		return economy;
	}

	/**
	 * 
	 * @param economy The strength of the economy
	 */
	public void setEconomy(Scale economy) {
		this.economy = economy;
	}

	/**
	 * 
	 * @return Hygiene standards of city
	 */
	public Scale getHygiene() {
		return hygiene;
	}

	/**
	 * 
	 * @param hygiene Hygiene standards of city
	 */
	public void setHygiene(Scale hygiene) {
		this.hygiene = hygiene;
	}
	
	/**
	 * Adds an event to City that should take place in this city.
	 * @param event Event that takes places in city and should be added.
	 */
	@SuppressWarnings("unchecked")
	public void addEvent(Event  event) {
		if(!event.getType().isMultipleEventType())
			this.singleEvents.put(event.getType(), event);
		else {
			if(this.multipleEvents.get(event.getType()) == null) {
				this.multipleEvents.put(event.getType(), new HashSet<>());
			}
			((HashSet<Event>) this.multipleEvents.get(event.getType())).add(event);
		}
	}
	
	/**
	 * 
	 * @return The prevalence of virus in city. If no virus exists in City, 0 is returned.
	 */
	public double getPrevalance () {
		E_Outbreak outbreak = this.getOutbreak();
		return outbreak == null? 0: outbreak.getPrevalence();
	}
	
	//	Getters for Events in the City.
	/**
	 * 
	 * @return Null if City airport is not closed, AirportClosed Object otherwise
	 */
	public E_AirportClosed getAirportClosed () {
		return (E_AirportClosed) this.singleEvents.get(EventType.airportClosed);
	}
	
	/**
	 * 
	 * @return Null if City has no antivaccionationism, AntiVacc Object otherwise
	 */
	public E_AntiVacc getAntiVacc () {
		return (E_AntiVacc) this.singleEvents.get(EventType.antiVaccinationism);
	}
	
	/**
	 * 
	 * @return Null if there's no Bio Terrorism in City, BioTerror Object otherwise
	 */
	public E_BioTerror getBioTerror () {
		return (E_BioTerror) this.singleEvents.get(EventType.bioTerrorism);
	}
	
	/**
	 * 
	 * @return Set of all Connections that are closed as ConnectionClosed Events.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_ConnectionClosed> getConnectionClosed () {
		if(!this.multipleEvents.containsKey(EventType.connectionClosed)) {
			return new HashSet<>();
		}
				
		return (HashSet<E_ConnectionClosed>) this.multipleEvents.get(EventType.connectionClosed);
	} 
	
	/**
	 * 
	 * @return Set of all medication that is deployed in City
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationDeployed> getMedicationDeployed () {
		if(!this.multipleEvents.containsKey(EventType.medicationDeployed)) {
			return new HashSet<>();
		}
		
		return (HashSet<E_MedicationDeployed>) this.multipleEvents.get(EventType.medicationDeployed);
	}
	
	/**
	 * 
	 * @return Null if there is no outbreak in City. OutbreakEvent Object otherwise.
	 */
	public E_Outbreak getOutbreak () {
		return (E_Outbreak) this.singleEvents.get(EventType.outbreak);
	}
	
	/**
	 * 
	 * @return Null if City is not under quarantine, QuarantineEvent Object otherwise
	 */
	public E_Quarantine getQuarantine () {
		return (E_Quarantine) this.singleEvents.get(EventType.quarantine);
	}
	
	/**
	 * 
	 * @return Null if City has no uprising, UprisingEvent Object otherwise
	 */
	public E_Uprising getUprising () {
		return (E_Uprising) this.singleEvents.get(EventType.uprising);
	}
	
	/**
	 * 
	 * @return Set of all vaccines being deployed in city.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineDeployed> getVaccineDeployed () {
		if(!this.multipleEvents.containsKey(EventType.vaccineDeployed)) {
			return new HashSet<>();
		}
		
		return (HashSet<E_VaccineDeployed>) this.multipleEvents.get(EventType.vaccineDeployed);
	}
}
