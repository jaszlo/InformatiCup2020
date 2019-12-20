package app.game;

import java.util.Collection;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import app.game.events.E_AirportClosed;
import app.game.events.E_AntiVacc;
import app.game.events.E_BioTerror;
import app.game.events.E_ConnectionClosed;
import app.game.events.E_MedicationAvailable;
import app.game.events.E_MedicationDeployed;
import app.game.events.E_MedicationInDevelopment;
import app.game.events.E_Outbreak;
import app.game.events.E_PathogenEncounter;
import app.game.events.E_Quarantine;
import app.game.events.E_Uprising;
import app.game.events.E_VaccineAvailable;
import app.game.events.E_VaccineDeployed;
import app.game.events.E_VaccineInDevelopment;
import app.game.events.Event;
import app.game.events.EventType;

public class Game {

	// General
	private final HashMap<String, City> cities = new HashMap<String, City>();
	private final HashMap<String, Pathogen> pathogenes = new HashMap<String, Pathogen>();
	// private final HashMap<City,HashSet<Event>> eventsByCity = new
	// HashMap<City,HashSet<Event>>();
	private final HashMap<EventType, HashSet<? extends Event>> events = new HashMap<>(); // Eventtypen nach Namen

	// A map with all pathogenes we want to ignore in out heueristic
	private HashMap<Pathogen, Boolean> ignoredPathogenes = new HashMap<>();

	private int ecoCrisisStart = -1, panicStart = -1;

	private int initialPopulation = -1; // -1 if not known

	private int population;

	private int round;

	private int points;

	private String outcome;

	/**
	 * Creates a game object
	 * 
	 * @param game The String representing the game in UTF-8 format.
	 */
	public Game(String game) {
		parseGame(game);
		this.getPathogens().stream().forEach(p -> ignorePathogenThisRound(p));
	}

	/**
	 * 
	 * @return String An overview of the game's current state as a String.
	 */
	public String gameInformation() {
		String game = "";
		for (HashSet<? extends Event> eventType : events.values()) {
			for (Event event : eventType)
				game += event + "\n";
		}
		game += "Points: " + getPoints() + "\nOutcome: " + getOutcome() + "\nRound:" + getRound()
				+ "\ninitial Population:" + getInitialPopulation() + "\nPopulation:" + getPopulation() + "\n"
				+ "Economic crisis since: " + ecoCrisisStart + "\nLarge Scale Panic since: " + panicStart + "\n";
		return game;
	}

	private Pathogen parsePathogen(JSONObject pathogen) {
		String name = (String) pathogen.get("name");
		if (this.getPathogen(name) != null)
			return this.getPathogen(name);
		else {
			Scale infectivity = Scale.parse((String) pathogen.get("infectivity"));
			Scale mobility = Scale.parse((String) pathogen.get("mobility"));
			Scale duration = Scale.parse((String) pathogen.get("duration"));
			Scale lethality = Scale.parse((String) pathogen.get("lethality"));
			Pathogen v = new Pathogen(name, infectivity, mobility, duration, lethality);
			pathogenes.put(name, v);
			return v;
		}
	}

	private void parseEvent(JSONObject event, City city) {
		String type = (String) event.get("type");
		if (type.equals("outbreak")) { // stadt
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			double prevalence = Double.parseDouble(event.get("prevalence").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_Outbreak e = new E_Outbreak(city, sinceRound, pathogen, prevalence);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("bioTerrorism")) { // stadt
			int sinceRound = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_BioTerror e = new E_BioTerror(city, sinceRound, pathogen);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("antiVaccinationism")) { // stadt
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			E_AntiVacc e = new E_AntiVacc(city, sinceRound);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("pathogenEncountered")) { // global
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_PathogenEncounter e = new E_PathogenEncounter(round, pathogen);
			addToGeneralEventMap(e);
		} else if (type.equals("largeScalePanic")) { // global
			panicStart = Integer.parseInt(event.get("sinceRound").toString());
		} else if (type.equals("economicCrisis")) { // global
			ecoCrisisStart = Integer.parseInt(event.get("sinceRound").toString());
		} else if (type.equals("uprising")) { // stadt
			int round = Integer.parseInt(event.get("sinceRound").toString());
			int participants = Integer.parseInt(event.get("participants").toString());
			E_Uprising e = new E_Uprising(city, round, participants);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("quarantine")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			E_Quarantine e = new E_Quarantine(until, since, city);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("vaccineInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_VaccineInDevelopment e = new E_VaccineInDevelopment(since, until, pathogen);
			addToGeneralEventMap(e);
		} else if (type.equals("vaccineAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_VaccineAvailable e = new E_VaccineAvailable(since, pathogen);
			addToGeneralEventMap(e);
		} else if (type.equals("medicationInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_MedicationInDevelopment e = new E_MedicationInDevelopment(since, until, pathogen);
			addToGeneralEventMap(e);
		} else if (type.equals("medicationAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_MedicationAvailable e = new E_MedicationAvailable(since, pathogen);
			addToGeneralEventMap(e);
			;
		} else if (type.equals("connectionClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			City to = getCities().get((String) event.get("city"));
			E_ConnectionClosed e = new E_ConnectionClosed(since, until, city, to);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("airportClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			E_AirportClosed e = new E_AirportClosed(since, until, city);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("medicationDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_MedicationDeployed e = new E_MedicationDeployed(round, pathogen, city);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else if (type.equals("vaccineDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			E_VaccineDeployed e = new E_VaccineDeployed(round, pathogen, city);
			addToGeneralEventMap(e);
			addEventToCity(e, city);
		} else {
			System.out.println(event + "  NOT IMPLEMENTED");
			System.exit(0);
		}
	}

	// helper method to put an Event into the events map
	@SuppressWarnings("unchecked")
	private void addToGeneralEventMap(Event event) {
		String name = event.getName();
		EventType type = Enum.valueOf(EventType.class, name);
		if (events.containsKey(type)) {
			((HashSet<Event>) events.get(type)).add(event);
		} else {
			HashSet<Event> eventType = new HashSet<Event>();
			eventType.add(event);
			events.put(type, eventType);
		}
	}

	// helper method to add an event to a city
	private void addEventToCity(Event event, City city) {
		city.addEvent(event);
	}

	private void parseGame(String game) {
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(game);
			// parse general information
			round = Integer.parseInt(obj.get("round").toString());
			outcome = (String) obj.get("outcome");
			points = Integer.parseInt(obj.get("points").toString());

			JSONObject cities = (JSONObject) obj.get("cities");
			int totalPop = 0;
			// parse Cities
			for (Object o : cities.values()) {
				JSONObject city = (JSONObject) o;
				Scale government = Scale.parse((String) city.get("government"));
				Scale awareness = Scale.parse((String) city.get("awareness"));
				Scale hygiene = Scale.parse((String) city.get("hygiene"));
				Scale economy = Scale.parse((String) city.get("economy"));
				double y = Double.parseDouble(city.get("latitude").toString());
				double x = Double.parseDouble(city.get("longitude").toString());
				String name = (String) city.get("name");
				int pop = (int) ((long) city.get("population"));
				totalPop += pop;

				City c = new City(name, x, y, new HashSet<City>(), pop, economy, government, hygiene, awareness);
				this.cities.put(name, c);
			}
			// parse City connections
			for (Object o : cities.values()) {
				JSONObject city = (JSONObject) o;
				City source = getCities().get(city.get("name"));
				JSONArray arr = (JSONArray) city.get("connections");
				for (Object connection : arr) {
					City sink = getCities().get(connection);
					source.getConnections().add(sink);
				}
				JSONArray cityEvents = (JSONArray) city.get("events");
				if (cityEvents != null) {
					for (Object event : cityEvents) {
						parseEvent((JSONObject) event, source);
					}
				}
			}
			// Set, if possible, the initial population. Only possible at round 1.
			population = totalPop;
			if (getRound() == 1)
				initialPopulation = getPopulation();
			else
				initialPopulation = -1;
			// Parse non city-specific Events
			JSONArray globalEvents = (JSONArray) obj.get("events"); // parse global events
			if (globalEvents != null) {
				for (Object event : globalEvents)
					parseEvent((JSONObject) event, null);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param cityName
	 * @return the City with the given name.
	 */
	public City getCity (String cityName) {
		return this.cities.get(cityName);
	}
	
	/**
	 * 
	 * @return A Map of Cities in the game. The Key is the unique name of the City.
	 */
	public HashMap<String, City> getCities() {
		return cities;
	}

	/**
	 * 
	 * @return The current round the game is in.
	 */
	public int getRound() {
		return round;
	}

	/**
	 * 
	 * @return The initial population of the game. -1 if not known.
	 */
	public int getInitialPopulation() {
		return initialPopulation;
	}

	/**
	 * 
	 * @return The current total population. Equal to the sum of population of all
	 *         cities.
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * 
	 * @return A Set of all anti-vaccionationism events
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_AntiVacc> getAntiVaccEvents() {
		if (events.containsKey(EventType.antiVaccinationism))
			return (HashSet<E_AntiVacc>) events.get(EventType.antiVaccinationism);
		else
			return new HashSet<E_AntiVacc>();
	}

	/**
	 * 
	 * @return A Set of all the bio-terrorism taking place
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_BioTerror> getBioTerrorEvents() {
		if (events.containsKey(EventType.bioTerrorism))
			return (HashSet<E_BioTerror>) events.get(EventType.bioTerrorism);
		else
			return new HashSet<E_BioTerror>();
	}

	/**
	 * 
	 * @return A set of all pathogen-outbreaks
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_Outbreak> getOutbreakEvents() {
		if (events.containsKey(EventType.outbreak))
			return (HashSet<E_Outbreak>) events.get(EventType.outbreak);
		else
			return new HashSet<E_Outbreak>();
	}

	/**
	 * 
	 * @return The set of all uprisings taking place
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_Uprising> getUprisingEvents() {
		if (events.containsKey(EventType.uprising))
			return (HashSet<E_Uprising>) events.get(EventType.uprising);
		else
			return new HashSet<E_Uprising>();
	}

	/**
	 * 
	 * @return A set of all the pathogens encountered
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_PathogenEncounter> getPathEncounterEvents() {
		if (events.containsKey(EventType.pathogenEncountered))
			return (HashSet<E_PathogenEncounter>) events.get(EventType.pathogenEncountered);
		else
			return new HashSet<E_PathogenEncounter>();
	}

	/**
	 * 
	 * @return A set of all Quarantines
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_Quarantine> getQuarantineEvents() {
		if (events.containsKey(EventType.quarantine))
			return (HashSet<E_Quarantine>) events.get(EventType.quarantine);
		else
			return new HashSet<E_Quarantine>();
	}

	/**
	 * 
	 * @return A set of all vaccines currently in development
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineInDevelopment> getVaccDevEvents() {
		if (events.containsKey(EventType.vaccineInDevelopment))
			return (HashSet<E_VaccineInDevelopment>) events.get(EventType.vaccineInDevelopment);
		else
			return new HashSet<E_VaccineInDevelopment>();
	}

	/**
	 * 
	 * @return A set of all available vaccines
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineAvailable> getVaccAvailableEvents() {
		if (events.containsKey(EventType.vaccineAvailable))
			return (HashSet<E_VaccineAvailable>) events.get(EventType.vaccineAvailable);
		else
			return new HashSet<E_VaccineAvailable>();
	}

	/**
	 * 
	 * @return A set of all medication currently in development
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationInDevelopment> getMedDevEvents() {
		if (events.containsKey(EventType.medicationInDevelopment))
			return (HashSet<E_MedicationInDevelopment>) events.get(EventType.medicationInDevelopment);
		else
			return new HashSet<E_MedicationInDevelopment>();
	}

	/**
	 * 
	 * @return A set of all medication available.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationAvailable> getMedAvailableEvents() {
		if (events.containsKey(EventType.medicationAvailable))
			return (HashSet<E_MedicationAvailable>) events.get(EventType.medicationAvailable);
		else
			return new HashSet<E_MedicationAvailable>();
	}

	/**
	 * 
	 * @return A set of all connections closed.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_ConnectionClosed> getConnClosedEvents() {
		if (events.containsKey(EventType.connectionClosed))
			return (HashSet<E_ConnectionClosed>) events.get(EventType.connectionClosed);
		else
			return new HashSet<E_ConnectionClosed>();
	}

	/**
	 * 
	 * @return A set of all airports currently closed.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_AirportClosed> getAirportClosedEvents() {
		if (events.containsKey(EventType.airportClosed))
			return (HashSet<E_AirportClosed>) events.get(EventType.airportClosed);
		else
			return new HashSet<E_AirportClosed>();
	}

	/**
	 * 
	 * @return A set of all cities a certain medication is being deployed in.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_MedicationDeployed> getMedDeployedEvents() {
		if (events.containsKey(EventType.medicationDeployed))
			return (HashSet<E_MedicationDeployed>) events.get(EventType.medicationDeployed);
		else
			return new HashSet<E_MedicationDeployed>();
	}

	/**
	 * 
	 * @return A set of all cities a certain vaccine is being deployed in.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<E_VaccineDeployed> getVaccDeployedEvents() {
		if (events.containsKey(EventType.vaccineDeployed))
			return (HashSet<E_VaccineDeployed>) events.get(EventType.vaccineDeployed);
		else
			return new HashSet<E_VaccineDeployed>();
	}

	/**
	 * 
	 * @return The outcome of the game as String. This can either be 'loss', 'win'
	 *         or 'pending'
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * 
	 * @return The round the large scale panic started. If it hasn't started yet, -1
	 *         is returned.
	 */
	public int getPanicStart() {
		return panicStart;
	}

	/**
	 * 
	 * @return The round the economic crisis has started. If it hasn't started yet,
	 *         -1 is returned.
	 */
	public int getEcoCrisisStart() {
		return ecoCrisisStart;
	}

	/**
	 * 
	 * @return The amount of points available for spending on actions.
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Returns the pathogen with the given name. If the name does not match any pathogen in
	 * the game, null is returned.
	 * 
	 * @param name Name of the pathogen.
	 * @return Pathogen with the given name.
	 */
	public Pathogen getPathogen(String name) {
		return this.pathogenes.get(name);
	}
	
	/**
	 * Returns all pathogens in the game.
	 * 
	 * @return All pathogens.
	 */
	public Collection<Pathogen> getPathogens() {
		return this.pathogenes.values();
	}

	public boolean ignorePathogenThisRound(Pathogen pathogen) {

		if (pathogen == null) {
			return false;
		}

		if (this.ignoredPathogenes.containsKey(pathogen)) {
			return this.ignoredPathogenes.get(pathogen);
		}

		Optional<E_PathogenEncounter> encounter = this.getPathEncounterEvents().stream()
				.filter(e -> e.getPathogen() == pathogen).findAny();

		// Ignore useless bio terror.
		if (!encounter.isPresent()) {
			this.ignoredPathogenes.put(pathogen, true);
			return true;
		}

		// Check if a pathogen is no longer active by checking if it has been there for
		// over 10 rounds and has not infected more than 10% of all citie's population
		// on average or has not infected more than 5 cities.
		// This is to guess stateless that a pathogen is old and no longer a threat.
		boolean result = (this.getRound() - encounter.get().getRound() >= 10) && (this.getOutbreakEvents().stream()
				.filter(e -> e.getPathogen() == pathogen).mapToDouble(e -> e.getPrevalence()).average()
				.orElseGet(() -> 0) <= 0.10
				|| this.getOutbreakEvents().stream().filter(e -> e.getPathogen() == pathogen).count() <= 5);
		
		this.ignoredPathogenes.put(pathogen, result);
				
		return result;
	}
}
