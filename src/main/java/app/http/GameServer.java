package app.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.App;
import app.game.Game;
import app.solver.ActionHeuristic;
import app.solver.GameEvaluator;
import javafx.application.Platform;

/**
 * Server that the GI clients connects to.
 */
public class GameServer {

	private HttpServer server;
	private static LinkedBlockingDeque<GameEvaluator> repliesToSend = new LinkedBlockingDeque<>();

	/**
	 * As long as the LOCK is the next reply auto play is enabled. This means the
	 * heuristic computes the best possible turn and executes it.
	 */
	public static final GameEvaluator LOCK = new GameEvaluator() {
		@Override
		public String evaluate(Game currentGame) {

			System.err.println("Blocking deque LOCK was executed!");
			GameServer.addReply(GameServer.LOCK);

			return ActionHeuristic.solve(currentGame).toString();
		}
	};

	/**
	 * Creates a new local game server with the port 50123. The GI client can
	 * connect with this game server.
	 */
	public GameServer() {
		try {
			
			// Creates a new HTTP server with the port 50123.
			this.server = HttpServer.create(new InetSocketAddress(50123), 0);

			// Set execution mode to parallel.
			server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

			// Create new end point
			HttpContext context = server.createContext("/");
			context.setHandler(GameServer::handleRequest);
			server.start();

		} catch (IOException e) {
			System.err.println("Could not create Server!");
			System.exit(1);
		}
	}

	/**
	 * Function called to handle a request. It is called for each request.
	 * 
	 * @param exchange The request that will be handled.
	 */
	private static void handleRequest(HttpExchange exchange) {

		// Only handle post request.
		if (!exchange.getRequestMethod().equals("POST")) {
			System.err.println("Got a non POST request: Closing server!");
			String response = "<center>Succefully closed server! :D</center>";
			try {
				exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
				OutputStream os = exchange.getResponseBody();

				// Write the response to the GI client
				os.write(response.getBytes("UTF-8"));

				// Close the output stream and set the last action string in the GUI
				os.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

		// Create a new game exchange to work with.
		GameExchange ge = new GameExchange(exchange);

		GameEvaluator eval = null;

		/*
		 * Within the synchronized block a certain way of playing is chosen. There are
		 * three ways of playing available. (1) Manual playing via the GUI (2) Sending
		 * replies from the "repliesToSend" queue (3) Auto play for multiple games at
		 * once.
		 */
		synchronized (GameServer.class) {
			// (3)
			if (hasReplies() && peekReply() == LOCK) {
				eval = (Game g) -> ActionHeuristic.solve(g).toString();

				// (2)
			} else if (hasReplies()) {
				eval = getReply();

				// (1)
			} else if (App.guiController != null && App.guiController.ready()) {
				App.guiController.setGame(ge);

				// Initializing the (3) way of playing.
			} else {
				// Detected more than one game. CLose GUI and enable auto play.
				addReply(LOCK);
				Platform.runLater(() -> {
					App.guiController.executeAction(ActionHeuristic.solve(ge.getGame()));
					App.guiController.close();
				});

				// Set heuristic as game evaluator
				eval = (Game g) -> ActionHeuristic.solve(g).toString();
			}
		}
		
		// Only send a reply if the GUI was not selected
		if (eval != null) {
			ge.sendReply(eval.evaluate(ge.getGame()));
		}
	}

	/**
	 * Add a reply to the "repliesToSend" queue.
	 * 
	 * @param reply The reply to add to the queue.
	 */
	public static void addReply(GameEvaluator reply) {
		repliesToSend.add(reply);
	}

	/**
	 * Get and remove the first element from the "repliesToSend" queue.
	 * 
	 * @return The next reply that will be send.
	 */
	public static GameEvaluator getReply() {
		return repliesToSend.poll();
	}

	/**
	 * Get and do not remove the first element from the "repliesToSend" queue.
	 * 
	 * @return The next reply that will be send.
	 */
	public static GameEvaluator peekReply() {
		return repliesToSend.peek();
	}

	/**
	 * @return Whether there are replies to send.
	 */
	public static boolean hasReplies() {
		return !repliesToSend.isEmpty();
	}
	
	/**
	 * @return Whether there are multiple replies to send.
	 */
	public static boolean hasMultipleReplies() {
		return repliesToSend.size() > 1;
	}

	/**
	 * Removes all available replies.
	 */
	public static void clearReplies() {
		repliesToSend.clear();
	}
}