package app.http;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import app.Testing;
import app.game.Game;
import app.gui.GuiController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            GuiController.setLastAction(response);
//           System.out.println(response);
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
		Testing.guiController.setGame(this);
	}
	
}
