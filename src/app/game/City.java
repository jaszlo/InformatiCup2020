package app.game;

import java.util.HashMap;
import java.util.HashSet;

public class City {

	private final String name;
	
	private final double x,y;
	
	private final HashSet<City> connections;
	private final HashMap<EventType, Event> events;
	
	private int citizens;
	
	private Scale economy, government, hygiene, awareness;
	
	public City(String name, double x, double y, HashSet<City> connections, int citizens,
			Scale economy, Scale government, Scale hygiene, Scale awareness) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.connections = connections;
		this.setCitizens(citizens);
		this.setEconomy(economy);
		this.setGovernment(government);
		this.setHygiene(hygiene);
		this.setAwareness(awareness);
		this.events = new HashMap<>();
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

	public int getCitizens() {
		return citizens;
	}

	public void setCitizens(int citizens) {
		this.citizens = citizens;
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
	
	public void addEvent(Event event) {
		this.events.put(event.getType(), event);
	}
	
	@Deprecated
	public HashSet<Event> getEventsAsSet() {
		return new HashSet<>(this.events.values());
	}
	
	public double getPrevalance () {
		E_Outbreak outbreak = this.getOutbreak();
		return outbreak == null? 0: outbreak.getPrevalence();
	}
	
	///	Getters for Events in the City.
	public E_AirportClosed getAirportClosed () {
		return (E_AirportClosed) this.events.get(EventType.airportClosed);
	}
	
	public E_AntiVacc getAntiVacc () {
		return (E_AntiVacc) this.events.get(EventType.antiVaccinationism);
	}
	
	public E_BioTerror getBioTerror () {
		return (E_BioTerror) this.events.get(EventType.bioTerrorism);
	}
	
	public E_ConnectionClosed getConnectionClosed () {
		return (E_ConnectionClosed)  this.events.get(EventType.connectionClosed);
	}
	
	public E_MedicationDeployed getMedicationDeployed () {
		return (E_MedicationDeployed) this.events.get(EventType.medicationDeployed);
	}
	
	public E_Outbreak getOutbreak () {
		return (E_Outbreak) this.events.get(EventType.outbreak);
	}
	
	public E_Quarantine getQuarantine () {
		return (E_Quarantine) this.events.get(EventType.quarantine);
	}
	
	public E_Uprising getUprising () {
		return (E_Uprising) this.events.get(EventType.uprising);
	}
	
	public E_VaccineDeployed getVaccineDeployed () {
		return (E_VaccineDeployed) this.events.get(EventType.vaccineDeployed);
	} 
}
