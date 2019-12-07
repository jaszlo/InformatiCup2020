package app.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import app.game.events.E_Outbreak;
import app.game.events.E_PathogenEncounter;
import app.game.City;
import app.game.Game;
import app.game.Virus;
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
import javafx.util.Pair;
import javafx.scene.canvas.GraphicsContext;

public class GuiController {

	/// Elements of the MAP
	private City selectedCity = null;
	private Virus selectedPathogen = null;

	// private booleans for the checkboxes.
	boolean showConnections, showPopulation, showInfected, showCityNames;

	// All FXML elements that have interaction.
	@FXML
	private Button selectCityB, selectAllHealthyCitiesB, selectInfectedCitiesB, resetB;

	@FXML
	private TextField selectCityT;

	@FXML // CityInfo
	private Text population, economy, goverment, awareness, hygiene, events;

	@FXML // PathogenInfo
	private Text infectivity, mobility, duration, lethality, prevalance;
	
	@FXML
	private CheckBox connectionBox, populationBox, infectedBox, cityNamesBox;

	@FXML
	private ChoiceBox<String> selectedPathogenCB;

	@FXML
	private Canvas currentMap;
	private GraphicsContext gc;

	/// Elements of the Controller
	// All Buttons (each button corresponds to one event.
	@FXML
	private Button quitB, endRoundB, putUnderQuarantineB, closeAirportB, closeConnectionB, developVaccineB,
			deployVaccineB, developMedicationB, deployMedicationB, applyHygienicMeasuresB, exertInfluenceB,
			callElectionsB, launchCampaignB, medicateBiggestCitiesB, autoTurnB;

	private GameExchange currentGameExchange;
	private Game currentGame;

	@FXML
	private TextField cityFrom, cityTo, rounds, amountT;

	@FXML
	private Text currentRound, currentPoints, lastAction;
	private static String lastActionString;
	// Constructor
	public GuiController() {

		/*
		 * Has no real purpose. The Class-Objects (GUI-elements) will be assigned to the
		 * corresponding element in the fxml-file.
		 */
//		System.out.println("GUI has been created.");
	}

	public void initialize() {

		// Create a default selection for the CheckBoxes.
		this.connectionBox.setSelected(true);
		this.populationBox.setSelected(true);
		this.infectedBox.setSelected(true);
		this.cityNamesBox.setSelected(true);
		// Set all checkBox booleans as the selection.
		this.showConnections = true;
		this.showPopulation = true;
		this.showInfected = true;
		this.showCityNames = true;

		// Initialize the canvas as a 2D graphics object.
		this.gc = this.currentMap.getGraphicsContext2D();

		// Set the selected City/Pathogen back to null
		this.selectedCity = null;
		this.selectedPathogen = null;

		// Setting up a prompt text in the textField. Therefore when selected it will
		// disappear.
		this.cityFrom.setPromptText("enter city...");
		this.cityTo.setPromptText("enter city...");
		this.rounds.setPromptText("enter duration...");
		this.amountT.setPromptText("enter the amount ...");

		// Set Points and current Round.
		if (this.currentGameExchange != null) {
			this.currentRound.setText(this.currentGame.getRound() + "");

			// uncomment when points are implemented.
			this.currentPoints.setText(this.currentGame.getPoints() + "");

		}

		// Set the last Action in the TextField to the static String where it was saved.
		this.lastAction.setText(lastActionString);

		// Set the Button Tooltips.
		this.quitB.setTooltip(new Tooltip("Close the server and this window."));
		this.endRoundB.setTooltip(new Tooltip("End the current round.\nRequired input:\n none"));
		this.putUnderQuarantineB.setTooltip(new Tooltip("Put a city under qurantine.\nRequired input:\ncity name\nrounds"));
		this.closeAirportB.setTooltip(new Tooltip("Close a city's airport.\nRequired input:\ncity name\nround"));
		this.closeConnectionB.setTooltip(new Tooltip("Close a connection between two cities.\nRequired input:"));
		this.developVaccineB.setTooltip(new Tooltip("Develop a vaccine against a certain pathogen.\nRequired input:\npathogen name"));
		this.deployVaccineB.setTooltip(new Tooltip("Deploy a vaccine in against a certain pathogen in a certain city.\nRequired input:\npathogen name\ncity name"));
		this.developMedicationB.setTooltip(new Tooltip("Develop a medication against a certain pathogen.\nRequired input:\npathogen name"));
		this.deployMedicationB.setTooltip(new Tooltip("Deploy a medication in against a certain pathogen in a certain city.\nRequired input:\npathogen name\ncity name"));
		this.applyHygienicMeasuresB.setTooltip(new Tooltip("Randomly increase hygine in a certain city\nRequired input:\n city name"));
		this.exertInfluenceB.setTooltip(new Tooltip("Reset the economic strength of a certain city\nRequired input:\ncity name"));
		this.callElectionsB.setTooltip(new Tooltip("Reset the political strength of a certain city\nRequired input:\ncity name"));
		this.launchCampaignB.setTooltip(new Tooltip("The attentiveness of a certain city's population will be increased\nRequired input:\ncity name"));
		this.autoTurnB.setTooltip(new Tooltip("Let the computer play.\nRequired input:\n None. Although an amount of turn can be entered."));
		
		// Set the
		
		// Update the ChoiceBox items.
		this.updatePathogenChoiceBox();
		
		// Draw Call for the MapCanvas
		drawMap();
	}

	// Getter (for the GUI-Controller)
	public Virus getSlectedPathogen() {
		return this.selectedPathogen;
	}

	// is called from the quitButton from the GuiController.
	@FXML
	public void quit() {
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();
		if (primaryStage == null)
			System.out.println("error");
		primaryStage.close();
		System.exit(1);
	}

	@FXML // start of checkBox methodes.
	private void checkShowConnections() {
		this.showConnections = !this.showConnections;
		drawMap();
	}

	@FXML
	private void checkShowPopulation() {

		this.showPopulation = !this.showPopulation;
		drawMap();
	}

	@FXML
	private void checkShowInfected() {

		this.showInfected = !this.showInfected;
		drawMap();
	}

	@FXML
	private void checkShowCityNames() {

		this.showCityNames = !this.showCityNames;
		drawMap();
	} // End of checkBox methodes

	@FXML // Reset Button
	private void reset() {
		initialize();
		drawMap();
	}

	@FXML // selectCityButton
	private void selectCity() {

		// Get the city's name from the textField
		String selectedCity = this.selectCityT.getText();
		// Get the city object via its name.
		this.selectedCity = this.currentGame.getCities().get(selectedCity);
		if (this.selectedCity == null) {
			System.out.println("City not found");
			return;
		}

		// show the info about the selected city in the mapUI.
		setCityInfo();
		setPathogenInfo();
		drawMap();
	}

	// TextSetter called by the selectCityButton
	private void setCityInfo() {

		if (this.selectedCity == null)
			return;

		// Get all the city info as Strings.
		String population = this.selectedCity.getPopulation() + "";
		String economy = this.selectedCity.getEconomy().toString();
		String goverment = this.selectedCity.getGovernment().toString();
		String hygiene = this.selectedCity.getHygiene().toString();
		String awareness = this.selectedCity.getAwareness().toString();
		String eventString = this.selectedCity.getPrevalance() + "";
		
		// Set all the info to the corresponding textElemt.
		this.population.setText(population);
		this.economy.setText(economy);
		this.goverment.setText(goverment);
		this.hygiene.setText(hygiene);
		this.awareness.setText(awareness);
		this.events.setText(eventString);

	}

	// TextSetter called onAction from the selectPathogenChoiceBox
	private void setPathogenInfo() {

		if (this.selectedPathogen == null)
			return;

		String infectivity = this.selectedPathogen.getInfectivity().toString();
		String mobility = this.selectedPathogen.getMobility().toString();
		String duration = this.selectedPathogen.getDuration().toString();
		String lethality = this.selectedPathogen.getLethality().toString();
		String prevalance = new String();

		// Get the prevalance for the selected City if one has been selected.
		if (this.selectedCity != null) {
			for (E_Outbreak e : this.currentGame.getOutbreakEvents()) {
				if (e.getCity() == this.selectedCity) {
					prevalance = e.getPrevalence() + "";
					break;
				}
			}
		}

		this.infectivity.setText(infectivity);
		this.mobility.setText(mobility);
		this.duration.setText(duration);
		this.lethality.setText(lethality);
		this.prevalance.setText(prevalance);
	}

	@FXML // Button implementation
	private void selectInfectedCities() {

		if (this.selectedPathogen == null)
			return;

		this.gc.setFill(Color.WHITE);
		this.gc.setStroke(Color.BLACK);
		this.gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());
		
		for (E_Outbreak e : this.currentGame.getOutbreakEvents()) {
			if (this.selectedPathogen == e.getVirus())
				drawCity(e.getCity(), currentGame);
		}
	}

	@FXML // Button implementation
	private void selectAllHealthyCities() {

		this.gc.setFill(Color.WHITE);
		this.gc.setStroke(Color.BLACK);
		this.gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());
		
		for (City c : this.currentGame.getCities().values()) {
			if (c.getOutbreak() == null)
				drawCity(c, currentGame);
		}
	}

	// Draw Map methodes.
	public void drawMap() {

		if (this.currentGameExchange == null)
			return;

		Game currentGame = this.currentGame;

		// Set fill, stroke and clear the whole canvas
		this.gc.setFill(Color.WHITE);
		this.gc.setStroke(Color.BLACK);
		this.gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		// Get the City HashMap
		HashMap<String, City> cities = currentGame.getCities();

		// Iterate over the City HashMap
		Iterator<Entry<String, City>> iterator = cities.entrySet().iterator();

		while (iterator.hasNext()) {

			// Get the current City.
			HashMap.Entry<String, City> pair = (HashMap.Entry<String, City>) iterator.next();
			City currentCity = pair.getValue();

			// don't ask questions. It works.
			if ((this.selectedCity != null
					&& (this.selectedCity == currentCity
							|| (this.selectedCity.getConnections().contains(currentCity) && this.showConnections))
					|| this.selectedCity == null)) {

				drawCity(currentCity, currentGame);
			}

		}
	}

	// Draws one City on the Canvas (and all additional features)
	private void drawCity(City currentCity, Game currentGame) {

		currentGame = this.currentGame;
		// Get all the info from the current city.
		String cityName = currentCity.getName();
		int diameter = currentCity.getPopulation() / 120;
		int x = (int) currentCity.getX() + 180;
		int y = (int) -currentCity.getY() + 90;
		double prev = 0;

		// Get prevalance in the current city.
		for (E_Outbreak e : currentGame.getOutbreakEvents()) {
			if (e.getCity() == currentCity && (this.selectedPathogen == null || e.getVirus() == this.selectedPathogen))
				// Get the strongest infection in one city (if multiple infections are to be
				// shown).
				prev = Math.max(prev, e.getPrevalence());
		}

		this.gc.setFill(new Color(prev, 0, 0, prev));

		// adjust x,y so it fits nicely on the canvas.
		x *= 4;
		y *= 5;

		// Draw Population Circle. If wanted.
		if (this.showPopulation)
			this.gc.strokeOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Color for infectedRate. If a pathogen is selected and showInfected is true.
		if (this.showInfected)
			this.gc.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		// Draw City Name. If wanted.
		if (this.showCityNames)
			this.gc.strokeText(cityName, x, y);

	}
	// End of draw map methodes.

	@FXML // onAction call for the selectPathogen choiceBox
	private void changeSelectedPathogen() {

		Game currentGame = this.currentGame;
		String pathogen = this.selectedPathogenCB.getValue();
		this.selectedPathogen = currentGame.getViruses().get(pathogen);
		setPathogenInfo();
		setCityInfo();
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

			GameServer.addReply((Game g) -> "{\"type\": \"endRound\"}");
		}

//		System.out.println("endRound");
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

	//helper methode
	private void setLastAction(String s) {
		GuiController.lastActionString = s;
	}

	@FXML
	private void putUnderQuarantine() {
		
//		System.out.println("quarantine");
		this.executeEvent("{\"type\": \"putUnderQuarantine\", \"city\":\"" + this.cityFrom.getText()
				+ "\", \"rounds\": " + this.rounds.getText() + "}");
	}

	@FXML
	private void closeAirport() {

//		System.out.println("closeAirport");
		this.executeEvent("{\"type\": \"closeAirport\", \"city\": \"" + this.cityFrom.getText() + "\", \"rounds\": "
				+ this.rounds.getText() + "}");
	}

	@FXML
	private void closeConnection() {

//		System.out.println("closeConnection");
		this.executeEvent("{\"type\": \"closeConnection\", \"fromCity\": \"" + this.cityFrom.getText()
				+ "\", \"toCity\": \"" + this.cityTo.getText() + "\", \"rounds\": " + this.rounds.getText() + "}");
	}

	@FXML
	private void developVaccine() {

//		System.out.println("developVaccine");
		this.executeEvent(
				"{\"type\": \"developVaccine\", \"pathogen\": \"" + this.selectedPathogenCB.getValue() + "\"}");
	}

	@FXML
	private void deployVaccine() {

		String cityAmount = this.amountT.getText();
		String selectedPathogen = this.selectedPathogenCB.getValue();

		if (selectedPathogen.equals(""))
			return;

		if (this.cityFrom.getText().equals("")) {

			// check if the actions can be added.
			if (cityAmount == null || selectedPathogen == null)
				return;

			// convert to Integer
			int cityAmountInt = 0;
			try {
				cityAmountInt = Integer.parseInt(cityAmount);
			} catch (NumberFormatException e) {
//				Amount field empty
			}

			for (int i = 0; i < cityAmountInt; i++) {

				GameServer.addReply((Game g) -> {
					Collection<City> cities = g.getCities().values();
					ArrayList<Pair<City, Double>> citiesPrev = new ArrayList<>();

					// get all cities that are infected by the selected Pathogen.
					for (City c : cities) {
						double prev = 0;
						boolean alreadyVacc = false;
						if (c.getOutbreak() != null) {
							if (selectedPathogen.equals(c.getOutbreak().getVirus().getName()))
								prev = c.getPrevalance();
						}
						if (c.getVaccineDeployed() != null) {
							alreadyVacc = true;
						}
						if (!alreadyVacc)
							citiesPrev.add(new Pair<>(c, prev));
					}

					// sort the infectedCities by size.
					// don't ask questions. It works.
					citiesPrev.sort((Pair<City, Double> c2,
							Pair<City, Double> c1) -> (int) ((c1.getKey().getPopulation() * (1 - c1.getValue()))
									- (c2.getKey().getPopulation()) * (1 - c2.getValue())));

					return "{\"type\": \"deployVaccine\", \"pathogen\": \"" + selectedPathogen + "\", \"city\": \""
							+ citiesPrev.get(0).getKey().getName() + "\"}";
				});

			}

			this.executeEvent(GameServer.getReply().evaluate(currentGame));
		} else {

			System.out.println("developVaccine");
			this.executeEvent("{\"type\": \"deployVaccine\", \"pathogen\": \"" + selectedPathogen + "\", \"city\": \""
					+ this.cityFrom.getText() + "\"}");
		}
	}

	@FXML
	private void developMedication() {

		System.out.println("developMedication");
		this.executeEvent(
				"{\"type\": \"developMedication\", \"pathogen\": \"" + this.selectedPathogenCB.getValue() + "\"}");
	}

	@FXML
	private void deployMedication() {

//		System.out.println("deployMedication");
		this.executeEvent("{\"type\": \"deployMedication\", \"pathogen\": \"" + this.selectedPathogenCB.getValue()
				+ "\", \"city\": \"" + this.cityFrom.getText() + "\"}");
	}

	@FXML
	private void applyHygienicMeasures() {

//		System.out.println("applyHygienicMeasures");
		this.executeEvent("{\"type\": \"applyHygienicMeasures\", \"city\":\"" + this.cityFrom.getText() + "\"}");
	}

	@FXML
	private void exertInfluence() {

//		System.out.println("exertInfluence");
		this.executeEvent("{\"type\": \"exertInfluence\", \"city\": \"" + this.cityFrom.getText() + "\"}");
	}

	@FXML
	private void callElections() {

//		System.out.println("callElections");
		this.executeEvent("{\"type\": \"callElections\", \"city\": \"" + this.cityFrom.getText() + "\"}");
	}

	@FXML
	private void launchCampaign() {

//		System.out.println("launchCampaign");
		this.executeEvent("{\"type\": \"launchCampaign\", \"city\": \"" + this.cityFrom.getText() + "\"}");
	}

	@FXML // Button implementation
	private void medicateBiggestCities() {

		String cityAmount = this.amountT.getText();
		String selectedPathogen = this.selectedPathogenCB.getValue();

		// check if the actions can be added.
		if (cityAmount == null || selectedPathogen == null)
			return;

		// convert to Integer
		int cityAmountInt = Integer.parseInt(cityAmount);

		for (int i = 0; i < cityAmountInt; i++) {

			GameServer.addReply((Game g) -> {
				HashSet<E_Outbreak> outbreaks = g.getOutbreakEvents();
				ArrayList<Pair<City, Double>> infectedCities = new ArrayList<>();

				// get all cities that are infected by the selected Pathogen.
				for (E_Outbreak e : outbreaks) {
					if (e.getVirus().getName().equals(selectedPathogen))
						infectedCities.add(new Pair<City, Double>(e.getCity(), e.getPrevalence()));
				}

				// sort the infectedCities by size.
				// don't ask questions. It works.
				infectedCities.sort((Pair<City, Double> c2,
						Pair<City, Double> c1) -> (int) ((c1.getKey().getPopulation() * c1.getValue())
								- (c2.getKey().getPopulation()) * c2.getValue()));

				return "{\"type\": \"deployMedication\", \"pathogen\": \"" + selectedPathogen + "\", \"city\": \""
						+ infectedCities.get(0).getKey().getName() + "\"}";
			});

		}

		this.executeEvent(GameServer.getReply().evaluate(currentGame));
	}

	private void executeEvent(String event) {
		if (this.currentGameExchange == null || !this.currentGameExchange.isAlive())
			return;
		setLastAction(event);
		this.currentGameExchange.sendReply(event);
		
		// close the GUI
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();
		if (primaryStage == null)
			System.out.println("error");
		primaryStage.close();
	}

	public void setGame(GameExchange exchange) {
		this.currentGameExchange = exchange;
		this.currentGame = this.currentGameExchange.getGame();
	}

	private void updatePathogenChoiceBox() {

		Game currentGame = this.currentGame;
		if (currentGame == null) {
			return;
		}

		ArrayList<E_PathogenEncounter> activePathogenes = new ArrayList<E_PathogenEncounter>(
				currentGame.getPathEncounterEvents());
		ArrayList<String> pathogenNames = new ArrayList<String>();

		for (E_PathogenEncounter p : activePathogenes) {
			pathogenNames.add(p.getVirus().getName());
		}

		ObservableList<String> activePathogenesList = FXCollections.observableList(pathogenNames);
		this.selectedPathogenCB.setItems(activePathogenesList);

	}
}
