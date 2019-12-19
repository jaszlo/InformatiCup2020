package app.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import app.game.events.E_Outbreak;
import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.actions.Action;
import app.game.actions.ActionType;
import app.http.GameExchange;
import app.http.GameServer;
import app.solver.Main;
import javafx.application.Platform;
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
	private TextField rounds, amountT;
	@FXML
	private ChoiceBox<String> pathogenesCB, citiesCB, citiesToCB;

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
	private static String selectedCity;
	private static String selectedCityTo;
	private static String selectedPathogen;

	// Booleans corresponding
	private static boolean  showHealthyCities, showInfectedCities;

	// Constructor
	public GuiController() {
		/*
		 * Has no real purpose. Just need to be declared for the FXML-file
		 */
	}

	public void initialize() {
		
		// If game is not set return 
		if(this.currentGame == null) { 
			ClassLoader classLoader = getClass().getClassLoader();
			
			InputStream jsonStream = classLoader.getResourceAsStream("resources/EmptyGame.json");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
			String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			
			this.currentGame = new Game(json);
		}
		
		// Set all checkBox booleans as selected but only of this is the first
		// initialize.
		// Also initialize the selectedCity and selectedPathogen as null
		
		// Set ChoiceBoxes to default
		this.connectionBox.setSelected(true);
		this.populationBox.setSelected(true);
		this.infectedBox.setSelected(true);
		this.cityNamesBox.setSelected(true);
		
		// Set default values
		showHealthyCities = false;
		showInfectedCities = false;
		selectedCity = null;
		selectedCityTo = null;
		selectedPathogen = null;

		
		// Setting up a prompt text in the textField. Therefore when selected it will
		// disappear.
		this.rounds.setPromptText("enter duration...");
		this.amountT.setPromptText("enter the amount ...");

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
		// Set input Tooltip for inputs TODO
		
		this.update();
		// Draw Call for the MapCanvas
		drawMap();
	}
	
	public void update() {
		
		// Set the last Action in the Text to the static String where it was saved.
		this.lastAction.setText(lastActionString);
		
		// Set Points and current Round.
		if (this.currentGameExchange != null) {

			// Set the current round and points.
			this.currentRound.setText(this.currentGame.getRound() + "");
			this.currentPoints.setText(this.currentGame.getPoints() + "");
		}
				
		// Update the ChoiceBoxes items.
		updateChoiceBox();

		// Set PathogenChoiceBox selected field to the selected Pathogen
		if (selectedPathogen != null) {
			this.pathogenesCB.setValue(selectedPathogen);
		}

		// Set SelectedCityChoiceBox field to the selected City
		if (selectedCity != null) {
			this.citiesCB.setValue(selectedCity);
		}

		if (selectedCityTo != null) {
			this.citiesToCB.setValue(selectedCityTo);
		}

	}

	public void setGame(GameExchange exchange) {
		if (exchange == null)
			return;
		this.currentGameExchange = exchange;
		this.currentGame = this.currentGameExchange.getGame();
		this.update();
	}

	public boolean ready() {
		return this.currentGameExchange == null;
	}

	// is called from the quitButton from the GuiController.
	@FXML
	public void quit() {
		// Get the primaryStage from any element of the GUI (It can be any element
		// because they all have the same root)
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();

		// If there was no primaryStage found something went wrong.
		if (primaryStage == null)
			System.err.println("Could not find primary stage");

		// Close the primaryStage and close the Server (terminate the Programm)
		primaryStage.close();
		System.exit(1);
	}


	@FXML // Reset Button
	private void reset() {
		initialize();
	}

	@FXML // selectCityButton
	private void setInfo() {

		// Get the city's name from the textField
		String selectedCityString = this.citiesCB.getValue();

		// Set the static GuiController.selectedCity
		selectedCity = selectedCityString;

		// Get the city object via its name.
		City selectedCity = this.currentGame.getCities().get(selectedCityString);

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
			Pathogen selectedPathogen = this.currentGame.getPathogenes().get(GuiController.selectedPathogen);

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
		showInfectedCities = true;
		showHealthyCities = false;
		drawMap();
	}

	@FXML // Button implementation
	private void selectHealthyCities() {
		showHealthyCities = true;
		showInfectedCities = false;
		drawMap();
	}

	@FXML // onAction call for the selectPathogen choiceBox
	private void changeSelectedPathogen() {
		selectedPathogen = this.pathogenesCB.getValue();

		// A method that is called when a the selectCityButton has been pressed. It
		// updates the city and the pathogen info
		// Therefore it can be used to do just that at this point.
		setInfo();
		drawMap();
	}

	@FXML
	private void changeSelectedCity() {
		selectedCity = this.citiesCB.getValue();

		// A method that is called when a the selectCityButton has been pressed. It
		// updates the city and the pathogen info
		// Therefore it can be used to do just that at this point.
		setInfo();
		drawMap();
	}

	@FXML
	private void changeSelectedCityTo() {
		selectedCityTo = this.citiesToCB.getValue();

		// A method that is called when a the selectCityButton has been pressed. It
		// updates the city and the pathogen info
		// Therefore it can be used to do just that at this point.
		setInfo();
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
	public static void setLastAction(String s) {
		GuiController.lastActionString = s;
	}

	@FXML
	private void putUnderQuarantine() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		int r = 1;
		try {
			r = Integer.parseInt(this.rounds.getText());
		} catch (Exception e) {
			// r could not be parsed. As a default it will be set to one.
			r = 1;
		}
		// CHECK FOR NPE TODO
		this.executeEvent(new Action(ActionType.putUnderQuarantine, g, c, r).toString());
	}

	@FXML
	private void closeAirport() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
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
		City cFrom = g.getCities().get(selectedCity);
		City cTo = g.getCities().get(selectedCityTo);
		int r = Integer.parseInt(this.rounds.getText());
		this.executeEvent(new Action(g, cFrom, cTo, r).toString());
	}

	@FXML
	private void developVaccine() {
		Game g = this.currentGame;
		Pathogen p = g.getPathogenes().get(selectedPathogen);
		if (g == null || p == null) {
			return;
		}

		this.executeEvent(new Action(ActionType.developVaccine, g, p).toString());
	}

	@FXML
	private void deployVaccine() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		Pathogen p = g.getPathogenes().get(selectedPathogen);
		if (g == null || c == null || p == null) {
			return;
		}
		System.out.println("DEPVAC");
		this.executeEvent(new Action(ActionType.deployVaccine, g, c, p).toString());
	}

	@FXML
	private void developMedication() {
		Game g = this.currentGame;
		Pathogen p = g.getPathogenes().get(selectedPathogen);
		if (g == null || p == null) {
			return;
		}

		this.executeEvent(new Action(ActionType.developMedication, g, p).toString());
	}

	@FXML
	private void deployMedication() {

		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		Pathogen p = g.getPathogenes().get(selectedPathogen);
		if (g == null || c == null || p == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.deployMedication, g, c, p).toString());
	}

	@FXML
	private void applyHygienicMeasures() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.applyHygienicMeasures, g, c).toString());
	}

	@FXML
	private void exertInfluence() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.exertInfluence, g, c).toString());
	}

	@FXML
	private void callElections() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.callElections, g, c).toString());
	}

	@FXML
	private void launchCampaign() {
		Game g = this.currentGame;
		City c = g.getCities().get(selectedCity);
		if (g == null || c == null) {
			return;
		}
		this.executeEvent(new Action(ActionType.launchCampaign, g, c).toString());
	}
	
	@FXML
	private void vaccinateBiggestCities() {

		String cityAmountText = this.amountT.getText();

		if (selectedPathogen == null) {
			return;
		}

		// Initialize Integer we want to convert to
		int cityAmount = 1;

		try {
			// Convert to Integer
			cityAmount = Integer.parseInt(cityAmountText);
		} catch (NumberFormatException e) {
			// Error parsing to an Integer
		}

		for (int i = 0; i < cityAmount; i++) {

			GameServer.addReply((Game g) -> {
				Pathogen selectedPathogen = g.getPathogenes().get(GuiController.selectedPathogen);

				// First get all infected cities. Then check in which city the most people can
				// be medicated
				// Set PathogenChoiceBox selected field to the selected Pathogen
				City bestCity = g.getCities().values().stream()
						.filter(c -> c.getVaccineDeployed().stream().allMatch(e -> e.getPathogen() != selectedPathogen))
						.max((City c1, City c2) -> {
							// Get the healthy population as we want to vaccinate the city which impacts the
							// most people
							double healthyPopulation1 = c1.isInfected(selectedPathogen) ? 1 - c1.getPrevalance() : 1;
							double healthyPopulation2 = c2.isInfected(selectedPathogen) ? 1 - c2.getPrevalance() : 1;

							return (int) (c1.getPopulation() * healthyPopulation1
									- c2.getPopulation() * healthyPopulation2);
						}).orElseGet(() -> null);

				// If no city is infected by the selected pathogen just end the round
				if (bestCity == null) {
					GameServer.clearReplies();
					return new Action(g).toString();
				}

				// Return the new Action which medicates the city where currently the most
				// people are influenced by the selected pathogen.
				return new Action(ActionType.deployVaccine, g, bestCity, selectedPathogen).toString();
			});
		}

		// Check if there is an event to execute
		if (!GameServer.hasReplies()) {
			return;
		}

		// Execute the Action generated addReply methode above.
		this.executeEvent(GameServer.getReply().evaluate(currentGame));

	}

	@FXML // Button implementation
	private void medicateBiggestCities() {
		String cityAmountText = this.amountT.getText();

		if (selectedPathogen == null) {
			return;
		}

		// Initialize Integer we want to convert to
		int cityAmount = 1;

		try {
			// Convert to Integer
			cityAmount = Integer.parseInt(cityAmountText);
		} catch (NumberFormatException e) {
			// Error parsing to an Integer
		}

		for (int i = 0; i < cityAmount; i++) {

			GameServer.addReply((Game g) -> {
				Pathogen selectedPathogen = g.getPathogenes().get(GuiController.selectedPathogen);

				// First get all infected cities. Then check in which city the most people can
				// be medicated
				// Set PathogenChoiceBox selected field to the selected Pathogen

				City bestCity = g
						.getCities().values().stream().filter(
								c -> c.isInfected(selectedPathogen))
						.max((City c1, City c2) -> (int) (c1.getPopulation() * c1.getPrevalance()
								- c2.getPopulation() * c2.getPrevalance()))
						.orElseGet(() -> null);

				// If no city is infected by the selected pathogen just end the round
				if (bestCity == null) {
					GameServer.clearReplies();
					return new Action(g).toString();
				}

				// Return the new Action which medicates the city where currently the most
				// people are influenced by the selected pathogen.
				return new Action(ActionType.deployMedication, g, bestCity, selectedPathogen).toString();
			});
		}

		// Check if there is an event to execute
		if (!GameServer.hasReplies()) {
			return;
		}

		// Execute the Action generated addReply methode above.
		this.executeEvent(GameServer.getReply().evaluate(currentGame));

	}

	private void executeEvent(String event) {
		// Check if there is an active GameExchange
		if (this.currentGameExchange == null || !this.currentGameExchange.isAlive()) {
			this.currentGameExchange = null;
			return;
		}

		this.currentGameExchange.sendReply(event);
		this.currentGameExchange = null;
	}

	private void updateChoiceBox() {

		// Check if there is a game
		Game currentGame = this.currentGame;
		if (currentGame == null) {
			return;
		}
		
		
		// Add all pathogens to the pathogens ChoiceBox
		this.pathogenesCB.setItems(currentGame.getPathogenes().values().stream().map(p -> p.getName()).collect(
				FXCollections::<String>observableArrayList, ObservableList<String>::add,
				ObservableList<String>::addAll));

		// Add all cities to the cities ChoiceBox
		ObservableList<String> o = currentGame.getCities().values().stream().map(c -> c.getName()).sorted().collect(
				FXCollections::<String>observableArrayList, ObservableList<String>::add,
				ObservableList<String>::addAll);
		
		// Update ChoiceBox in a new thread to boost performance
		Platform.runLater(() -> this.citiesCB.setItems(o));
		
		// Show only connections of selected city in city to ChoiceBox
		Collection<City> cities = selectedCity == null? new HashSet<>(): currentGame.getCity(selectedCity).getConnections();
		this.citiesToCB.setItems(cities.stream().map(c -> c.getName()).sorted().collect(
				FXCollections::<String>observableArrayList, ObservableList<String>::add,
				ObservableList<String>::addAll));
	}

	/**
	 * Draws the map on the canvas
	 */
	public void drawMap() {

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		if (this.currentGame == null) {
			return;
		}

		// Clear the Canvas
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.BLACK);
		gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		// Get the selected city and the selected pathogen
		HashMap<String, City> cities = this.currentGame.getCities();
		City selectedCity = this.currentGame.getCities().get(GuiController.selectedCity);
		Pathogen selectedPathogen = this.currentGame.getPathogenes().get(GuiController.selectedPathogen);

		// Iterate over all cities find the one we want to print and print those
		cities.values().stream().filter(c -> !showHealthyCities || !c.isInfected())
				.filter(c -> !showInfectedCities || c.isInfected(selectedPathogen) && selectedPathogen != null
						|| selectedPathogen == null && c.isInfected())
				.filter(c -> selectedCity == null || selectedCity == c
						|| (selectedCity.getConnections().contains(c) && connectionBox.isSelected()))
				.forEach(c -> drawCity(c));
	}

	/**
	 * Draws a city on the canvas including name and prevalence
	 * 
	 * @param city City to draw
	 */
	private void drawCity(City city) {

		Pathogen selectedPathogen = this.currentGame.getPathogenes().get(GuiController.selectedPathogen);

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		// Get all the info from the current city.
		String cityName = city.getName();
		int diameter = city.getPopulation() / 120;
		int x = (int) city.getX() + 180;
		int y = (int) -city.getY() + 90;

		// If no pathogen is selected or the selected pathogen matches the cities
		// outbreak
		// set the prevalence
		double prev = selectedPathogen == null || city.getPathogen() == selectedPathogen ? city.getPrevalance() : 0.0;

		gc.setFill(new Color(prev, 0, 0, prev));

		// adjust x,y so it fits nicely on the canvas.
		x *= 4;
		y *= 5;

		// Draw Population Circle. If wanted.
		if (populationBox.isSelected())
			gc.strokeOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Color for infectedRate. If a pathogen is selected and showInfected is true.
		if (infectedBox.isSelected())
			gc.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Draw City Name. If wanted.
		if (cityNamesBox.isSelected())
			gc.strokeText(cityName, x, y);

	}
	// End of draw map methods.
}
