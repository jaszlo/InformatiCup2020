package app.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import app.game.Game;
import app.solver.ActionHeuristic;

public class AWSHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		String body = br.lines().collect(Collectors.joining(System.lineSeparator()));
		br.close();
		String response = ActionHeuristic.solve(new Game(body)).toString();
		output.write(response.getBytes("UTF-8"));
		output.close();
	}
}
