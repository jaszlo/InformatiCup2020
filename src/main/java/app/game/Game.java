package app.game;

import java.util.Collection;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import app.game.events.Event;
import app.game.events.EventType;

/**
 * Class to represent the state of the game and stores all necessary
 * information.
 */
public class Game {

	//Map of all cities by their name
	private final Map<String, City> cities = new HashMap<>();
	//Map of all encountered pathogens by their name
	private final Map<String, Pathogen> pathogenes = new HashMap<>();
	//Map of all events grouped by their type
	private final Map<EventType, Set<Event>> events = new HashMap<>(); // Events by type
	// A map with all pathogens we want to ignore in our heuristic
	private Map<Pathogen, Boolean> ignoredPathogens = new HashMap<>();
	
	///Basic attributes - detailed description in the corresponding getter		
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
	 * Parses a given JSONObject to an event. The event is added to the event map
	 * and if related to a city, the event is also added to be city specific event map.
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
			City to = getCity((String) event.get("city"));
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
				events.put(type, new HashSet<>());
			}
		}
	}

	/**
	 * Helper method to put an event into the event map.
	 * 
	 * @param event Event to add.
	 */
	private void addToGeneralEventMap(Event event) {
		events.get(event.getType()).add(event);
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

				City c = new City(name, x, y, new HashSet<>(), pop, economy, government, hygiene, awareness);
				this.cities.put(name, c);
			}

			// Parse city connections
			for (Object o : cities.values()) {
				JSONObject city = (JSONObject) o;
				City source = getCity((String) city.get("name"));
				JSONArray arr = (JSONArray) city.get("connections");

				for (Object connection : arr) {
					City sink = getCity((String) connection);
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
	 * @param name The name of the City.
	 * @return the City with the given name.
	 */
	public City getCity(String name) {
		return this.cities.get(name);
	}

	/**
	 * @return A Collection of all cities in the game.
	 */
	public Collection<City> getCities() {
		return cities.values();
	}

	/**
	 * @return The current round of the game.
	 */
	public int getRound() {
		return round;
	}

	/**
	 * @return The initial population of the game. -1 if not known.
	 */
	public int getInitialPopulation() {
		return initialPopulation;
	}

	/**
	 * @return The current total population. Equal to the sum of the population of
	 *         all cities.
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * @return A set containing all anti-vaccionationism events.
	 */
	public Set<Event> getAntiVaccEvents() {
		return events.get(EventType.antiVaccinationism);
	}

	/**
	 * @return A set containing all bio-terrorism events.
	 */
	public Set<Event> getBioTerrorEvents() {
		return events.get(EventType.bioTerrorism);
	}

	/**
	 * @return A set containing all pathogen outbreaks.
	 */
	public Set<Event> getOutbreakEvents() {
		return events.get(EventType.outbreak);

	}

	/**
	 * @return A set containing all uprising-events.
	 */
	public Set<Event> getUprisingEvents() {
		return events.get(EventType.uprising);
	}

	/**
	 * @return A set containing all pathogens that have appeared in the game.
	 */
	public Set<Event> getPathEncounterEvents() {
		return events.get(EventType.pathogenEncountered);
	}

	/**
	 * @return A set containing all quarantine-events.
	 */
	public Set<Event> getQuarantineEvents() {
		return events.get(EventType.quarantine);
	}

	/**
	 * @return A set containing all vaccine in development events.
	 */
	public Set<Event> getVaccDevEvents() {
		return events.get(EventType.vaccineInDevelopment);
	}

	/**
	 * @return A set containing all vaccine available events.
	 */
	public Set<Event> getVaccAvailableEvents() {
		return events.get(EventType.vaccineAvailable);
	}

	/**
	 * @return A set containing all medication in development events.
	 */
	public Set<Event> getMedDevEvents() {
		return events.get(EventType.medicationInDevelopment);
	}

	/**
	 * @return A set containing all medication available events.
	 */
	public Set<Event> getMedAvailableEvents() {
		return events.get(EventType.medicationAvailable);
	}

	/**
	 * @return A set containing all connection-closed-events.
	 */
	public Set<Event> getConnClosedEvents() {
		return events.get(EventType.connectionClosed);

	}

	/**
	 * @return A set containing all airport-closed-events.
	 */
	public Set<Event> getAirportClosedEvents() {
		return events.get(EventType.airportClosed);

	}

	/**
	 * @return A set containing all medication deployed events.
	 */
	public Set<Event> getMedDeployedEvents() {
		return events.get(EventType.medicationDeployed);
	}

	/**
	 * @return A set containing all vaccine deployed events.
	 */
	public Set<Event> getVaccDeployedEvents() {
		return events.get(EventType.vaccineDeployed);
	}

	/**
	 * @return The outcome of the game as a String. Possible returns values are
	 *         'loss', 'win' or 'pending'.
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @return The round the large scale panic started. If it has not started yet, -1
	 *         is returned.
	 */
	public int getPanicStart() {
		return panicStart;
	}

	/**
	 * @return The round the economic crisis has started. If it has not started yet,
	 *         -1 is returned.
	 */
	public int getEcoCrisisStart() {
		return ecoCrisisStart;
	}

	/**
	 * @return The amount of points available for spending on actions.
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Returns the pathogen by its identifier. If the name does not match any
	 * pathogen in the game, null is returned.
	 * 
	 * @param name Name of the pathogen.
	 * @return Pathogen with the given name.
	 */
	public Pathogen getPathogen(String name) {
		return this.pathogenes.get(name);
	}

	/**
	 * Returns all pathogens that have appeared in the game so far. If none exist, an empty Collection
	 * is returned. 
	 * 
	 * @return All pathogens that have appeared in the game.
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

		Optional<Event> encounter = this.getPathEncounterEvents().stream().filter(e -> e.getPathogen() == pathogen)
				.findAny();

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
