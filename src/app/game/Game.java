package app.game;

import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Game {
	
	//General
	private final HashMap<String,City> cities = new HashMap<String,City>();
	private final HashMap<String,Virus> viruses = new HashMap<String,Virus>();
	private final HashMap<City,HashSet<Event>> eventsByCity = new HashMap<City,HashSet<Event>>();
	private final HashMap<EventType,HashSet<? extends Event>> events = new HashMap<EventType,HashSet<? extends Event>>();	//Eventtypen nach Namen
	
	private int ecoCrisisStart=-1, panicStart=-1;

	private int initialPopulation = -1; //-1 if not known
	
	private int population;
	
	private int round;
	
	private int points;
	
	private String outcome;
	
	public Game(String game) {
		parseGame(game);
		//Main.solve(this);
	}
	
	public String gameInformation() {
		String game = "";
		for(HashSet<? extends Event> eventType : events.values()) {
			for(Event event : eventType)
				game+=event+"\n";
		}
		game+="Points: "+getPoints()+"\nOutcome: "+getOutcome()+"\nRound:"+getRound()+"\ninitial Population:"+getInitialPopulation()+
				"\nPopulation:"+getPopulation()+"\n"+"Economic crisis since: "+ecoCrisisStart+
				"\nLarge Scale Panic since: "+panicStart+"\n";
		return game;
	}
	
	private Virus parseVirus(JSONObject virus) {
		String name = (String) virus.get("name");
		if(getViruses().containsKey(name))
			return getViruses().get(name);
		else {
			Scale infectivity = Scale.parse((String)virus.get("infectivity"));
			Scale mobility = Scale.parse((String)virus.get("mobility"));
			Scale duration = Scale.parse((String)virus.get("duration"));
			Scale lethality = Scale.parse((String)virus.get("lethality"));
			Virus v = new Virus(name, infectivity, mobility, duration, lethality);
			viruses.put(name,v);
			return v;
		}
	}
	
	private void parseEvent(JSONObject event, City city) {
		String type = (String) event.get("type");
		if(type.equals("outbreak")) { //stadt
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			double prevalence = Double.parseDouble(event.get("prevalence").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_Outbreak e = new E_Outbreak(city, sinceRound, virus, prevalence);
			addToGeneralEventMap(e);
			addToCityEventMap(e,city);
		}else if(type.equals("bioTerrorism")){ //stadt
			int sinceRound = Integer.parseInt(event.get("round").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_BioTerror e = new E_BioTerror(city, sinceRound, virus);
			addToGeneralEventMap(e);
			addToCityEventMap(e,city);
		}else if(type.equals("antiVaccinationism")) { //stadt
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			E_AntiVacc e = new E_AntiVacc(city, sinceRound);
			addToGeneralEventMap(e);
			addToCityEventMap(e,city);
		}else if(type.equals("pathogenEncountered")) { //global
			int round = Integer.parseInt(event.get("round").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_PathogenEncounter e = new E_PathogenEncounter(round, virus);
			addToGeneralEventMap(e);
		}else if(type.equals("largeScalePanic")) { //global 
			panicStart = Integer.parseInt(event.get("sinceRound").toString());
		} else if(type.equals("economicCrisis")) { //global
			ecoCrisisStart = Integer.parseInt(event.get("sinceRound").toString());
		}else if(type.equals("uprising")) { //stadt
			int round = Integer.parseInt(event.get("sinceRound").toString());
			int participants = Integer.parseInt(event.get("participants").toString());
			E_Uprising e = new E_Uprising(city, round, participants);
			addToGeneralEventMap(e);
			addToCityEventMap(e,city);
		} else if(type.equals("quarantine")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			E_Quarantine e = new E_Quarantine(until, since,city);
			addToGeneralEventMap(e);
			addToCityEventMap(e, city);
		} else if(type.equals("vaccineInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_VaccineInDevelopment e = new E_VaccineInDevelopment(since, until, virus);
			addToGeneralEventMap(e);
		} else if(type.equals("vaccineAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_VaccineAvailable e = new E_VaccineAvailable(since, virus);
			addToGeneralEventMap(e);
		} else if(type.equals("medicationInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_MedicationInDevelopment e = new E_MedicationInDevelopment(since, until, virus);
			addToGeneralEventMap(e);
		} else if(type.equals("medicationAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_MedicationAvailable e = new E_MedicationAvailable(since, virus);
			addToGeneralEventMap(e);;
		} else if(type.equals("connectionClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			City to =getCities().get((String)event.get("city"));
			E_ConnectionClosed e = new E_ConnectionClosed(since, until, city, to);
			addToGeneralEventMap(e);
			addToCityEventMap(e, city);
		} else if(type.equals("airportClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			E_AirportClosed e = new E_AirportClosed(since, until, city);
			addToGeneralEventMap(e);
			addToCityEventMap(e, city);
		} else if(type.equals("medicationDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_MedicationDeployed e = new E_MedicationDeployed(round, virus, city);
			addToGeneralEventMap(e);
			addToCityEventMap(e, city);
		} else if(type.equals("vaccineDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Virus virus = parseVirus((JSONObject)event.get("pathogen"));
			E_VaccineDeployed e = new E_VaccineDeployed(round, virus, city);
			addToGeneralEventMap(e);
			addToCityEventMap(e, city);
		}
		else {
			System.out.println(event+"  NOT IMPLEMENTED");
			System.exit(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addToGeneralEventMap(Event event) {
		String name = event.getName();
		EventType type = Enum.valueOf(EventType.class, name);
		if(events.containsKey(type)) {
			((HashSet<Event>) events.get(type)).add(event);
		}
		else {
			HashSet<Event> eventType = new HashSet<Event>();
			eventType.add(event);
			events.put(type, eventType);
		}
	}
	
	private void addToCityEventMap(Event event, City city) {
		if(eventsByCity.containsKey(city)){
			eventsByCity.get(city).add(event);
		}else {
			HashSet<Event> set = new HashSet<Event>();
			set.add(event);
			eventsByCity.put(city, set);
		}
	}
	
	private void parseGame(String game) {
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(game);
			round = Integer.parseInt(obj.get("round").toString());
			outcome = (String) obj.get("outcome");
			points = Integer.parseInt(obj.get("points").toString());
			
			JSONObject cities = (JSONObject) obj.get("cities");
			int totalPop = 0;
			for(Object o : cities.values()) { //Parse all cities
				JSONObject city = (JSONObject) o;
				Scale government = Scale.parse((String)city.get("government"));
				Scale awareness = Scale.parse((String)city.get("awareness"));
				Scale hygiene = Scale.parse((String)city.get("hygiene"));
				Scale economy = Scale.parse((String)city.get("economy"));
				double y =	Double.parseDouble(city.get("latitude").toString());
				double x = Double.parseDouble(city.get("longitude").toString());
				String name = (String) city.get("name");
				int pop = (int) ((long)city.get("population"));
				totalPop+=pop;
				
				//adding prevelance of a city to the city.
				double prevalance = 0;
				//Grab city events and not all because we only want outbreaks that happened in this city.
				JSONArray cityEvents = (JSONArray) city.get("events");
				if (!(cityEvents == null)) {
					for (Object event : cityEvents) {
						JSONObject jsonEvent = (JSONObject) event;
						String type = (String) jsonEvent.get("type");
						if (type.equals("outbreak")) {
							prevalance = Double.parseDouble(jsonEvent.get("prevalence").toString());
							// DEBUG PRINT: System.out.println(prevalance);
						}
					}
				}
				City c = new City(name, x, y, new HashSet<City>(), pop, economy, government, hygiene, awareness);
				c.setPrevalance(prevalance);
				this.cities.put(name, c);
				//End of parsing the prevalance and adding it to the cities.
			}
			for(Object o : cities.values()) { //Parse city connections				
				JSONObject city = (JSONObject) o;
				City source = getCities().get(city.get("name"));
				JSONArray arr = (JSONArray) city.get("connections");
				for(Object connection : arr) {
					City sink = getCities().get(connection);
					source.getConnections().add(sink);
				}
				JSONArray cityEvents = (JSONArray) city.get("events");
				if(cityEvents != null) {
					for(Object event : cityEvents) {
						parseEvent((JSONObject) event,source);
					}
				}
			}
			population = totalPop;
			if(getRound() == 1)
				initialPopulation = getPopulation();
			else
				initialPopulation = -1;
			
			JSONArray globalEvents = (JSONArray) obj.get("events"); //parse global events
			if(globalEvents != null) {
				for(Object event : globalEvents)
					parseEvent((JSONObject) event,null);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public HashSet<Event> getEventsByCity (City city) {
		HashSet<Event> e = this.eventsByCity.get(city);
		return e == null ? new HashSet<Event>(): e; 
	}
	
	public HashMap<String,City> getCities() {
		return cities;
	}

	public int getRound() {
		return round;
	}

	public int getInitialPopulation() {
		return initialPopulation;
	}

	public int getPopulation() {
		return population;
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_AntiVacc> getAntiVaccEvents() {
		if(events.containsKey(EventType.antiVaccinationism))
			return (HashSet<E_AntiVacc>) events.get(EventType.antiVaccinationism);
		else
			return new HashSet<E_AntiVacc>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_BioTerror> getBioTerrorEvents() {
		if(events.containsKey(EventType.bioTerrorism))
			return (HashSet<E_BioTerror>) events.get(EventType.bioTerrorism);
		else
			return new HashSet<E_BioTerror>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_Outbreak> getOutbreakEvents() {
		if(events.containsKey(EventType.outbreak))
			return (HashSet<E_Outbreak>) events.get(EventType.outbreak);
		else
			return new HashSet<E_Outbreak>();
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<E_Uprising> getUprisingEvents() {
		if(events.containsKey(EventType.uprising))
			return (HashSet<E_Uprising>) events.get(EventType.uprising);
		else
			return new HashSet<E_Uprising>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_PathogenEncounter> getPathEncounterEvents() {
		if(events.containsKey(EventType.pathogenEncountered))
			return (HashSet<E_PathogenEncounter>) events.get(EventType.pathogenEncountered);
		else
			return new HashSet<E_PathogenEncounter>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_Quarantine> getQuarantineEvents() {
		if(events.containsKey(EventType.quarantine))
			return (HashSet<E_Quarantine>) events.get(EventType.quarantine);
		else
			return new HashSet<E_Quarantine>();
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineInDevelopment> getVaccDevEvents() {
		if(events.containsKey(EventType.vaccineInDevelopment))
			return (HashSet<E_VaccineInDevelopment>) events.get(EventType.vaccineInDevelopment);
		else
			return new HashSet<E_VaccineInDevelopment>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineAvailable> getVaccAvailableEvents() {
		if(events.containsKey(EventType.vaccineAvailable))
			return (HashSet<E_VaccineAvailable>) events.get(EventType.vaccineAvailable);
		else
			return new HashSet<E_VaccineAvailable>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationInDevelopment> getMedDevEvents() {
		if(events.containsKey(EventType.medicationInDevelopment))
			return (HashSet<E_MedicationInDevelopment>) events.get(EventType.medicationInDevelopment);
		else
			return new HashSet<E_MedicationInDevelopment>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationAvailable> getMedAvailableEvents() {
		if(events.containsKey(EventType.medicationAvailable))
			return (HashSet<E_MedicationAvailable>) events.get(EventType.medicationAvailable);
		else
			return new HashSet<E_MedicationAvailable>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_ConnectionClosed> getConnClosedEvents() {
		if(events.containsKey(EventType.connectionClosed))
			return (HashSet<E_ConnectionClosed>) events.get(EventType.connectionClosed);
		else
			return new HashSet<E_ConnectionClosed>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_AirportClosed> getAirportClosedEvents() {
		if(events.containsKey(EventType.airportClosed))
			return (HashSet<E_AirportClosed>) events.get(EventType.airportClosed);
		else
			return new HashSet<E_AirportClosed>();
	}
	
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationDeployed> getMedDeployedEvents() {
		if(events.containsKey(EventType.medicationDeployed))
			return (HashSet<E_MedicationDeployed>) events.get(EventType.medicationDeployed);
		else
			return new HashSet<E_MedicationDeployed>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineDeployed> getVaccDeployedEvents() {
		if(events.containsKey(EventType.vaccineDeployed))
			return (HashSet<E_VaccineDeployed>) events.get(EventType.vaccineDeployed);
		else
			return new HashSet<E_VaccineDeployed>();
	}
		

	public String getOutcome() {
		return outcome;
	}

	public int getPanicStart() {
		return panicStart;
	}
	
	public int getEcoCrisisStart() {
		return ecoCrisisStart;
	}

	public int getPoints() {
		return points;
	}
	
	public HashMap<String,Virus> getViruses() {
		return viruses;
	}
	
	public boolean cityContains (City city, EventType type) {
		return this.getEventsByCity(city).stream().anyMatch(event -> event.getType() == type);
	}
}
