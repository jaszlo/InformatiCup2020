package app.gui;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import app.game.events.E_Outbreak;
import app.game.City;
import app.game.Game;
import app.game.Virus;
import app.http.GameExchange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;

public class MapController {
	
	//GameState
	private GameExchange currentGameExchange;
	private Game currentGame;
	
	private City selectedCity = null;
	private Virus selectedPathogen = null;
	
	//private booleans for the checkboxes.
	boolean showConnections, showPopulation, showInfected, showCityNames;
	
	//All FXML elements that have interaction.
	@FXML
	private Button selectCityB, selectRoundB, printAllOutbreaksB, printSelectInfectedCitiesB, resetB;
	
	@FXML
	private TextField selectCityT, selectRoundT;
	
	@FXML //CityInfo
	private Text population, economy, goverment, awareness, hygiene, events;
	
	@FXML //PathogenInfo
	private Text infectivity, mobility, duration, lethality, prevalance;
	
	@FXML
	private CheckBox connectionBox, populationBox, infectedBox, cityNamesBox;

	@FXML
	private ChoiceBox<String> selectPathogenC;
	
	@FXML
	private Canvas currentMap;
	private GraphicsContext gc;
	
	
	//Constructor
	public MapController () {
		
		/*
		 * Has no real purpose. 
		 * The Class-Objects (GUI-elements) will be assigned
		 * to the corresponding element in the fxml-file.
		 */
		System.out.println("MapController has been created.");
	}
	
	public void initialize () {
		
		//create a default selection for the CheckBoxes.
		this.connectionBox.setSelected(true);
		this.populationBox.setSelected(true);
		this.infectedBox.setSelected(true);
		this.cityNamesBox.setSelected(true);
		//Set all checkBox booleans as the selection.
		this.showConnections = true;
		this.showPopulation = true;
		this.showInfected = true;
		this.showCityNames = true;
				
		//Initialize the canvas as a 2D graphics object.
		this.gc = this.currentMap.getGraphicsContext2D();
		
		//Set the selected City/Pathogen back to null
		this.selectedCity = null;
		this.selectedPathogen = null;
		 
		//Draw Call for the MapCanvas
		drawMap();
		
		//Update the ChoiceBox items.
		this.updatePathogenChoiceBox();
		
		System.out.println("MapController has been initialized.");
	}
	
	//Getter (for the GUI-Controller)
	public Virus getSlectedPathogen () {
		return this.selectedPathogen;
	}
	
	//is called from the quitButton from the GuiController.
	public void quit () {
		Stage primaryStage = (Stage) this.selectCityB.getScene().getWindow();
		if (primaryStage == null) System.out.println("error");
        primaryStage.close();
	}
	
	//Setter for the gameExchange
	public void setGame(GameExchange exchange) {
		this.currentGameExchange = exchange;
		this.currentGame = this.currentGameExchange.getGame();
	}
	
	
	
	
	
	@FXML //start of checkBox methodes.
	private void checkShowConnections () {

		this.showConnections =  !this.showConnections;
		drawMap();
	}
	
	@FXML
	private void checkShowPopulation () {

		this.showPopulation =  !this.showPopulation;
		drawMap();
	}
	
	@FXML
	private void checkShowInfected () {

		this.showInfected =  !this.showInfected;
		drawMap();
	}
	
	@FXML private void checkShowCityNames () {

		this.showCityNames =  !this.showCityNames;
		drawMap();
	} //End of checkBox methodes
	
	
	

	@FXML //Reset Button
	private void reset () {
		initialize();
		drawMap();
	}

	@FXML //selectCityButton
	private void selectCity () {
		
		//Get the city's name from the textField
		String selectedCity = this.selectCityT.getText();
		//Get the city object via its name.
		this.selectedCity = this.currentGame.getCities().get(selectedCity);	
		if (this.selectedCity == null) {
			System.out.println("City not found");
			return;
		}
		
		//show the info about the selected city in the mapUI.
		setCityInfo();
		setPathogenInfo();
		drawMap();
	}
	
	//TextSetter called by the selectCityButton
	private void setCityInfo () {
		
		if (this.selectedCity == null) return;
		
		//Get all the city info as Strings.
		String population = this.selectedCity.getPopulation() + "";
		String economy = this.selectedCity.getEconomy().toString();
		String goverment = this.selectedCity.getGovernment().toString();
		String hygiene = this.selectedCity.getHygiene().toString();
		String awareness = this.selectedCity.getAwareness().toString();
		
		//ISSUE: If in a city no event has happened this will create a NullPointerException.
//		HashSet<Event> events = this.currentGame.getGame().getFromCityEventMap(selectedCity);
//		String eventString = eventsToString(events);
		
		//Set all the info to the corresponding textElemt.
		this.population.setText(population);
		this.economy.setText(economy);
		this.goverment.setText(goverment);
		this.hygiene.setText(hygiene);
		this.awareness.setText(awareness);
//		this.events.setText(eventSting);
	
	}
	
	//TextSetter called onAction from the selectPathogenChoiceBox
	private void setPathogenInfo () {
		
		if (this.selectedPathogen == null) return;
		
		String infectivity = this.selectedPathogen.getInfectivity().toString();
		String mobility = this.selectedPathogen.getMobility().toString();
		String duration = this.selectedPathogen.getDuration().toString();
		String lethality = this.selectedPathogen.getLethality().toString();
		String prevalance =  new String();
		
		
		
		//Get the prevalance for the selected City if one has been selected.
		if (this.selectedCity != null) {
			for(E_Outbreak e: this.currentGame.getOutbreakEvents()) {
				if(e.getCity() == this.selectedCity) {
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
	
	
	
	@FXML //Button implementation
	private void printSelectInfectedCities () {
		
		if (this.selectedPathogen == null) return;
		
		System.out.println("The Virus " +  this.selectedPathogen.getName() + " has infected:");

		for (E_Outbreak e: this.currentGame.getOutbreakEvents()) {
			if (this.selectedPathogen == e.getVirus()) 
				System.out.print(e.getCity().getName() + ", ");
		}
			
		System.out.println();
	}
	
	
	@FXML //Button implementation
	private void printAllOutbreaks () {
		
		System.out.println("All current outbreaks:");
		for (E_Outbreak e: this.currentGame.getOutbreakEvents()) {
			
			System.out.println("Virus " + e.getVirus().getName() + " is active in " + e.getCity().getName());
		}	
		
		System.out.println();

	}
	
	
	//DRAW THE MAP STUF//
	public void drawMap () {	
		
		if (this.currentGameExchange == null) return;
		
		Game currentGame = this.currentGame;
		
		//Set fill, stroke and clear the whole canvas
		this.gc.setFill(Color.WHITE);
		this.gc.setStroke(Color.BLACK);
		this.gc.clearRect(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight());

		//Get the City HashMap
		HashMap<String, City> cities = currentGame.getCities(); 
		
		//Iterate over the City HashMap
		Iterator<Entry<String, City>> iterator = cities.entrySet().iterator();
		
		while(iterator.hasNext()) {
			
			//Get the current City.
			HashMap.Entry<String, City> pair = (HashMap.Entry<String, City>)iterator.next();
			City currentCity = pair.getValue();
			
			//don't ask questions. It works.
			if ((this.selectedCity != null && (this.selectedCity == currentCity 
						|| (this.selectedCity.getConnections().contains(currentCity)
								&& this.showConnections))
					|| this.selectedCity == null)) {
				
				drawCity(currentCity, currentGame);
			}
			
		}
	}
	
	//Draws one City on the Canvas (and all additional features)
	private void drawCity (City currentCity, Game currentGame) {
		
		currentGame = this.currentGame;
		//Get all the info from the current city.
		String cityName = currentCity.getName();
		int diameter = currentCity.getPopulation() / 120;
		int x = (int) currentCity.getX() + 180;  
		int y = (int) - currentCity.getY() + 90;
		double prev = 0;
		
		//Get prevalance in the current city.
		for(E_Outbreak e: currentGame.getOutbreakEvents()) {
			if(e.getCity() == currentCity && (this.selectedPathogen == null || e.getVirus() == this.selectedPathogen))
				//Get the strongest infection in one city (if multiple infections are to be shown).
				prev = Math.max(prev, e.getPrevalence());
		}
		
		this.gc.setFill(new Color(prev, 0, 0, prev));
		
		//adjust x,y so it fits nicely on the canvas.
		x *= 4;
		y *= 5;

		//Draw Population Circle. If wanted.
		if (this.showPopulation) 
			this.gc.strokeOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		//Color for infectedRate. If a pathogen is selected and showInfected is true.
		if (this.showInfected)
			this.gc.fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
		//Draw City Name. If wanted.
		if (this.showCityNames) 
			this.gc.strokeText(cityName, x, y);
		
	}
	
	private void updatePathogenChoiceBox () {
	
		Game currentGame = this.currentGame;
		
		List<String> activePathogenes = new ArrayList<String>(currentGame.getViruses().keySet());
		ObservableList<String> activePathogenesList = FXCollections.observableList(activePathogenes);
		this.selectPathogenC.setItems(activePathogenesList);
		
//		System.out.println(activePathogenesList.toString());
	}
	
	@FXML //onAction call for the selectPathogen choiceBox
	private void changeSelectedPathogen () {
		
		Game currentGame = this.currentGame;
		String pathogen = this.selectPathogenC.getValue();
		this.selectedPathogen = currentGame.getViruses().get(pathogen);
		setPathogenInfo();
		setCityInfo();
		drawMap();
	}	
}
