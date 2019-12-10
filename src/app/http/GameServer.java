package app.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.solver.GameEvaluater;

public class GameServer {
	HttpServer server;
	public static double games = 0;
	public static double wins = 0;
	
	public static LinkedBlockingDeque<GameEvaluater> repliesToSend = new LinkedBlockingDeque<>();;
	
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
		    			wins++;
		    		} 
		    		games++;
	    			System.out.println(ge.getGame().getOutcome() + " - current winRate = " + ((wins/games)*100) + "%.");
	    		}
	    		if(hasReplies())
	    			ge.sendReply(getReply().evaluate(ge.getGame()));
	    		else
	    			ge.playGui();
    		
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
}