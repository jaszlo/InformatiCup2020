package app.game;

import java.util.Collection;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import app.game.events.Event;
import app.game.events.EventType;

/**
 * Class to represent the state of the game and stores all necessary information.
 */
public class Game {

	/// General
	private final HashMap<String, City> cities = new HashMap<String, City>();
	private final HashMap<String, Pathogen> pathogenes = new HashMap<String, Pathogen>();
	private final HashMap<EventType, HashSet<? extends Event>> events = new HashMap<>(); // Eventtypen nach Namen

	/// A map with all pathogens we want to ignore in our heuristic
	private HashMap<Pathogen, Boolean> ignoredPathogens = new HashMap<>();

	private int ecoCrisisStart = -1, panicStart = -1;

	private int initialPopulation = -1; // -1 if not known

	private int population;

	private int round;

	private int points;

	private String outcome;

	/**
	 * Creates a game object.
	 * 
	 * @param game The String representing of the game in UTF-8 format.
	 */
	public Game(String game) {

		this.initGeneralEventMap();
		parseGame(game);

		// Initialize the ignored pathogens map.
		this.getPathogens().stream().forEach(p -> ignorePathogenThisRound(p));
	}

	/**
	 * Parses a given JSONObject to a pathogen. If a pathogen with the same name was
	 * already parsed, the reference to that pathogen is returned.
	 * 
	 * @param pathogen Pathogen to be parsed.
	 * @return Parsed pathogen.
	 */
	private Pathogen parsePathogen(JSONObject pathogen) {

		String name = (String) pathogen.get("name");
		if (this.getPathogen(name) != null) {
			return this.getPathogen(name);

		} else {
			Scale infectivity = Scale.parse((String) pathogen.get("infectivity"));
			Scale mobility = Scale.parse((String) pathogen.get("mobility"));
			Scale duration = Scale.parse((String) pathogen.get("duration"));
			Scale lethality = Scale.parse((String) pathogen.get("lethality"));
			Pathogen v = new Pathogen(name, infectivity, mobility, duration, lethality);
			pathogenes.put(name, v);
			return v;
		}
	}

	/**
	 * Parses a given JSONObject to an event. The event is added to the general
	 * event map and to the city map.
	 * 
	 * @param event The JSONObject to be parsed.
	 * @param city  The city in which the event takes place. If the event is not
	 *              related to a city, null is the input.
	 */
	private void parseEvent(JSONObject event, City city) {

		String type = (String) event.get("type");
		if (type.equals("outbreak")) {
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			double prevalence = Double.parseDouble(event.get("prevalence").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(sinceRound, pathogen, city, prevalence);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("bioTerrorism")) {
			int sinceRound = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.bioTerrorism, sinceRound, pathogen, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("antiVaccinationism")) {
			int sinceRound = Integer.parseInt(event.get("sinceRound").toString());
			Event e = new Event(EventType.antiVaccinationism, sinceRound, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("pathogenEncountered")) {
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.pathogenEncountered, round, pathogen);
			addToGeneralEventMap(e);

		} else if (type.equals("largeScalePanic")) {
			panicStart = Integer.parseInt(event.get("sinceRound").toString());

		} else if (type.equals("economicCrisis")) {
			ecoCrisisStart = Integer.parseInt(event.get("sinceRound").toString());

		} else if (type.equals("uprising")) {
			int round = Integer.parseInt(event.get("sinceRound").toString());
			int participants = Integer.parseInt(event.get("participants").toString());
			Event e = new Event(round, city, participants);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("quarantine")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Event e = new Event(EventType.quarantine, until, since, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("vaccineInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.vaccineInDevelopment, since, until, pathogen);
			addToGeneralEventMap(e);

		} else if (type.equals("vaccineAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.vaccineAvailable, since, pathogen);
			addToGeneralEventMap(e);

		} else if (type.equals("medicationInDevelopment")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.medicationInDevelopment, since, until, pathogen);
			addToGeneralEventMap(e);

		} else if (type.equals("medicationAvailable")) {
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.medicationAvailable, since, pathogen);
			addToGeneralEventMap(e);

		} else if (type.equals("connectionClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			City to = getCities().get((String) event.get("city"));
			Event e = new Event(since, until, city, to);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("airportClosed")) {
			int until = Integer.parseInt(event.get("untilRound").toString());
			int since = Integer.parseInt(event.get("sinceRound").toString());
			Event e = new Event(EventType.airportClosed, since, until, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("medicationDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.medicationDeployed, round, pathogen, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.equals("vaccineDeployed")) {
			int round = Integer.parseInt(event.get("round").toString());
			Pathogen pathogen = parsePathogen((JSONObject) event.get("pathogen"));
			Event e = new Event(EventType.vaccineDeployed, round, pathogen, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.contentEquals("campaignLaunched")) {
			int round = Integer.parseInt(event.get("round").toString());
			Event e = new Event(EventType.campaignLaunched, round, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.contentEquals("electionsCalled")) {
			int round = Integer.parseInt(event.get("round").toString());
			Event e = new Event(EventType.electionsCalled, round, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.contentEquals("hygienicMeasuresApplied")) {
			int round = Integer.parseInt(event.get("round").toString());
			Event e = new Event(EventType.hygienicMeasuresApplied, round, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else if (type.contentEquals("influenceExerted")) {
			int round = Integer.parseInt(event.get("round").toString());
			Event e = new Event(EventType.influenceExerted, round, city);
			addToGeneralEventMap(e);
			city.addEvent(e);

		} else {
			System.err.println(event + " IS NOT IMPLEMENTED");
		}
	}

	/**
	 * Helper method to initialize the events map.
	 */
	private void initGeneralEventMap() {

		for (EventType type : EventType.values()) {
			if (!events.containsKey(type)) {
				HashSet<Event> eventType = new HashSet<Event>();
				events.put(type, eventType);
			}
		}
	}

	/**
	 * Helper method to put an event into the event map.
	 */
	@SuppressWarnings("unchecked")
	private void addToGeneralEventMap(Event event) {

		EventType type = event.getType();
		if (events.containsKey(type)) {
			((HashSet<Event>) events.get(type)).add(event);

		} else {
			HashSet<Event> eventType = new HashSet<Event>();
			eventType.add(event);
			events.put(type, eventType);
		}
	}

	/**
	 * Parses a string into a game object.
	 * 
	 * @param game The string representing a game.
	 */
	private void parseGame(String game) {

		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(game);
			// Parse general information
			round = Integer.parseInt(obj.get("round").toString());
			outcome = (String) obj.get("outcome");
			points = Integer.parseInt(obj.get("points").toString());

			// Parse cities
			JSONObject cities = (JSONObject) obj.get("cities");
			int totalPop = 0;
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

			// Parse city connections
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
			initialPopulation = getRound() == 1 ? getPopulation() : -1;

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
	 * //TODO java doc getters
	 * 
	 * @param cityName
	 * @return the City with the given name.
	 */
	public City getCity(String cityName) {
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
	public HashSet<Event> getAntiVaccEvents() {
		return (HashSet<Event>) events.get(EventType.antiVaccinationism);
	}

	/**
	 * 
	 * @return A Set of all the bio-terrorism taking place
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getBioTerrorEvents() {
		return (HashSet<Event>) events.get(EventType.bioTerrorism);
	}

	/**
	 * 
	 * @return A set of all pathogen-outbreaks
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getOutbreakEvents() {
		return (HashSet<Event>) events.get(EventType.outbreak);

	}

	/**
	 * 
	 * @return The set of all uprisings taking place
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getUprisingEvents() {
		return (HashSet<Event>) events.get(EventType.uprising);
	}

	/**
	 * 
	 * @return A set of all the pathogens encountered
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getPathEncounterEvents() {
		return (HashSet<Event>) events.get(EventType.pathogenEncountered);
	}

	/**
	 * 
	 * @return A set of all Quarantines
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getQuarantineEvents() {
		return (HashSet<Event>) events.get(EventType.quarantine);
	}

	/**
	 * 
	 * @return A set of all vaccines currently in development
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getVaccDevEvents() {
		return (HashSet<Event>) events.get(EventType.vaccineInDevelopment);
	}

	/**
	 * 
	 * @return A set of all available vaccines
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getVaccAvailableEvents() {
		return (HashSet<Event>) events.get(EventType.vaccineAvailable);
	}

	/**
	 * 
	 * @return A set of all medication currently in development
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getMedDevEvents() {
		return (HashSet<Event>) events.get(EventType.medicationInDevelopment);
	}

	/**
	 * 
	 * @return A set of all medication available.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getMedAvailableEvents() {
		return (HashSet<Event>) events.get(EventType.medicationAvailable);
	}

	/**
	 * 
	 * @return A set of all connections closed.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getConnClosedEvents() {
		return (HashSet<Event>) events.get(EventType.connectionClosed);

	}

	/**
	 * 
	 * @return A set of all airports currently closed.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getAirportClosedEvents() {
		return (HashSet<Event>) events.get(EventType.airportClosed);

	}

	/**
	 * 
	 * @return A set of all cities a certain medication is being deployed in.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getMedDeployedEvents() {
		return (HashSet<Event>) events.get(EventType.medicationDeployed);
	}

	/**
	 * 
	 * @return A set of all cities a certain vaccine is being deployed in.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Event> getVaccDeployedEvents() {
		return (HashSet<Event>) events.get(EventType.vaccineDeployed);
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
	 * Returns the pathogen with the given name. If the name does not match any
	 * pathogen in the game, null is returned.
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

	/**
	 * Returns whether a pathogen should be ignored in the current round.
	 * 
	 * @param pathogen The pathogen to check.
	 * @return Whether the pathogen should be ignored or not. If pathogen is null,
	 *         false is returned.
	 */
	public boolean ignorePathogenThisRound(Pathogen pathogen) {

		if (pathogen == null) {
			return false;
		}

		if (this.ignoredPathogens.containsKey(pathogen)) {
			return this.ignoredPathogens.get(pathogen);
		}

		Optional<Event> encounter = this.getPathEncounterEvents().stream()
				.filter(e -> e.getPathogen() == pathogen).findAny();

		// Ignore bio terror.
		if (!encounter.isPresent()) {
			this.ignoredPathogens.put(pathogen, true);
			return true;
		}

		/*
		 * Check if a pathogen is no longer active by checking if it has been there for
		 * over 10 rounds and has not infected more than 10% of all the infected cities
		 * population on average. This is to guess stateless that a pathogen is old and
		 * no longer a threat in order to safe points.
		 */
		boolean isOld = this.getRound() - encounter.get().getRound() >= 10;
		boolean hasLessAverage = this.getOutbreakEvents().stream().filter(e -> e.getPathogen() == pathogen)
				.mapToDouble(e -> e.getPrevalence()).average().orElseGet(() -> 0) <= 0.10;
		long numberOfCities = this.getOutbreakEvents().stream().filter(e -> e.getPathogen() == pathogen).count();
		boolean hasFewCities = numberOfCities <= 10;
		boolean hasNoCity = numberOfCities <= 0;

		// If both of these are true do not ignore the pathogen, as we have enough
		// points.
		boolean enoughPoints = this.points <= 200;
		boolean hasVeryFewCities = this.getOutbreakEvents().stream().count() <= 5;
		if (enoughPoints && hasVeryFewCities) {
			return false;
		}

		boolean result = (isOld && (hasLessAverage || hasFewCities)) || hasNoCity;
		this.ignoredPathogens.put(pathogen, result);
		return result;
	}
}
