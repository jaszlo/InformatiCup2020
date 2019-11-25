package app;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import app.http.GameServer;

//Imports for the GUI
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class WinrateTest extends Application { 
	
	public static void main  (String[] args) {
		//ALl the code from the main was moved to the start-methode.
		launch(args);
	}
	
	//@Override
	public void start (Stage primaryStage) throws Exception {
		new GameServer(true);
		Platform.setImplicitExit(false);
		JFileChooser jf = new JFileChooser();
		jf.showSaveDialog(null);
		cmdTool = jf.getSelectedFile();
		//new ProcessBuilder(cmdTool.getAbsolutePath()).start(); //TODO: make it work
	}
	
	private static final int TRIALS = 100;

	private static File cmdTool;
	
	private static int wins=0,losses=0;
	
	public static void addResult(String outcome) {
		if(outcome.equals("loss"))
			losses++;
		else
			wins++;
		System.out.println("Win ratio: "+wins+"/"+(wins+losses)+" "+(wins/(float)(wins+losses))+"% winrate");
		if(wins+losses < TRIALS) {
//			try {
//			//	new ProcessBuilder(cmdTool.getAbsolutePath()).start(); //TODO: make it work
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
}