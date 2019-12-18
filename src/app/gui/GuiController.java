package app.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import app.game.events.E_Outbreak;
import app.game.events.E_PathogenEncounter;
import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.actions.Action;
import app.game.actions.ActionType;
import app.http.GameExchange;
import app.http.GameServer;
import app.solver.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;

public class GuiController {

	@FXML // All Buttons in the GUI
	private Button quitB, endRoundB, putUnderQuarantineB, closeAirportB, closeConnectionB, developVaccineB,
			deployVaccineB, developMedicationB, deployMedicationB, applyHygienicMeasuresB, exertInfluenceB,
			callElectionsB, launchCampaignB, medicateBiggestCitiesB, vaccinateBiggestCitiesB, autoTurnB, selectCityB,
			selectHealthyCitiesB, selectInfectedCitiesB, resetB;

	@FXML // Input fields
	private TextField cityFrom, cityTo, rounds, amountT, selectCityT;
	@FXML
	private ChoiceBox<String> selectedPathogenCB;

	@FXML // CityInfo
	private Text population, economy, goverment, awareness, hygiene, events;
	@FXML // PathogenInfo
	private Text infectivity, mobility, duration, lethality, prevalance;
	@FXML // OtherInfo
	private Text currentRound, currentPoints, lastAction;

	@FXML // Draw elements
	private Canvas currentMap;

	@FXML // Checking what to draw
	private CheckBox connectionBox, populationBox, infectedBox, cityNamesBox;

	// Backend components of the GUI
	private static String lastActionString;
	private GameExchange currentGameExchange;
	private Game currentGame;

	// Info about current selection
	private static City selectedCity;
	private static Pathogen selectedPathogen;

	// Booleans corresponding
	private static boolean showConnections, showPopulation, showInfected, showCityNames;
	private static boolean init = true;

	// Constructor
	public GuiController() {
		/*
		 * Has no real purpose. Just need to be declared for the FXML-file
		 */
	}

	public void initialize() {

		// Set all checkBox booleans as selected but only of this is the first
		// initialize.
		// Also initialize the selectedCity and selectedPathogen as null
		if (init) {
			showConnections = true;
			showPopulation = true;
			showInfected = true;
			showCityNames = true;
			selectedCity = null;
			selectedPathogen = null;
			init = false;
		}

		// Set selection for the CheckBoxes.
		this.connectionBox.setSelected(showConnections);
		this.populationBox.setSelected(showPopulation);
		this.infectedBox.setSelected(showInfected);
		this.cityNamesBox.setSelected(showCityNames);

		// Set the last Action in the Text to the static String where it was saved.
		this.lastAction.setText(lastActionString);

		// Set Points and current Round.
		if (this.currentGameExchange != null) {

			// Set the current round and points.
			this.currentRound.setText(this.currentGame.getRound() + "");
			this.currentPoints.setText(this.currentGame.getPoints() + "");
		}

		// Update the ChoiceBox items.
		updatePathogenChoiceBox();

		// Set PathogenChoiceBox selected field to the selected Pathogen
		if (selectedPathogen != null) {
			this.selectedPathogenCB.setValue(selectedPathogen.getName());
		}

		// Setting up a prompt text in the textField. Therefore when selected it will
		// disappear.
		this.cityFrom.setPromptText("enter city...");
		this.cityTo.setPromptText("enter city...");
		this.rounds.setPromptText("enter duration...");
		this.amountT.setPromptText("enter the amount ...");
		this.selectCityT.setPromptText("enter city manually ...");
		// If there is a selected City set as text in the TextField
		if (selectedCity != null) {
			this.selectCityT.setText(selectedCity.getName());
		}

		// Set the Button Tooltips.
		this.quitB.setTooltip(new Tooltip("Close the server and this window."));
		this.endRoundB.setTooltip(new Tooltip("End the current round.\namount : number of rounds that just get ended"));
		this.putUnderQuarantineB.setTooltip(
				new Tooltip("Put a city under qurantine.\ncity (from): city name\nrounds : number of rounds"));
		this.closeAirportB
				.setTooltip(new Tooltip("Close a city's airport.\ncity (from): city name\nrounds : number ofrounds"));
		this.closeConnectionB.setTooltip(new Tooltip(
				"Close a connection between two cities.\ncity (from) : first city name\ncity (to) : second city name\nrounds : number of rounds"));
		this.developVaccineB
				.setTooltip(new Tooltip("Develop a vaccine against a certain pathogen.\npathogen : pathogen name"));
		this.deployVaccineB.setTooltip(new Tooltip(
				"Deploy a vaccine in against a certain pathogen in a certain city.\ncity (from) city name\npathogen : pathogen name\n"));
		this.developMedicationB
				.setTooltip(new Tooltip("Develop a medication against a certain pathogen.\npathogen : pathogen name"));
		this.deployMedicationB.setTooltip(new Tooltip(
				"Deploy a medication in against a certain pathogen in a certain city.\ncity (from) : city name\npathogen : pathogen name"));
		this.applyHygienicMeasuresB
				.setTooltip(new Tooltip("Randomly increase hygine in a certain city\ncity (from) : city name"));
		this.exertInfluenceB
				.setTooltip(new Tooltip("Reset the economic strength of a certain city\ncity (from) : city name"));
		this.callElectionsB
				.setTooltip(new Tooltip("Reset the political strength of a certain city\ncity (from) : city name"));
		this.launchCampaignB.setTooltip(new Tooltip(
				"The attentiveness of a certain city's population will be increased\ncity (from) : city name"));
		this.autoTurnB.setTooltip(new Tooltip("Let the computer play.\namount : amount of actions"));
		this.medicateBiggestCitiesB.setTooltip(new Tooltip(
				"Medicate a certain amount of the biggest cities.\npathogen : pathogen name\namount : amount of cities to medicate"));
		this.vaccinateBiggestCitiesB.setTooltip(new Tooltip(
				"Vaccinate a certain amount of the biggest cities.\npathogen : pathogen name\namount : amount of cities to vaccinate"));
		// Set input Tooltip for inputs

		// Draw Call for the MapCanvas
		drawMap();
	}

	public void setGame(GameExchange exchange) {
		this.currentGameExchange = exchange;
		this.currentGame = this.currentGameExchange.getGame();
	}

	// is called from the quitButton from the GuiController.
	@FXML
	public void quit() {
		// Get the primaryStage from any element of the GUI (It can be any element
		// because they all have the same root)
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();

		// If there was no primaryStage found something went wrong.
		if (primaryStage == null)
			System.out.println("error");

		// Reset the init value. If a new ic20 is executed the window will be
		// initialized
		init = true;

		// Close the primaryStage and close the Server (terminate the Programm)
		primaryStage.close();
		System.exit(1);
	}

	@FXML
	private void checkShowConnections() {
		showConnections = !showConnections;
		drawMap();
	}

	@FXML
	private void checkShowPopulation() {
		showPopulation = !showPopulation;
		drawMap();
	}

	@FXML
	private void checkShowInfected() {
		showInfected = !showInfected;
		drawMap();
	}

	@FXML
	private void checkShowCityNames() {
		showCityNames = !showCityNames;
		drawMap();
	} // End of checkBox methodes

	@FXML // Reset Button
	private void reset() {
		init = true;
		initialize();
		drawMap();
	}

	@FXML // selectCityButton
	private void selectCity() {

		// Get the city's name from the textField
		String selectedCityString = this.selectCityT.getText();

		// Get the city object via its name.
		selectedCity = this.currentGame.getCities().get(selectedCityString);

		// Only set the info of the selected city if there is a selected city.
		if (selectedCity != null) {

			// Get all the infos of the selected city info as Strings.
			String population = selectedCity.getPopulation() + "";
			String economy = selectedCity.getEconomy().toString();
			String goverment = selectedCity.getGovernment().toString();
			String hygiene = selectedCity.getHygiene().toString();
			String awareness = selectedCity.getAwareness().toString();

			// Set all the info to the corresponding textElemt.
			this.population.setText(population);
			this.economy.setText(economy);
			this.goverment.setText(goverment);
			this.hygiene.setText(hygiene);
			this.awareness.setText(awareness);
		}

		// Only set the info of the selected pathogen if there is a selected pathogen.
		if (selectedPathogen != null) {
			// Get all the infos of the selected Pathogen as String
			String infectivity = selectedPathogen.getInfectivity().toString();
			String mobility = selectedPathogen.getMobility().toString();
			String duration = selectedPathogen.getDuration().toString();
			String lethality = selectedPathogen.getLethality().toString();
			String prevalance = new String();

			// Get the prevalance for the selected City if one has been selected.
			if (selectedCity != null) {
				for (E_Outbreak e : this.currentGame.getOutbreakEvents()) {
					if (e.getCity() == selectedCity) {
						prevalance = e.getPrevalence() + "";
						break;
					}
				}
			}

			// Only get 5 decimals of the prevalance
			if (prevalance.length() > 5) {
				prevalance = prevalance.substring(0, 5);
			}

			// Set the texts in the GUI to the Info gotten above.
			this.infectivity.setText(infectivity);
			this.mobility.setText(mobility);
			this.duration.setText(duration);
			this.lethality.setText(lethality);
			this.prevalance.setText(prevalance);
		}

		// update the GUI
		drawMap();
	}

	@FXML // Button implementation
	private void selectInfectedCities() {

		if (selectedPathogen == null)
			return;

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		// Clear the Canvas
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		// Draw all the infected cities
		for (E_Outbreak e : this.currentGame.getOutbreakEvents()) {
			if (selectedPathogen == e.getPathogen())
				drawCity(e.getCity(), currentGame);
		}
	}

	@FXML // Button implementation
	private void selectHealthyCities() {

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		// Clear the Canvas
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		// Draw all uninfected cities
		for (City c : this.currentGame.getCities().values()) {
			if (!c.isInfected())
				drawCity(c, currentGame);
		}
	}

	@FXML // onAction call for the selectPathogen choiceBox
	private void changeSelectedPathogen() {

		Game currentGame = this.currentGame;
		String pathogen = this.selectedPathogenCB.getValue();
		selectedPathogen = currentGame.getPathogenes().get(pathogen);

		// A methode that is called when a the selectCityButton has been pressed. It
		// updates the city and the pathogen info
		// Therefore it can be used to do just that at this point.
		selectCity();
		drawMap();
	}

	@FXML
	private void endRound() {

		String cityAmount = this.amountT.getText();
		int amount = 1;
		if (!cityAmount.equals("")) {
			try {
				amount = Integer.parseInt(cityAmount);
			} catch (NumberFormatException e) {
//				Amount field empty
			}
		}

		for (int i = 0; i < amount; i++) {

			GameServer.addReply((Game g) -> new Action(g).toString());
		}

		this.executeEvent(GameServer.getReply().evaluate(currentGame));
	}

	@FXML
	private void autoTurn() {
		String amountString = new String();
		try {
			amountString = this.amountT.getText();
		} catch (Exception e) {
			return;
		}
		int amount = 1;
		if (!amountString.equals("")) {
			try {
				amount = Integer.parseInt(amountString);
			} catch (Exception e) {

			}
		}
		this.autoTurnB.setDisable(true);
		for (int i = 0; i < amount; i++) {
			GameServer.addReply((Game g) -> {
				String result = Main.solve(g);
				return result;
			});
		}
		this.executeEvent(GameServer.getReply().evaluate(this.currentGame));
		this.autoTurnB.setDisable(false);
	}

	// helper methode
	private void setLastAction(String s) {
		GuiController.lastActionString = s;
	}

	@FXML
	private void putUnderQuarantine() {
		Game g = this.currentGame;
		City c = g.getCities().get(this.cityFrom.getText());
		int r = 1;
		try {
			r = Integer.parseInt(this.rounds.getText());
		} catch (Exception e) {
			// r could not be parsed. As a default it will be set to one.
			r = 1;
		}
		this.executeEvent(new Action(ActionType.putUnderQuarantine, g, c, r).toString());
	}

	@FXML
	private void closeAirport() {
		Game g = this.currentGame;
		City c = g.getCities().get(this.cityFrom.getText());
		int r = 1;
		try {
			r = Integer.parseInt(this.rounds.getText());
		} catch (Exception e) {
			// r could not be parsed. As a default it will be set to one.
			r = 1;
		}
		this.executeEvent(new Action(ActionType.closeAirport, g, c, r).toString());
	}

	@FXML
	private void closeConnection() {
		Game g = this.currentGame;
		City cFrom = g.getCities().get(this.cityFrom.getText());
		City cTo = g.getCities().get(this.cityTo.getText());
		int r = Integer.parseInt(this.rounds.getText());
		this.executeEvent(new Action(g, cFrom, cTo, r).toString());
	}

	@FXML
	private void developVaccine() {
		Game g = this.currentGame;
		Pathogen v = selectedPathogen;
		if (g == null || v == null) {
			return;
		}

		this.executeEvent(new Action(ActionType.developVaccine, g, v).toString());
	}

	@FXML
	private void deployVaccine() {
		Game g = this.currentGame;
		City c = g.getCities().get(this.cityFrom.getText());
		Pathogen v = selectedPathogen;
		if (g == null || c == null || v == null) {
			return;
		}
		System.out.println("DEPVAC");
		this.executeEvent(new Action(ActionType.deployVaccine, g, c, v).toString());
	}

	@FXML
	private void vaccinateBiggestCities() {

		String cityAmountText = this.amountT.getText();

		if (selectedPathogen == null || selectedPathogen.equals("")) {
			return;
		}

		// check if the actions can be added.
		if (cityAmountText == null || cityAmountText.equals("")) {
			cityAmountText = "1";
		}
		// Initialize Integer we want to convert to
		int cityAmount = 0;

		try {
			// Convert to Integer
			cityAmount = Integer.parseInt(cityAmountText);
		} catch (NumberFormatException e) {
			// Error parsing to an Integer
		}

		for (int i = 0; i < cityAmount; i++) {

			GameServer.addReply((Game g) -> {

				// First get all unvaccinated cities. Then check in which city the most people
				// can be vaccinated.
				Object bestCities[] = g.getCities().values().stream().filter(c -> c.getVaccineDeployed() == null
						|| (c.getVaccineDeployed().stream().filter(e -> e.getPathogen() != selectedPathogen)) != null)
						.sorted(((City c1, City c2) -> {
							return (int) (c1.getPopulation() * c1.getPrevalance())
									- (int) (c2.getPopulation() * c2.getPrevalance());
						})).toArray();
				// Return the new Action which vacinates the city where currently the most
				// people are influenced by the selected Pathogen.
				return new Action(ActionType.deployVaccine, g, ((City) bestCities[0]), selectedPathogen).toString();
			});
		}
		// Check if there is an event to execute
		if (!GameServer.hasReplies()) {
			return;
		}

		// Execute the Action generated addReply methode above.
		this.executeEvent(GameServer.getReply().evaluate(currentGame));
	}

	@FXML
	private void developMedication() {
		Game g = this.currentGame;
		Pathogen v = selectedPathogen;
		if (g == null || v == null) {
			return;
		}

		this.executeEvent(new Action(ActionType.developMedication, g, v).toString());
	}

	@FXML
	private void deployMedication() {

		Game g = this.currentGame;
		City c = g.getCities().get(this.cityFrom.getText());
		Pathogen v = selectedPathogen;
		if (g == null || c == null || v == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.deployMedication, g, c, v).toString());
	}

	@FXML
	private void applyHygienicMeasures() {
		Game g = this.currentGame;
		City c = selectedCity;
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.applyHygienicMeasures, g, c).toString());
	}

	@FXML
	private void exertInfluence() {
		Game g = this.currentGame;
		City c = selectedCity;
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.exertInfluence, g, c).toString());
	}

	@FXML
	private void callElections() {
		Game g = this.currentGame;
		City c = selectedCity;
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.callElections, g, c).toString());
	}

	@FXML
	private void launchCampaign() {
		Game g = this.currentGame;
		City c = selectedCity;
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.launchCampaign, g, c).toString());
	}

	@FXML // Button implementation
	private void medicateBiggestCities() {
		String cityAmountText = this.amountT.getText();

		if (selectedPathogen == null || selectedPathogen.equals("")) {
			return;
		}

		// check if the actions can be added.
		if (cityAmountText == null || cityAmountText.equals("")) {
			cityAmountText = "1";
		}
		// Initialize Integer we want to convert to
		int cityAmount = 0;

		try {
			// Convert to Integer
			cityAmount = Integer.parseInt(cityAmountText);
		} catch (NumberFormatException e) {
			// Error parsing to an Integer
		}

		for (int i = 0; i < cityAmount; i++) {

			GameServer.addReply((Game g) -> {

				// First get all infected cities. Then check in which city the most people can
				// be medicated.
				Optional<City> bestCity = g.getCities().values().stream()
						.filter(c -> c.getOutbreak() != null && c.getOutbreak().getPathogen() == selectedPathogen)
						.max((City c1, City c2) -> {
							return (int) (c1.getPopulation() * c1.getPrevalance())
									- (int) (c2.getPopulation() * c2.getPrevalance());
						});

				if (!bestCity.isPresent()) {
					return "";
				}

				// Return the new Action which medicates the city where currently the most
				// people are influenced by the selected pathogen.
				return new Action(ActionType.deployMedication, g, bestCity.get(), selectedPathogen).toString();
			});

			// Check if there is an event to execute
			if (!GameServer.hasReplies()) {
				return;
			}

			// Execute the Action generated addReply methode above.
			this.executeEvent(GameServer.getReply().evaluate(currentGame));
		}
	}

	private void executeEvent(String event) {
		// Check if there is an active GameExchange
		if (this.currentGameExchange == null || !this.currentGameExchange.isAlive()) {
			return;
		}

		// Set the lastActionString and execute the event
		setLastAction(event);
		this.currentGameExchange.sendReply(event);

		// Close the GUI
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();

		// If there was no GUI at this point something went wrong.
		if (primaryStage == null)
			System.out.println("error");
		primaryStage.close();
	}

	private void updatePathogenChoiceBox() {

		// Check if there is an active GameExchange
		Game currentGame = this.currentGame;
		if (currentGame == null) {
			return;
		}

		// Create a new ArrayList of all active pathogens
		ArrayList<E_PathogenEncounter> pathogensEncountered = new ArrayList<E_PathogenEncounter>(
				currentGame.getPathEncounterEvents());
		ArrayList<String> activePathogens = new ArrayList<>();
		// Add all pathogens to this ArrayList that are not allready in it.
		for (E_PathogenEncounter p : pathogensEncountered) {
			activePathogens.add(p.getPathogen().getName());
		}

		// Convert the ArrayList values to the choiceBox
		ObservableList<String> activePathogensList = FXCollections.observableList(activePathogens);
		this.selectedPathogenCB.setItems(activePathogensList);

	}
	

	// Draw Map methodes.
	public void drawMap() {

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		if (this.currentGameExchange == null) {
			return;
		}

		Game currentGame = this.currentGame;

		// Clear the Canvas
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		// Get the City HashMap
		HashMap<String, City> cities = currentGame.getCities();

//			// Iterate over all cities find the one we want to print and print those
		cities.values().stream()
				.filter(c -> ((selectedCity != null
						&& (selectedCity == c || (selectedCity.getConnections().contains(c) && showConnections))
						|| selectedCity == null)))
				.forEach(c -> drawCity(c, currentGame));
	}

	// Draws one City on the Canvas (and all additional features)
	private void drawCity(City currentCity, Game currentGame) {

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		currentGame = this.currentGame;
		// Get all the info from the current city.
		String cityName = currentCity.getName();
		int diameter = currentCity.getPopulation() / 120;
		int x = (int) currentCity.getX() + 180;
		int y = (int) -currentCity.getY() + 90;
		double prev = 0;

		// Get prevalance in the current city.
		for (E_Outbreak e : currentGame.getOutbreakEvents()) {
			if (e.getCity() == currentCity && (selectedPathogen == null || e.getPathogen() == selectedPathogen))
				// Get the strongest infection in one city (if multiple infections are to be
				// shown).
				prev = Math.max(prev, e.getPrevalence());
		}

		gc.setFill(new Color(prev, 0, 0, prev));

		// adjust x,y so it fits nicely on the canvas.
		x *= 4;
		y *= 5;

		// Draw Population Circle. If wanted.
		if (showPopulation)
			gc.strokeOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Color for infectedRate. If a pathogen is selected and showInfected is true.
		if (showInfected)
			gc.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Draw City Name. If wanted.
		if (showCityNames)
			gc.strokeText(cityName, x, y);

	}
	// End of draw map methodes.
}
