
package controller.Filtereinstellungen;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

import application.OSValidator;
import application.xml;
import controller.contr_hauptmenue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class contr_overlay extends Bildverarbeitung{
	@FXML private RadioButton radio_obj;
	@FXML private RadioButton radio_back;
	@FXML private RadioButton radio_both;
	@FXML private ComboBox<String> combo_filter;
	@FXML private RadioButton radio_folderbac;
	@FXML private RadioButton radio_fixedimg;
	@FXML private VBox layout_image;
	@FXML private HBox layout_folder;
	@FXML private ImageView image;
	@FXML private TextField txt_img;
	@FXML private TextField txt_folder;
	@FXML private TextField txt_alpha;
	@FXML private Slider slider_alpha;
	@FXML private Button button_image_open;
	@FXML private Button button_folder_open;

	private static File std_path = null;

	ObservableList<String> combo_items = FXCollections.observableArrayList();

	@FXML 
	void initialize(){
		addPreviewListener(radio_back, radio_obj, radio_both);

		slider_alpha.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt_alpha.setText(""+new_val.intValue());
				updatePreview();
			}
		});

		layout_folder.managedProperty().bind(layout_folder.visibleProperty());
		layout_image.managedProperty().bind(layout_image.visibleProperty());
		radio_back.managedProperty().bind(radio_back.visibleProperty());
		radio_both.managedProperty().bind(radio_both.visibleProperty());
		layout_image.setVisible(false);

		combo_items.add("Multiply");
		combo_items.add("Color");
		combo_items.add("Brighten");
		combo_items.add("Darken");
		combo_filter.setItems(combo_items);
		combo_filter.getSelectionModel().select(0);
		
		button_image_open.managedProperty().bind(button_image_open.visibleProperty());
		button_folder_open.managedProperty().bind(button_folder_open.visibleProperty());
		if(OSValidator.isWindows()==false && OSValidator.isMac()==false) {
			button_image_open.setVisible(false);
			button_folder_open.setVisible(false);
		}
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
	void act_combo_filter(ActionEvent event) {
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
					System.out.println("JAAAAAAAAAAA "+output);
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
		}catch(Exception e) {
			e.printStackTrace();
		}

		return ok;
	}

	@Override
	public xml generateXML() {
		xml root = new xml("overlay");
		xml overlay_img_path = new xml("overlay_img_path");

		//Filter
		int filter = combo_filter.getSelectionModel().getSelectedIndex();
		root.getElement().setAttribute("filter", ""+filter);

		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;

		root.getElement().setAttribute("target",""+target);

		int path_type = 0;
		if(radio_fixedimg.isSelected()) {
			path_type = 1;
			if(getState())
				overlay_img_path.getElement().addContent(txt_img.getText().replace("\\", "/"));
		}
		else if(radio_folderbac.isSelected()) {
			path_type = 0;
			if(getState())
				overlay_img_path.getElement().addContent(contr_hauptmenue.addSlasch_end(txt_folder.getText().replace("\\", "/")));
		}

		overlay_img_path.getElement().setAttribute("path_type",""+path_type);
		root.getElement().setAttribute("intensity",""+slider_alpha.getValue());
		root.getElement().addContent(overlay_img_path.getElement());

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
		radio_obj.setSelected(false);
		radio_back.setSelected(false);
		radio_both.setSelected(false);

		layout_folder.setVisible(false);
		layout_image.setVisible(false);

		try {
			//Filter
			int filter_index = root.getAttribute("filter").getIntValue();
			combo_filter.getSelectionModel().select(filter_index);
			int intesity = root.getAttribute("intensity").getIntValue();
			slider_alpha.setValue(intesity);
//			txt_alpha.setText(""+intesity);

			int target = root.getAttribute("target").getIntValue();
			if(target == xml.TARGET_OBJ)
				radio_obj.setSelected(true);
			else if(target == xml.TARGET_BACK)
				radio_back.setSelected(true);
			else if(target == xml.TARGET_BOTH)
				radio_both.setSelected(true);
		}catch(Exception e) {
			radio_obj.setSelected(true);
		}


		String data = "";
		try {
			Element overlay_img_path =  root.getChild("overlay_img_path");
			int path_type = overlay_img_path.getAttribute("path_type").getIntValue();
			data = overlay_img_path.getText();
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
		} catch (DataConversionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("overlay");
		xml overlay_img_path = new xml("overlay_img_path");

		//Filter
		int filter = combo_filter.getSelectionModel().getSelectedIndex();
		root.getElement().setAttribute("filter", ""+filter);

		int target = 0;
		if(radio_obj.isSelected())
			target = xml.TARGET_OBJ;
		else if(radio_back.isSelected())
			target = xml.TARGET_BACK;
		else if(radio_both.isSelected())
			target = xml.TARGET_BOTH;

		root.getElement().setAttribute("target",""+target);

		int path_type = 0;
		if(radio_fixedimg.isSelected()) {
			path_type = 1;
			if(getState())
				overlay_img_path.getElement().addContent(txt_img.getText().replace("\\", "/"));
		}
		else if(radio_folderbac.isSelected()) {
			path_type = 0;
			if(getState())
				overlay_img_path.getElement().addContent(contr_hauptmenue.addSlasch_end(txt_folder.getText().replace("\\", "/")));
		}

		overlay_img_path.getElement().setAttribute("path_type",""+path_type);
		root.getElement().setAttribute("intensity",""+(int)slider_alpha.getValue());
		root.getElement().addContent(overlay_img_path.getElement());

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
