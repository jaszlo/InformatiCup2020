package app.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingDeque;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import app.WinrateTest;
import app.game.Game;
import app.solver.GameEvaluater;
import app.solver.Main;

public class GameServer {
	HttpServer server;
	public static LinkedBlockingDeque<GameEvaluater> repliesToSend = new LinkedBlockingDeque<>();;
	private static boolean testMode;
	
	public GameServer(boolean showGui) {
		try {
			GameServer.testMode = showGui;
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
	    		System.out.println(ge.getGame().getOutcome());
	    		if(!testMode) {
		    		if(hasReplies())
		    			ge.sendReply(getReply().evaluate(ge.getGame()));
		    		else
		    			ge.playGui();
	    		}
	    		else {
	    			if(!ge.getGame().getOutcome().equals("pending")) 
	    				WinrateTest.addResult(ge.getGame().getOutcome());
	    			else
	    				ge.sendReply(app.solver.Main.solve(ge.getGame()));
	    		}
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