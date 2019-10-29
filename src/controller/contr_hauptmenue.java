package controller;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import application.xml;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class contr_hauptmenue {
	@FXML private ScrollPane scroll2;
	@FXML private VBox layout2;
	@FXML private Label label_gesamt;
	@FXML private SplitPane split;
	@FXML private CheckMenuItem item_one_window;
	@FXML private MenuItem menuitem_save;
	@FXML private MenuItem menuitem_save_setting;
	@FXML private contr_Voreinstellungen ctr_VoreinstellungenController;

	boolean b_noComponents = true;

	String programm_name = "Image Generator";
	String open_file_name = "new";

	double divider_pos[] = {0.35, 0.65, 1.0};
	double divider_pos2[] = {0.0, 1.0};

	//	ArrayList<VBox> box_frei = new ArrayList<>();
	int image_obj_count = 0;
	ArrayList<contr_filterFrame> filterFrameController_list = new ArrayList<>();
	ArrayList<AnchorPane> filterFrame_pane_list = new ArrayList<>();

	AnchorPane pane = null;
	Stage paneStage = null;
	Stage stage = null;
	Stage stage_progress = null;
	Stage stage_edit = null;

	AnchorPane pane_auswahl = null;
	//	AnchorPane pane_auswahl2 = null;
	AnchorPane pane_vorschau = null;
	contr_Auswahl_Frame controll_auswahl = null;
	//	contr_Auswahl_Frame controll_auswahl2 = null;
	contr_Vorschau_Frame control_vorschau = null;
	contr_Bearbeiten_Window control_layout = null;
	contr_Progress controll_progress = null;
	contr_hauptmenue control_haupt = null;

	SplitPane split_vorschau = new SplitPane();


	File file_save = null;
	File file_save_setting = null;
	//	boolean b_enable_save = false;
	//	boolean b_enable_save = false;

	double xOffset = 0;
	double yOffset = 0;


	//SceneBuilder-Funktionen ///////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	@FXML 
	void initialize(){
		scroll2.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
			Bounds v = newVal;
			layout2.setPrefWidth(v.getWidth());
			System.out.println("width: "+v.getWidth());
		});


		ctr_VoreinstellungenController.init(filterFrameController_list);

		//		Rectangle2D bound = Screen.getPrimary().getVisualBounds();
		//		System.out.println(""+bound.getMaxX()+" "+bound.getWidth()+" "+bound.getHeight()+ " "+bound.getMaxY());

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Progress.fxml"));
			AnchorPane pane_progress = (AnchorPane)loader.load();
			controll_progress = loader.<contr_Progress>getController();
			Scene scene_progress = new Scene(pane_progress,557,293);
			stage_progress = new Stage();
			stage_progress.setScene(scene_progress);
			stage_progress.setTitle("Progress");
			stage_progress.initStyle(StageStyle.UNIFIED);
			stage_progress.setOnCloseRequest(e->e.consume());
			stage_progress.setResizable(false);
			stage_progress.initModality(Modality.APPLICATION_MODAL);
			String url = "/background.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			stage_progress.getIcons().add(image); 

			//			pane_progress.setOnMousePressed((MouseEvent event) -> {
			//		            xOffset = event.getSceneX();
			//		            yOffset = event.getSceneY();
			//		        });
			//			pane_progress.setOnMouseDragged((MouseEvent event) -> {
			//				stage_progress.setX(event.getScreenX() - xOffset);
			//				stage_progress.setY(event.getScreenY() - yOffset);
			//		        });


			loader = new FXMLLoader(getClass().getResource("/GUI/Auswahl_Frame.fxml"));
			pane_auswahl = (AnchorPane)loader.load();
			controll_auswahl = loader.<contr_Auswahl_Frame>getController();


			//			loader = new FXMLLoader(getClass().getResource("/GUI/Auswahl_Frame.fxml"));
			//			pane_auswahl2 = (AnchorPane)loader.load();
			//			controll_auswahl2 = loader.<contr_Auswahl_Frame>getController();


			loader = new FXMLLoader(getClass().getResource("/GUI/Bearbeiten_Window.fxml"));
			AnchorPane pane_layout = (AnchorPane)loader.load();
			control_layout = loader.<contr_Bearbeiten_Window>getController();
			Scene scene = new Scene(pane_layout,1000,760);
			stage_edit = new Stage();
			stage_edit.setScene(scene);
			stage_edit.setTitle("Image Generation");
			stage_edit.getIcons().add(image); 

			loader = new FXMLLoader(getClass().getResource("/GUI/Vorschau_Frame.fxml"));
			pane_vorschau = (AnchorPane)loader.load();
			control_vorschau = loader.<contr_Vorschau_Frame>getController();

			control_layout.init(stage_edit);
			control_layout.setVorschau(pane_vorschau,control_vorschau);

			File file_setting = new File("settings.xml");
			if(file_setting!=null)
				LoadSettings(file_setting, true, false);

			control_vorschau.init(ctr_VoreinstellungenController, this);

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void genTitelName(String name_open) {
		open_file_name = name_open;
		stage.setTitle(programm_name+" - ("+open_file_name+")*");
	}

	@FXML
	void act_button_add(ActionEvent event) {
		try {
			deselectAll();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/FilterFrame.fxml"));
			AnchorPane pane2 = (AnchorPane)loader.load();
			contr_filterFrame control2 = loader.<contr_filterFrame>getController();
			control2.setData(this, controll_auswahl);
			filterFrameController_list.add(control2);
			filterFrame_pane_list.add(pane2);

			control2.setImageCount(ctr_VoreinstellungenController.getObjSize());
			control2.initWindow(true, true);
			layout2.getChildren().add(pane2);

			b_noComponents = false;

			control_vorschau.updatePreview();

		}catch(Exception e) {
		}
	}

	public void deleteFilterFrame(contr_filterFrame obj) {//TODO
		obj.closeWindow();
		int index = filterFrameController_list.indexOf(obj);
		if(index!=-1) {
			filterFrameController_list.remove(index);
			layout2.getChildren().remove(filterFrame_pane_list.get(index));
			filterFrame_pane_list.remove(index);
			removeSplitComponents();
			calcSumme();

			if(index>0) {
				index--;
				filterFrameController_list.get(index).setSelectedAndUpdateGUI(false);
			}
			else if(filterFrameController_list.size()>0) {
				filterFrameController_list.get(0).setSelectedAndUpdateGUI(false);
			}
			else {
				b_noComponents = true;
			}
		}
	}

	private void Save(File file) {
		menuitem_save.setDisable(false);
		xml root = generateXML(true, true, true, true);
		xml.saveXML(root.getElement(), file);
	}

	public void saveSettings(File file) {
		menuitem_save_setting.setDisable(false);
		xml root = generateXML(true, false, true, false);
		xml.saveXML(root.getElement(), file);
	}

	public void saveSettings() {
		File f = new File("settings.xml");
		saveSettings(f);
	}



	public void LoadSettings(File file, boolean load_paths, boolean loadVorschauSettings) {
		Element elem = null;
		try {
			elem = xml.readXML(file);

			Element general = elem.getChild("general");

			try {
				ctr_VoreinstellungenController.setYoloVersion(general.getChild("yolo_verison").getText());
			}catch(Exception e) {}

			if(System.getProperty("os.name").contains("Windows")) {
				ctr_VoreinstellungenController.setObjPath(general.getChild("obj_path").getText().replace("/", "\\"));
				ctr_VoreinstellungenController.setBackgroundPath(general.getChild("bg_path").getText().replace("/", "\\"));
				ctr_VoreinstellungenController.setOutputPath(general.getChild("output_path").getText().replace("/", "\\"));
				//				ctr_VoreinstellungenController.setSimilarPath(general.getChild("similar_path").getText().replace("/", "\\"));
				//				ctr_VoreinstellungenController.setOverlapPath(general.getChild("overlap_path").getText().replace("/", "\\"));
				ctr_VoreinstellungenController.setPixelsPath(general.getChild("output_width").getText().replace("/", "\\"), general.getChild("output_height").getText().replace("/", "\\"));
			}
			else {
				ctr_VoreinstellungenController.setObjPath(general.getChild("obj_path").getText().replace("\\", "/"));
				ctr_VoreinstellungenController.setBackgroundPath(general.getChild("bg_path").getText().replace("\\", "/"));
				ctr_VoreinstellungenController.setOutputPath(general.getChild("output_path").getText().replace("\\", "/"));
				//				ctr_VoreinstellungenController.setSimilarPath(general.getChild("similar_path").getText().replace("\\", "/"));
				//				ctr_VoreinstellungenController.setOverlapPath(general.getChild("overlap_path").getText().replace("\\", "/"));
				ctr_VoreinstellungenController.setPixelsPath(general.getChild("output_width").getText().replace("\\", "/"), general.getChild("output_height").getText().replace("\\", "/"));
			}

			if(loadVorschauSettings) {
				try {
					control_vorschau.setXMLData(general.getChild("preview"));
				}catch(Exception e) {}
			}

			if(load_paths) {
				try {			
					file_save_setting = new File(general.getChild("setting_path").getText());
				}catch(Exception e) {}
				try {	
					file_save = new File(general.getChild("save_path").getText());
				}catch(Exception e) {}
			}

		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	private void Load(File file) {
		control_vorschau.clear();
		LoadSettings(file, false, true);
		Element root = null;

		try {
			//Daten laden
			root = xml.readXML(file);
			List<Element> list = root.getChildren();
			for(int i=0;i<list.size();i++) {
				Element elem = list.get(i);
				if(elem.getName().equals("filter")) {
					//Neuen Filterframe hinzufügen
					deselectAll();
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/FilterFrame.fxml"));
					AnchorPane pane2 = (AnchorPane)loader.load();
					contr_filterFrame control2 = loader.<contr_filterFrame>getController();
					control2.setData(this, controll_auswahl);
					filterFrameController_list.add(control2);
					filterFrame_pane_list.add(pane2);

					control2.setImageCount(ctr_VoreinstellungenController.getObjSize());
					control2.initWindow(false, false);
					layout2.getChildren().add(pane2);

					contr_filterFrame contr = filterFrameController_list.get(filterFrameController_list.size()-1);
					contr.loadDataXML(elem);
				}
			}
			
			deselectAll();
			if(filterFrameController_list.size()>0) {
				filterFrameController_list.get(0).setSelectedAndUpdateGUI(false);
				b_noComponents = false;
			}

			calcSumme();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public contr_filterFrame getSelectedFrame() {
		for(int i=0;i<filterFrameController_list.size();i++) {
			if(filterFrameController_list.get(i).isSelected())
				return filterFrameController_list.get(i);
		}
		return null;
	}

	public static String addSlasch_end(String s) {
		if(s.endsWith("/"))
			return s;
		else
			return (s += "/");
	}

	private xml generateXML(boolean b_voreinstellungen, boolean b_filters, boolean save_settings, boolean vorschau) {
		xml root = new xml("Daten");

		if(b_voreinstellungen) {
			xml voreinstell = new xml("general");
			voreinstell.addData("obj_path", addSlasch_end(ctr_VoreinstellungenController.getObjPath()));
			voreinstell.addData("bg_path", addSlasch_end(ctr_VoreinstellungenController.getBackgroundPath()));
			voreinstell.addData("output_path", addSlasch_end(ctr_VoreinstellungenController.getOutputPath()));
			//			voreinstell.addData("similar_path", addSlasch_end(ctr_VoreinstellungenController.getSimilarPath()));
			//			voreinstell.addData("overlap_path", addSlasch_end(ctr_VoreinstellungenController.getOverlapPath()));
			voreinstell.addData("output_height", ctr_VoreinstellungenController.getHeight_output());		
			voreinstell.addData("output_width", ctr_VoreinstellungenController.getWidth_output());
			if(save_settings) {
				if(file_save_setting != null)
					voreinstell.addData("setting_path", file_save_setting.getAbsolutePath().replace("\\", "/"));
				if(file_save != null)
					voreinstell.addData("save_path", file_save.getAbsolutePath().replace("\\", "/"));
				voreinstell.addData("yolo_verison", ""+getVoreinstellungen().getYoloVersion());
			}
			if(vorschau)
				voreinstell.addNode(control_vorschau.getXMLCode());
			root.addNode(voreinstell);
		}

		if(b_filters) {
			for(int i=0;i<filterFrameController_list.size();i++) {
				root.addNode(filterFrameController_list.get(i).getXMLCode(save_settings));
			}
		}

		return root;
	}

	//Eigene-Funktionen //////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	public void deselectAll() {
		for(int i=0;i<filterFrameController_list.size();i++) {
			filterFrameController_list.get(i).setSelected(false);
		}
	}

	public void calcSumme() {
		int summe = 0;
		for(int i=0;i<filterFrameController_list.size();i++) {
			summe += filterFrameController_list.get(i).getSumme();
		}
		String s = NumberFormat.getInstance().format(summe);
		label_gesamt.setText(""+s);
	}


	public int getSumme() {
		int summe = 0;
		for(int i=0;i<filterFrameController_list.size();i++) {
			summe += filterFrameController_list.get(i).getSumme();
		}
		return summe;
	}





	public void init(Stage stage, contr_hauptmenue control_haupt) {
		this.stage = stage;
		this.control_haupt = control_haupt;

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Stage is closing");
				saveSettings();

				//testen, ob gespeichert werden muss:
				if(filterFrameController_list.size()>0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Save?");
					alert.setHeaderText("Save changes before quitting?");

					ButtonType exit = new ButtonType("Close");
					ButtonType canel = new ButtonType("Abort");
					ButtonType save = new ButtonType("Save");

					// Remove default ButtonTypes
					alert.getButtonTypes().clear();

					alert.getButtonTypes().addAll(exit, canel, save);

					// option != null.
					Optional<ButtonType> option = alert.showAndWait();

					if (option.get() == null) {
						we.consume();
						//	     	            this.label.setText("No selection!");
					} else if (option.get() == canel) {
						we.consume();
					} else if (option.get() == exit) {

					} else if (option.get() == save) {

						boolean saveas = true;
						if(file_save!=null) {
							if(file_save.exists() && menuitem_save.isDisable()==false) {
								Save(file_save);
								saveas = false;
							}
						}
						if(saveas) {
							FileChooser fileChooser = new FileChooser();
							FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
							fileChooser.getExtensionFilters().add(extFilter);
							if(file_save!=null) {
								File tmp = file_save.getParentFile();
								if(tmp.exists() && tmp.isDirectory())
									fileChooser.setInitialDirectory(tmp);
							}
							File file  = fileChooser.showSaveDialog(stage);

							if(file!=null) {
								file_save = file;
								Save(file_save);
								genTitelName(file.getName());
							}
							else {
								we.consume();
							}
						}

					}
				}

				System.exit(0);
			}
		});

		stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				System.out.println("maximized:" + t1.booleanValue());
				if(t1.booleanValue()==false || item_one_window.isSelected()==false) {
					removeSplitComponents();
				}
				else {
					if(pane!=null && b_noComponents == false) {//TODO
						AnchorPane.setLeftAnchor(pane, 0.0);
						AnchorPane.setRightAnchor(pane, 0.0);
						AnchorPane.setBottomAnchor(pane, 0.0);
						AnchorPane.setTopAnchor(pane, 0.0);

						split_vorschau.setOrientation(Orientation.VERTICAL);
						split_vorschau.getItems().add(pane);
						split_vorschau.getItems().add(pane_vorschau);
						split_vorschau.setDividerPosition(1, 1.0);
						split.getItems().add(1,split_vorschau);
						split.getItems().add(2,pane_auswahl);

						split.setDividerPositions(divider_pos);
						split_vorschau.setDividerPositions(divider_pos2);
						stage_edit.close();
					}
				}
			}
		});
	}

	private void removeSplitComponents() {
		double tmp[] = split.getDividerPositions();
		if(tmp.length==2) {
			divider_pos = split.getDividerPositions();
		}
		for(int i=split.getItems().size()-1;i>=1;i--) {
			split.getItems().remove(i);
		}

		double tmp2[] = split_vorschau.getDividerPositions();
		System.out.println("split_vorschau: "+tmp2.length);
		if(tmp2.length==1) {
			divider_pos2 = split_vorschau.getDividerPositions();
		}
		split_vorschau.getItems().clear();
		control_layout.clear();
	}

	public void setPane(contr_filter con_filter, AnchorPane pane, Stage paneStage, contr_filterFrame contr_filterFrame, boolean show) {
		if(pane!=null) {
			this.pane = pane;
			this.paneStage = paneStage;

			//Items entfernen
			removeSplitComponents();
			controll_auswahl.setController(con_filter);

			if(this.stage.isMaximized() == true && item_one_window.isSelected()) {
				AnchorPane.setLeftAnchor(pane, 0.0);
				AnchorPane.setRightAnchor(pane, 0.0);
				AnchorPane.setBottomAnchor(pane, 0.0);
				AnchorPane.setTopAnchor(pane, 0.0);

				split_vorschau.setOrientation(Orientation.VERTICAL);
				split_vorschau.getItems().add(pane);
				split_vorschau.getItems().add(pane_vorschau);
				split_vorschau.setDividerPosition(1, 1.0);
				split.getItems().add(1,split_vorschau);
				split.getItems().add(2,pane_auswahl);

				split.setDividerPositions(divider_pos);
				split_vorschau.setDividerPositions(divider_pos2);
			}
			else {
				control_layout.setRegeln(pane, con_filter);
				control_layout.setVorschau(pane_vorschau, control_vorschau);
				control_layout.setAuswahl(pane_auswahl, controll_auswahl);
				if(show)
					control_layout.show();
			}
			deselectAll();
			contr_filterFrame.setSelected(true);
			con_filter.setPositions();
		}

	}



	private boolean TestAll() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		boolean ok = true;
		System.out.println("GENERATE: "+filterFrameController_list.size());
		if(filterFrameController_list.size()==0) {
			alert.setHeaderText("No filter composition was added");
			alert.showAndWait();
			return false;
		}
		if(ok) {
			for(int i=0;i<filterFrameController_list.size();i++) {
				boolean error = filterFrameController_list.get(i).hasError();
				if(error == true) {
					ok = false;
				}
			}
			if(ok==false) {
				alert.setHeaderText("Error in Filter settings. Path was not found. Please check the red marked filters");
				alert.showAndWait();
				return false;
			}
		}
		if(ok) {
			String s = ctr_VoreinstellungenController.getOutputPath();
			File f = new File(s);
			if(f.exists() == false || f.isDirectory()==false) {
				alert.setHeaderText("No valid output path selected in the default settings");
				alert.showAndWait();
				return false;
			}
		}
		//		if(ok) {
		//			int anz = Integer.parseInt(ctr_VoreinstellungenController.txt_anz_frei.getText());
		//			if(anz == 0) {
		//				alert.setHeaderText("No isolated images found at the given path");
		//				alert.showAndWait();
		//				return false;
		//			}
		//		}
		if(ok) {
			int anz = Integer.parseInt(ctr_VoreinstellungenController.txt_anz_back.getText());
			if(anz == 0) {
				alert.setHeaderText("No background images found at the given path");
				alert.showAndWait();
				return false;
			}
		}
		int i_ok = 0, i_err = -1, i_not = -1;
		try {
			i_ok = Integer.parseInt(ctr_VoreinstellungenController.txt_ok.getText());
		}catch(Exception e) {}
		try {
			i_err = Integer.parseInt(ctr_VoreinstellungenController.txt_err.getText());
		}catch(Exception e) {}
		try {
			i_not = Integer.parseInt(ctr_VoreinstellungenController.txt_not.getText());
		}catch(Exception e) {}

		if(i_ok == 0) {
			alert.setHeaderText("No classified object was found.\r\n" + 
					"Please classify all object images in the default settings");
			alert.showAndWait();
			return false;
		}
		if(i_err != 0) {
			alert.setHeaderText("Invalid object images were found in the object images settings.\r\n" + 
					"Please delete all invalid files");
			alert.showAndWait();
			return false;
		}
		if(i_not != 0) {
			alert.setHeaderText("Not all object images were classified.\r\n" + 
					"Please classify all object images in the default settings");
			alert.showAndWait();
			return false;
		}


		return ok;
	}

	@FXML
	void act_button_generate(ActionEvent event) {

		//Testabfragen


		boolean ok = TestAll();


		if(ok) {
			xml root = generateXML(true, true, false, false);
			xml.saveXML(root.getElement(), new File("data.xml"));

			controll_progress.init(stage_progress, control_haupt);
			stage_progress.show();


		}
		//				Load(new File("data.xml"));
	}

	@FXML
	void menu_new(ActionEvent event) {
		for(int i=filterFrameController_list.size()-1; i>=0;i--)
			deleteFilterFrame(filterFrameController_list.get(i));
		genTitelName("new");
		control_vorschau.clear();
	}

	@FXML
	void menu_open(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		if(file_save!=null) {
			File tmp = file_save.getParentFile();
			if(tmp.exists() && tmp.isDirectory())
				fileChooser.setInitialDirectory(tmp);
		}

		File file = fileChooser.showOpenDialog(stage);
		if(file!=null) {
			file_save = file;
			if(file_save!=null) {
				if(file_save.exists()) {
					menu_new(event);
					Load(file_save);
					menuitem_save.setDisable(false);
					genTitelName(file_save.getName());
				}
			}
		}

	}

	@FXML
	void menu_open_setting(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		if(file_save_setting!=null) {
			File tmp = file_save_setting.getParentFile();
			if(tmp.exists() && tmp.isDirectory()) {
				fileChooser.setInitialDirectory(tmp);
			}
		}

		File file = fileChooser.showOpenDialog(stage);
		if(file!=null) {
			file_save_setting = file;
			if(file_save_setting!=null) {
				if(file_save_setting.exists()) {
					LoadSettings(file_save_setting, false, false);
					calcSumme();
					menuitem_save_setting.setDisable(false);
				}
			}
		}
	}

	@FXML
	void menu_save(ActionEvent event) {
		if(file_save==null)
			menu_save_as(event);
		else if(file_save.exists() == false)
			menu_save_as(event);
		else
			Save(file_save);
	}

	@FXML
	void menu_save_as(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		if(file_save!=null) {
			File tmp = file_save.getParentFile();
			if(tmp.exists() && tmp.isDirectory())
				fileChooser.setInitialDirectory(tmp);
		}
		File file  = fileChooser.showSaveDialog(stage);

		if(file!=null) {
			file_save = file;
			Save(file_save);
			genTitelName(file.getName());
		}
	}

	@FXML
	void menu_save_settings(ActionEvent event) {
		if(file_save_setting==null)
			menu_save_settings_as(event);
		else if(file_save_setting.exists() == false)
			menu_save_settings_as(event);
		else
			saveSettings(file_save_setting);
	}

	@FXML
	void menu_save_settings_as(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		if(file_save_setting!=null) {
			File tmp = file_save_setting.getParentFile();
			if(tmp.exists() && tmp.isDirectory())
				fileChooser.setInitialDirectory(tmp);
		}
		File file  = fileChooser.showSaveDialog(stage);

		if(file!=null) {
			file_save_setting = file;
			saveSettings(file_save_setting);
		}
	}

	@FXML
	void menu_close(ActionEvent event) {
		stage.close();
	}

	@FXML
	void menu_info(ActionEvent event) {

	}

	@FXML
	void act_item_one_window(ActionEvent event) {
		if(item_one_window.isSelected()) {
			if(pane!=null && stage.isMaximized() && b_noComponents == false) {
				paneStage.close();

				AnchorPane.setLeftAnchor(pane, 0.0);
				AnchorPane.setRightAnchor(pane, 0.0);
				AnchorPane.setBottomAnchor(pane, 0.0);
				AnchorPane.setTopAnchor(pane, 0.0);

				split_vorschau.setOrientation(Orientation.VERTICAL);
				split_vorschau.getItems().add(pane);
				split_vorschau.getItems().add(pane_vorschau);
				split_vorschau.setDividerPosition(1, 1.0);
				split.getItems().add(1,split_vorschau);
				split.getItems().add(2,pane_auswahl);

				split.setDividerPositions(divider_pos);
				split_vorschau.setDividerPositions(divider_pos2);
			}
		}
		else {
			removeSplitComponents();
		}
	}

	public contr_Voreinstellungen getVoreinstellungen() {
		return ctr_VoreinstellungenController;
	}

	public void duplicate(contr_filterFrame controll_filterFrame) {
		xml data = controll_filterFrame.getXMLCode(false);
		try {
			deselectAll();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/FilterFrame.fxml"));
			AnchorPane pane2 = (AnchorPane)loader.load();
			contr_filterFrame control2 = loader.<contr_filterFrame>getController();
			control2.setData(this, controll_auswahl);
			filterFrameController_list.add(control2);
			filterFrame_pane_list.add(pane2);

			control2.setImageCount(ctr_VoreinstellungenController.getObjSize());
			control2.initWindow(false, false);
			layout2.getChildren().add(pane2);

			contr_filterFrame contr = filterFrameController_list.get(filterFrameController_list.size()-1);
			contr.loadDataXML(data.getElement());

			control_vorschau.updatePreview();

		}catch(Exception e) {
		}
	}
}
