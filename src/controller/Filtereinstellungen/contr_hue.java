package controller.Filtereinstellungen;

import org.jdom2.Element;

import application.xml;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class contr_hue extends Bildverarbeitung{

	@FXML private Slider slider_from;
	@FXML private Slider slider_to;
	@FXML private Slider slider_fix;

	@FXML private TextField txt_from;
	@FXML private TextField txt_to;
	@FXML private TextField txt_fix;

	@FXML private RadioButton radio_obj;
	@FXML private RadioButton radio_back;
	@FXML private RadioButton radio_both;
	@FXML private RadioButton radio_range;
	@FXML private RadioButton radio_fix;

	@FXML private Pane pane_hue1;
	@FXML private Pane pane_hue2;
	@FXML private HBox box_colors;
	@FXML private HBox box_colors2;

	@FXML private GridPane grid_range;
	@FXML private GridPane grid_fix;

	Rectangle rc1 = new Rectangle(0, 0, 18, 15);
	Rectangle rc2 = new Rectangle(0, 0, 18, 15);
	Rectangle rc3 = new Rectangle(0, 0, 42, 15);
	Stop[] stop1 = new Stop[] {
			new Stop(0, Color.web("#FF0000")), 
			new Stop(1, Color.web("#FFFC01")),
	};
	Stop[] stop2 = new Stop[] {
			new Stop(0, Color.web("#FFFC01")),
			new Stop(1, Color.web("#01FF1F")),
	};
	Stop[] stop3 = new Stop[] {
			new Stop(0, Color.web("#01FF1F")),
			new Stop(1, Color.web("#01FCFF")),
	};
	Stop[] stop4 = new Stop[] {
			new Stop(0, Color.web("#01FCFF")),
			new Stop(1, Color.web("#0000FF")),
	};
	Stop[] stop5 = new Stop[] {
			new Stop(0, Color.web("#0000FF")),
			new Stop(1, Color.web("#FF00FF")),
	};
	Stop[] stop6 = new Stop[] {
			new Stop(0, Color.web("#FF00FF")),
			new Stop(1, Color.web("#FF0101"))
	};
	LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop1);
	LinearGradient lg2 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop2);
	LinearGradient lg3 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop3);
	LinearGradient lg4 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop4);
	LinearGradient lg5 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop5);
	LinearGradient lg6 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stop6);


	@FXML
	void act_radio_range(ActionEvent event) {
		if(radio_range.isSelected()){
			grid_range.setVisible(true);
			grid_fix.setVisible(false);
		}
		else {
			grid_range.setVisible(false);
			grid_fix.setVisible(true);
		}
		updatePreview();
	}

	@FXML
	void act_radio_fix(ActionEvent event) {
		if(radio_fix.isSelected()){
			grid_fix.setVisible(true);
			grid_range.setVisible(false);
		}
		else {
			grid_fix.setVisible(false);
			grid_range.setVisible(true);
		}
		updatePreview();
	}

	@FXML 
	void initialize(){
		addPreviewListener(radio_back, radio_obj, radio_both);
		addPreviewListener(slider_from, slider_to, slider_fix, radio_fix);
		
		grid_fix.managedProperty().bind(grid_fix.visibleProperty());
		grid_range.managedProperty().bind(grid_range.visibleProperty());
		radio_back.managedProperty().bind(radio_back.visibleProperty());
		radio_both.managedProperty().bind(radio_both.visibleProperty());
		grid_fix.setVisible(false);


		rc1.setFill(Color.web("#FF0000"));
		rc2.setFill(Color.web("#FF0000"));
		rc3.setFill(Color.web("#FF0000"));
		box_colors.getChildren().addAll(rc1, rc2);
		box_colors2.getChildren().add(rc3);

		slider_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_from.setText(""+new_val.intValue());
				rc1.setFill(Color.hsb(slider_from.getValue(), 1.0, 1.0));
			}
		});

		slider_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_to.setText(""+new_val.intValue());
				rc2.setFill(Color.hsb(slider_to.getValue(), 1.0, 1.0));
			}
		});

		slider_fix.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_fix.setText(""+new_val.intValue());
				rc3.setFill(Color.hsb(slider_fix.getValue(), 1.0, 1.0));
			}
		});

		pane_hue1.widthProperty().addListener((obs, oldVal, newVal) -> {
			updateGradientSize();
		});
	}
	
	
	public void updateGradientSize() {
		pane_hue1.getChildren().clear();
		pane_hue2.getChildren().clear();
		
		

		int width = (int) pane_hue1.getWidth();
		double size = width/255.0;
		Rectangle r1 = new Rectangle(0, 0, (int)(size*42), 10);
		Rectangle r2 = new Rectangle(0, 0, (int)(size*48), 10);
		Rectangle r3 = new Rectangle(0, 0, (int)(size*38), 10);
		Rectangle r4 = new Rectangle(0, 0, (int)(size*41), 10);
		Rectangle r5 = new Rectangle(0, 0, (int)(size*43), 10);
		Rectangle r6 = new Rectangle(0, 0, (int)(size*43), 10);
		r1.setFill(lg1);
		r2.setFill(lg2);
		r3.setFill(lg3);
		r4.setFill(lg4);
		r5.setFill(lg5);
		r6.setFill(lg6);
		Rectangle r01 = new Rectangle(0, 0, (int)(size*42), 10);
		Rectangle r02 = new Rectangle(0, 0, (int)(size*48), 10);
		Rectangle r03 = new Rectangle(0, 0, (int)(size*38), 10);
		Rectangle r04 = new Rectangle(0, 0, (int)(size*41), 10);
		Rectangle r05 = new Rectangle(0, 0, (int)(size*43), 10);
		Rectangle r06 = new Rectangle(0, 0, (int)(size*43), 10);
		r01.setFill(lg1);
		r02.setFill(lg2);
		r03.setFill(lg3);
		r04.setFill(lg4);
		r05.setFill(lg5);
		r06.setFill(lg6);

		HBox box = new HBox();
		box.getChildren().addAll(r1, r2, r3 ,r4, r5, r6);
		HBox box2 = new HBox();
		box2.getChildren().addAll(r01, r02, r03 ,r04, r05, r06);
		pane_hue1.getChildren().add(box);
		pane_hue2.getChildren().add(box2);
	}

	
	@Override
	public xml generateXML() {
		xml root = new xml("hue");

		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;

		root.getElement().setAttribute("target",""+target);


		String range1 = "";
		if(radio_fix.isSelected()) {
			range1 = ""+((int)slider_fix.getValue())+":"+((int)slider_fix.getValue());
		}
		else {
			range1 = ""+((int)slider_from.getValue())+":"+((int)slider_to.getValue());
		}

		root.getElement().addContent(range1);

		return root;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return Bildverarbeitung.FILTER_HUE;
	}

	@Override
	public void loadXML(Element root) {
		// TODO Auto-generated method stub

	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("hue");

		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;

		root.getElement().setAttribute("target",""+target);

		if(radio_fix.isSelected()==false)
			prev_SliderData(slider_from, slider_to, PREVIEW, root, true, random);
		else
			prev_SliderData(slider_fix, slider_fix, PREVIEW, root, true, random);
		return root;
	}

	@Override
	public void setObjectMode(boolean b) {
		super.setObjectMode(b);
		
		if(isObjectMode()) {
			radio_back.setVisible(false);
			radio_both.setVisible(false);
			radio_obj.setDisable(true);
		}
	}

}
