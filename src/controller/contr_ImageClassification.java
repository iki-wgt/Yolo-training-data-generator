package controller;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.ArrayList;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import Data.data_class;
import application.xml;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import komponenten.ImageButton;

public class contr_ImageClassification {
	@FXML private ScrollPane scroll1;
	@FXML private FlowPane layout1;
	@FXML private ScrollPane scroll2;
	@FXML private Button button_add;
	@FXML private Button button_remove;
	@FXML private Button button_edit;
	@FXML private ComboBox<data_class> combo;
	@FXML private Button button_save;
	@FXML private Button button_add_class;
	@FXML private Button button_remove_class;
	@FXML private Button button_abort;
	@FXML private Button button_up;
	@FXML private Button button_down;
	@FXML private Button imp_class_names_btn;
	@FXML private FlowPane layout2;
	@FXML private TextField txt_name;
	@FXML private TableView<data_class> table;
	@FXML private ProgressBar progress;
	@FXML private VBox layout_class;
	@FXML private TitledPane pane1;
	@FXML private TitledPane pane2;
	@FXML private GridPane grid;
	@FXML private StackPane stack1;
	@FXML private StackPane stack2;
	@FXML private AnchorPane selection1;
	@FXML private AnchorPane selection2;
	@FXML private VBox vbox1;
	@FXML private CheckBox check;
	@FXML private Label label_name;

	Label txt_ok = null;
	Label txt_not = null;
	Label txt_err = null;
	Label txt_anz_frei = null;

	ProgressBar progress_image = null;
	boolean status = false;

	Thread thread = null;

	Stage stage = null;
	String path = "";
	int error_cout = 0;

	Point2D p_start = null;

	double mousex = 0, mousey = 0;
	boolean control = false;
	boolean b_thread_flag = false;
	Thread scrollThread = new Thread();

	List<Element> classxml = null;

	ObservableList<data_class> dataset = FXCollections.observableArrayList();
	ObservableList<data_class> dataset2 = FXCollections.observableArrayList();
	SpinnerValueFactory<Integer> data_spinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);

	Spinner<Integer> spinner = new Spinner<>();

	contr_Voreinstellungen ctr_voreinstellungen = null;


	//	private boolean existClassIndex(int value) {
	//		for(int i=0;i<dataset.size();i++) {
	//			if(dataset.get(i).getIndex() == value)
	//				return true;
	//		}
	//		return false;
	//	}


	//	private int getNextClassIndex(boolean up, int value) {
	//		if(up==true) {
	//			if(existClassIndex(value))
	//				return getNextClassIndex(up,value+1);
	//			else
	//				return value;
	//		}
	//		else if(up == false && value >=0) {
	//			if(existClassIndex(value))
	//				return getNextClassIndex(up,value-1);
	//			else
	//				return value;
	//		}
	//		else {
	//			up = true;
	//			return getNextClassIndex(up,value+1);
	//		}
	//	}

	public boolean isSelected(FlowPane layout) {
		//		if(layout == layout1) {
		ObservableList<Node> list = layout.getChildren();
		for(int i=0;i<list.size();i++) {
			if(((ImageButton)list.get(i)).isSelected())
				return true;
		}
		return false;
		//		}
		//		else {
		//			
		//		}
		//		return false;
	}

	private void checkButtonDisable() {
		if(isSelected(layout1) && combo.getValue()!=null) {
			button_add_class.setDisable(false);
		}
		else {
			button_add_class.setDisable(true);
		}

		if(isSelected(layout2)) {
			button_remove_class.setDisable(false);
		}
		else {
			button_remove_class.setDisable(true);
		}
	}

	@FXML
	void initialize() {
		progress.setVisible(false);

		table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("Index"));
		table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("Name"));
		table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("Size"));

		table.setItems(dataset);
		combo.setItems(dataset2);

		//		combo.valueProperty().addListener((obs, oldVal, newVal) -> {
		//		});

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			ObservableList<Node> list =  layout2.getChildren();
			for(int i=0;i<layout2.getChildren().size();i++) {
				ImageButton button = (ImageButton)list.get(i);
				button.setSelected(false);
			}
			showOnlyTableButtons(newVal);
		});

		layout_class.getChildren().add(spinner);

		spinner.setValueFactory(data_spinner);
		int width = 60;
		spinner.setMaxWidth(width);
		spinner.setPrefWidth(width);
		spinner.setMinWidth(width);
		spinner.setPrefHeight(30);
		spinner.setEditable(false);
		spinner.valueProperty().addListener((obs, oldVal, newVal) -> {

			//			if(existClassIndex(newVal)==false) {
			if(newVal > dataset.size())
				data_spinner.setValue(dataset.size());
			//			}

			//			
			//			
			////			if(existClassIndex(newVal)) {
			//				int dif = newVal - oldVal;
			//				System.out.println("dif: "+dif+ " new: "+newVal+" old: "+oldVal);
			//				boolean up = false;
			//				if(dif>=0)
			//					up = true;
			//				else
			//					up = false;
			//
			//				int next = validateIndex(up, newVal);
			//				System.out.println("next: "+next);
			//				data_spinner.setValue(next);
			//			}
		});


		combo.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(newVal == null)
				button_add_class.setDisable(true);
			else {
				if(isSelected(layout1)==false)
					button_add_class.setDisable(true);
				else
					button_add_class.setDisable(false);
			}
		});

		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				button_edit.setDisable(false);
				button_remove.setDisable(false);
			}
			else {
				button_edit.setDisable(true);
				button_remove.setDisable(true);
			}
		});


		NumberFormat format = NumberFormat.getIntegerInstance();
		UnaryOperator<TextFormatter.Change> filter = c -> {
			if (c.isContentChange()) {
				ParsePosition parsePosition = new ParsePosition(0);
				format.parse(c.getControlNewText(), parsePosition);
				if (parsePosition.getIndex() == 0 ||
						parsePosition.getIndex() < c.getControlNewText().length()) {
					return null;
				}
			}
			return c;
		};
		TextFormatter<Integer> priceFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, filter);
		spinner.getEditor().setTextFormatter(priceFormatter);
	}

	//	private data_class getKlass(int classindex) {
	//		
	//	}

	@SuppressWarnings("deprecation")
	private void loadImages() {
		File file = new File(path);
		File[] files = file.listFiles();

		if(thread != null){
			while(thread.isAlive()) {
				thread.interrupt();
				thread.stop();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		thread = new Thread() {
			int count_pic = 0;

			@Override
			public void run() {
				error_cout = 0;
				for(int i=0;i<files.length;i++) {
					ImageButton imgb = null;
					if(files[i].getName().toLowerCase().endsWith(".png") || files[i].getName().toLowerCase().endsWith(".jpg") || files[i].getName().toLowerCase().endsWith(".bmp")) {
						boolean found = false;
						String filename = "";
						count_pic++;
						if(classxml != null) {
							for(int x=0;x<classxml.size();x++) {
								filename = classxml.get(x).getValue();
								if(filename.equals(files[i].getName())) {
									int index = Integer.parseInt(classxml.get(x).getAttribute("class").getValue());
									dataset.get(index).incrementSize();
									imgb = new ImageButton(files[i],dataset.get(index));
									imgb.managedProperty().bind(imgb.visibleProperty());
									addImage(imgb,i,files.length);
									found = true;
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											table.refresh();
										}
									});
									break;
								}
							}
						}

						if(found == false) {
							System.out.println("Image from class.xml not found: "+filename);
							imgb = new ImageButton(files[i]);
							imgb.managedProperty().bind(imgb.visibleProperty());
							addImage(imgb,i,files.length);
						}
						
//						System.out.println("imageButton to byte: "+imgb.getByte().length);
					}
					else if(files[i].getName().equals("class.xml") == false) {
						error_cout++;
					}
					
					int count = i;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							progress_image.setVisible(true);
							progress_image.setProgress(count / (double)files.length);
						}

					});
					
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						txt_ok.setText(""+getSizeClassified());
						txt_not.setText(""+getSizeNotClassified());
						txt_err.setText(""+getSizeInfalidFiles());
						table.refresh();
						progress.setVisible(false);
						progress_image.setVisible(false);
						status = true;
						button_save.setDisable(false);
						txt_anz_frei.setText(""+count_pic);

						if(check.isSelected()) {
							showOnlyTableButtons(table.getSelectionModel().getSelectedItem());
						}

						ctr_voreinstellungen.setImageCount();
					}
				});
			};
		};


		thread.start();
	}

	public void updateClassIndex() {
		for(int i=0;i<dataset.size();i++) {
			dataset.get(i).setIndex(i);
		}

		ObservableList<Node> list =  layout2.getChildren();
		for(int i=list.size()-1;i>=0;i--) {
			ImageButton button = (ImageButton)list.get(i);
			button.refreshLabelData();
		}
	}

	public void addImage(ImageButton button, int index, int length) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if(button.getData()==null)
					layout1.getChildren().add(button);
				else {
					layout2.getChildren().add(button);
				}
				progress.setProgress(((double)index+1)/(double)length);
			}
		});
	}

	private void showOnlyTableButtons(data_class data) {
		if(data != null) {
			if(check.isSelected() == true) {
				ObservableList<Node> list =  layout2.getChildren();
				for(int i=0;i<layout2.getChildren().size();i++) {
					ImageButton button = (ImageButton)list.get(i);
					if(button.getData() == data) {
						button.setVisible(true);
					}
					else {
						button.setSelected(false);
						button.setVisible(false);
					}
				}
			}
		}
	}

	@FXML
	void act_check(ActionEvent event) {
		if(check.isSelected() == true) {
			ObservableList<Node> list =  layout2.getChildren();
			for(int i=0;i<layout2.getChildren().size();i++) {
				ImageButton button = (ImageButton)list.get(i);
				button.setSelected(false);
			}
			data_class data = table.getSelectionModel().getSelectedItem();
			showOnlyTableButtons(data);
		}
		else {
			ObservableList<Node> list =  layout2.getChildren();
			for(int i=0;i<layout2.getChildren().size();i++) {
				ImageButton button = (ImageButton)list.get(i);
				button.setVisible(true);
			}
		}
	}


	@FXML
	void act_button_up(ActionEvent event) {
		int index = table.getSelectionModel().getSelectedIndex();
		if(index != -1 && index != 0) {
			data_class tmp = dataset.get(index);
			dataset.remove(index);
			dataset.add(index-1, tmp);
			dataset2.remove(index);
			dataset2.add(index-1, tmp);
			updateClassIndex();
			table.getSelectionModel().select(index-1);
		}
	}


	@FXML
	void act_button_down(ActionEvent event) {
		int index = table.getSelectionModel().getSelectedIndex();
		if(index != -1 && index < dataset.size()-1) {
			data_class tmp = dataset.get(index);
			dataset.remove(index);
			dataset.add(index+1, tmp);
			dataset2.remove(index);
			dataset2.add(index+1, tmp);
			updateClassIndex();
			table.getSelectionModel().select(index+1);
		}
	}
	
	@FXML
	void import_class_names(ActionEvent event) {
		// Choose file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select class name text file");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Types", "*.txt", "*.names"),
				new FileChooser.ExtensionFilter("TXT", "*.txt"),
				new FileChooser.ExtensionFilter("NAMES", "*.names")
				);
		File file = fileChooser.showOpenDialog(null);
		System.out.println("Chosen file: " + file.getName());
		
		// create list for all imported classes
		List<String> class_list = new ArrayList<String>();
		
		// read file content and add each line to class_list
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				class_list.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		// add contents of class list to GUI
		for(int i=0; i<class_list.size(); i++) {
			String name = class_list.get(i);
			if(name.equals("") == false) {
				data_class data = new data_class(i, name, 0);
				dataset.add(i,data);
				dataset2.add(i,data);
				txt_name.setText("");
				data_spinner.setValue(dataset.size());
				updateClassIndex();
			}
		}
	}

	@FXML
	void act_combo(ActionEvent event) {

	}


	@FXML
	void act_button_add_class(ActionEvent event) {
		ObservableList<Node> list2 =  layout2.getChildren();
		boolean add = false;
		data_class data = combo.getValue();

		ObservableList<Node> list =  layout1.getChildren();
		for(int i=list.size()-1;i>=0;i--) {
			ImageButton button = (ImageButton)list.get(i);
			if(button.isSelected()) {

				if(add==false) {
					for(int x=0; x<list2.size(); x++) {
						((ImageButton)list2.get(x)).setSelected(false);
					}
					add = true;
				}
				button.setData(data);
				data.incrementSize();
				table.refresh();
				layout2.getChildren().add(0, button);
				showOnlyTableButtons(table.getSelectionModel().getSelectedItem());
				list.remove(button);
			}
		}
		button_add_class.setDisable(true);
		button_remove_class.setDisable(false);
	}

	@FXML
	void act_button_remove_class(ActionEvent event) {
		ObservableList<Node> list1 =  layout1.getChildren();
		boolean add = false;

		ObservableList<Node> list =  layout2.getChildren();
		for(int i=list.size()-1;i>=0;i--) {
			ImageButton button = (ImageButton)list.get(i);
			if(button.isSelected() && button.isVisible()) {

				if(add==false) {
					for(int x=0; x<list1.size(); x++) {
						((ImageButton)list1.get(x)).setSelected(false);
					}
					add = true;
				}
				data_class data = button.getData();
				data.decrementSize();
				table.refresh();
				button.setData(null);
				layout1.getChildren().add(0, button);
				list.remove(button);
			}
		}

		button_remove_class.setDisable(true);
		button_add_class.setDisable(false);
	}

	@FXML
	void act_button_edit(ActionEvent event) {
		try {
			data_class data = table.getSelectionModel().getSelectedItem();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ImageClassification_edit.fxml"));
			AnchorPane pane_edit = (AnchorPane)loader.load();
			contr_ImageClassification_edit controll_edit = loader.<contr_ImageClassification_edit>getController();
			Scene scene_progress = new Scene(pane_edit);
			Stage stage_edit = new Stage();
			stage_edit.setScene(scene_progress);
			stage_edit.setTitle("Edit existing Classification");
			stage_edit.initStyle(StageStyle.UNIFIED);
			stage_edit.setResizable(false);
			stage_edit.initModality(Modality.APPLICATION_MODAL);
			String url = "/background.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			stage_edit.getIcons().add(image); 

			controll_edit.init(stage_edit, data, dataset, table, combo, this);
			stage_edit.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void act_button_remove(ActionEvent event) {
		data_class data = table.getSelectionModel().getSelectedItem();
		ObservableList<Node> list2 =  layout2.getChildren();
		ObservableList<Node> list =  layout2.getChildren();
		for(int i=list.size()-1;i>=0;i--) {
			ImageButton button = (ImageButton)list.get(i);
			button.setSelected(false);
		}
		for(int i=list2.size()-1;i>=0;i--) {
			ImageButton button = (ImageButton)list2.get(i);
			button.setSelected(false);
			if(button.getData().getIndex() == data.getIndex()) {
				list2.remove(i);
				button.setData(null);
				button.setSelected(true);
				layout1.getChildren().add(button);
			}
		}
		data_spinner.setValue(data.getIndex());
		dataset.remove(data);
		dataset2.remove(data);
		updateClassIndex();
		//		for(int i=0;i<dataset2.size();i++) {
		//			if(dataset2.get(i).getIndex() == data.getIndex()) {
		//				dataset2.remove(i);
		//				break;
		//			}
		//		}
	}

	@FXML
	void act_button_add(ActionEvent event) {
		int index = spinner.getValue();
		String name = txt_name.getText();
		boolean err = true;
		if(name != null) {
			if(name.equals("") == false) {
				data_class data = new data_class(index, name, 0);
				dataset.add(index,data);
				dataset2.add(index,data);
				txt_name.setText("");
				data_spinner.setValue(dataset.size());
				updateClassIndex();
				err = false;
			}
		}
		
		if(err == true)
			label_name.setTextFill(Color.RED);
		else
			label_name.setTextFill(null);
	}

	@FXML
	void act_button_save(ActionEvent event) {
		saveXML();
		txt_ok.setText(""+getSizeClassified());
		txt_not.setText(""+getSizeNotClassified());
		txt_err.setText(""+getSizeInfalidFiles());
		ctr_voreinstellungen.setImageCount();
		stage.close();
	}

	@FXML
	void act_button_abort(ActionEvent event) {
		stage.close();
	}

	private void initMouseEvents(AnchorPane selection, FlowPane layout, ScrollPane scroll, StackPane stack) {

		scroll.heightProperty().addListener((obs, oldVal, newVal) -> {
			stack.setPrefHeight(((double)newVal)-5);
			selection.setPrefHeight(((double)newVal)-5);
		});

		scroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
			stack.setPrefWidth(newVal.getWidth());
			selection.setMinWidth(newVal.getWidth());
			selection.setMinHeight(newVal.getHeight()-4);
			b_thread_flag = true;
		});

		selection.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				ObservableList<Node> list =  layout.getChildren();
				Point2D point = new Point2D(event.getX(), event.getY());
				for(int i=0;i<list.size();i++) {
					if(event.isControlDown()==false) {
						((ImageButton)list.get(i)).enableDrag(true);
						((ImageButton)list.get(i)).setSelected(false);
					}
					if(list.get(i).getBoundsInParent().contains(point)) {
						((ImageButton)list.get(i)).toggleSelection();
					}
				}
				checkButtonDisable();
			}
		});


		selection.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				p_start = new Point2D(event.getX(), event.getY());
				ObservableList<Node> list =  layout.getChildren();
				for(int i=0;i<list.size();i++) {
					((ImageButton)list.get(i)).enableDrag(true);
					if(event.isControlDown()==false) {
						((ImageButton)list.get(i)).setSelected(false);
					}
				}
				checkButtonDisable();
			}
		});


		selection.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(scrollThread.isAlive()) {
					scrollThread.interrupt();

					Bounds paneBounds = selection.localToScene(selection.getBoundsInParent());
					Bounds paneBounds2 = scroll.localToScene(scroll.getBoundsInParent());
					double miny_scroll = paneBounds2.getMinY()+2;
					double scrollHight = scroll.getHeight();
					double miny = paneBounds.getMinY() - miny_scroll;
					mousey = scrollHight - miny;

					selectAllInField(selection, layout);
				}
				selection.getChildren().clear();
				ObservableList<Node> list =  layout.getChildren();
				for(int i=0;i<list.size();i++) {
					((ImageButton)list.get(i)).enableDrag(false);
				}
				checkButtonDisable();
			}
		});


		scroll.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//				ObservableList<Node> list =  layout.getChildren();
				//				for(int i=0;i<list.size();i++) {
				//					((ImageButton)list.get(i)).hover(false);
				//				}
				if(event.isPrimaryButtonDown()) {
					b_scroll_exit = true;
				}
			}
		});


		selection.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//hover effekt:
				//				ObservableList<Node> list =  layout.getChildren();
				//				Point2D point = new Point2D(event.getX(), event.getY());
				//				for(int i=0;i<list.size();i++) {
				//					((ImageButton)list.get(i)).hover(false);
				//					if(list.get(i).getBoundsInParent().contains(point)) {
				//						((ImageButton)list.get(i)).hover(true);
				//					}
				//				}
			}
		});


		scroll.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				b_scroll_exit = false;
				//				if(scrollThread.isAlive())
				//					scrollThread.interrupt();
			}
		});


		selection.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				mousex = event.getX();
				mousey = event.getY();
				control = event.isControlDown();
				selectAllInField(selection, layout);

				Bounds paneBounds = selection.localToScene(selection.getBoundsInParent());
				Bounds paneBounds2 = scroll.localToScene(scroll.getBoundsInParent());
				double miny_scroll = paneBounds2.getMinY()+2;
				double scrollHight = scroll.getHeight();
				double miny = paneBounds.getMinY() - miny_scroll;
				double value = scrollHight - miny;

				//Abbruchkriterium
				if(b_scroll_exit == true && scrollThread.isAlive()) {	
					if(((value - event.getY()) > 2) && ((value - event.getY()) < scroll.getHeight()-5)) {
						scrollThread.interrupt();
					}
					checkButtonDisable();
				}

				//Startkriterium
				if((selection.getHeight()+4) > scroll.getHeight()) {
					if(b_scroll_exit == true && scrollThread.isAlive()==false) {
						if((value - event.getY()) < 0) {		//scroll down
							scroll(layout, scroll, selection, true);
						}
						else if((value - event.getY()) > scroll.getHeight()){	//scroll up
							scroll(layout, scroll, selection, false);
						}
					}
				}
			}
		});
	}

	private void selectAllInField(AnchorPane selection, FlowPane layout) {
		double x1 = p_start.getX();
		double x2 = mousex;
		double y1 = p_start.getY();
		double y2 = mousey;
		if(x1 > x2) {
			double tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if(y1 > y2) {
			double tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		BoundingBox bound = new BoundingBox(x1, y1, x2-x1, y2-y1);
		Rectangle  rectangle_back = new Rectangle (0, 0, selection.getWidth(), selection.getHeight());
		Rectangle  rectangle = new Rectangle (x1, y1, x2-x1, y2-y1);
		rectangle.setFill(Color.web("#0078D799"));
		rectangle_back.setFill(Color.TRANSPARENT);
		selection.getChildren().clear();
		selection.getChildren().add(rectangle_back);
		selection.getChildren().add(rectangle);
		//				selection1.setStyle("-fx-background-color: #66772255");
		ObservableList<Node> list =  layout.getChildren();
		for(int i=0;i<list.size();i++) {
			if(list.get(i) instanceof ImageButton) {
				if(control) {
					if(list.get(i).getBoundsInParent().intersects(bound)) {
						((ImageButton)list.get(i)).toggleSelection(true);
					}
					else {
						((ImageButton)list.get(i)).toggleSelection(false);
					}
				}
				else {
					if(list.get(i).getBoundsInParent().intersects(bound)) {
						((ImageButton)list.get(i)).setSelected(true);
					}
					else {
						((ImageButton)list.get(i)).setSelected(false);
					}
				}
			}
		}
	}
	
	int count_ok = 0;
	int count_err = 0;
	int count_not = 0;
	
	@SuppressWarnings("deprecation")
	public void loadOnlyData(contr_Voreinstellungen ctr_voreinstellungen, String objPath) {
		status = false;
		clearAll();
		this.ctr_voreinstellungen = ctr_voreinstellungen;
		this.path = objPath;
		count_ok = 0;
		count_err = 0;
		count_not = 0;
		
		txt_anz_frei.setText("...");
		txt_err.setText("...");
		txt_ok.setText("...");
		txt_not.setText("...");
		File file = new File(objPath);
		File[] files = file.listFiles();
		progress_image.setProgress(0);
		progress_image.setVisible(true);
		loadXML();
		
		if(thread != null){
			while(thread.isAlive()) {
				thread.interrupt();
				thread.stop();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		thread = new Thread() {
			int count_pic = 0;

			@Override
			public void run() {
				error_cout = 0;
				for(int i=0;i<files.length;i++) {
					if(files[i].getName().toLowerCase().endsWith(".png") || files[i].getName().toLowerCase().endsWith(".jpg") || files[i].getName().toLowerCase().endsWith(".bmp")) {
						boolean found = false;
						String filename = "";
						count_pic++;
						if(classxml != null) {
							for(int x=0;x<classxml.size();x++) {
								filename = classxml.get(x).getValue();
								if(filename.equals(files[i].getName())) {
									found = true;
									count_ok++;
								}
							}
						}

						if(found == false) {
							count_not ++;
						}
						
					}
					else if(files[i].getName().equals("class.xml") == false) {
						error_cout++;
						count_err++;
					}
					
					int count = i;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							progress_image.setVisible(true);
							progress_image.setProgress(count / (double)files.length);
						}
					});
					
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						txt_ok.setText(""+count_ok);
						txt_not.setText(""+count_not);
						txt_err.setText(""+count_err);
						table.refresh();
						progress.setVisible(false);
						progress_image.setVisible(false);
						status = true;
						button_save.setDisable(false);
						txt_anz_frei.setText(""+count_pic);

						ctr_voreinstellungen.setImageCount();
					}
				});
			};
		};

		thread.start();
		
	}

	@SuppressWarnings("deprecation")
	public void init(Stage stage_classify, contr_Voreinstellungen ctr_voreinstellungen, String objPath) {
		clearAll();
		status = false;
		button_save.setDisable(true);
		txt_anz_frei.setText("...");
		txt_err.setText("...");
		txt_ok.setText("...");
		txt_not.setText("...");

		if(thread != null) {
			if(thread.isAlive())
			{
				try {
					while(thread.isAlive()) {
						thread.interrupt();
						thread.stop();
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		progress.setProgress(0);
		progress.setVisible(true);
		progress_image.setProgress(0);
		progress_image.setVisible(true);
		this.stage = stage_classify;
		this.ctr_voreinstellungen = ctr_voreinstellungen;
		this.path = ctr_voreinstellungen.getObjPath();
		loadXML();
		loadImages();
		data_spinner.setValue(dataset.size());


		stage_classify.maximizedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(10);
							stack1.setPrefWidth(scroll1.getWidth()-3);
							stack1.setPrefHeight(scroll1.getHeight()-3);
						} catch (InterruptedException e) {
						}
					}
				});
			}
		});


		pane1.expandedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ObservableList<RowConstraints> list = grid.getRowConstraints();
				if(newValue==false) {
					list.get(0).setMaxHeight(25);
					list.get(0).setMinHeight(25);
					list.get(0).setPrefHeight(25);
				}
				else {
					list.get(0).setMaxHeight(Integer.MAX_VALUE);
					list.get(0).setMinHeight(25);
					list.get(0).setPrefHeight(25);

					Thread thread = new Thread() {
						@Override
						public void run() {
							try {
								do {
									Thread.sleep(10);
									System.out.println("flag noch false!!!!");
								}while(b_thread_flag==false);
								scroll2.setPrefHeight(scroll1.getPrefHeight());
							} catch (InterruptedException e) {
							}
						};
					};
					thread.start();
				}
			}
		});

		pane2.expandedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				ObservableList<RowConstraints> list = grid.getRowConstraints();
				b_thread_flag = false;
				if(newValue==false) {
					list.get(2).setMaxHeight(25);
					list.get(2).setMinHeight(25);
					list.get(2).setPrefHeight(25);
				}
				else {
					list.get(2).setMaxHeight(Integer.MAX_VALUE);
					list.get(2).setPrefHeight(25);
					list.get(2).setMinHeight(25);

					Thread thread = new Thread() {
						@Override
						public void run() {
							try {
								do {
									Thread.sleep(10);
									System.out.println("flag noch false!!!!");
								}while(b_thread_flag==false);
								scroll1.setPrefHeight(scroll1.getPrefHeight());
							} catch (InterruptedException e) {
							}
						};
					};
					thread.start();
				}

			}
		});


		initMouseEvents(selection1, layout1, scroll1, stack1);
		initMouseEvents(selection2, layout2, scroll2, stack2);

	}


	boolean b_scroll_down = true;
	boolean b_scroll_exit = false;

	private void scroll(FlowPane layout, ScrollPane scroll, AnchorPane selection, boolean down) {
		this.b_scroll_down = down;
		double speed = 20;
		double h = layout.getBoundsInLocal().getHeight() - scroll.getBoundsInLocal().getHeight();
		double step = (1 / h) * speed;

		if(scrollThread.isAlive() == true) {
			//			scrollThread.interrupt();
		}
		else {
			scrollThread = new Thread() {
				@Override
				public void run() {
					super.run();
					try {

						while(true) {
							Thread.sleep(35);

							double value = scroll.getVvalue();
							if(down) {
								if((value+step)>1) {
									scroll.setVvalue(1.0);
									throw new InterruptedException("Ende erreicht");
								}
								else {
									scroll.setVvalue(value+step);
								}
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										mousey = selection.getBoundsInParent().getMaxY();
										selectAllInField(selection, layout);
									}
								});
							}
							else {
								if((value-step)<=0) {
									scroll.setVvalue(0);
									throw new InterruptedException("Ende erreicht");
								}
								else {
									scroll.setVvalue(value-step);
								}
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										mousey = selection.getBoundsInParent().getMinY();
										selectAllInField(selection, layout);
									}
								});
							}

						}

					}catch(InterruptedException e) {
						System.out.println("Thread interrupted");
					}
				}
			};
		}
		scrollThread.start();
	}

	public void clearAll() {
		dataset.clear();
		dataset2.clear();
		layout1.getChildren().clear();
		layout2.getChildren().clear();
	}

	public void updateDataChange() {
		ObservableList<Node> list = layout2.getChildren();
		for(int i=0;i<list.size();i++) {
			((ImageButton)list.get(i)).refresh();
		}
		//		table.refresh();
		//		data_class tmp = combo.getValue();
		////		combo.setValue(null);
		////		combo.setItems(dataset);
		//		combo.setValue(tmp);
	}


	public int getSizeClassified() {
		return layout2.getChildren().size();
	}

	public int getSizeNotClassified() {
		return layout1.getChildren().size();
	}

	public int getSizeInfalidFiles() {
		return error_cout;
	}




	public void saveXML() {
		xml root = new xml("root");

		//Output-Path ---------------------------------
		String tmp = path;
		if(tmp.endsWith("/")==false)
			tmp +="/class.xml";
		else
			tmp += "class.xml";
		File file_out = new File(tmp);

		//Add Classes ----------------------------------
		for(int i=0;i<dataset.size();i++) {
			Element elem = new Element("class");
			elem.setAttribute("class", ""+dataset.get(i).getIndex());
			elem.setAttribute("name", dataset.get(i).getName());
			root.addNode(elem);
		}

		//Add File-Data --------------------------------
		ObservableList<Node> list = layout2.getChildren();
		for(int i=0;i<list.size();i++) {
			Element elem = new Element("picture");
			ImageButton button = (ImageButton) list.get(i);
			File file = button.getFile();
			data_class data = button.getData();

			elem.setText(file.getName());
			elem.setAttribute("class", ""+data.getIndex());
			elem.setAttribute("name",data.getName());
			root.addNode(elem);
		}

		xml.saveXML(root.getElement(), file_out);
	}


	public void loadXML() {
		//Input-Path ---------------------------------
		String tmp = path;
		classxml = null;
		if(tmp.endsWith("/")==false)
			tmp +="/class.xml";
		else
			tmp += "class.xml";

		try {
			Element root = xml.readXML(new File(tmp));
			List<Element> classes = root.getChildren("class");

			for(int i=0;i<classes.size();i++) {
				int index = Integer.parseInt(classes.get(i).getAttribute("class").getValue());
				String name = classes.get(i).getAttribute("name").getValue();
				data_class d = new data_class(index, name, 0);
				dataset.add(d);
				dataset2.add(d);
			}

			classxml = root.getChildren("picture");


		} catch (JDOMException | IOException e) {
			//			e.printStackTrace();
			classxml = null;
		}
	}

	public boolean isReaddy() {
		return status;
	}

	public void initLabels(Label txt_ok, Label txt_not, Label txt_err, Label txt_anz_frei, ProgressBar progress_image) {
		this.txt_ok = txt_ok;
		this.txt_not = txt_not;
		this.txt_err = txt_err;
		this.txt_anz_frei = txt_anz_frei;
		this.progress_image = progress_image;
	}

}
