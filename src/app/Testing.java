package app;

import app.http.GameServer;

//Imports for the GUI
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Testing extends Application { 
	
	public static void main  (String[] args) {
		//ALl the code from the main was moved to the start-methode.
		launch(args);
	}
	
	//@Override
	public void start (Stage primaryStage) throws Exception {
		new GameServer();
		Platform.setImplicitExit(false);
	}
}