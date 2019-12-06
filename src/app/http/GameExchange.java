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
//            System.out.println(response);
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
			
			//Create the stages for the gui.
			Stage GUI = new Stage();
			//Create the loader for the gui.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/resources/gui.fxml"));			
			
			//Create and assign the controllers.
			GuiController guiController = new GuiController();
			guiController.setGame(this);
			loader.setController(guiController);
			Parent root = null;
		
			//Create the scenes for the gui
			root = (Parent)loader.load();
			Scene scene = new Scene(root);
			
			//Set the position of the stage. Set Stage boundaries to the upper right corner of the visible bounds of the main screen
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			GUI.setX(screenBounds.getMaxX() - screenBounds.getWidth());
			GUI.setY(screenBounds.getMaxY() - screenBounds.getHeight());
			
			//Attaches the newly created scene to the stage. Shows the Stage in a resizable window. Also set Title to Pandemie.
			GUI.setResizable(true);
			GUI.setScene(scene);
			GUI.setTitle("Pandemie");
			GUI.show();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error occoured while starting the GUI.");
		}
	}
}
