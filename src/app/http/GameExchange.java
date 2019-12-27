package app.http;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

import app.App;
import app.game.Game;

/**
 * Represent a request from the GI client.
 */
public class GameExchange {
	private HttpExchange exchange;
	private Game game;

	/**
	 * Creates a game exchange from a HTTP exchange.
	 * 
	 * @param exchange The HTTP exchange with the GI client.
	 */
	public GameExchange(HttpExchange exchange) {

		this.exchange = exchange;

		// Read the game given by the GI client.
		String body = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
			body = br.lines().collect(Collectors.joining(System.lineSeparator()));

		} catch (IOException e) {
			System.err.println("Error while reading body!");
		}

		this.game = new Game(body);
	}

	/**
	 * Sends a response to the GI client.
	 * 
	 * @param response The response for the GI client.
	 */
	public void sendReply(String response) {

		try {
			// Set Headers
			exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
			OutputStream os = exchange.getResponseBody();

			// Write the response to the GI client
			os.write(response.getBytes("UTF-8"));

			// Close the output stream and set the last action string in the GUI
			os.close();
			App.guiController.setLastAction(response);
			this.exchange = null;

		} catch (IOException e) {
			System.err.println("Error while sending response!");
		}
	}

	/**
	 * Checks if the game exchange is alive. It is alive as long as no response was
	 * send.
	 * 
	 * @return Whether the game exchange is alive.
	 */
	public boolean isAlive() {
		return this.exchange != null;
	}

	/**
	 * @return The current game for this game exchange.
	 */
	public Game getGame() {
		return this.game;
	}
}
