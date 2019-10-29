package controller;

import java.text.NumberFormat;
import java.util.List;

import org.jdom2.Element;

import application.xml;
import controller.Filtereinstellungen.Bildverarbeitung;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import komponenten.FilterButton;

public class contr_filterFrame {
	@FXML private HBox layout;
	@FXML private TextField txt_bezeichnung;
	@FXML private ScrollPane scroll;
	@FXML private TextField txt_value;
	@FXML private TextField obj_per_img;
	@FXML private Label label_imgsize;
	@FXML private Label label_summe;
	@FXML private VBox layout2;
	private int prev_obj_per_img;

	Stage stage = null;
	AnchorPane pane = null;
	int image_count = 0;

	contr_filter controll = null;
	contr_hauptmenue contr_hauptmenue = null;
	contr_Auswahl_Frame contr_auswahlframe = null;

	xml root = new xml("Filter");
	boolean selected = false;
	boolean b_error = false;


	@FXML 
	void initialize(){
		layout.prefWidthProperty().bind(scroll.widthProperty());
		txt_value.setStyle("-fx-border-color: transparent; -fx-border-width: 2");
	}

	@FXML
	void mouse_pane_root(MouseEvent event) {
		setSelectedAndUpdateGUI(false);
	}

	public void setSelected(boolean b) {
		if(b==false) {
			if(b_error)
				layout2.setStyle("-fx-background-color: #FFCBCB;");
			else
				layout2.setStyle("-fx-background-color: #F4F4F4;");
		}
		else {
			if(b_error)
				layout2.setStyle("-fx-background-color: #FA000C;");
			else
				layout2.setStyle("-fx-background-color: #9CEAD3;");
		}
		selected = b;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelectedAndUpdateGUI(boolean show) {
		boolean tmp = selected;
		layout2.setStyle("-fx-background-color: #9CEAD3;");
		selected = true;
		contr_hauptmenue.setPane(controll, pane, stage, this, show);
		if(tmp == false)
			contr_hauptmenue.control_vorschau.updatePreview();
	}

	public void initWindow(boolean show, boolean add_bg) {
		try {
			stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filter.fxml"));
			pane = (AnchorPane)loader.load();
			controll = loader.<contr_filter>getController();
			controll.init(layout, stage, add_bg, contr_hauptmenue, this);
			Scene scene = new Scene(pane,1000,760);
			stage.setScene(scene);
			stage.setTitle("Image Generation");
			contr_hauptmenue.setPane(controll, pane, stage, this, show);
			setSelected(true);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	@FXML
	void key_txt_value(KeyEvent event) {
		try {
			Integer.parseInt(txt_value.getText());
			calc();
			txt_value.setStyle("-fx-border-color: transparent; -fx-border-width: 2");
			contr_hauptmenue.calcSumme();
		}catch(Exception e) {
			txt_value.setStyle("-fx-border-color: #FF8D87; -fx-border-width: 2");
		}
	}
	
	@FXML
	void key_obj_per_img(KeyEvent event) {
		System.out.println(obj_per_img.getText());
		try {
			if (Integer.parseInt(obj_per_img.getText()) < 1) {
				obj_per_img.setText("1");
			}
			else
				prev_obj_per_img = Integer.parseInt(obj_per_img.getText());
		}catch(Exception e) {
			obj_per_img.setText(Integer.toString(prev_obj_per_img));
		}
	}

	public void closeWindow() {
		stage.close();
	}

	@FXML
	void act_button_del(ActionEvent event) {
		stage.close();
		contr_hauptmenue.deleteFilterFrame(this);
	}

	@FXML
	void act_button_edit(ActionEvent event) {
		contr_hauptmenue.setPane(controll, pane, stage, this, true);
	}
	
	@FXML
	void act_button_duplicate(ActionEvent event) {
		contr_hauptmenue.duplicate(this);
	}

	//	public void setData(Stage stage, contr_filter controll) {
	//		this.stage = stage;
	//		this.controll = controll;
	//	}

	public void setImageCount(int size) {
		image_count = size;
		label_imgsize.setText(""+image_count);
		calc();
		contr_hauptmenue.calcSumme();
	}

	private int calc() {
		try {
			int count = Integer.parseInt(txt_value.getText());
			int summe = image_count * count;
			String s = NumberFormat.getInstance().format(summe);
			label_summe.setText("("+s+" Pictures)");
			return summe;
		}catch(Exception e) {
			label_summe.setText("("+0+" Pictures)");
			return 0;
		}
	}

	public int getSumme() {
		return calc();
	}

	//	public void addImage(Image img) {
	//		
	//		images
	//	}


	public HBox getLayout() {
		return layout;
	}

	public xml getXMLCode(boolean save) {
		System.out.println("YYYYUUUIIIII");
		int count = 0;
		int num_obj_per_img = 1;
		try {
			count = Integer.parseInt(txt_value.getText());
			num_obj_per_img = Integer.parseInt(obj_per_img.getText());
		}catch(Exception e) {}
		xml root = controll.getXMLData(count, num_obj_per_img);
		xml description = new xml("description");
		description.getElement().setText(txt_bezeichnung.getText());
		if(save) {
			root.getElement().addContent(description.getElement());
		}
		return root;
	}
	
	public xml getXMLCodeVorschau(boolean allFilter, boolean random, int previewMin) {
		return controll.getXMLDataVorschau(allFilter, random, previewMin);
	}

	public void setData(contr_hauptmenue contr_hauptmenue, contr_Auswahl_Frame contr_auswahlframe) {
		this.contr_hauptmenue = contr_hauptmenue;
		this.contr_auswahlframe = contr_auswahlframe;
		contr_hauptmenue.calcSumme();
	}

	public void loadDataXML(Element elem) {
		try {
			//Number aktualisieren:
			int num = elem.getAttribute("num").getIntValue();
			txt_value.setText(""+num);
			calc();
			txt_value.setStyle("-fx-border-color: transparent; -fx-border-width: 2");
			contr_hauptmenue.calcSumme();

			//Filterelemente entschlüsseln
			List<Element> list = elem.getChildren();

			for (int x=0;x<list.size();x++) {
				System.out.println("list["+x+"]: " + list.get(x).getName());

				if(list.get(x).getName().equals("description"))
					txt_bezeichnung.setText(list.get(x).getText());
				else {
					int id = Bildverarbeitung.getID(list.get(x).getName());
					FilterButton filter = new FilterButton(id, controll.filter, true, contr_hauptmenue.getVoreinstellungen(), this, controll.getVorschauController(), 85);
					controll.addFilter(filter, id, true);
					filter.getController().loadXML(list.get(x));
				}
			}
		}catch(Exception e) {

		}
	}

	public boolean hasError() {
		return controll.hasError();
	}
	
	public void updateErrorColor() {
		b_error = controll.hasError();
		setSelected(selected);
	}


	//	public void 
}
