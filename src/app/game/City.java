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

	public String getName() {
		return name;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public HashSet<City> getConnections() {
		return connections;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public Scale getAwareness() {
		return awareness;
	}

	public void setAwareness(Scale awareness) {
		this.awareness = awareness;
	}

	public Scale getGovernment() {
		return government;
	}

	public void setGovernment(Scale government) {
		this.government = government;
	}

	public Scale getEconomy() {
		return economy;
	}

	public void setEconomy(Scale economy) {
		this.economy = economy;
	}

	public Scale getHygiene() {
		return hygiene;
	}

	public void setHygiene(Scale hygiene) {
		this.hygiene = hygiene;
	}
	
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
	
	public double getPrevalance () {
		E_Outbreak outbreak = this.getOutbreak();
		return outbreak == null? 0: outbreak.getPrevalence();
	}
	
	///	Getters for Events in the City.
	public E_AirportClosed getAirportClosed () {
		return (E_AirportClosed) this.singleEvents.get(EventType.airportClosed);
	}
	
	public E_AntiVacc getAntiVacc () {
		return (E_AntiVacc) this.singleEvents.get(EventType.antiVaccinationism);
	}
	
	public E_BioTerror getBioTerror () {
		return (E_BioTerror) this.singleEvents.get(EventType.bioTerrorism);
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<E_ConnectionClosed> getConnectionClosed () {
		if(!this.multipleEvents.containsKey(EventType.connectionClosed)) {
			return new HashSet<>();
		}
				
		return (HashSet<E_ConnectionClosed>) this.multipleEvents.get(EventType.connectionClosed);
	} 
	
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationDeployed> getMedicationDeployed () {
		if(!this.multipleEvents.containsKey(EventType.medicationDeployed)) {
			return new HashSet<>();
		}
		
		return (HashSet<E_MedicationDeployed>) this.multipleEvents.get(EventType.medicationDeployed);
	}
	
	public E_Outbreak getOutbreak () {
		return (E_Outbreak) this.singleEvents.get(EventType.outbreak);
	}
	
	public E_Quarantine getQuarantine () {
		return (E_Quarantine) this.singleEvents.get(EventType.quarantine);
	}
	
	public E_Uprising getUprising () {
		return (E_Uprising) this.singleEvents.get(EventType.uprising);
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineDeployed> getVaccineDeployed () {
		if(!this.multipleEvents.containsKey(EventType.vaccineDeployed)) {
			return new HashSet<>();
		}
		
		return (HashSet<E_VaccineDeployed>) this.multipleEvents.get(EventType.vaccineDeployed);
	}
}
