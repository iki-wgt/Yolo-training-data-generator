package controller;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.jdom2.Element;

import application.xml;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class contr_Vorschau_Settings {

	@FXML private RadioButton radio_fix_bg;
	@FXML private RadioButton radio_std_bg;
	@FXML private RadioButton radio_fix_ob;
	@FXML private RadioButton radio_std_ob;

	@FXML private CheckBox check_all;
	@FXML private CheckBox check_random;

	@FXML private ImageView image;
	@FXML private ImageView image1;
	@FXML private TextField txt_img;
	@FXML private TextField txt_img1;
	@FXML private VBox layout_image;
	@FXML private VBox layout_image1;

	Stage stage = null;

	contr_Voreinstellungen contr_voreinstellungen = null;
	contr_Vorschau_Frame vorschau = null;

	ColorAdjust monochrome = new ColorAdjust();
	Blend blush = new Blend(BlendMode.MULTIPLY,monochrome,new ColorInput(0,0,100,100,Color.WHITE));

	@FXML
	void act_radio_std_bg(ActionEvent event) {
		layout_image.setDisable(true);
		image.setDisable(true);
		image.setEffect(blush);
		vorschau.updatePreview();
	}

	@FXML
	void act_radio_fix_bg(ActionEvent event) {
		layout_image.setDisable(false);
		image.setDisable(false);
		image.setEffect(null);

		File f = new File(txt_img.getText());
		if(f.exists())
			if(f.isFile())
				vorschau.updatePreview();
	}

	@FXML
	void act_radio_std_ob(ActionEvent event) {
		layout_image1.setDisable(true);
		image1.setDisable(true);
		image1.setEffect(blush);
		vorschau.updatePreview();
	}

	@FXML
	void act_radio_fix_ob(ActionEvent event) {
		layout_image1.setDisable(false);
		image1.setDisable(false);
		image1.setEffect(null);

		File f = new File(txt_img1.getText());
		if(f.exists())
			if(f.isFile())
				vorschau.updatePreview();
	}


	public void init(contr_Voreinstellungen contr_voreinstellungen, contr_Vorschau_Frame vorschau, Stage stage) {
		this.contr_voreinstellungen = contr_voreinstellungen;
		this.stage = stage;
		this.vorschau = vorschau;
	}

	@FXML 
	void initialize(){
		layout_image.managedProperty().bind(layout_image.visibleProperty());
		layout_image1.managedProperty().bind(layout_image1.visibleProperty());
		layout_image.setDisable(true);
		layout_image1.setDisable(true);
		image.setDisable(true);
		image1.setDisable(true);

		monochrome.setSaturation(-1.0);
		//		monochrome.setBrightness(0.3);

		image.setEffect(blush);
		image1.setEffect(blush);
	}

	@FXML
	void act_check_all(ActionEvent event) {
		vorschau.updatePreview();
	}

	@FXML
	void act_check_random(ActionEvent event) {
		vorschau.updatePreview();
		vorschau.setButtonVisible(check_random.isSelected());
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

		File output = new File(contr_voreinstellungen.getBackgroundPath());
		if(output != null) {
			if(output.exists() && output.isDirectory()) {
				if(output.isDirectory()) {
					fileChooser.setInitialDirectory(output);
				}
			}
		}


		File file = fileChooser.showOpenDialog(null);
		if(file != null) {
			loadImage(file, true);
		}

		//Testen, ob OK
	}

	private void loadImage(File file, boolean updatePreview) {
		String path = file.getAbsolutePath();
		txt_img.setText(path);
		if(path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".gif") || path.endsWith(".bmp")) {
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(path);
				Image img = new Image(inputStream);
				image.setImage(img);
				image.setPreserveRatio(true);
				image.setFitWidth(600);
				image.setFitHeight(100);

				//Ordnerinhalte löschen:
				File[] del1 = contr_Vorschau_Frame.file_background.listFiles();
				for(int i=del1.length-1;i>=0;i--) {
					del1[i].delete();
				}

				//Kopieren
				BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
				try {
					if(path.endsWith(".jpg"))
						ImageIO.write(bImage, "jpg", contr_Vorschau_Frame.file_background_N);
					else if(path.endsWith(".png"))
						ImageIO.write(bImage, "png", contr_Vorschau_Frame.file_background_N);
					else if(path.endsWith(".gif"))
						ImageIO.write(bImage, "gif", contr_Vorschau_Frame.file_background_N);
					else if(path.endsWith(".bmp"))
						ImageIO.write(bImage, "bmp", contr_Vorschau_Frame.file_background_N);

					if(updatePreview)
						vorschau.updatePreview();
				} catch (IOException e) {

				}
			}
			catch(Exception e2) {
			}
		}
		else {
			image.setImage(null);
			image.setFitWidth(100);
			image.setFitHeight(100);
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
	void act_button_image1(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select opject image");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("BMP", "*.bmp"),
				new FileChooser.ExtensionFilter("GIF", "*.gif")
				);

		File output = new File(contr_voreinstellungen.getObjPath());
		if(output != null) {
			if(output.exists() && output.isDirectory()) {
				if(output.isDirectory()) {
					fileChooser.setInitialDirectory(output);
				}
			}
		}


		File file = fileChooser.showOpenDialog(null);
		if(file != null) {
			loadImage1(file, true);
		}
	}

	private void loadImage1(File file, boolean updatePreview) {
		String path = file.getAbsolutePath();
		txt_img1.setText(path);
		if(path.endsWith(".png")) {
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(file.getAbsolutePath());
				Image img = new Image(inputStream);
				image1.setImage(img);
				image1.setPreserveRatio(true);
				image1.setFitWidth(600);
				image1.setFitHeight(100);

				//Ordnerinhalte löschen:
				File[] del1 = contr_Vorschau_Frame.file_object.listFiles();
				for(int i=del1.length-1;i>=0;i--) {
					if(del1[i].getName().equals("class.xml") == false)
						del1[i].delete();
				}
				

				//Kopieren
				BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
				try {
					ImageIO.write(bImage, "png", contr_Vorschau_Frame.file_object_N);

					if(updatePreview)
						vorschau.updatePreview();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			} catch (FileNotFoundException e) {
			}
		}
		else {
			image1.setImage(null);
			image1.setFitWidth(100);
			image1.setFitHeight(100);
		}
	}

	@FXML
	void act_button_image_open1(ActionEvent event) {
		File f = new File(txt_img1.getText());
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
	void act_button_close(ActionEvent event) {
		stage.close();
	}


	public boolean isStd_obj_path() {
		boolean b = radio_std_ob.isSelected();
		if(b==false) {
			File f = new File(txt_img1.getText());
			if(f.exists())
				if(f.isFile())
					if(f.getName().endsWith(".png"))
						return false;
			return true;
		}
		else
			return true;
	}

	public boolean isStd_bg_path() {

		boolean b = radio_std_bg.isSelected();
		if(b==false) {
			File f = new File(txt_img.getText());
			if(f.exists())
				if(f.isFile())
					if(f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".gif") || f.getName().endsWith(".bmp"))
						return false;
			return true;
		}
		else
			return true;
	}

	public boolean isOnlyOne() {
		return check_all.isSelected();
	}

	public boolean isRandomSelected() {
		return check_random.isSelected();
	}

	public xml getXMLCode() {
		xml root = new xml("preview");
		xml bg_path = new xml("bg_path");
		xml ob_path = new xml("ob_path");
		int type = 0;

		try {
			bg_path.getElement().addContent(txt_img.getText());
		}catch(Exception e) {}
		try {
			ob_path.getElement().addContent(txt_img1.getText());
		}catch(Exception e) {}

		if(isStd_bg_path()==false) {
			type = 0;
		}
		else {
			type = 1;
		}
		bg_path.getElement().setAttribute("type",""+type);

		if(isStd_obj_path()==false) {
			type = 0;
		}
		else {
			type = 1;
		}
		ob_path.getElement().setAttribute("type",""+type);

		root.addNode(bg_path);
		root.addNode(ob_path);

		return root;
	}

	public void clear() {
		check_all.setSelected(false);
		check_random.setSelected(false);
		image.setImage(null);
		image.setFitWidth(100);
		image.setFitHeight(100);
		image1.setImage(null);
		image1.setFitWidth(100);
		image1.setFitHeight(100);
		radio_std_bg.setSelected(true);
		radio_std_ob.setSelected(true);
		layout_image.setDisable(true);
		layout_image1.setDisable(true);
		txt_img.setText("");
		txt_img1.setText("");
	}

	public void setXMLData(Element root) {

		clear();

		try {
			System.out.println("ROOT: "+root);
			Element bg_path = root.getChild("bg_path");
			Element ob_path = root.getChild("ob_path");

			try {
				txt_img.setText(bg_path.getText());
				txt_img1.setText(ob_path.getText());
			}catch(Exception e2) {}
			loadImage(new File(bg_path.getText()), false);
			loadImage1(new File(ob_path.getText()), false);


			int type = bg_path.getAttribute("type").getIntValue();
			if(type == 0) {
				radio_fix_bg.setSelected(true);
			}
			else {
				radio_std_bg.setSelected(true);
			}

			type = ob_path.getAttribute("type").getIntValue();
			if(type == 0) {
				radio_fix_ob.setSelected(true);
			}
			else {
				radio_std_ob.setSelected(true);
			}

			if(isStd_bg_path()==false) {
				image.setEffect(null);
				layout_image.setDisable(false);
			}
			else {
				image.setEffect(blush);
			}

			if(isStd_obj_path()==false) {
				image1.setEffect(null);
				layout_image1.setDisable(false);
			}
			else {
				image1.setEffect(blush);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
