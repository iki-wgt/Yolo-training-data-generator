package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class contr_Bearbeiten_Window {

	@FXML private SplitPane split1;
	@FXML private SplitPane split2;
	@FXML private SplitPane split3;
	@FXML private AnchorPane pane_filterregeln;
	@FXML private AnchorPane pane_filter;
	@FXML private AnchorPane pane_vorschau;

	
	Stage stage = null;

	private static double divider_pos1[]= {0.0, 1.0};
	private static double divider_pos2[]= {0.5, 0.5};

	//Eigene Funktionen /////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	public void init(Stage stage) {
		this.stage = stage;
	}

	public void show() {
		split1.setDividerPositions(divider_pos1);
		split2.setDividerPositions(divider_pos2);
		if(stage.isShowing()) {
			stage.toFront();
		}
		else
			stage.show();
	}

	public void setVorschau(AnchorPane pane_vorschau, contr_Vorschau_Frame control_vorschau) {
		this.pane_vorschau.getChildren().clear();

		AnchorPane.setLeftAnchor(pane_vorschau, 0.0);
		AnchorPane.setRightAnchor(pane_vorschau, 0.0);
		AnchorPane.setBottomAnchor(pane_vorschau, 0.0);
		AnchorPane.setTopAnchor(pane_vorschau, 0.0);

		this.pane_vorschau.getChildren().add(pane_vorschau);
		split2.setDividerPositions(divider_pos2);
	}

	public void setRegeln(AnchorPane pane, contr_filter con_filter) {
		this.pane_filterregeln.getChildren().clear();

		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);

		this.pane_filterregeln.getChildren().add(pane);
		split1.setDividerPositions(divider_pos1);
	}

	public void setAuswahl(AnchorPane pane_auswahl, contr_Auswahl_Frame controll_auswahl) {
		this.pane_filter.getChildren().clear();

		AnchorPane.setLeftAnchor(pane_auswahl, 0.0);
		AnchorPane.setRightAnchor(pane_auswahl, 0.0);
		AnchorPane.setBottomAnchor(pane_auswahl, 0.0);
		AnchorPane.setTopAnchor(pane_auswahl, 0.0);

		this.pane_filter.getChildren().add(pane_auswahl);
		split1.setDividerPositions(divider_pos1);
	}


	public void setFilterButtons(AnchorPane pane, contr_filter con_filter) {

	}


	void clear() {
		divider_pos1 = split1.getDividerPositions();
		divider_pos2 = split2.getDividerPositions();
		pane_vorschau.getChildren().clear();
		pane_filter.getChildren().clear();
		pane_filterregeln.getChildren().clear();
	}


	//Scenebuilder-Funktionen ///////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	@FXML
	void initialize() {

	}


	@FXML
	void act_button_close(ActionEvent event) {
		stage.close();
	}






}
