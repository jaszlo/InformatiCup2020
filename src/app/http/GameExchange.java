package app.http;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import app.game.Game;
import app.gui.GuiController;
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
			Stage MAP = new Stage();
			//Create the loader for the gui and the map.
			FXMLLoader mapLoader = new FXMLLoader();
			mapLoader.setLocation(getClass().getResource("/resources/gui.fxml"));			
			
			//Create and assign the controllers.
			GuiController mapController = new GuiController();
			mapController.setGame(this);
			mapLoader.setController(mapController);
			
			//Create the scenes for the map and the gui
			Parent rootMap = (Parent)mapLoader.load();
			Scene mapScene = new Scene(rootMap);
			
			//Set the position of the stages. set Stages boundaries to the upper right corner of the visible bounds of the main screen
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			MAP.setX(screenBounds.getMaxX() - screenBounds.getWidth());
			MAP.setY(screenBounds.getMaxY() - screenBounds.getHeight());
			
			//Attaches the newly created scene to the MAPStage. Shows the MAPStage in a unresizable window. Also set Title to PandemieMAP.
			MAP.setResizable(true);
			MAP.setScene(mapScene);
			MAP.setTitle("PandemieMAP");
			MAP.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error occoured while starting the GUI.");
		}
	}
}
