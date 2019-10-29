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
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class contr_gamma extends Bildverarbeitung{

	@FXML private RadioButton radio_obj;
	@FXML private RadioButton radio_back;
	@FXML private RadioButton radio_both;
	@FXML private RadioButton radio_fix;
	@FXML private TextField txt_fix;
	@FXML private Slider slider_from;
	@FXML private Slider slider_fix;
	@FXML private TextField txt_to;
	@FXML private Slider slider_to;
	@FXML private TextField txt_from;
	@FXML private RadioButton radio_range;
	@FXML private ToggleGroup group;
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
		addPreviewListener(radio_back, radio_obj, radio_both);
		addPreviewListener(slider_from, slider_to, slider_fix, radio_fix);
		
		grid_fix.managedProperty().bind(grid_fix.visibleProperty());
		grid_range.managedProperty().bind(grid_range.visibleProperty());
		radio_back.managedProperty().bind(radio_back.visibleProperty());
		radio_both.managedProperty().bind(radio_both.visibleProperty());
		grid_fix.setVisible(false);
		
		slider_from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.doubleValue()!=0 && slider_to.getValue() > new_val.doubleValue()) {
					slider_to.setMin(new_val.doubleValue());
					txt_from.setText(""+new_val.doubleValue());
				}
			}
		});

		slider_to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.doubleValue()!=0  && slider_from.getValue() < new_val.doubleValue()) {
					slider_from.setMax(new_val.doubleValue());
					txt_to.setText(""+new_val.doubleValue());
				}
			}
		});

		slider_fix.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_fix.setText(""+new_val.doubleValue());
			}
		});
	}

	@Override
	public xml generateXML() {
		xml root = new xml("gamma");
		
		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;
		
		root.getElement().setAttribute("target",""+target);
		
		if(radio_fix.isSelected()) {
			root.getElement().addContent(""+((int)slider_fix.getValue())+":"+((int)slider_fix.getValue()));
		}
		else {
			root.getElement().addContent(""+((int)slider_from.getValue())+":"+((int)slider_to.getValue()));
		}
		return root;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return Bildverarbeitung.FILTER_SCALE;
	}

	@Override
	public void loadXML(Element root) {
			String data = root.getText();
			
			String s[] = data.split(":");
			int d1 = Integer.parseInt(s[0]);
			int d2 = Integer.parseInt(s[1]);
			
			if(d1 == d2) {
				radio_fix.setSelected(true);
				slider_fix.setValue(d1);
			}
			else {
				radio_fix.setSelected(false);
				slider_from.setValue(d1);
				slider_to.setValue(d2);
			}
			
			//GUI update
			act_radio_range(new ActionEvent());
			act_radio_fix(new ActionEvent());
			
	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("gamma");
		
		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;
		
		root.getElement().setAttribute("target",""+target);
		
		if(radio_fix.isSelected()==false)
			prev_SliderData(slider_from, slider_to, PREVIEW, root, false, random);
		else
			prev_SliderData(slider_fix, slider_fix, PREVIEW, root, false, random);
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
