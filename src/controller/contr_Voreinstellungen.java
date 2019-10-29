package controller;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import application.OSValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class contr_Voreinstellungen {

	@FXML private TextField txt_pixel1;
	@FXML private TextField txt_pixel2;
	@FXML private TextField txt_path_back;
	@FXML private TextField txt_path_frei;
	@FXML private TextField txt_output_path;
	
	@FXML private ComboBox<String> combo_version;

	@FXML Label txt_anz_out;
	@FXML Label txt_anz_frei;
	@FXML Label txt_anz_false;
	@FXML Label txt_anz_overlap;
	@FXML Label txt_anz_back;
	@FXML Label txt_ok;
	@FXML Label txt_not;
	@FXML Label txt_err;

	@FXML private Label label_frei;
	@FXML private Label label_ueber;
	@FXML private Label label_back;
	@FXML private Label label_false;

	@FXML private Button button_class;
	@FXML private Button button_open_output;
	@FXML private Button button_open_frei;
	@FXML private Button button_open_back;

	@FXML private ImageView image_hs;
	
	@FXML private ProgressBar progress_image;

	//	ArrayList<VBox> box_frei = null;
//	int objSize = 0;
	ArrayList<contr_filterFrame> controller = null;

	File file_std = new File(""+System.getProperty("user.dir"));

	Stage stage_classify = new Stage();
	contr_ImageClassification controll_img = null;
	
	ObservableList<String> yolo_version = FXCollections.observableArrayList();

	//Eigene Funktionen /////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	public void init(ArrayList<contr_filterFrame> controller) {
		//		this.box_frei = box_frei;
		this.controller = controller;
		
		button_open_output.managedProperty().bind(button_open_output.visibleProperty());
		button_open_frei.managedProperty().bind(button_open_frei.visibleProperty());
		button_open_back.managedProperty().bind(button_open_back.visibleProperty());
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ImageClassification.fxml"));
			AnchorPane pane_progress = (AnchorPane)loader.load();
			controll_img = loader.<contr_ImageClassification>getController();
			Scene scene_progress = new Scene(pane_progress, 1009, 637);
			stage_classify.setScene(scene_progress);
			stage_classify.setTitle("Classification");
			stage_classify.initModality(Modality.APPLICATION_MODAL);
			stage_classify.setMinHeight(600);
			stage_classify.setMinWidth(900);
			String url = "/background.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			stage_classify.getIcons().add(image); 
			controll_img.initLabels(txt_ok, txt_not, txt_err, txt_anz_frei, progress_image);
			stage_classify.setMaximized(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(OSValidator.isWindows()==false && OSValidator.isMac()==false) {
			button_open_output.setVisible(false);
			button_open_frei.setVisible(false);
			button_open_back.setVisible(false);
		}
	}
	
	public int getYoloVersion() {
		if(combo_version.getSelectionModel().getSelectedIndex() == 0)
			return 2;
		else
			return 3;
	}
	
	public void setYoloVersion(String version) {
		try {
			int v = Integer.parseInt(version);
			if(v == 3)
				combo_version.getSelectionModel().select(1);
			else
				combo_version.getSelectionModel().select(0);
		}catch(Exception e) {};
	}

	public String getHeight_output() {
		return txt_pixel2.getText();
	}

	public String getWidth_output() {
		return txt_pixel1.getText();
	}

	private int findPictures(File file, boolean onlyPNG, boolean subfolder, int max_recusive_tiefe) {
		int anz = 0;
		int t = max_recusive_tiefe -1;
		File f[] = file.listFiles();

		if(f != null) {
			for(int i=0;i<f.length;i++) {
				if(f[i].isFile()) {
					boolean b1 = f[i].getName().toLowerCase().endsWith(".png");
					boolean b2 = false;
					boolean b3 = false;
					if(onlyPNG==false) {
						b2 = f[i].getName().toLowerCase().endsWith(".jpg");
						b3 = f[i].getName().toLowerCase().endsWith(".bmp");
					}
					if(b1 || b2 || b3) {
						anz++;
					}
				}
				else if(f[i].isDirectory() && subfolder && t>0) {
					anz += findPictures(f[i], onlyPNG, subfolder,t);
				}
			}
		}

		return anz;
	}


//	private ArrayList<File> findPictureFiles(File file, boolean onlyPNG, boolean subfolder) {
//		ArrayList<File> files = new ArrayList<>();
//		File f[] = file.listFiles();
//
//		if(f != null) {
//			for(int i=0;i<f.length;i++) {
//				if(f[i].isFile()) {
//					boolean b1 = f[i].getName().toLowerCase().endsWith(".png");
//					boolean b2 = false;
//					boolean b3 = false;
//					if(onlyPNG==false) {
//						b2 = f[i].getName().toLowerCase().endsWith(".jpg");
//						b3 = f[i].getName().toLowerCase().endsWith(".bmp");
//					}
//					if(b1 || b2 || b3) {
//						files.add(f[i]);
//					}
//				}
//				else if(f[i].isDirectory() && subfolder) {
//					files.addAll(findPictureFiles(f[i], onlyPNG, subfolder));
//				}
//			}
//		}
//
//		return files;
//	}












	//Scene-Builder-Funktionen //////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	@FXML
	void initialize() {
		//		layout_false.prefWidthProperty().bind(scroll_false.widthProperty());
		//		layout_ueber.prefWidthProperty().bind(scroll_ueber.widthProperty());
		progress_image.setVisible(false);
		try {
			String url = "/HS_Logo.jpg";
			Image image = new Image(getClass().getResourceAsStream(url));
			image_hs.setImage(image);
		}catch(Exception e) {
			e.printStackTrace();
		};

		yolo_version.add("version v2");
		yolo_version.add("version v3");
		combo_version.setItems(yolo_version);
		combo_version.getSelectionModel().select(0);

	}

	private String path_to_Linux(String path) {
		if(path == null)
			return null;
		return path.replace("\\", "/");
	}

	//Output festlegen /////////////////////////////////////////////////////
	@FXML
	void act_button_open_output(ActionEvent event) {
		try {
			if(txt_output_path.getText()!=null && !txt_output_path.getText().equals("")) {
				File f = new File(path_to_Linux(txt_output_path.getText()));
				if(f.exists()) {
					Desktop.getDesktop().open(f);
				}
				else
					Desktop.getDesktop().open(file_std);
			}
			else
				Desktop.getDesktop().open(file_std);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Thread thread = new Thread();

	@FXML
	void act_button_festlegen_output(ActionEvent event) {
		TextField tmp = txt_output_path;
		File output = new File(tmp.getText());
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select output folder");
		if(output != null)
			if(output.exists())
				chooser.setInitialDirectory(output);
		File file = chooser.showDialog(null);

		if(file != null) {
			tmp.setText(file.getAbsolutePath());
			int anz = findPictures(file, false, true, 3);
			txt_anz_out.setText(""+anz);
		}
	}


	//Freistellen ///////////////////////////////////////////////////////////
	@FXML
	void act_button_open_frei(ActionEvent event) {
		try {
			String s = txt_path_frei.getText();
			if(s!=null && !s.equals("")) {
				s = path_to_Linux(s);
				File f = new File(s);
				if(f.exists()) {
					Desktop.getDesktop().open(f);
				}
				else
					Desktop.getDesktop().open(file_std);
			}
			else
				Desktop.getDesktop().open(file_std);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void act_button_festlegen_frei(ActionEvent event) {
		TextField tmp = txt_path_frei;
		File output = new File(tmp.getText());
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select output folder");
		if(output != null)
			if(output.exists())
				chooser.setInitialDirectory(output);
		File file = chooser.showDialog(null);

		if(file != null) {
			tmp.setText(file.getAbsolutePath());
//			ArrayList<File> files = findPictureFiles(file, true, false);
//			objSize = files.size();
//			txt_anz_frei.setText(""+objSize);
			
			controll_img.loadOnlyData(this, txt_path_frei.getText());
//			controll_img.init(stage_classify, this, txt_path_frei.getText());

			//			box_frei.clear();
			//			for(int i=0;i<anz;i++) {
			//				addImageFreigestellt(files.get(i));
			//			}
		}
	}

	@FXML
	void act_button_class(ActionEvent event) {
		try {
			controll_img.init(stage_classify, this, txt_path_frei.getText());
			stage_classify.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}



	//Hintergrund ////////////////////////////////////////////////////////////
	@FXML
	void act_button_open_back(ActionEvent event) {
		try {
			String s = txt_path_back.getText();
			if(s!=null && !s.equals("")) {
				s = path_to_Linux(s);
				File f = new File(s);
				if(f.exists()) {
					Desktop.getDesktop().open(f);
				}
				else
					Desktop.getDesktop().open(file_std);
			}
			else
				Desktop.getDesktop().open(file_std);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void act_button_festlegen_back(ActionEvent event) {
		TextField tmp = txt_path_back;
		File output = new File(tmp.getText());
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select output folder");
		if(output != null)
			if(output.exists())
				chooser.setInitialDirectory(output);
		File file = chooser.showDialog(null);

		if(file != null) {
			tmp.setText(file.getAbsolutePath());
			int anz = findPictures(file, false, true, 3);
			txt_anz_back.setText(""+anz);
		}
	}

	
	Process process_hsv = null;
	Thread thread_hsv = new Thread() {
		@Override
		public void run() {
			super.run();
			try {		
				String[] cmd = new String[4];
				cmd[0] = "python3";
				cmd[1] = "Python/hsv_finder_start.py";
				cmd[2] = "-p";
				cmd[3] = ""+path_to_Linux(txt_path_frei.getText());
				Runtime rt = Runtime.getRuntime();
				try {
					process_hsv = rt.exec(cmd);
				} catch (IOException e) {
					cmd[0] = "python";
					System.out.println("Can't find program 'python3', try 'python'");
					try {
						process_hsv = rt.exec(cmd);
					} catch (IOException exc) {
						exc.printStackTrace();
						Alert alert2 = new Alert(AlertType.ERROR);
						alert2.setTitle("Error");
						alert2.setHeaderText("Error while starting the Python script");
						alert2.showAndWait();
					}
				}

				BufferedReader bfr_error = new BufferedReader(new InputStreamReader(process_hsv.getErrorStream()));

				String line = "";
				System.out.println("\nOUTPUT FROM HSV-Skript:");
				while((line = bfr_error.readLine()) != null) {
					System.err.println(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	
	@SuppressWarnings("deprecation")
	@FXML
	void act_button_hsv(ActionEvent event) {
		if(process_hsv != null) {
			if(process_hsv.isAlive()) {
				System.out.println("process_hsv isAlive!");
				process_hsv.destroy();
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		while(thread_hsv.isAlive()) {
			thread_hsv.stop();
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		thread_hsv = new Thread() {
			@Override
			public void run() {
				super.run();
				//TODO
				try {		
					String[] cmd = new String[4];
					cmd[0] = "python3";
					cmd[1] = "Python/hsv_finder_start.py";
					cmd[2] = "-p";
					cmd[3] = ""+path_to_Linux(txt_path_frei.getText());
					Runtime rt = Runtime.getRuntime();
					try {
						process_hsv = rt.exec(cmd);
					} catch (IOException e) {
						cmd[0] = "python";
						System.out.println("Can't find program 'python3', try 'python'");
						try {
							process_hsv = rt.exec(cmd);
						} catch (IOException exc) {
							exc.printStackTrace();
							Alert alert2 = new Alert(AlertType.ERROR);
							alert2.setTitle("Error");
							alert2.setHeaderText("Error while starting the Python script");
							alert2.showAndWait();
						}
					}

					BufferedReader bfr_error = new BufferedReader(new InputStreamReader(process_hsv.getErrorStream()));

					String line = "";
					System.out.println("\nOUTPUT FROM HSV-Skript:");
					while((line = bfr_error.readLine()) != null) {
						System.err.println(line);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread_hsv.start();
	}


	//	//Falsche Bilder /////////////////////////////////////////////////////////
	//	@FXML
	//	void act_button_open_false(ActionEvent event) {
	//		try {
	//			String s = txt_path_false.getText();
	//			if(s!=null && !s.equals("")) {
	//				File f = new File(s);
	//				if(f.exists()) {
	//					Desktop.getDesktop().open(f);
	//				}
	//				else
	//					Desktop.getDesktop().open(file_std);
	//			}
	//			else
	//				Desktop.getDesktop().open(file_std);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}


	//	@FXML
	//	void act_button_festlegen_false(ActionEvent event) {
	//		TextField tmp = txt_path_false;
	//		File output = new File(tmp.getText());
	//		DirectoryChooser chooser = new DirectoryChooser();
	//		chooser.setTitle("Select output folder");
	//		if(output != null)
	//			if(output.exists())
	//				chooser.setInitialDirectory(output);
	//		File file = chooser.showDialog(null);
	//
	//		if(file != null) {
	//			tmp.setText(file.getAbsolutePath());
	//			int anz = findPictures(file, false, true, 3);
	//			txt_anz_false.setText(""+anz);
	//		}
	//	}



	//Überlagerungen //////////////////////////////////////////////////////////
	//	@FXML
	//	void act_button_open_ueber(ActionEvent event) {
	//		try {
	//			String s = txt_path_overlap.getText();
	//			if(s!=null && !s.equals("")) {
	//				File f = new File(s);
	//				if(f.exists()) {
	//					Desktop.getDesktop().open(f);
	//				}
	//				else
	//					Desktop.getDesktop().open(file_std);
	//			}
	//			else
	//				Desktop.getDesktop().open(file_std);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}


	//	@FXML
	//	void act_button_festlegen_ueber(ActionEvent event) {
	//		TextField tmp = txt_path_overlap;
	//		File output = new File(tmp.getText());
	//		DirectoryChooser chooser = new DirectoryChooser();
	//		chooser.setTitle("Select output folder");
	//		if(output != null)
	//			if(output.exists())
	//				chooser.setInitialDirectory(output);
	//		File file = chooser.showDialog(null);
	//
	//		if(file != null) {
	//			tmp.setText(file.getAbsolutePath());
	//			int anz = findPictures(file, true, true, 3);
	//			txt_anz_overlap.setText(""+anz);
	//		}
	//	}



	/**
	 * Wird aufgerufen, wenn Ein neues Bild zum Freistellen entweder per drag&drop oder mit dem Explorer hinzugefügt wurde.
	 * @param f
	 */
	//	private void addImageFreigestellt(File f) {
	//		FileInputStream stream = null;
	//		try {
	//			stream = new FileInputStream(f.getAbsolutePath());
	//			Image img = new Image(stream);
	//
	//			VBox box = new VBox();
	//			box.setStyle("-fx-border-color: #00AEAD");
	//			ImageView imageView = new ImageView(img);
	//			imageView.setPreserveRatio(true);
	//			imageView.setFitHeight(130-26);
	//			//			imageView.setFitHeight(scroll_frei.getPrefHeight()-26);
	//			//			imageView.setFitWidth(scroll_frei.getHeight()-26);
	//			box.getChildren().add(imageView);
	//			box_frei.add(box);
	//			layout_frei.getChildren().add(box);
	//			setImageCount();
	//
	//			for(int i=0;i<controller.size();i++) {
	//				controller.get(i).setImageCount(box_frei.size());
	//			}
	//
	//		} catch (FileNotFoundException ex) {
	//			ex.printStackTrace();
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		try {
	//			stream.close();
	//		} catch (IOException e) {
	//		}
	//	}

	public void setImageCount() {
		try {
		for(int i=0;i<controller.size();i++) {
			controller.get(i).setImageCount(Integer.parseInt(txt_ok.getText()));
		}
		}catch(Exception e) {}
	}

	public String getObjPath() {
		return txt_path_frei.getText().replace("\\",	 "/");
	}

	public String getBackgroundPath() {
		return txt_path_back.getText().replace("\\",	 "/");
	}

	public String getOutputPath() {
		return txt_output_path.getText().replace("\\",	 "/");
	}

	//	public String getSimilarPath() {
	//		return txt_path_false.getText().replace("\\",	 "/");
	//	}

	//	public String getOverlapPath() {
	//		return txt_path_overlap.getText().replace("\\",	 "/");
	//	}



	public void setObjPath(String text) {
		TextField tmp = txt_path_frei;
		File file = new File(text);
		if(file != null) {
			if(file.isDirectory()) {
				tmp.setText(file.getAbsolutePath());
//				ArrayList<File> files = findPictureFiles(file, false, false);
//				objSize = files.size();
//				txt_anz_frei.setText(""+objSize);
				setImageCount();
			}
		}

		controll_img.loadOnlyData(this, txt_path_frei.getText());
//		controll_img.init(stage_classify, this, txt_path_frei.getText());

	}

	public void setBackgroundPath(String text) {
		TextField tmp = txt_path_back;
		File file = new File(text);
		if(file != null) {
			if(file.isDirectory()) {
				tmp.setText(file.getAbsolutePath());
				int anz = findPictures(file, false, true, 3);
				txt_anz_back.setText(""+anz);
			}
		}
	}

	public void setOutputPath(String text) {
		TextField tmp = txt_output_path;
		File file = new File(text);
		if(file != null) {
			if(file.isDirectory()) {
				tmp.setText(file.getAbsolutePath());
				int anz = findPictures(file, false, true, 3);
				txt_anz_out.setText(""+anz);
			}
		}
	}

	//	public void setSimilarPath(String text) {
	//		TextField tmp = txt_path_false;
	//		File file = new File(text);
	//		if(file != null) {
	//			if(file.isDirectory()) {
	//				tmp.setText(file.getAbsolutePath());
	//				int anz = findPictures(file, false, true, 3);
	//				txt_anz_false.setText(""+anz);
	//			}
	//		}
	//	}

	//	public void setOverlapPath(String text) {
	//		TextField tmp = txt_path_overlap;
	//		File file = new File(text);
	//		if(file != null) {
	//			if(file.isDirectory()) {
	//				tmp.setText(file.getAbsolutePath());
	//				int anz = findPictures(file, true, true, 3);
	//				txt_anz_overlap.setText(""+anz);
	//			}
	//		}
	//	}

	public void setPixelsPath(String width, String height) {
		txt_pixel1.setText(width);
		txt_pixel2.setText(height);
	}

	public int getObjSize() {
		try {
			return Integer.parseInt(txt_ok.getText());
		}catch(Exception e) {
			return 0;
		}
	}

	


	//	private void deselectAllFreigestellteBilder() {
	//		for(int i=0;i<box_frei.size();i++) {
	//			if(box_frei.get(i).equals(selected_box)) {
	//				box_frei.get(i).setStyle("-fx-border-color: black");
	//			}
	//			else {
	//				box_frei.get(i).setStyle("-fx-border-color: #00AEAD");
	//			}
	//		}
	//	}

}
