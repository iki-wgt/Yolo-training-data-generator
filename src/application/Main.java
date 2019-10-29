package application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import controller.contr_hauptmenue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		try {
//			File debug = new File("debug.txt");
//			System.setOut(outputFile(debug));
//			System.setErr(outputFile(debug));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Hauptmenue.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			contr_hauptmenue control = loader.<contr_hauptmenue>getController();
			control.init(primaryStage, control);
			Scene scene = new Scene(root,800,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Image Generator - (new)*");
			String url = "/background.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			primaryStage.getIcons().add(image); 
//			primaryStage.setMaximized(true);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected PrintStream outputFile(File name) {
		try {
			return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

