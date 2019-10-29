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

public class contr_resolution extends Bildverarbeitung{
	
	private static int MIN_RES_X = 320;
	private static int MAX_RES_X = 3840;
	private static int MIN_RES_Y = 200;
	private static int MAX_RES_Y = 2160;
	private static int MIN_X = 50;
	private static int MIN_Y = 50;

	@FXML private RadioButton radio_fix;
	@FXML private RadioButton radio_range;
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
	@FXML private GridPane grid_range;
	@FXML private GridPane grid_fix;



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
		addPreviewListener(slider_x_from, slider_x_to, slider_x_fix, radio_fix);
		addPreviewListener(slider_y_from, slider_y_to, slider_y_fix, radio_fix);
		
		grid_fix.managedProperty().bind(grid_fix.visibleProperty());
		grid_range.managedProperty().bind(grid_range.visibleProperty());
		grid_fix.setVisible(false);
		
		slider_x_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_x_to.getValue() > new_val.doubleValue()) {
					int temp_x_to = Math.min(new_val.intValue()+MIN_X, MAX_RES_X-MIN_X);
					slider_x_to.setMin(temp_x_to);
					txt_x_from.setText(""+new_val.intValue());
				}
			}
		});

		slider_x_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0  && slider_x_from.getValue() < new_val.doubleValue()) {
					int temp_x_from = Math.max(new_val.intValue()-MIN_X, MIN_RES_X+MIN_X);
					slider_x_from.setMax(temp_x_from);
					txt_x_to.setText(""+new_val.intValue());
				}
			}
		});
		
		slider_y_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_y_to.getValue() > new_val.doubleValue()) {
					int temp_y_to = Math.min(new_val.intValue()+MIN_Y, MAX_RES_Y-MIN_Y);
					slider_y_to.setMin(temp_y_to);
					txt_y_from.setText(""+new_val.intValue());
				}
			}
		});
		
		slider_y_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && slider_y_from.getValue() < new_val.doubleValue()) {
					int temp_y_from = Math.max(new_val.intValue()-MIN_Y, MAX_RES_Y+MIN_Y);
					slider_y_from.setMax(temp_y_from);
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
		xml root = new xml("resolution");
		xml x = new xml("x");
		xml y = new xml("y");
		if(radio_fix.isSelected()) {
			x.getElement().addContent(""+((int)slider_x_fix.getValue())+":"+((int)slider_x_fix.getValue()));
			y.getElement().addContent(""+((int)slider_y_fix.getValue())+":"+((int)slider_y_fix.getValue()));
		}
		else {
			x.getElement().addContent(""+(Integer.parseInt(txt_x_from.getText())+":"+(Integer.parseInt(txt_x_to.getText()))));
			y.getElement().addContent(""+(Integer.parseInt(txt_y_from.getText())+":"+(Integer.parseInt(txt_y_to.getText()))));
		}
		
		root.getElement().addContent(x.getElement());
		root.getElement().addContent(y.getElement());
		return root;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return Bildverarbeitung.FILTER_RESOLUTION;
	}

	@Override
	public void loadXML(Element root) {
		String x = root.getChild("x").getText();
		String y = root.getChild("y").getText();
		
		String sx[] = x.split(":");
		int d1 = Integer.parseInt(sx[0]);
		int d2 = Integer.parseInt(sx[1]);
		String sy[] = y.split(":");
		int d3 = Integer.parseInt(sy[0]);
		int d4 = Integer.parseInt(sy[1]);
		
		if(d1 == d2 && d3 == d4) {
			radio_fix.setSelected(true);
			slider_x_fix.setValue(d1);
			slider_y_fix.setValue(d3);
		}
		else {
			radio_fix.setSelected(false);
			slider_x_from.setValue(d1);
			slider_x_to.setValue(d2);
			slider_y_from.setValue(d3);
			slider_y_to.setValue(d4);
		}
		
		//GUI update
		act_radio_range(new ActionEvent());
		act_radio_fix(new ActionEvent());
	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		if(getParent() != null) {
			if(getParent().getID() == Bildverarbeitung.FILTER_OBJECT) {
				return generateXML();
			}
		}
		xml root = new xml("resolution");
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
		root.getElement().addContent(x.getElement());
		root.getElement().addContent(y.getElement());
		return root;
	}

}
