package app.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import app.App;
import app.game.City;
import app.game.Game;
import app.game.Pathogen;
import app.game.actions.Action;
import app.game.actions.ActionType;
import app.http.GameExchange;
import app.http.GameServer;
import app.io.FileHandler;
import app.solver.ActionHeuristic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SnapshotResult;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for the GUI.
 */
public class GuiController {

	@FXML /// All Buttons in the GUI
	private Button quitB, endRoundB, putUnderQuarantineB, closeAirportB, closeConnectionB, developVaccineB,
			deployVaccineB, developMedicationB, deployMedicationB, applyHygienicMeasuresB, exertInfluenceB,
			callElectionsB, launchCampaignB, medicateBiggestCitiesB, vaccinateBiggestCitiesB, autoTurnB;

	@FXML /// Input fields
	private TextField roundsT, amountT;
	@FXML
	private ChoiceBox<String> pathogenesCB, citiesToCB, showDistinctCityCB;
	@FXML
	private ComboBox<String> citiesCB;

	@FXML /// CityInfo
	private Text population, economy, goverment, awareness, hygiene, events;
	@FXML /// PathogenInfo
	private Text infectivity, mobility, duration, lethality, prevalance;
	@FXML /// OtherInfo
	private Text currentRound, currentPoints, output;

	@FXML /// Draw elements
	private Canvas currentMap;

	@FXML /// Checking what to draw
	private CheckBox connectionBox, populationBox, infectedBox, cityNamesBox;

	/// Backend components of the GUI
	private static String outputString = "Waiting for game to start ...";
	private GameExchange currentGameExchange;
	private Game currentGame;

	/**
	 * Creates the controller for the GUI. The map draws an "empty" game state.
	 */
	public GuiController() {

		String json = FileHandler.readFile("resources/EmptyGame.json").stream()
				.collect(Collectors.joining(System.lineSeparator()));

		this.currentGame = new Game(json);
	}

	/**
	 * Initializes the controller for the GUI by calling the update method.
	 */
	public void initialize() {

		// Update and draw
		this.update();
	}

	/**
	 * Updates the GUI. That means drawing the map and updating displayed
	 * information.
	 */
	public void update() {

		// Set the output in the Text to the static String where it was saved.
		this.output.setText(outputString);

		// Set Points and current Round.
		if (this.currentGameExchange != null) {

			// Set the current round and points.
			this.currentRound.setText(this.currentGame.getRound() + "");
			this.currentPoints.setText(this.currentGame.getPoints() + "");
		}

		// Update the ChoiceBoxes items.
		this.updateChoiceBox();

		// Update the Info views
		this.updateInfo();

		// Draw Call for the MapCanvas
		this.drawMap();
	}

	/**
	 * Update method for the choice boxes. It will completely empty the choice
	 * boxes. Afterwards the choice boxes are refilled with valid city and pathogen
	 * names
	 */
	private void updateChoiceBox() {

		// Check if there is a game
		Game currentGame = this.currentGame;
		if (currentGame == null) {
			return;
		}

		// Add all pathogens to the pathogens ChoiceBox if they have changed
		ObservableList<String> allPathogens = this.currentGame.getPathogens().stream().map(p -> p.getName()).sorted()
				.collect(FXCollections::<String>observableArrayList, ObservableList<String>::add,
						ObservableList<String>::addAll);

		// Add "no pathogen" as choice
		allPathogens.add(0, "None");
		if (!this.pathogenesCB.getItems().equals(allPathogens)) {
			String current = this.pathogenesCB.getValue();
			this.pathogenesCB.setItems(allPathogens);

			if (!this.pathogenesCB.getItems().contains(current)) {
				this.pathogenesCB.setValue("None");

			} else {
				this.pathogenesCB.setValue(current);
			}
		}

		// Add all cities to the cities ChoiceBox
		ObservableList<String> allCities = currentGame.getCities().stream().map(c -> c.getName()).sorted().collect(
				FXCollections::<String>observableArrayList, ObservableList<String>::add,
				ObservableList<String>::addAll);
		// Add no city as choice
		allCities.add(0, "None");
		// Update ChoiceBox if changed in a new thread to boost performance
		if (!this.citiesCB.getItems().equals(allCities)) {
			String current = this.citiesCB.getValue();
			this.citiesCB.setItems(allCities);

			if (!this.citiesCB.getItems().contains(current)) {
				this.citiesCB.setValue("None");

			} else {
				this.citiesCB.setValue(current);
			}
		}

		// Show only connections of selected city in city to ChoiceBox
		// If no city is selected clear choicebox
		Collection<City> cities = this.getSelectedCity() == null ? Collections.<City>emptySet()
				: this.getSelectedCity().getConnections();
		ObservableList<String> citiesTo = cities.stream().map(c -> c.getName()).sorted().collect(
				FXCollections::<String>observableArrayList, ObservableList<String>::add,
				ObservableList<String>::addAll);
		// Add no city as choice
		citiesTo.add(0, "None");
		// Update ChoiceBox if changed
		if (!this.citiesToCB.getItems().equals(citiesTo)) {
			String current = this.citiesCB.getValue();
			this.citiesToCB.setItems(citiesTo);

			if (!this.citiesToCB.getItems().contains(current)) {
				this.citiesToCB.setValue("None");

			} else {
				this.citiesToCB.setValue(current);
			}

		}
	}

	public void setGame(GameExchange exchange) {
		if (exchange == null || !this.ready()) {
			return;
		}

		this.currentGameExchange = exchange;
		this.currentGame = this.currentGameExchange.getGame();
		Platform.runLater(() -> this.update());
	}

	/**
	 * Method to detect whether the GUI is ready for a new round to play. The GUI is
	 * ready if there is no active round at the time.
	 * 
	 * @return True if GUI is ready.
	 */
	public boolean ready() {
		return this.currentGameExchange == null;
	}

	/**
	 * Closes the GUI
	 */
	public void close() {
		// Get the primaryStage from any element of the GUI (It can be any element
		// because they all have the same root)
		Stage primaryStage = (Stage) this.autoTurnB.getScene().getWindow();

		// If there was no primaryStage found something went wrong.
		if (primaryStage == null)
			System.err.println("Could not find primary stage");

		// Close the primaryStage and close the Server (terminate the Programm)
		primaryStage.close();
	}

	/**
	 * On action method for the "Quit" button in the GUI. When pressed closes the
	 * GUI and ends the programm.
	 */
	@FXML
	public void quit() {
		// Exit and close
		System.exit(0);
	}

	/**
	 * Updates the text fields with information about the selected pathogen and the
	 * selected city.
	 */
	private void updateInfo() {

		// Get the selected city.
		City selectedCity = this.getSelectedCity();

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

		// Get the selected pathogen.
		Pathogen selectedPathogen = this.getSelectedPathogen();

		// Only set the info of the selected pathogen if there is a selected pathogen.
		if (selectedPathogen != null) {

			// Get all the infos of the selected Pathogen as String
			String infectivity = selectedPathogen.getInfectivity().toString();
			String mobility = selectedPathogen.getMobility().toString();
			String duration = selectedPathogen.getDuration().toString();
			String lethality = selectedPathogen.getLethality().toString();
			String prevalance = selectedCity != null ? selectedCity.getPrevalance() + "" : "";

			// Only get 5 chars of the prevalance
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

	/**
	 * Sets the output for the GUI to any String.
	 * 
	 * @param output The string that will be set as the output.
	 */
	public void setOutput(String output) {
		outputString = output;
	}

	/**
	 * Sets the Output text in the GUI to an executed action.
	 * 
	 * @param action The action that will be displayed in the output.
	 */
	public void setOutput(Action action, Game game) {

		// Get all relevant usual information most events have.
		City city = action.getCity();
		Pathogen pathogen = action.getPathogen();
		String rounds = action.getRounds() + "";

		// Create a formated String and call the overloaded method with a string as the
		// input.
		switch (action.getType()) {
		case endRound:
			App.guiController.setOutput("Round was ended");
			break;
		case putUnderQuarantine:
			App.guiController
					.setOutput("The city " + city.getName() + " was put under quarantine for " + rounds + " rounds");
			break;
		case closeAirport:
			App.guiController.setOutput(
					"The airport of the city " + city.getName() + " airport was closed for " + rounds + " rounds");
			break;
		case closeConnection:
			App.guiController.setOutput("The city " + city.getName() + " closed the connection to the city "
					+ action.getCityTo().getName() + " for " + rounds + " Rounds");
			break;
		case developVaccine:
			App.guiController.setOutput("Started developing vaccines for the pathogen " + pathogen.getName()
					+ ". Development will be finished in round " + (game.getRound() + 7));
			break;
		case deployVaccine:
			App.guiController.setOutput(
					"Deployed vaccines in the city " + city.getName() + " for the pathogen " + pathogen.getName());
			break;
		case developMedication:
			App.guiController.setOutput("Started developing medication for the pathogen " + pathogen.getName()
					+ ". Development will be finished in round " + (game.getRound() + 3));
			break;
		case deployMedication:
			App.guiController.setOutput(
					"Deployed medication in the city " + city.getName() + " for the pathogen " + pathogen.getName());
			break;
		case exertInfluence:
			App.guiController.setOutput("Economy of the city " + city.getName() + " will randomly be incread");
			break;
		case callElections:
			App.guiController.setOutput("Elections were called for the city " + city.getName());
			break;
		case applyHygienicMeasures:
			App.guiController.setOutput("Hygiene standards of the city " + city.getName() + "will be randomly reset");
			break;
		case launchCampaign:
			App.guiController
					.setOutput("Launched informative campaign to raise awareness in the city " + city.getName());
			break;
		default:
			App.guiController.setOutput("Could not identify executed action");
		}
	}

	/**
	 * Returns the amount given by the user in the amount text field. If the number
	 * is less or equal to zero or no number is given the method returns 1.
	 * 
	 * @return Amount in text field.
	 */
	private int getAmount() {
		int amount;
		try {
			// Return amount out of the text field
			amount = Integer.parseInt(this.amountT.getText());
		} catch (Exception e) {
			// Invalid option in amount field
			// Return default value
			amount = 1;
		}

		return amount > 0 ? amount : 1;
	}

	/**
	 * Returns the number of rounds given by the user in the rounds text field. If
	 * the number is less or equal to zero or no number or an invalid one is given
	 * the method returns 1.
	 * 
	 * @return Amount in text field.
	 */
	private int getRounds() {
		int rounds;
		try {
			// Return number of rounds out of the text field
			rounds = Integer.parseInt(this.roundsT.getText());
		} catch (Exception e) {
			// Invalid option in rounds field
			// Return default value
			rounds = 1;
		}

		return rounds > 0 ? rounds : 1;
	}

	/**
	 * Returns the city selected by the user in the city choice box
	 * 
	 * @return Selected city.
	 */
	private City getSelectedCity() {
		return this.currentGame.getCity(this.citiesCB.getValue());
	}

	/**
	 * Returns the city selected by the user in the cityTo choice box
	 * 
	 * @return Selected city in the cityTo choice box.
	 */
	private City getSelectedCityTo() {
		return this.currentGame.getCity(this.citiesToCB.getValue());
	}

	/**
	 * Returns the pathogen selected by the user in the pathogens choice box
	 * 
	 * @return Selected pathogen.
	 */
	private Pathogen getSelectedPathogen() {
		return this.currentGame.getPathogen(this.pathogenesCB.getValue());
	}

	/**
	 * On action method for the "End round" button in the GUI. When pressed send the
	 * end round action
	 */
	@FXML
	private void endRound() {

		// Get amount
		int amount = getAmount();

		// Add actions into action queue
		for (int i = 0; i < amount; i++) {
			GameServer.addReply((Game g) -> new Action(g).toString());
		}

		// Set output
		App.guiController.setOutput(new Action(this.currentGame), this.currentGame);

		// Execute first action
		this.executeAction();
	}

	/**
	 * On action method for the "Auto turn" button in the GUI. When pressed the end
	 * round action is passed to the GameExchange
	 */
	@FXML
	private void autoTurn() {

		// Get amount
		int amount = getAmount();

		// Add actions into action queue
		for (int i = 0; i < amount; i++) {
			GameServer.addReply((Game g) -> ActionHeuristic.solve(g));
		}

		// Execute first action
		this.executeAction();
	}

	/**
	 * On action method for the "Quarantine" button in the GUI. The action requires
	 * a city and rounds as its input. When pressed the quarantine action is passed
	 * to the GameExchange. If no city or amount of rounds was selected the method
	 * does nothing.
	 */
	@FXML
	private void putUnderQuarantine() {
		// Get the selected city
		City city = this.getSelectedCity();

		// Get the rounds specified by the user
		int rounds = this.getRounds();

		// Execute action if a valid city was selected
		if (city != null) {

			// Create the action and calculate its cost.
			Action a = new Action(ActionType.putUnderQuarantine, this.currentGame, city, rounds);
			int costs = (10 * a.getRounds() + 20);

			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints()) + " points");
			}

		} else {
			App.guiController.setOutput("Missing selected city");
		}

		this.update();
	}

	/**
	 * On action method for the "Close Airport" button in the GUI. The action
	 * requires a city and rounds as its input. When pressed the quarantine action
	 * is passed to the GameExchange. If no city or amount of rounds was selected
	 * the method does nothing.
	 */
	@FXML
	private void closeAirport() {
		// Get the selected city
		City city = this.getSelectedCity();

		// Get the rounds specified by the user.
		int rounds = this.getRounds();

		// Execute action if a valid city was selected.
		if (city != null) {

			// Create the action and calculate its cost.
			Action a = new Action(ActionType.closeAirport, this.currentGame, city, rounds);
			int costs = (5 * a.getRounds() + 15);

			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints() + " points"));
			}

		} else {
			App.guiController.setOutput("Missing selected city");
		}

		this.update();
	}

	/**
	 * On action method for the "Close Connection" button in the GUI. The action
	 * requires two cities and rounds as its input. When pressed the quarantine
	 * action is passed to the GameExchange. If no cities or amount of rounds was
	 * selected the method does nothing.
	 */
	@FXML
	private void closeConnection() {
		// Get the selected city.
		City city = this.getSelectedCity();

		// Get the selected cityTo.
		City cityTo = this.getSelectedCityTo();

		// Get the rounds specified by the user.
		int rounds = this.getRounds();

		// Execute action if two valid cities were selected.
		if (city != null && cityTo != null) {

			// Create the action and calculate its cost.
			Action a = new Action(this.currentGame, city, cityTo, rounds);
			int costs = (3 * a.getRounds() + 3);
			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints()) + " points");
			}

			return;

		} else if (city == null && cityTo != null) {
			App.guiController.setOutput("Missing the first city");

		} else if (city != null && cityTo == null) {
			App.guiController.setOutput("Missing the second city");

		} else {
			App.guiController.setOutput("Missing selected cities");
		}

		this.update();
	}

	/**
	 * On action method for the "Develop Vaccine" button in the GUI. The action
	 * requires a pathogen as its input. When pressed the develop vaccine action is
	 * passed to the GameExchange. If no pathogen was selected the method does
	 * nothing.
	 */
	@FXML
	private void developVaccine() {

		// Get the selected pathogen
		Pathogen pathogen = this.getSelectedPathogen();

		// Execute action if a valid pathogen was selected
		if (pathogen != null) {

			// Create the action and calculate its cost.
			Action a = new Action(ActionType.developVaccine, this.currentGame, pathogen);
			int costs = 40;
			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints()) + " points");
			}

		} else {
			App.guiController.setOutput("Missing selected pathogen");
		}

		this.update();
	}

	/**
	 * On action method for the "Deploy Vaccine" button in the GUI. The action
	 * requires a pathogen and a city as its input. When pressed the deploy vaccine
	 * action is passed to the GameExchange. If no pathogen or city was selected the
	 * method does nothing.
	 */
	@FXML
	private void deployVaccine() {

		// Get the selected city
		City city = this.getSelectedCity();

		// Get the selected pathogen
		Pathogen pathogen = this.getSelectedPathogen();

		// Execute action if a valid city and pathogen were selected
		if (city != null && pathogen != null) {

			Action a = new Action(ActionType.deployVaccine, this.currentGame, city, pathogen);
			int costs = 10;
			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints()) + " points");
			}

		} else if (city == null && pathogen != null) {
			App.guiController.setOutput("Missing selected city");

		} else if (city != null && pathogen == null) {
			App.guiController.setOutput("Missing selected pathogen");

		} else {
			App.guiController.setOutput("Missing selected city and pathogen");

		}

		this.update();
	}

	/**
	 * On action method for the "Develop Medication" button in the GUI. The action
	 * requires a pathogen as its input. When pressed the develop medication action
	 * is passed to the GameExchange. If no pathogen was selected the method does
	 * nothing
	 */
	@FXML
	private void developMedication() {

		// Get the selected pathogen
		Pathogen pathogen = this.getSelectedPathogen();

		// Execute action if a valid pathogen was selected
		if (pathogen != null) {

			// Create the action and calculate its cost.
			Action a = new Action(ActionType.developMedication, this.currentGame, pathogen);
			int costs = 20;

			if (this.currentGame.getPoints() >= costs) {
				App.guiController.setOutput(a, this.currentGame);
				this.executeAction(a);

			} else {
				App.guiController.setOutput("Missing " + (costs - this.currentGame.getPoints()) + " points");
			}

		} else {
			App.guiController.setOutput("Missing selected pathogen");
		}
	}

	/**
	 * On action method for the "Deploy Medication" button in the GUI. The action
	 * requires a pathogen and a city as its input. When pressed the deploy
	 * medication action is passed to the GameExchange. If no pathogen or city was
	 * selected the method does nothing.
	 */
	@FXML
	private void deployMedication() {

		// Get the selected city
		City city = this.getSelectedCity();

		// Get the selected pathogen
		Pathogen pathogen = this.getSelectedPathogen();

		// Execute action if a valid city and pathogen were selected
		if (city != null && pathogen != null) {
			this.executeAction(new Action(ActionType.deployMedication, this.currentGame, city, pathogen).toString());
		}
	}

	/**
	 * Executes an reroll action of the given type in the selected city.
	 * 
	 * @param type Type of action to execute.
	 */
	private void doRerollAction(ActionType type) {

		// Get the selected city
		City city = this.getSelectedCity();

		// Execute action if a valid city was selected
		if (city != null) {
			this.executeAction(new Action(type, this.currentGame, city));
		}
	}

	/**
	 * On action method for the "Apply Hygiene" button in the GUI. The action
	 * requires a city as its input. When pressed the apply hygiene action is passed
	 * to the GameExchange. If no city was selected the method does nothing.
	 */
	@FXML
	private void applyHygienicMeasures() {
		this.doRerollAction(ActionType.applyHygienicMeasures);
	}

	/**
	 * On action method for the "Exert Influence" button in the GUI. The action
	 * requires a city as its input. When pressed the exert influence action is
	 * passed to the GameExchange. If no city was selected the method does nothing.
	 */
	@FXML
	private void exertInfluence() {
		this.doRerollAction(ActionType.exertInfluence);
	}

	/**
	 * On action method for the "Call Elections" button in the GUI. The action
	 * requires a city as its input. When pressed the call elections action is
	 * passed to the GameExchange. If no city was selected the method does nothing.
	 */
	@FXML
	private void callElections() {
		this.doRerollAction(ActionType.callElections);
	}

	/**
	 * On action method for the "Launch Campaign" button in the GUI. The action
	 * requires a city as its input. When pressed the launch campaign action is
	 * passed to the GameExchange. If no city was selected the method does nothing.
	 */
	@FXML
	private void launchCampaign() {
		this.doRerollAction(ActionType.launchCampaign);
	}

	/**
	 * On action method for the "Vaccinate big Cities" button in the GUI. The action
	 * requreds a pathogen and can be given a number of cities it should try to
	 * vaccinate as its input. When pressed the method calculates which cities will
	 * be the most affected by vaccination and adds the deploy vaccines action for
	 * those cities to the repliesToSend Queue in the GameServer If no pathogen was
	 * selected the method does nothing. If no number of cities was given this will
	 * vaccinate the single most effected city.
	 */
	@FXML
	private void vaccinateBiggestCities() {

		// Get the selected pathogen
		Pathogen selectedPathogen = this.getSelectedPathogen();

		// If no valid pathogen was selected do nothing and return
		if (selectedPathogen == null) {
			return;
		}

		// Get the selected amount
		int amount = this.getAmount();

		for (int i = 0; i < amount; i++) {

			GameServer.addReply((Game g) -> {
				// Due to the fact, that we check the pathogen for equality by reference
				// we need to get the pathogen out of the current game and cannot use the
				// selected pathogen of the GUI
				Pathogen pathogen = g.getPathogen(selectedPathogen.getName());

				// Search the city with the most uninfected people to maximize the resulting
				// immunity
				City bestCity = g.getCities().stream() // Search all cities
						.filter(c -> c.getVaccineDeployed().stream().allMatch(e -> e.getPathogen() != pathogen)) // Filter
																													// cities
																													// that
																													// were
																													// already
																													// vaccinated
						.max((City c1, City c2) -> {
							// Get the healthy population as we want to vaccinate the city which impacts the
							// most people
							double healthyPopulation1 = c1.isInfected(pathogen) ? 1 - c1.getPrevalance() : 1;
							double healthyPopulation2 = c2.isInfected(pathogen) ? 1 - c2.getPrevalance() : 1;

							return (int) (c1.getPopulation() * healthyPopulation1
									- c2.getPopulation() * healthyPopulation2);
						}).orElseGet(() -> null);

				// If no city is infected by the selected pathogen just end the round
				// and delete all vaccinate biggest cities actions
				if (bestCity == null) {
					GameServer.clearReplies();
					return new Action(g).toString();
				}

				// Return the new Action which medicates the city where currently the most
				// people are not yet infected by the pathogen
				return new Action(ActionType.deployVaccine, g, bestCity, pathogen).toString();
			});
		}

		// Execute the Action generated addReply methode above.
		this.executeAction();

	}

	/**
	 * On action method for the "Medicate big Cities" button in the GUI. The action
	 * requreds a pathogen and can be given a number of cities it should try to
	 * medicate as its input. When pressed the method calculates which cities will
	 * be the most affected by medication and adds the deploy mediaction action for
	 * those cities to the repliesToSend Queue in the GameServer If no pathogen was
	 * selected the method does nothing. If no number of cities was given this will
	 * medicate the single most effected city.
	 */
	@FXML
	private void medicateBiggestCities() {

		// Get the selected pathogen
		Pathogen selectedPathogen = this.getSelectedPathogen();

		// If no valid pathogen was selected do nothing and return
		if (selectedPathogen == null) {
			return;
		}

		// Get the selected amount
		int amount = this.getAmount();

		for (int i = 0; i < amount; i++) {

			GameServer.addReply((Game g) -> {
				// Due to the fact, that we check the pathogen for equality by reference
				// we need to get the pathogen out of the current game and cannot use the
				// selected pathogen of the GUI
				Pathogen pathogen = g.getPathogen(selectedPathogen.getName());

				// Search the city with the most infected people to maximize the resulting
				// healing
				City bestCity = g.getCities().stream() // Search all cities
						.filter(c -> c.isInfected(pathogen)) // Filter cities that are not infected by the
																// pathogen
						.max((City c1,
								City c2) -> (int) (c1.getPopulation() * c1.getPrevalance()
										- c2.getPopulation() * c2.getPrevalance()))
						.orElseGet(() -> null);

				// If no city is infected by the selected pathogen just end the round
				// and delete all vaccinate biggest cities actions
				if (bestCity == null) {
					GameServer.clearReplies();
					return new Action(g).toString();
				}

				// Return the new Action which medicates the city where currently the most
				// people are influenced by the selected pathogen.
				return new Action(ActionType.deployMedication, g, bestCity, pathogen).toString();
			});
		}

		// Execute the Action generated addReply methode above.
		this.executeAction();

	}

	/**
	 * Executes the given action.
	 * 
	 * @param action Action to execute.
	 */
	public void executeAction(String action) {
		// Check if there is an active GameExchange
		if (this.currentGameExchange == null || !this.currentGameExchange.isAlive()) {
			this.currentGameExchange = null;
			return;
		}

		this.currentGameExchange.sendReply(action);
		this.currentGameExchange = null;
	}

	/**
	 * Executes the given action.
	 * 
	 * @param action Action to execute.
	 */
	public void executeAction(Action action) {
		this.executeAction(action.toString());
	}

	/**
	 * Executes the first action out of the action queue.
	 */
	public void executeAction() {
		if (GameServer.hasReplies()) {
			this.executeAction(GameServer.getReply().evaluate(this.currentGame));
		}
	}

	/**
	 * On action method for the "Export Map" button in the GUI. When pressed this
	 * will export the current representation of the game state in the Canvas as a
	 * picture.
	 */
	@FXML
	public void exportMap() {
		this.currentMap.snapshot((SnapshotResult sr) -> {
			FileHandler.writeFile("Test.png", sr.getImage());
			return null;
		}, null, null);
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
		Collection<City> cities = this.currentGame.getCities();
		City selectedCity = this.getSelectedCity();
		Pathogen selectedPathogen = this.getSelectedPathogen();

		// Stream all cities find the one we want to draw and draw those
		cities.stream().filter(c -> !this.showDistinctCityCB.getValue().contains("Healthy") || !c.isInfected())
				.filter(c -> !this.showDistinctCityCB.getValue().contains("Infected")
						|| c.isInfected(selectedPathogen) && selectedPathogen != null
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

		Pathogen selectedPathogen = this.getSelectedPathogen();

		// Initialize the canvas as a 2D graphics object.
		GraphicsContext gc = this.currentMap.getGraphicsContext2D();

		// Get all the info from the current city.
		String cityName = city.getName();
		int diameter = city.getPopulation();
		int x = (int) city.getX() + 180;
		int y = (int) -city.getY() + 90;

		// If no pathogen is selected or the selected pathogen matches the cities
		// outbreak
		// set the prevalence
		double prev = selectedPathogen == null || city.getPathogen() == selectedPathogen ? city.getPrevalance() : 0.0;

		gc.setFill(new Color(prev, 0, 0, prev));

		// adjust x, y, diameter so it fits nicely on the canvas.
		// TODO: Get actual width/height
		x *= Math.min(this.currentMap.getWidth(), this.currentMap.getHeight() * 2) / 360;
		y *= Math.min(this.currentMap.getWidth(), this.currentMap.getHeight() * 2) / 360;
		diameter *= 12 / Math.min(this.currentMap.getWidth(), this.currentMap.getHeight() * 2);

		// Draw Population Circle. If wanted.
		if (populationBox.isSelected())
			gc.strokeOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Color for infectedRate. If a pathogen is selected and showInfected is true.
		if (infectedBox.isSelected())
			gc.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Draw City Name. If wanted.
		if (cityNamesBox.isSelected()) {
			// TODO: Find height and width of text
			double height = 0;
			double width = 0;

			gc.strokeText(cityName, x - height / 2, y - width / 2);
		}
	}
}
