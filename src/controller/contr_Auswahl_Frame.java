package controller;

import java.util.ArrayList;

import controller.Filtereinstellungen.Bildverarbeitung;
import controller.Filtereinstellungen.contr_object;
import controller.Filtereinstellungen.contr_overlap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import komponenten.FilterButton;

public class contr_Auswahl_Frame {

	@FXML private FlowPane layout;
	@FXML private AnchorPane pane;
	@FXML private AnchorPane pane_style;
	@FXML private ScrollPane scroll_filter;
	@FXML private VBox button_move;
	@FXML private VBox button_clip;
	@FXML private VBox button_scale;
	@FXML private VBox button_rotate;
	@FXML private VBox button_noise;
	@FXML private VBox button_blur;
	@FXML private VBox button_hue;
	@FXML private VBox button_saturation;
	@FXML private VBox button_value;
	@FXML private VBox button_brightnes;
	@FXML private VBox button_contrast;
	@FXML private VBox button_gamma;
	@FXML private VBox button_overlay;
	@FXML private VBox button_overlap;
	@FXML private VBox button_object;
	@FXML private VBox button_resolution;
	@FXML private Label label_titel;
	@FXML private Label label1;
	@FXML private Label label2;
	@FXML private Label label3;
	@FXML private Label label4;
	@FXML private Label label5;
	@FXML private Label label6;
	@FXML private Label label7;
	@FXML private Label label8;
	@FXML private Label label9;
	@FXML private Label label10;
	@FXML private Label label11;
	@FXML private Label label12;
	@FXML private Label label13;
	@FXML private Label label14;
	@FXML private Label label15;
	@FXML private Label label16;


	contr_filter con_filter = null;
	contr_object con_obj = null;
	contr_overlap con_overlap = null;


	//Eigene Funktionen /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	public void setController(contr_filter con_filter) {
		con_obj = null;
		con_overlap = null;
		this.con_filter = con_filter;
		setDisableFilterList(con_filter.getDeleteList());
	}

	public void setController(contr_object con_obj) {
		con_filter = null;
		con_overlap = null;
		this.con_obj = con_obj;
		setDisableFilterList(con_obj.getDeleteList());
	}

	public void setController(contr_overlap con_overlap) {
		con_filter = null;
		con_obj = null;
		this.con_overlap = con_overlap;
		setDisableFilterList(con_overlap.getDeleteList());
	}

	public void setTitle() {
		label_titel.setText("Add object filter");
	}

	public void disableFilter(int filterID, boolean b) {
		if(filterID == Bildverarbeitung.FILTER_BLUR)
			button_blur.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_BRIGHTNESS)
			button_brightnes.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_CLIP)
			button_clip.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_CONTRAST)
			button_contrast.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_GAMMA)
			button_gamma.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_HUE)
			button_hue.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_NOISE)
			button_noise.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_OVERLAP)
			button_overlap.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_OVERLAY)
			button_overlay.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_ROTATE)
			button_rotate.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_SATURATION)
			button_saturation.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_SCALE)
			button_scale.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_TRANSLATE)
			button_move.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_VALUE)
			button_value.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_OBJECT)
			button_object.setDisable(b);
		else if(filterID == Bildverarbeitung.FILTER_RESOLUTION)
			button_resolution.setDisable(b);
	}

	public void enableObjectMode(boolean b) {
		button_object.setVisible(!b);
		button_overlap.setVisible(!b);
		button_resolution.setVisible(!b);

		if(b) {
			label_titel.setTextFill(Color.web("#9C698D"));
			label_titel.setText("Add filter for object");
			label1.setTextFill(Color.web("#9C698D"));
			label2.setTextFill(Color.web("#9C698D"));
			label3.setTextFill(Color.web("#9C698D"));
			label4.setTextFill(Color.web("#9C698D"));
			label5.setTextFill(Color.web("#9C698D"));
			label6.setTextFill(Color.web("#9C698D"));
			label7.setTextFill(Color.web("#9C698D"));
			label8.setTextFill(Color.web("#9C698D"));
			label9.setTextFill(Color.web("#9C698D"));
			label10.setTextFill(Color.web("#9C698D"));
			label11.setTextFill(Color.web("#9C698D"));
			label12.setTextFill(Color.web("#9C698D"));
			label13.setTextFill(Color.web("#9C698D"));
			label14.setTextFill(Color.web("#9C698D"));
			label15.setTextFill(Color.web("#9C698D"));
			label16.setTextFill(Color.web("#9C698D"));
		}
		else {
			label_titel.setTextFill(Color.web("#566f83"));
			label_titel.setText("Add filter");
			label1.setTextFill(Color.BLACK);
			label2.setTextFill(Color.BLACK);
			label3.setTextFill(Color.BLACK);
			label4.setTextFill(Color.BLACK);
			label5.setTextFill(Color.BLACK);
			label6.setTextFill(Color.BLACK);
			label7.setTextFill(Color.BLACK);
			label8.setTextFill(Color.BLACK);
			label9.setTextFill(Color.BLACK);
			label10.setTextFill(Color.BLACK);
			label11.setTextFill(Color.BLACK);
			label12.setTextFill(Color.BLACK);
			label13.setTextFill(Color.BLACK);
			label14.setTextFill(Color.BLACK);
			label15.setTextFill(Color.BLACK);
			label16.setTextFill(Color.BLACK);
		}
	}

	public void enableOverlapModeMode(boolean b) {
		button_object.setVisible(!b);
		button_overlap.setVisible(!b);
		button_move.setVisible(!b);
		button_clip.setVisible(!b);
		button_resolution.setVisible(!b);

		if(b) {
			label_titel.setTextFill(Color.web("#9C698D"));
			label_titel.setText("Add filter for object");
			label1.setTextFill(Color.web("#9C698D"));
			label2.setTextFill(Color.web("#9C698D"));
			label3.setTextFill(Color.web("#9C698D"));
			label4.setTextFill(Color.web("#9C698D"));
			label5.setTextFill(Color.web("#9C698D"));
			label6.setTextFill(Color.web("#9C698D"));
			label7.setTextFill(Color.web("#9C698D"));
			label8.setTextFill(Color.web("#9C698D"));
			label9.setTextFill(Color.web("#9C698D"));
			label10.setTextFill(Color.web("#9C698D"));
			label11.setTextFill(Color.web("#9C698D"));
			label12.setTextFill(Color.web("#9C698D"));
			label13.setTextFill(Color.web("#9C698D"));
			label14.setTextFill(Color.web("#9C698D"));
			label15.setTextFill(Color.web("#9C698D"));
			label16.setTextFill(Color.web("#9C698D"));
		}
		else {
			label_titel.setTextFill(Color.web("#566f83"));
			label_titel.setText("Add filter");
			label1.setTextFill(Color.BLACK);
			label2.setTextFill(Color.BLACK);
			label3.setTextFill(Color.BLACK);
			label4.setTextFill(Color.BLACK);
			label5.setTextFill(Color.BLACK);
			label6.setTextFill(Color.BLACK);
			label7.setTextFill(Color.BLACK);
			label8.setTextFill(Color.BLACK);
			label9.setTextFill(Color.BLACK);
			label10.setTextFill(Color.BLACK);
			label11.setTextFill(Color.BLACK);
			label12.setTextFill(Color.BLACK);
			label13.setTextFill(Color.BLACK);
			label14.setTextFill(Color.BLACK);
			label15.setTextFill(Color.BLACK);
			label16.setTextFill(Color.BLACK);
		}
	}

	public void setDisableFilterList(ArrayList<Integer> list) {
		resetDisable();

		if(list!=null) {
			for(int i=0;i<list.size();i++) {
				if(list.get(i) == Bildverarbeitung.FILTER_BLUR)
					button_blur.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_BRIGHTNESS)
					button_brightnes.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_TRANSLATE) 
					button_move.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_CLIP) 
					button_clip.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_CONTRAST)
					button_contrast.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_GAMMA)
					button_gamma.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_HUE)
					button_hue.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_NOISE)
					button_noise.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_ROTATE)
					button_rotate.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_SCALE)
					button_scale.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_VALUE)
					button_value.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_SATURATION)
					button_saturation.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_OVERLAP)
					button_overlap.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_OVERLAY)
					button_overlay.setDisable(true);
				else if(list.get(i) == Bildverarbeitung.FILTER_OBJECT)
					button_object.setDisable(false);
				else if(list.get(i) == Bildverarbeitung.FILTER_RESOLUTION)
					button_resolution.setDisable(true);
				else
					System.err.println("setDisableFilterList() in contr_Auswahl_Frame, ID nicht bekannt!");
			}
		}
	}

	private void resetDisable() {
		button_blur.setDisable(false);
		button_brightnes.setDisable(false);
		button_clip.setDisable(false);
		button_contrast.setDisable(false);
		button_gamma.setDisable(false);
		button_hue.setDisable(false);
		button_noise.setDisable(false);
		button_overlap.setDisable(false);
		button_overlay.setDisable(false);
		button_rotate.setDisable(false);
		button_scale.setDisable(false);
		button_move.setDisable(false);
		button_value.setDisable(false);
		button_saturation.setDisable(false);
		button_resolution.setDisable(false);
		//		button_object.setDisable(false);
	}

	//Events Scene-Builder //////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	@FXML 
	void initialize(){
		layout.prefWidthProperty().bind(pane.widthProperty());
		button_object.managedProperty().bind(button_object.visibleProperty());
		button_overlap.managedProperty().bind(button_overlap.visibleProperty());
		button_overlay.managedProperty().bind(button_overlay.visibleProperty());
		button_move.managedProperty().bind(button_move.visibleProperty());
		button_clip.managedProperty().bind(button_clip.visibleProperty());
	}

	@FXML
	void act_button_move(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_TRANSLATE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter, id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
//		disableFilter(Bildverarbeitung.FILTER_CLIP, true);
	}

	@FXML
	void act_button_contrast(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_CONTRAST;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter, id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_move_out(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_CLIP;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
//		disableFilter(Bildverarbeitung.FILTER_TRANSLATE, true);
	}

	@FXML
	void act_button_scale(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_SCALE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_rotate(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_ROTATE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_overlay(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_OVERLAY;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_ueberdeckung(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_OVERLAP;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_farbton(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_HUE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_helligkeit(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_BRIGHTNESS;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_noise(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_NOISE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_bloom(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_BLUR;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_saturation(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_SATURATION;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_gamma(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_GAMMA;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_value(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_VALUE;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}

	@FXML
	void act_button_object(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_OBJECT;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}
	
	@FXML
	void act_button_resolution(ActionEvent event) {
		int id = Bildverarbeitung.FILTER_RESOLUTION;
		if(con_filter!=null) {
			FilterButton filter = new FilterButton(id, con_filter.filter, true, null, con_filter.contr_filterFrame, con_filter.getVorschauController(), 85);
			con_filter.addFilter(filter,id, true);
		}
		else if(con_obj!=null){
			FilterButton filter = new FilterButton(id, con_obj.filter, true, null, con_obj.controll_filterframe, con_obj.controll_vorschau, contr_object.WIDTH_OBJ);
			con_obj.addFilter(filter, id, true);
		}
		else {
			FilterButton filter = new FilterButton(id, con_overlap.filter, true, null, con_overlap.controll_filterframe, con_overlap.controll_vorschau, contr_object.WIDTH_OBJ);
			con_overlap.addFilter(filter, id, true);
		}
		disableFilter(id, true);
	}


}
