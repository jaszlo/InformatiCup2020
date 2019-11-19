package app.game;

import java.util.HashSet;

import app.knapsack.Item;

public class Action extends Item {

	private final ActionType type;

	private final Game game;

	// Parameters for the actions
	private final City city;
	private final City toCity;
	private final Virus virus;
	private final int rounds;

	// Basis constructor
	public Action(ActionType type, Game game, City city, City toCity, Virus virus, int rounds) {
		super(type.getPoints(rounds));
		this.type = type;
		this.game = game;
		this.city = city;
		this.toCity = toCity;
		this.virus = virus;
		this.rounds = rounds;
	}

	// endRound constructor
	public Action(Game game) {
		this(ActionType.endRound, game, null, null, null, 0);
	}

	// quarantine and closeAirport constructor
	public Action(ActionType type, Game game, City city, int rounds) {
		this(type, game, city, null, null, rounds);
	}

	// developVaccine or developMedication constructor
	public Action(ActionType type, Game game, Virus virus) {
		this(type, game, null, null, virus, 0);
	}

	// deployVaccine or deployMedication constructor
	public Action(ActionType type, Game game, City city, Virus virus) {
		this(type, game, city, null, virus, 0);
	}

	// closeConnection constructor
	public Action(Game game, City city, City toCity, int rounds) {
		this(ActionType.closeConnection, game, city, toCity, null, rounds);
	}
	
	// exertInfluence callElections applyHygienicMeasures launchCampaign constructor
		public Action(ActionType type, Game game, City city) {
			this(type, game, city, null, null, 0);
		}
	
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
					this.getCity().getName(), this.getToCity().getName(), this.getRounds());

		case developMedication:
			return String.format("{\"type\": \"developMedication\", \"pathogen\":\"%s\"}", this.getVirus().getName());

		case deployMedication:
			return String.format("{\"type\": \"deployMedication\", \"pathogen\":\"%s\", \"city\": \"%s\"}",
					this.getVirus().getName(), this.getCity().getName());

		case developVaccine:
			return String.format("{\"type\": \"developVaccine\", \"pathogen\":\"%s\"}", this.getVirus().getName());

		case deployVaccine:
			return String.format("{\"type\": \"deployVaccine\", \"pathogen\":\"%s\", \"city\": \"%s\"}",
					this.getVirus().getName(), this.getCity().getName());

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

	public Game getGame() {
		return game;
	}

	@SuppressWarnings("unchecked")
	public int getSetValue(HashSet<? extends Item> set) {
		return ActionHeuristic.getValue((HashSet<Action>) set);
	}

	public ActionType getType() {
		return type;
	}

	public City getCity() {
		return city;
	}

	public City getToCity() {
		return toCity;
	}

	public Virus getVirus() {
		return virus;
	}

	public int getRounds() {
		return rounds;
	}
}
