package controller;

import Data.data_class;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class contr_ImageClassification_edit {

	@FXML private VBox layout_class;
	@FXML private TextField txt_name;
	Spinner<Integer> spinner = new Spinner<>();
	
	SpinnerValueFactory<Integer> data_spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);

	Stage stage = null;
	data_class data = null;
	contr_ImageClassification controll = null;
	ObservableList<data_class> dataset = null;
	TableView<data_class> table = null;
	ComboBox<data_class> combo = null;

	public void init(Stage stage, data_class data, ObservableList<data_class> dataset, TableView<data_class> table, ComboBox<data_class> combo, contr_ImageClassification controll) {
		this.stage = stage;
		this.data = data;
		this.dataset = dataset;
		this.controll = controll;
		this.table = table;
		this.combo = combo;
		txt_name.setText(data.getName());

		spinner.setValueFactory(data_spinner);
		int width = 60;
		spinner.setMaxWidth(width);
		spinner.setPrefWidth(width);
		spinner.setMinWidth(width);
		spinner.setPrefHeight(30);
		spinner.setMinHeight(30);
		spinner.setEditable(false);
		data_spinner.setValue(data.getIndex());
		spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(newVal > dataset.size()-1)
				data_spinner.setValue(dataset.size()-1);
			
		});
		layout_class.getChildren().add(spinner);
	}

	@FXML
	void initialize() {

	}

	@FXML
	void act_button_close(ActionEvent event) {
		stage.close();
	}

	@FXML
	void act_button_save(ActionEvent event) {
		
//		data_class tmp = data;
		data.setIndex(spinner.getValue());
		data.setName(txt_name.getText());
		dataset.remove(data);
		dataset.add(spinner.getValue(), data);
		
//		data_class tmp = combo.getValue();
//		data.setIndex(spinner.getValue());
//		data.setName(txt_name.getText());
//		for(int i=0;i<dataset.size();i++) {
//			if(dataset.get(i) == data) {
//				dataset.remove(i);
//				dataset.add(i,data);
//			}
//		}
		table.refresh();
		table.getSelectionModel().select(data);
		controll.updateDataChange();
		combo.setValue(data);
		controll.updateClassIndex();
		stage.close();
	}

//	private boolean existClassIndex(int value) {
//		for(int i=0;i<dataset.size();i++) {
//			if(dataset.get(i).getIndex() == value && dataset.get(i) != data)
//				return true;
//		}
//		return false;
//	}
//
//	private int getNextClassIndex(boolean up, int value) {
//		if(up==true) {
//			if(existClassIndex(value))
//				return getNextClassIndex(up,value+1);
//			else
//				return value;
//		}
//		else if(up == false && value >=0) {
//			if(existClassIndex(value))
//				return getNextClassIndex(up,value-1);
//			else
//				return value;
//		}
//		else {
//			up = true;
//			return getNextClassIndex(up,value+1);
//		}
//	}
	


}
