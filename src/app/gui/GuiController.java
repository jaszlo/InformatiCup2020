package app.gui;

//Imports for the GUI
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import app.game.events.E_Outbreak;
import app.game.City;
import app.game.Game;
import app.http.GameExchange;
import app.http.GameServer;
import app.solver.Main;
import javafx.stage.Stage;
import javafx.util.Pair;

public class GuiController {
	
	//All Buttons (each button corresponds to one event.
	@FXML
	private Button quitB, endRoundB, putUnderQuarantineB, closeAirportB, closeConnectionB,
	developVaccineB, deployVaccineB, developMedicationB, deployMedicationB, getGameB,
	applyHygienicMeasuresB, exertInfluenceB, callElectionsB, launchCampaignB, medicateBiggestCitiesB,
	autoTurn;
	
	private GameExchange currentGame;
	private MapController mapControllerRefrence;
	
	@FXML
	private TextField cityfrom, cityto, rounds, pathogen, amountT;
	
	@FXML
	private Text currentRound, currentPoints;
	
	
	//Constructor
	public GuiController () {
		/*
		 * Has no real purpose. 
		 * The Class-Objects (GUI-elements) will be assigned
		 * to the corresponding element in the fxml-file.
		 */
		System.out.println("GuiController has been created.");
	}
	
	@FXML
	public void initialize () {
		
		System.out.println("GuiController has been initialized.");
		
		//Setting up a prompt text in the textField. Therefore when selected it will disappear.
		this.cityfrom.setPromptText("enter city...");
		this.cityto.setPromptText("enter city...");
		this.rounds.setPromptText("enter duration...");
		this.pathogen.setPromptText("enter pathogen...");
		this.amountT.setPromptText("enter the amount ...");
	
		//Set Points and current Round.
		if(this.currentGame != null) {
			this.currentRound.setText(this.currentGame.getGame().getRound() + "");
//			MapFactory.createMap(this.currentGame.getGame());			
			
			//uncomment when points are implemented.
			this.currentPoints.setText(this.currentGame.getGame().getPoints() + "");
			
		}
	}	
	
	public void setMapResource (MapController mapController) {
		
		this.mapControllerRefrence = mapController;
	}
	
	//Button-Events:
	@FXML
	private void quit () {
		
//		System.out.println("quit");
		this.mapControllerRefrence.quit();
		Stage primaryStage = (Stage) quitB.getScene().getWindow();
		if (primaryStage == null) System.out.println("error");
        primaryStage.close();
	}
	
	@FXML
	private void endRound () {
		
		String cityAmount = this.amountT.getText();
		int amount = 1;
		if(cityAmount != null) {
			try {
				amount = Integer.parseInt(cityAmount);
			} catch (NumberFormatException e) {
//				Amount field empty
			}
		}
		
		for (int i = 0; i < amount; i++) {
			
			GameServer.addReply((Game g)-> "{\"type\": \"endRound\"}");
		}
		
		System.out.println("endRound");
		this.executeEvent(GameServer.getReply().evalutate(currentGame.getGame()));
		
	}
	
	@FXML
	private void autoTurn () {
		String amountString = this.amountT.getText();
		int amount = 1;
		if (!amountString.equals("")) {
			amount = Integer.parseInt(amountString);
		}
		for (int i = 0; i < amount; i++) {
			this.executeEvent(Main.solve((this.currentGame.getGame())));
		}
	}
	
	@FXML
	private void putUnderQuarantine () {
		
		System.out.println("quarantine");
		this.executeEvent("{\"type\": \"putUnderQuarantine\", \"city\":\"" + this.cityfrom.getText() +
						"\", \"rounds\": " + this.rounds.getText() + "}");
	}
	
	@FXML
	private void closeAirport () {
	
		System.out.println("closeAirport");
		this.executeEvent("{\"type\": \"closeAirport\", \"city\": \"" + this.cityfrom.getText() +
						"\", \"rounds\": " + this.rounds.getText() + "}");
	}	
	
	@FXML
	private void closeConnection () {
		
		System.out.println("closeConnection");
		this.executeEvent("{\"type\": \"closeConnection\", \"fromCity\": \"" + this.cityfrom.getText() +
						"\", \"toCity\": \"" + this.cityto.getText() +  "\", \"rounds\": " + 
						this.rounds.getText() + "}");
	}
	
	@FXML 
	private void developVaccine () {
		
		System.out.println("developVaccine");
		this.executeEvent("{\"type\": \"developVaccine\", \"pathogen\": \"" +
						this.pathogen.getText() + "\"}");
	}
	
	@FXML
	private void deployVaccine () {
		
		String cityAmount = this.amountT.getText();
		String selectedPathogen = this.pathogen.getText();

		if(selectedPathogen.equals("")) return;
		
		if (this.cityfrom.getText().equals("")) {
			
			//check if the actions can be added.
			if (cityAmount == null || selectedPathogen == null) return;
			
			//convert to Integer
			int cityAmountInt = 0;
			try {
				cityAmountInt = Integer.parseInt(cityAmount);
			} catch (NumberFormatException e) {
//				Amount field empty
			}
			
			for (int i = 0; i < cityAmountInt; i++) {
				
				GameServer.addReply((Game g)-> {
				Collection<City> cities = g.getCities().values();
				ArrayList<Pair<City, Double>> citiesPrev = new ArrayList<>();
				
				//get all cities that are infected by the selected Pathogen.
				for (City c: cities) {
					double prev = 0;
					boolean alreadyVacc = false;
					if (c.getOutbreak() != null) {
						if (selectedPathogen.equals(c.getOutbreak().getVirus().getName()))
							prev = c.getPrevalance();
					}
					if(c.getVaccineDeployed() != null) {
						alreadyVacc = true;
					}
					if(!alreadyVacc)
						citiesPrev.add(new Pair<>(c, prev));
				}
					
					//sort the infectedCities by size.
					//don't ask questions. It works.
					citiesPrev.sort((Pair<City, Double> c2, Pair<City, Double> c1)->
								(int) ((c1.getKey().getPopulation() * (1 - c1.getValue()))
								- (c2.getKey().getPopulation()) * (1 - c2.getValue())));
					
					return "{\"type\": \"deployVaccine\", \"pathogen\": \"" + selectedPathogen +
							"\", \"city\": \"" + citiesPrev.get(0).getKey().getName() + "\"}";
				});
				
			}
			
			this.executeEvent(GameServer.getReply().evalutate(currentGame.getGame()));
		} else {
			
			System.out.println("developVaccine");
			this.executeEvent("{\"type\": \"deployVaccine\", \"pathogen\": \"" + selectedPathogen +
					"\", \"city\": \"" + this.cityfrom.getText() + "\"}");
		}
	}
	
	@FXML
	private void developMedication () {
		
		System.out.println("developMedication");
		this.executeEvent("{\"type\": \"developMedication\", \"pathogen\": \""
						+ this.pathogen.getText() + "\"}");
	}
	
	@FXML
	private void deployMedication () {
		
		System.out.println("deployMedication");
		this.executeEvent("{\"type\": \"deployMedication\", \"pathogen\": \"" + this.pathogen.getText() +
							"\", \"city\": \"" + this.cityfrom.getText() + "\"}");
	}
	
	@FXML
	private void applyHygienicMeasures () {
		
		System.out.println("applyHygienicMeasures");
		this.executeEvent("{\"type\": \"applyHygienicMeasures\", \"city\":\"" + this.cityfrom.getText() + "\"}");
	}
	
	@FXML 
	private void exertInfluence () {
		
		System.out.println("exertInfluence");
		this.executeEvent("{\"type\": \"exertInfluence\", \"city\": \"" + this.cityfrom.getText() + "\"}");
	}
	
	@FXML 
	private void callElections () {
		
		System.out.println("callElections");
		this.executeEvent("{\"type\": \"callElections\", \"city\": \"" + this.cityfrom.getText() + "\"}");
	}
	
	@FXML
	private void launchCampaign () {
		
		System.out.println("launchCampaign");
		this.executeEvent("{\"type\": \"launchCampaign\", \"city\": \"" + this.cityfrom.getText() + "\"}");
	}
	
	@FXML //Button implementation
	private void medicateBiggestCities () {
		
		String cityAmount = this.amountT.getText();
		String selectedPathogen = this.pathogen.getText();
		
		//check if the actions can be added.
		if (cityAmount == null || selectedPathogen == null) return;
		
		//convert to Integer
		int cityAmountInt = Integer.parseInt(cityAmount);
		
		for (int i = 0; i < cityAmountInt; i++) {
			
			GameServer.addReply((Game g)-> {
				HashSet<E_Outbreak> outbreaks = g.getOutbreakEvents();
				ArrayList<Pair<City, Double>> infectedCities = new ArrayList<>();
				
				//get all cities that are infected by the selected Pathogen.
				for (E_Outbreak e : outbreaks) {
					if (e.getVirus().getName().equals(selectedPathogen)) 
						infectedCities.add(new Pair<City, Double>(e.getCity(), e.getPrevalence()));
				}
				
				//sort the infectedCities by size.
				//don't ask questions. It works.
				infectedCities.sort((Pair<City, Double> c2, Pair<City, Double> c1)->
							(int) ((c1.getKey().getPopulation() * c1.getValue())
							- (c2.getKey().getPopulation()) * c2.getValue()));
				
				return "{\"type\": \"deployMedication\", \"pathogen\": \"" + selectedPathogen +
				"\", \"city\": \"" + infectedCities.get(0).getKey().getName() + "\"}";
			});
			
		}
		
		this.executeEvent(GameServer.getReply().evalutate(currentGame.getGame()));
	}
	
	private void executeEvent(String event) {
		if(this.currentGame == null || !this.currentGame.isAlive()) return;
		this.currentGame.sendReply(event);
		
		this.quit();
		this.mapControllerRefrence.quit();
	}
	
	public void setGame(GameExchange exchange) {
		this.currentGame = exchange;
	}
	
	
}
