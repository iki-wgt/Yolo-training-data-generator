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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class contr_background extends Bildverarbeitung{
	@FXML private ToggleGroup Group;
	@FXML private TextField txt_img;
	@FXML private TextField txt_folder;
	@FXML private ColorPicker chooser;
	@FXML private HBox layout_color;
	@FXML private VBox layout_image;
	@FXML private HBox layout_folder;
	@FXML private RadioButton radio_random;
	@FXML private RadioButton radio_folderbac;
	@FXML private RadioButton radio_fixedimg;
	@FXML private RadioButton radio_fix_mono;
	@FXML private RadioButton radio_color;
	@FXML private ImageView image;
	@FXML private Button button_image_open;
	@FXML private Button button_folder_open;


	@FXML 
	void initialize(){
		layout_folder.managedProperty().bind(layout_folder.visibleProperty());
		layout_image.managedProperty().bind(layout_image.visibleProperty());
		layout_color.managedProperty().bind(layout_color.visibleProperty());
		layout_folder.setVisible(false);
		layout_image.setVisible(false);
		layout_color.setVisible(false);
		
		button_image_open.managedProperty().bind(button_image_open.visibleProperty());
		button_folder_open.managedProperty().bind(button_folder_open.visibleProperty());
		if(OSValidator.isWindows()==false && OSValidator.isMac()==false) {
			button_image_open.setVisible(false);
			button_folder_open.setVisible(false);
		}
	}

	@FXML
	void act_chooser(ActionEvent event) {
		updatePreview();
	}
	
	@FXML
	void act_button_folder(ActionEvent event) {
		TextField tmp = txt_folder;
		File output = new File(tmp.getText());
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select output folder");
		if(output != null)
			if(output.exists())
				chooser.setInitialDirectory(output);
		File file = chooser.showDialog(null);

		if(file != null) {
			tmp.setText(file.getAbsolutePath());
		}
		
		checkPath(file, true);
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
	void act_radio_random(ActionEvent event) {
		layout_folder.setVisible(false);
		layout_image.setVisible(false);
		layout_color.setVisible(false);
		setError(false);
		updatePreview();
	}
	@FXML
	void act_radio_folderbac(ActionEvent event) {
		layout_folder.setVisible(true);
		layout_image.setVisible(false);
		layout_color.setVisible(false);
		checkPath(new File(txt_folder.getText()), true);
		updatePreview();
	}

	@FXML
	void act_radio_fixedimg(ActionEvent event) {
		layout_folder.setVisible(false);
		layout_image.setVisible(true);
		layout_color.setVisible(false);
		checkPath(new File(txt_img.getText()), false);
		updatePreview();
	}

	@FXML
	void act_radio_color(ActionEvent event) {
		layout_folder.setVisible(false);
		layout_image.setVisible(false);
		layout_color.setVisible(false);
		setError(false);
		updatePreview();
	}

	@FXML
	void act_radio_fix_mono(ActionEvent event) {
		layout_folder.setVisible(false);
		layout_image.setVisible(false);
		layout_color.setVisible(true);
		setError(false);
		updatePreview();
	}

	@Override
	public boolean getState() {
		boolean ok = false;
		try {
			if(radio_fix_mono.isSelected()) {
				ok = true;
			}
			else if(radio_color.isSelected()) {
				ok = true;
			}
			else if(radio_fixedimg.isSelected()) {
				File f = new File(txt_img.getText());
				if(f.exists() && f.isFile())
					ok = true;
			}
			else if(radio_random.isSelected()) {
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
		xml root = new xml("background");

		int type = 3;
		if(radio_fix_mono.isSelected()) {
			type = 0;
			root.getElement().addContent(chooser.getValue().toString());
		}
		else if(radio_color.isSelected()) {
			type = 1;
		}
		else if(radio_fixedimg.isSelected()) {
			type = 2;
			if(getState())
				root.getElement().addContent(txt_img.getText().replace("\\", "/"));
		}
		else if(radio_random.isSelected()) {
			type = 3;
		}
		else if(radio_folderbac.isSelected()) {
			type = 4;
			if(getState())
				root.getElement().addContent(contr_hauptmenue.addSlasch_end(txt_folder.getText().replace("\\", "/")));
		}

		root.getElement().setAttribute("type",""+type);


		return root;
	}


	@Override
	public int getID() {
		return Bildverarbeitung.FILTER_BACKGROUND;
	}


	@Override
	public void loadXML(Element root) {
		radio_fix_mono.setSelected(false);
		radio_color.setSelected(false);
		radio_fixedimg.setSelected(false);
		radio_random.setSelected(false);
		radio_folderbac.setSelected(false);

		layout_folder.setVisible(false);
		layout_image.setVisible(false);
		layout_color.setVisible(false);


		String data = "";
		int type = 3;
		try {
			type = root.getAttribute("type").getIntValue();
		} catch (DataConversionException e) {
			e.printStackTrace();
		}
		data = root.getText();
		if(System.getProperty("os.name").contains("Windows")) {
			data = data.replace("/", "\\");
		}
		else {
			data = data.replace("\\", "/");
		}



		if(type == 0) {
			radio_fix_mono.setSelected(true);
			layout_color.setVisible(true);
			data = data.replace("0x", "#");
			Color c = Color.web(data);
			chooser.setValue(c);
		}
		else if(type == 1) {
			radio_color.setSelected(true);
		}
		else if(type == 2) {
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
		else if(type == 3) {
			radio_random.setSelected(true);
		}
		else if(type == 4) {
			radio_folderbac.setSelected(true);
			layout_folder.setVisible(true);
			txt_folder.setText(data);
			checkPath(new File(txt_folder.getText()), true);
		}

	}

	@Override
	public xml gererateXML_Preview(int PREVIEW, boolean random) {
		xml root = new xml("background");
		
		if(radio_fix_mono.isSelected()) {
			root.getElement().addContent(chooser.getValue().toString());
			root.getElement().setAttribute("type",""+0);
		}
		else if(radio_color.isSelected()) {
			root.getElement().setAttribute("type",""+1);
		}
		else {
			root.getElement().setAttribute("type",""+3);
		}
		
		
		return root;
	}

}
