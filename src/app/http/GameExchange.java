package app.http;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import app.game.Game;
import app.gui.GuiController;
import app.gui.MapController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameExchange {
	private HttpExchange exchange;
	private Game game;
	
	public GameExchange(HttpExchange exchange) {
		this.exchange = exchange;
		
		String body = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
            body = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch(IOException e) {
        	System.err.println("Error while reading body!");
        }
        
        this.game = new Game(body);
	}
	
	
	public void sendReply(String response) {
		try {
            exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes("UTF-8"));
            os.close();
            System.out.println(response);
            this.exchange = null;
        } catch (IOException e){
        	System.err.println("Error while sending response!");
        }
	}
	
	
	public boolean isAlive() {
		return this.exchange != null;
	}
	
	
	public Game getGame() {
		return this.game;
	}
	
	public void playGui() {
		Platform.runLater(() -> this.startGui());
	}
	
	private void startGui() {
		try {
			
			//Create the stages for the gui and the map.
			Stage GUI = new Stage();
			Stage MAP = new Stage();
			
			//Create the loader for the gui and the map.
			FXMLLoader guiLoader = new FXMLLoader();
			guiLoader.setLocation(getClass().getResource("/resources/gui.fxml"));
			
			FXMLLoader mapLoader = new FXMLLoader();
			mapLoader.setLocation(getClass().getResource("/resources/map.fxml"));			
			
			//Create and assign the controllers.
			GuiController guiController = new GuiController();
			guiController.setGame(this);
			guiLoader.setController(guiController);
			
			MapController mapController = new MapController();
			mapController.setGame(this);
			mapLoader.setController(mapController);
			
			//set mapController in guiController so that the guiController knows what to close.
			guiController.setMapResource(mapController);
			
			//Create the scenes for the map and the gui
			Parent root = (Parent)guiLoader.load();
			Scene scene = new Scene(root);
			
			Parent rootMap = (Parent)mapLoader.load();
			Scene mapScene = new Scene(rootMap);
			
			//Set the position of the stages. set Stages boundaries to the upper right corner of the visible bounds of the main screen
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			GUI.setX(0);
			GUI.setY(0);
			
			MAP.setX(screenBounds.getMaxX() - screenBounds.getWidth());
			MAP.setY(screenBounds.getMaxY() - screenBounds.getHeight());
			
			//Attaches the newly created scene to the GUIStage. Shows GUIStage in an unresizable window. Also set Title to PandemieGUI.
            GUI.setResizable(false);
			GUI.setScene(scene);
			GUI.setTitle("PandemieGUI");
			//Setting the background for the gui.
			String image = "/resources/BACKGROUND.jpg";
			root.setStyle("-fx-background-image: url('" + image + "'); " +
			           "-fx-background-position: center center; " +
			           "-fx-background-repeat: stretch;");
			
			//Attaches the newly created scene to the MAPStage. Shows the MAPStage in a unresizable window. Also set Title to PandemieMAP.
			MAP.setResizable(false);
			MAP.setScene(mapScene);
			MAP.setTitle("PandemieMAP");
			MAP.show();
			GUI.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error occoured while starting the GUI.");
		}
	}
}
