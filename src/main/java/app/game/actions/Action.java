package app.game.actions;

import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.solver.ActionHeuristic;

/**
 * Class to represent and hold information about an action that can be taken in
 * the current state of the game.
 */
public class Action {

	private final ActionType type;

	private final Game game;

	/// Parameters for the actions
	private final City city;
	private final City cityTo;
	private final Pathogen pathogen;
	private final int rounds;

	/// Heueristic parameter score
	private final double score;

	/**
	 * Creates a generic action.
	 * 
	 * @param type     The type of this action.
	 * @param game     The game for which the action will be used.
	 * @param city     The city in which the action will be used.
	 * @param cityTo   The second city to which the connection will be closed from
	 *                 the first city.
	 * @param pathogen The pathogen for which will be used.
	 * @param rounds   The amount of rounds this action will be active.
	 */
	public Action(ActionType type, Game game, City city, City cityTo, Pathogen pathogen, int rounds) {
		this.type = type;
		this.game = game;
		this.city = city;
		this.cityTo = cityTo;
		this.pathogen = pathogen;
		this.rounds = rounds;
		this.score = ActionHeuristic.getScore(this);
	}

	/**
	 * Creates an end round action.
	 * 
	 * @param game The game for which the action will be used.
	 */
	public Action(Game game) {
		this(ActionType.endRound, game, null, null, null, 0);
	}

	/**
	 * Creates a quarantine or close airport action..
	 * 
	 * @param type   The type of this action.
	 * @param game   The game for which the action will be used.
	 * @param city   The city in which the action will be used.
	 * @param rounds The amount of rounds this action will be active.
	 */
	public Action(ActionType type, Game game, City city, int rounds) {
		this(type, game, city, null, null, rounds);
	}

	/**
	 * Creates a develop vaccine or medication action.
	 * 
	 * @param type     The type of this action.
	 * @param game     The game for which the action will be used.
	 * @param pathogen The pathogen for which will be used.
	 */
	public Action(ActionType type, Game game, Pathogen pathogen) {
		this(type, game, null, null, pathogen, 0);
	}

	/**
	 * Creates a deploy vaccine or medication action.
	 * 
	 * @param type     The type of this action.
	 * @param game     The game for which the action will be used.
	 * @param city     The city in which the action will be used.
	 * @param pathogen The pathogen for which will be used.
	 */
	public Action(ActionType type, Game game, City city, Pathogen pathogen) {
		this(type, game, city, null, pathogen, 0);
	}

	/**
	 * Creates a close connection action.
	 * 
	 * @param game   The game for which the action will be used.
	 * @param city   The city in which the action will be used.
	 * @param toCity Only necessary for close connection events.
	 * @param rounds The amount of rounds this action will be active.
	 */
	public Action(Game game, City city, City toCity, int rounds) {
		this(ActionType.closeConnection, game, city, toCity, null, rounds);
	}

	/**
	 * Creates a exert influence, call elections, apply hygienic measures or launch
	 * campaign action.
	 * 
	 * @param type The type of this action.
	 * @param game The game for which the action will be used.
	 * @param city The city in which the action will be used.
	 */
	public Action(ActionType type, Game game, City city) {
		this(type, game, city, null, null, 0);
	}

	/**
	 * @return The basic information of pathogen as JSON formated string matching
	 *         the format of the GI client.
	 */
	public String toString() {

		switch (this.getType()) {
		case endRound:
			return "{\"type\": \"endRound\"}";

		case putUnderQuarantine:
			return String.format("{\"type\": \"putUnderQuarantine\", \"city\":\"%s\", \"rounds\": %d}",
					this.getCity().getName(), this.getRounds());

		case closeAirport:
			return String.format("{\"type\": \"closeAirport\", \"city\": \"%s\", \"rounds\": %d}",
					this.getCity().getName(), this.getRounds());

		case closeConnection:
			return String.format(
					"{\"type\": \"closeConnection\", \"fromCity\":\"%s\", \"toCity\": \"%s\", \"rounds\": %d}",
					this.getCity().getName(), this.getCityTo().getName(), this.getRounds());

		case developMedication:
			return String.format("{\"type\": \"developMedication\", \"pathogen\":\"%s\"}",
					this.getPathogen().getName());

		case deployMedication:
			return String.format("{\"type\": \"deployMedication\", \"pathogen\":\"%s\", \"city\": \"%s\"}",
					this.getPathogen().getName(), this.getCity().getName());

		case developVaccine:
			return String.format("{\"type\": \"developVaccine\", \"pathogen\":\"%s\"}", this.getPathogen().getName());

		case deployVaccine:
			return String.format("{\"type\": \"deployVaccine\", \"pathogen\":\"%s\", \"city\": \"%s\"}",
					this.getPathogen().getName(), this.getCity().getName());

		case exertInfluence:
			return String.format("{\"type\": \"exertInfluence\", \"city\": \"%s\"}", this.getCity().getName());

		case launchCampaign:
			return String.format("{\"type\": \"launchCampaign\", \"city\": \"%s\"}", this.getCity().getName());

		case applyHygienicMeasures:
			return String.format("{\"type\": \"applyHygienicMeasures\", \"city\":\"%s\"}", this.getCity().getName());

		case callElections:
			return String.format("{\"type\": \"callElections\", \"city\": \"%s\"}", this.getCity().getName());

		}

		return null;
	}

	/**
	 * @return The game for which the action will be used.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @return The score for this action in the action's game calculated by the
	 *         heuristic.
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @return The type of this action.
	 */
	public ActionType getType() {
		return type;
	}

	/**
	 * @return The city in which the action will be used.
	 */
	public City getCity() {
		return city;
	}

	/**
	 * @return Only necessary for close connection events.
	 */
	public City getCityTo() {
		return cityTo;
	}

	/**
	 * @return The pathogen for which will be used.
	 */
	public Pathogen getPathogen() {
		return pathogen;
	}

	/**
	 * @return The amount of rounds this action will be active.
	 */
	public int getRounds() {
		return rounds;
	}
}
