package controller;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class contr_loadProgress {

	@FXML private ProgressBar progress1;
	@FXML private Label label_name;
	
	File file = null;
	Stage stage = null;
	int size = 0;
	int val = 0;

	public void init(File file, Stage stage) {
		this.file = file;
		if(file!=null) {
		label_name.setText(file.getName());
		this.stage = stage;
		}
		else {
			stage.close();
		}
	}
	
	@FXML 
	void initialize(){
		
	}
	
	@FXML
	void act_cancel(ActionEvent event) {
		stage.close();
	}
	
	public void setGesamtSize(int size) {
		this.size = size;
	}
	
	public void setValue(int val) {
		this.val = val;
		progress1.setProgress(size/(double)val);
	}
	
	public void increment() {
		++val;
		progress1.setProgress(size/(double)val);
	}
	
}