package app.game;

import java.util.HashMap;
import java.util.HashSet;

public class City {

	private final String name;
	
	private final double x,y;
	
	private final HashSet<City> connections;
	private final HashMap<Class<?>, Event> events;
	
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
		this.events.put(event.getClass(), event);
	}
	
	@SuppressWarnings("unchecked")
	<A extends Event> A getEvent(A a) {
		return (A) events.get(a.getClass());
	}
	
}
