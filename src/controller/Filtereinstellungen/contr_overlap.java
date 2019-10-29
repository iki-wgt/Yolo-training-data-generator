package controller.Filtereinstellungen;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

import application.OSValidator;
import application.xml;
import controller.contr_Auswahl_Frame;
import controller.contr_Voreinstellungen;
import controller.contr_Vorschau_Frame;
import controller.contr_filter;
import controller.contr_filterFrame;
import controller.contr_hauptmenue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import komponenten.FilterButton;

public class contr_overlap extends Bildverarbeitung{
	@FXML private ComboBox<String> combo_filter;

	//Front/Biheind
	@FXML private RadioButton radio_front;
	@FXML private RadioButton radio_behind;

	//Background
	@FXML private RadioButton radio_folderbac;
	@FXML private RadioButton radio_fixedimg;

	@FXML private VBox layout_image;
	@FXML private HBox layout_folder;

	@FXML private ImageView image;

	@FXML private TextField txt_img;
	@FXML private TextField txt_folder;

	//Clip
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
	
	@FXML private Button button_image_open;
	@FXML private Button button_folder_open;


	private static File std_path = null;
	public final static int WIDTH_OBJ = 68;
	ArrayList<Integer> list_id = new ArrayList<>();
	HBox layout = new HBox();
	contr_Auswahl_Frame controll_auswahl = null;
	public contr_filterFrame controll_filterframe = null;	
	public contr_Vorschau_Frame controll_vorschau = null;	
	public contr_Voreinstellungen controll_voreinstellungen = null;	
	contr_filter controll_filter = null;	
	public ArrayList<FilterButton> filter = new ArrayList<>();
	AnchorPane pane_einstellungen = null;
	ScrollPane scroll_einstellungen = null;
	



	public void init(HBox layout_filter, contr_Auswahl_Frame controll_auswahl, contr_filterFrame controll_filterframe, contr_Vorschau_Frame controll_vorschau, AnchorPane pane_einstellungen, ScrollPane scroll_einstellungen, contr_filter controll_filter, contr_Voreinstellungen controll_voreinstellungen) {
		layout_filter.getChildren().clear();
		layout_filter.getChildren().add(layout);
		this.controll_auswahl = controll_auswahl;
		this.controll_filterframe = controll_filterframe;
		this.controll_vorschau = controll_vorschau;
		this.pane_einstellungen = pane_einstellungen;
		this.scroll_einstellungen = scroll_einstellungen;
		this.controll_filter = controll_filter;
		this.controll_voreinstellungen = controll_voreinstellungen;
	}

	public void addFilter(FilterButton filter, int id, boolean updatePreview) {
		clearSelection();
		filter.setWidth(WIDTH_OBJ);
		filter.setObjEdit(true);
		filter.setParent(this);
		filter.getController().setParent(this);

		filter.getButton().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if(filter.isSelected() == false) {
					clearSelection();
					setSelectedFilter(id, filter);
				}
				else {
					setSelectedFilter(id, filter);
				}
			}
		});

		this.filter.add(filter);
		layout.getChildren().add(filter.getComponent());
		addID(filter.getFilterID());

		setSelectedFilter(id, filter);
		controll_filterframe.updateErrorColor();

		if(updatePreview)
			filter.getController().updatePreview();
		setError(!getState());

	}

	private void setSelectedFilter(int id, FilterButton filter) {
		System.out.println("setSelectedFilter for object!");
		filter.setSelected(true);

		AnchorPane pane = filter.getPane();
		pane_einstellungen.getChildren().clear();
		pane_einstellungen.getChildren().add(pane);
		scroll_einstellungen.prefWidthProperty().unbind();
		pane.prefWidthProperty().bind(scroll_einstellungen.widthProperty().subtract(24));
		if(controll_vorschau.contr_settings.isOnlyOne())
			controll_vorschau.updatePreview();
	}

	public FilterButton getSelectedFilter() {
		for(int i=0;i<filter.size();i++) {
			if(filter.get(i).isSelected())
				return filter.get(i);
		}
		return null;
	}

	public void removeFilter(FilterButton filter) {

		FilterButton delfilter = getSelectedFilter();
		if(delfilter != null) {
			this.filter.remove(delfilter);
			removeID(delfilter.getFilterID());
			int index = layout.getChildren().indexOf(delfilter.getComponent());
			layout.getChildren().remove(delfilter.getComponent());

			index--;
			if(index == -1)
				index = 0;
			if(this.filter.size() > index) {
				setSelectedFilter(this.filter.get(index).getFilterID(), this.filter.get(index));
			}
			setError(!getState());
			controll_vorschau.updatePreview();
		}
	}

	public void left() {
		FilterButton button = getSelectedFilter();
		if(button != null) {
			int index = filter.indexOf(button);
			System.out.println("is selected: "+index);
			if(index != -1) {
				if(0<index) {
					FilterButton tmp = filter.get(index);
					filter.remove(tmp);
					layout.getChildren().remove(tmp.getComponent());
					filter.add(index-1, tmp);
					layout.getChildren().add(index-1, tmp.getComponent());
				}
			}
		}
	}

	public void right() {
		FilterButton button = getSelectedFilter();
		if(button != null) {
			int index = filter.indexOf(button);
			System.out.println("is selected: "+index);
			if(index != -1) {
				if(filter.size()-1>index) {
					FilterButton tmp = filter.get(index);
					filter.remove(tmp);
					layout.getChildren().remove(tmp.getComponent());
					filter.add(index+1, tmp);
					layout.getChildren().add(index+1, tmp.getComponent());
				}
			}
		}
	}

	private void addID(int id) {
		if(list_id.contains(id) == false)
			list_id.add(id);
	}

	private void removeID(int id) {
		if(list_id.contains(id) == true) {
			list_id.remove(list_id.indexOf(id));
			controll_auswahl.setDisableFilterList(list_id);
		}
	}

	private void clearSelection() {
		for(int i=0;i<filter.size();i++) {
			filter.get(i).setSelected(false);
		}
	}
	

	@FXML 
	void initialize(){
		addPreviewListener(slider_x_from, slider_x_to, slider_x_fix, radio_fix);
		addPreviewListener(slider_y_from, slider_y_to, slider_y_fix, radio_fix);
		
		//Background
		layout_folder.managedProperty().bind(layout_folder.visibleProperty());
		layout_image.managedProperty().bind(layout_image.visibleProperty());
		//		layout_folder.setVisible(false);
		layout_image.setVisible(false);

		//Clip
		grid_fix.managedProperty().bind(grid_fix.visibleProperty());
		grid_range.managedProperty().bind(grid_range.visibleProperty());
		grid_fix.setVisible(false);

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
		
		
		button_image_open.managedProperty().bind(button_image_open.visibleProperty());
		button_folder_open.managedProperty().bind(button_folder_open.visibleProperty());
		if(OSValidator.isWindows()==false && OSValidator.isMac()==false) {
			button_image_open.setVisible(false);
			button_folder_open.setVisible(false);
		}
	}


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
	void act_radio_folderbac(ActionEvent event) {
		layout_folder.setVisible(true);
		layout_image.setVisible(false);
		checkPath(new File(txt_folder.getText()), true);
		updatePreview();
	}

	@FXML
	void act_radio_fixedimg(ActionEvent event) {
		layout_folder.setVisible(false);
		layout_image.setVisible(true);
		checkPath(new File(txt_img.getText()), false);
		updatePreview();
	}

	@FXML
	void act_button_folder(ActionEvent event) {
		TextField tmp = txt_folder;
		File output = new File(tmp.getText());
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select output folder");
		if(std_path!=null && std_path.exists() && std_path.isDirectory())
			chooser.setInitialDirectory(std_path);
		if(output != null)
			if(output.exists())
				chooser.setInitialDirectory(output);
		File file = chooser.showDialog(null);

		if(file != null) {
			tmp.setText(file.getAbsolutePath());
			std_path = file;
		}
		checkPath(file, true);
		updatePreview();
	}

	@FXML
	void act_button_image(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select background image");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("BMP", "*.bmp"),
				new FileChooser.ExtensionFilter("GIF", "*.gif")
				);
		
		if(std_path!=null && std_path.exists() && std_path.isDirectory())
			fileChooser.setInitialDirectory(std_path);

		boolean set = false;
		File output = new File(txt_img.getText());
		if(output != null) {
			if(output.exists()) {
				output = output.getParentFile();
				if(output.isDirectory()) {
					fileChooser.setInitialDirectory(output);
					set = true;
				}
			}
		}
		if(set==false) {
			try {
				File bg_file = new File(paths.getBackgroundPath());
				if(bg_file.exists()) {
					fileChooser.setInitialDirectory(bg_file);
				}
			}catch(Exception e) {}
		}


		File file = fileChooser.showOpenDialog(null);
		if(file != null) {
			if(file.getParentFile().isDirectory())
				std_path = file.getParentFile();
			String path = file.getAbsolutePath();
			txt_img.setText(path);

			if(path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".gif") || path.endsWith(".bmp")) {
				InputStream inputStream;
				try {
					inputStream = new FileInputStream(file.getAbsolutePath());
					Image img = new Image(inputStream);
					image.setImage(img);
					image.setPreserveRatio(true);
					image.setFitWidth(600);
					image.setFitHeight(100);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			else {
				image.setImage(null);
				image.setFitWidth(100);
				image.setFitHeight(100);
			}
		}
		checkPath(file, false);
		updatePreview();
	}


	@FXML
	void act_button_folder_open(ActionEvent event) {
		File f = new File(txt_folder.getText());
		if(f != null) {
			if(f.exists()) {
				try {
					Desktop.getDesktop().open(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@FXML
	void act_button_image_open(ActionEvent event) {
		File f = new File(txt_img.getText());
		if(f != null) {
			if(f.exists()) {
				try {
					Desktop.getDesktop().open(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	void act_radio_front(ActionEvent event) {
		updatePreview();
	}
	
	@FXML
	void act_radio_behind(ActionEvent event) {
		updatePreview();
	}

	@Override
	public xml generateXML() {
		xml root = new xml("overlap");
		xml overlap_img_path = new xml("overlap_img_path");

		int type = 0;
		if(radio_front.isSelected())
			type = xml.TYPE_FRONT;
		else if(radio_behind.isSelected())
			type = xml.TYPE_BEHIND;
		root.getElement().setAttribute("type",""+type);

		int path_type = 0;
		if(radio_fixedimg.isSelected()) {
			path_type = 1;
			if(getState_noFilter())
				overlap_img_path.getElement().addContent(txt_img.getText().replace("\\", "/"));
		}
		else if(radio_folderbac.isSelected()) {
			path_type = 0;
			if(getState_noFilter())
				overlap_img_path.getElement().addContent(contr_hauptmenue.addSlasch_end(txt_folder.getText().replace("\\", "/")));
		}

		overlap_img_path.getElement().setAttribute("path_type",""+path_type);

		//Clip:
		xml x = new xml("x_percentage");
		xml y = new xml("y_percentage");
		if(radio_fix.isSelected()) {
			x.getElement().addContent(""+((int)slider_x_fix.getValue())+":"+((int)slider_x_fix.getValue()));
			y.getElement().addContent(""+((int)slider_y_fix.getValue())+":"+((int)slider_y_fix.getValue()));
		}
		else {
			x.getElement().addContent(""+((int)slider_x_from.getValue())+":"+((int)slider_x_to.getValue()));
			y.getElement().addContent(""+((int)slider_y_from.getValue())+":"+((int)slider_y_to.getValue()));
		}

		root.getElement().addContent(x.getElement());
		root.getElement().addContent(y.getElement());
		root.getElement().addContent(overlap_img_path.getElement());

		for(int i=0;i<filter.size();i++) {
			root.getElement().addContent(filter.get(i).getController().generateXML().getElement());
		}
		
		return root;
	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("overlap");
		xml overlap_img_path = new xml("overlap_img_path");
	
		int type = 0;
		if(radio_front.isSelected())
			type = xml.TYPE_FRONT;
		else if(radio_behind.isSelected())
			type = xml.TYPE_BEHIND;
		root.getElement().setAttribute("type",""+type);
	
		int path_type = 0;
		if(radio_fixedimg.isSelected()) {
			path_type = 1;
			if(getState_noFilter())
				overlap_img_path.getElement().addContent(txt_img.getText().replace("\\", "/"));
		}
		else if(radio_folderbac.isSelected()) {
			path_type = 0;
			if(getState_noFilter())
				overlap_img_path.getElement().addContent(contr_hauptmenue.addSlasch_end(txt_folder.getText().replace("\\", "/")));
		}
	
		overlap_img_path.getElement().setAttribute("path_type",""+path_type);
	
		//Clip:
		xml x = new xml("x_percentage");
		xml y = new xml("y_percentage");
		if(radio_fix.isSelected()) {
			x.getElement().addContent(""+((int)slider_x_fix.getValue())+":"+((int)slider_x_fix.getValue()));
			y.getElement().addContent(""+((int)slider_y_fix.getValue())+":"+((int)slider_y_fix.getValue()));
		}
		else {
			x.getElement().addContent(""+((int)slider_x_from.getValue())+":"+((int)slider_x_to.getValue()));
			y.getElement().addContent(""+((int)slider_y_from.getValue())+":"+((int)slider_y_to.getValue()));
		}
	
		root.getElement().addContent(x.getElement());
		root.getElement().addContent(y.getElement());
		root.getElement().addContent(overlap_img_path.getElement());
	
		for(int i=0;i<filter.size();i++) {
			root.getElement().addContent(filter.get(i).getController().gererateXML_Preview(PREVIEW, random).getElement());
		}
		
		return root;
	}

	@Override
	public int getID() {
		return Bildverarbeitung.FILTER_OVERLAY;
	}


	@Override
	public void loadXML(Element root) {
		radio_fixedimg.setSelected(false);
		radio_folderbac.setSelected(false);

		layout_folder.setVisible(false);
		layout_image.setVisible(false);

		String data = "";
		try {
			int type = root.getAttribute("type").getIntValue();
			
			if(type == xml.TYPE_FRONT)
				radio_front.setSelected(true);
			else if(type == xml.TYPE_BEHIND)
				radio_behind.setSelected(true);
			
			
			//Background
			Element overlap_img_path =  root.getChild("overlap_img_path");
			int path_type = overlap_img_path.getAttribute("path_type").getIntValue();
			data = overlap_img_path.getText();
			if(System.getProperty("os.name").contains("Windows")) {
				data = data.replace("/", "\\");
			}
			else {
				data = data.replace("\\", "/");
			}


			if(path_type == 1) {
				radio_fixedimg.setSelected(true);
				layout_image.setVisible(true);
				txt_img.setText(data);
				checkPath(new File(txt_img.getText()), false);

				if(data.endsWith(".jpg") || data.endsWith(".png") || data.endsWith(".gif") || data.endsWith(".bmp")) {
					InputStream inputStream;
					try {
						inputStream = new FileInputStream(data);
						Image img = new Image(inputStream);
						image.setImage(img);
						image.setPreserveRatio(true);
						image.setFitWidth(600);
						image.setFitHeight(100);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else {
					image.setImage(null);
					image.setFitWidth(100);
					image.setFitHeight(100);
				}
			}
			else if(path_type == 0) {
				radio_folderbac.setSelected(true);
				layout_folder.setVisible(true);
				txt_folder.setText(data);
				checkPath(new File(txt_folder.getText()), true);
			}


			//Clip
			String x = root.getChild("x_percentage").getText();
			String y = root.getChild("y_percentage").getText();

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

			//Filterelemente entschlüsseln
			List<Element> list = root.getChildren();

			for (int i=0;i<list.size();i++) {
				String name = list.get(i).getName();

				if(!name.equals("description") && !name.equals("overlap_img_path") && !name.equals("x_percentage") && !name.equals("y_percentage"))
				{
					int id = Bildverarbeitung.getID(list.get(i).getName());
					FilterButton filter = new FilterButton(id, this.filter, true, controll_voreinstellungen, controll_filterframe, controll_vorschau, WIDTH_OBJ);
					filter.setObjEdit(true);
					filter.setWidth(WIDTH_OBJ);
					addFilter(filter, id, true);
					filter.getController().loadXML(list.get(i));
				}
			}

			//GUI update
			act_radio_range(new ActionEvent());
			act_radio_fix(new ActionEvent());

		} catch (DataConversionException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<Integer> getDeleteList() {
		return list_id;
	}

//	@Override
//	public boolean getState() {
//		boolean ok = false;
//		try {
//			if(radio_fixedimg.isSelected()) {
//				File f = new File(txt_img.getText());
//				if(f.exists() && f.isFile())
//					ok = true;
//			}
//			else if(radio_folderbac.isSelected()) {
//				File f = new File(txt_folder.getText());
//				if(f.exists() && f.isDirectory())
//					ok = true;
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	
//		return ok;
//	}

	@Override
	public boolean getState() {
		boolean ok = false;
		try {
			if(radio_fixedimg.isSelected()) {
				File f = new File(txt_img.getText());
				if(f.exists() && f.isFile())
					ok = true;
			}
			else if(radio_folderbac.isSelected()) {
				File f = new File(txt_folder.getText());
				if(f.exists() && f.isDirectory())
					ok = true;
			}
			if(ok!=false) {
				for(int i=0;i<filter.size();i++) {
					if(filter.get(i).getController().getState() == false) {
						return false;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ok;
	}
	
	public boolean getState_noFilter() {
		boolean ok = false;
		try {
			if(radio_fixedimg.isSelected()) {
				File f = new File(txt_img.getText());
				if(f.exists() && f.isFile())
					ok = true;
			}
			else if(radio_folderbac.isSelected()) {
				File f = new File(txt_folder.getText());
				if(f.exists() && f.isDirectory())
					ok = true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ok;
	}

	@Override
	protected boolean checkPath(File file, boolean isDirectory) {
		boolean error = super.checkPath(file, isDirectory);
		setError(!getState());
		return error;
	}
}
