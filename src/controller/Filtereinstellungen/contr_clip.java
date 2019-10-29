package controller.Filtereinstellungen;

import org.jdom2.Element;

import application.xml;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class contr_clip extends Bildverarbeitung{

	@FXML private RadioButton radio_fix;
	@FXML private RadioButton radio_range;
	@FXML private RadioButton radio_clipX;
	@FXML private RadioButton radio_clipY;
	@FXML private RadioButton radio_clipXY;
	@FXML private TextField txt_x_from;
	@FXML private TextField txt_x_to;
	@FXML private TextField txt_x_fix;
	@FXML private TextField txt_y_from;
	@FXML private TextField txt_y_to;
	@FXML private TextField txt_y_fix;
	@FXML private Slider slider_x_from;
	@FXML private Slider slider_x_to;
	@FXML private Slider slider_x_fix;
	@FXML private Slider slider_y_from;
	@FXML private Slider slider_y_to;
	@FXML private Slider slider_y_fix;
	@FXML private GridPane grid_range1;
	@FXML private GridPane grid_range2;
	@FXML private GridPane grid_fix1;
	@FXML private GridPane grid_fix2;
	@FXML private VBox layout_range;
	@FXML private VBox layout_fix;
	@FXML private Separator seperatior;


	@FXML
	void act_radio_settings(ActionEvent event) {
		if(radio_clipX.isSelected()) {
			seperatior.setVisible(false);
			grid_range1.setVisible(true);
			grid_range2.setVisible(false);
			grid_fix1.setVisible(true);
			grid_fix2.setVisible(false);
		}
		else if(radio_clipY.isSelected()) {
			seperatior.setVisible(false);
			grid_range1.setVisible(false);
			grid_range2.setVisible(true);
			grid_fix1.setVisible(false);
			grid_fix2.setVisible(true);
		}
		else{
			seperatior.setVisible(true);
			grid_range1.setVisible(true);
			grid_range2.setVisible(true);
			grid_fix1.setVisible(true);
			grid_fix2.setVisible(true);
		}
		updatePreview();
	}


	@FXML
	void act_radio_range(ActionEvent event) {
		if(radio_range.isSelected()){
			layout_range.setVisible(true);
			layout_fix.setVisible(false);
		}
		else {
			layout_range.setVisible(false);
			layout_fix.setVisible(true);
		}
		updatePreview();
	}

	@FXML
	void act_radio_fix(ActionEvent event) {
		if(radio_fix.isSelected()){
			layout_fix.setVisible(true);
			layout_range.setVisible(false);
		}
		else {
			layout_fix.setVisible(false);
			layout_range.setVisible(true);
		}
		updatePreview();
	}


	@FXML 
	void initialize(){
		addPreviewListener(slider_x_from, slider_x_to, slider_x_fix, radio_fix);
		addPreviewListener(slider_y_from, slider_y_to, slider_y_fix, radio_fix);

		grid_fix1.managedProperty().bind(grid_fix1.visibleProperty());
		grid_fix2.managedProperty().bind(grid_fix2.visibleProperty());
		grid_range1.managedProperty().bind(grid_range1.visibleProperty());
		grid_range2.managedProperty().bind(grid_range2.visibleProperty());
		layout_range.managedProperty().bind(layout_range.visibleProperty());
		layout_fix.managedProperty().bind(layout_fix.visibleProperty());
		seperatior.managedProperty().bind(seperatior.visibleProperty());

		layout_fix.setVisible(false);
		grid_range1.setVisible(true);
		grid_range2.setVisible(false);
		grid_fix1.setVisible(true);
		grid_fix2.setVisible(false);

		slider_x_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_x_to.getValue() > new_val.doubleValue()) {
					slider_x_to.setMin(new_val.intValue());
					txt_x_from.setText(""+new_val.intValue());
				}
			}
		});

		slider_x_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0  && slider_x_from.getValue() < new_val.doubleValue()) {
					slider_x_from.setMax(new_val.intValue());
					txt_x_to.setText(""+new_val.intValue());
				}
			}
		});

		slider_y_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_y_to.getValue() > new_val.doubleValue()) {
					slider_y_to.setMin(new_val.intValue());
					txt_y_from.setText(""+new_val.intValue());
				}
			}
		});

		slider_y_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_y_from.getValue() < new_val.doubleValue()) {
					slider_y_from.setMax(new_val.intValue());
					txt_y_to.setText(""+new_val.intValue());
				}
			}
		});

		slider_x_fix.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_x_fix.setText(""+new_val.intValue());
			}
		});

		slider_y_fix.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_y_fix.setText(""+new_val.intValue());
			}
		});
	}




	@Override
	public xml generateXML() {
		xml root = new xml("clip");
		xml x = new xml("x");
		xml y = new xml("y");
		if(radio_fix.isSelected()) {
			x.getElement().addContent(""+((int)slider_x_fix.getValue())+":"+((int)slider_x_fix.getValue()));
			y.getElement().addContent(""+((int)slider_y_fix.getValue())+":"+((int)slider_y_fix.getValue()));
		}
		else {
			x.getElement().addContent(""+((int)slider_x_from.getValue())+":"+((int)slider_x_to.getValue()));
			y.getElement().addContent(""+((int)slider_y_from.getValue())+":"+((int)slider_y_to.getValue()));
		}

		if(radio_clipX.isSelected() || radio_clipXY.isSelected())
			root.getElement().addContent(x.getElement());
		if(radio_clipY.isSelected() || radio_clipXY.isSelected())
			root.getElement().addContent(y.getElement());
		return root;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return Bildverarbeitung.FILTER_CLIP;
	}

	@Override
	public void loadXML(Element root) {

		if(root.getChild("x") != null) {
			String x = root.getChild("x").getText();
			String sx[] = x.split(":");
			int d1 = Integer.parseInt(sx[0]);
			int d2 = Integer.parseInt(sx[1]);

			if(d1 == d2) {
				radio_fix.setSelected(true);
				slider_x_fix.setValue(d1);
			}
			else {
				radio_fix.setSelected(false);
				slider_x_from.setValue(d1);
				slider_x_to.setValue(d2);
			}
			radio_clipX.setSelected(true);
		}

		if(root.getChild("y") != null) {
			String y = root.getChild("y").getText();
			String sy[] = y.split(":");
			int d3 = Integer.parseInt(sy[0]);
			int d4 = Integer.parseInt(sy[1]);

			if(d3 == d4) {
				radio_fix.setSelected(true);
				slider_y_fix.setValue(d3);
			}
			else {
				radio_fix.setSelected(false);
				slider_y_from.setValue(d3);
				slider_y_to.setValue(d4);
			}
			radio_clipY.setSelected(true);
		}

		if(root.getChild("x") != null && (root.getChild("y") != null)){
			radio_clipXY.setSelected(true);
		}

		



		//		if(d1 == d2 && d3 == d4) {
		//			radio_fix.setSelected(true);
		//			slider_x_fix.setValue(d1);
		//			slider_y_fix.setValue(d3);
		//		}
		//		else {
		//			radio_fix.setSelected(false);
		//			slider_x_from.setValue(d1);
		//			slider_x_to.setValue(d2);
		//			slider_y_from.setValue(d3);
		//			slider_y_to.setValue(d4);
		//		}

		//GUI update
		act_radio_range(new ActionEvent());
		act_radio_fix(new ActionEvent());
		act_radio_settings(new ActionEvent());
	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("clip");
		xml x = new xml("x");
		xml y = new xml("y");
		if(radio_fix.isSelected()) {
			Bildverarbeitung.prev_SliderData(slider_x_fix, slider_x_fix, PREVIEW, x, true, random);
			Bildverarbeitung.prev_SliderData(slider_y_fix, slider_y_fix, PREVIEW, y, true, random);
		}
		else {
			Bildverarbeitung.prev_SliderData(slider_x_from, slider_x_to, PREVIEW, x, true, random);
			Bildverarbeitung.prev_SliderData(slider_y_from, slider_y_to, PREVIEW, y, true, random);
		}

		if(radio_clipX.isSelected() || radio_clipXY.isSelected())
			root.getElement().addContent(x.getElement());
		if(radio_clipY.isSelected() || radio_clipXY.isSelected())
			root.getElement().addContent(y.getElement());
		return root;
	}

}
