package app.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.App;
import app.game.actions.ActionHeuristic;
import app.game.actions.ConstantsSetup;
import app.knapsack.Solver;
import app.solver.GameEvaluater;
import app.solver.Main;

public class GameServer {
	HttpServer server;
	private static LinkedBlockingDeque<GameEvaluater> repliesToSend = new LinkedBlockingDeque<>();;
	
	public GameServer() {
		try {
			this.server = HttpServer.create(new InetSocketAddress(50123), 0);
			HttpContext context = server.createContext("/");
			context.setHandler(GameServer::handleRequest);
			server.start();
		} catch(IOException e) {
			System.err.println("Could not create Server!");
			System.exit(1);
		}
	}

    private static void handleRequest(HttpExchange exchange) {
    	if(!exchange.getRequestMethod().equals("POST")) {
    		System.err.println("Invalid request!");
    	}
    	new Thread(() -> {
	    		GameExchange ge = new GameExchange(exchange);
	    		if (!ge.getGame().getOutcome().equals("pending")) {
		    		if (ge.getGame().getOutcome().equals("win")) {
		    			ConstantsSetup.registerOutcome(true,ge.getGame());
		    		}else {
		    			ConstantsSetup.registerOutcome(false,ge.getGame());
		    		}
	    		}
//	    		System.out.println("Population %" + (100 * (ge.getGame().getPopulation() / population)) + "%");
	    		if(hasReplies())
	    			ge.sendReply(getReply().evaluate(ge.getGame()));
	    		else if(App.guiController.ready())
	    			ge.playGui();
	    		else
	    			ge.sendReply(Main.solve(ge.getGame()));
    		
    		}).start();
    }
    
    public static void addReply(GameEvaluater reply) {
    	repliesToSend.add(reply);
    }
    
    public static GameEvaluater getReply() {
    	return repliesToSend.poll();
    }
    
    public static boolean hasReplies() {
    	return !repliesToSend.isEmpty();
    }
    
    public static void clearReplies() {
    	repliesToSend.clear();
    }
}