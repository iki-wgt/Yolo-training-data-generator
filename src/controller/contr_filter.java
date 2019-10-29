package controller;

import java.util.ArrayList;

import application.xml;
import controller.Filtereinstellungen.Bildverarbeitung;
import controller.Filtereinstellungen.contr_object;
import controller.Filtereinstellungen.contr_overlap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import komponenten.FilterButton;

public class contr_filter {
	@FXML private ScrollPane scroll_zusam;
	@FXML private ScrollPane scroll_einstellungen;
	@FXML private ScrollPane scroll_filter;
	@FXML private Button button_bew_unsch;
	@FXML private AnchorPane pane_einstellungen;
	@FXML private HBox layout_zusammenstellung;
	@FXML private HBox layout_filter;
	@FXML private VBox layout_buttons;
	@FXML private HBox layout_buttons2;
	@FXML private HBox layout_scrollcontent;
	@FXML private VBox layout_obj1;
	@FXML private VBox layout_obj2;
	@FXML private ToggleButton toggle_edit;
	@FXML private Button button_entfernen;
	@FXML private Button button_delobj;

	contr_object cont_object = null;
	contr_overlap cont_overlap = null;

	ArrayList<FilterButton> filter = new ArrayList<>();
	ArrayList<Integer> list_id = new ArrayList<>();

	HBox layout_mini = null;
	Stage stage = null;
	contr_hauptmenue contr_hauptmenue = null;
	contr_filterFrame contr_filterFrame = null;
	contr_Auswahl_Frame controll_auswahl = null;

	public ArrayList<Integer> getDeleteList(){
		return list_id;
	}

	@FXML 
	void initialize(){
		layout_obj1.managedProperty().bind(layout_obj1.visibleProperty());
		layout_obj2.managedProperty().bind(layout_obj2.visibleProperty());
		scroll_zusam.managedProperty().bind(scroll_zusam.visibleProperty());
		layout_buttons2.managedProperty().bind(layout_buttons2.visibleProperty());
		layout_buttons.managedProperty().bind(layout_buttons.visibleProperty());

		layout_obj1.setVisible(false);
		layout_obj2.setVisible(false);

		button_delobj.setDisable(true);

		scroll_filter.setStyle(".scroll-pane > .viewport {-fx-background-color: green;}");

		scroll_zusam.viewportBoundsProperty().addListener((obs, oldSelection, newSelection) -> {
			int height = (int)newSelection.getHeight();
			if(height <= 87) {
				scroll_zusam.setPrefHeight(128);
				scroll_zusam.setMinHeight(128);
				scroll_zusam.setMaxHeight(128);
				FilterButton tmp = getSelectedFilter();
				tmp.setSelected(false);
				tmp.setSelected(true);
			}
			else if(height == 124) {
				scroll_zusam.setPrefHeight(108);
				scroll_zusam.setMinHeight(108);
				scroll_zusam.setMaxHeight(108);
				FilterButton tmp = getSelectedFilter();
				tmp.setSelected(false);
				tmp.setSelected(true);
			}
		});

		scroll_filter.setPrefHeight(89);
		scroll_filter.setMinHeight(89);
		scroll_filter.setMaxHeight(89);

		scroll_filter.viewportBoundsProperty().addListener((obs, oldSelection, newSelection) -> {
			int height = (int)newSelection.getHeight();
			System.out.println("tttttttttttttttttttttttttttttttttt: "+newSelection);

			if(layout_filter.getChildren().size()>0) {
				if(height <= 65) {
					scroll_filter.setPrefHeight(111);
					scroll_filter.setMinHeight(111);
					scroll_filter.setMaxHeight(111);
					if(getSelectedFilter().getController() instanceof contr_object) {
						contr_object obj = (contr_object) getSelectedFilter().getController();
						FilterButton tmp = obj.getSelectedFilter();
						tmp.setSelected(false);
						tmp.setSelected(true);
					}
					else if(getSelectedFilter().getController() instanceof contr_overlap) {
						contr_overlap obj = (contr_overlap) getSelectedFilter().getController();
						FilterButton tmp = obj.getSelectedFilter();
						tmp.setSelected(false);
						tmp.setSelected(true);
					}
				}
				else if(height == 109) {
					scroll_filter.setPrefHeight(89);
					scroll_filter.setMinHeight(89);
					scroll_filter.setMaxHeight(89);
					if(getSelectedFilter().getController() instanceof contr_object) {
						contr_object obj = (contr_object) getSelectedFilter().getController();
						FilterButton tmp = obj.getSelectedFilter();
						tmp.setSelected(false);
						tmp.setSelected(true);
					}
					else if(getSelectedFilter().getController() instanceof contr_overlap) {
						contr_overlap obj = (contr_overlap) getSelectedFilter().getController();
						FilterButton tmp = obj.getSelectedFilter();
						tmp.setSelected(false);
						tmp.setSelected(true);
					}
				}
			}
		});


	}


	private FilterButton getSelectedFilter() {
		for(int i=0;i<filter.size();i++) {
			if(filter.get(i).isSelected())
				return filter.get(i);
		}
		return null;
	}



	public void addFilter(FilterButton filter, int id, boolean updatePreview) {
		clearSelection();

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
		addID(id);

		filter.setObjEdit(false);
		layout_zusammenstellung.getChildren().add(filter.getComponent());
		layout_mini.getChildren().add(filter.getMiniComponent());
		//			}
		setSelectedFilter(id, filter);
		contr_filterFrame.updateErrorColor();

		if(updatePreview)
			filter.getController().updatePreview();
	}

	@FXML
	void act_button_entfernen(ActionEvent event) {
		FilterButton tmp = getSelectedFilter();
		if(tmp != null) {
			int index = filter.indexOf(tmp);
			if(index!=0) {
				entferne_Filter(tmp, index);
				//				if(tmp.getController() instanceof contr_object) {
				//					contr_object obj = (contr_object)tmp.getController();
				//					if(tmp.isSelectionDisabled()) {
				//						obj.removeFilter(tmp);
				//					}
				//					else {
				//						entferne_Filter(tmp, index);
				//					}
				//				}
				//				else {
				//					entferne_Filter(tmp, index);
				//				}
			}
		}
	}

	@FXML
	void act_button_delobj(ActionEvent event) {
		FilterButton tmp = getSelectedFilter();
		if(tmp != null) {
			int index = filter.indexOf(tmp);
			if(index!=0) {
				if(tmp.getController() instanceof contr_object) {
					contr_object obj = (contr_object)tmp.getController();
					obj.removeFilter(tmp);
				}
				else if(tmp.getController() instanceof contr_overlap) {
					contr_overlap obj = (contr_overlap)tmp.getController();
					obj.removeFilter(tmp);
				}
			}
		}
	}


	private void entferne_Filter(FilterButton tmp, int index) {
		layout_zusammenstellung.getChildren().remove(tmp.getComponent());
		layout_mini.getChildren().remove(tmp.getMiniComponent());

		removeID(filter.get(index).getFilterID());
		filter.remove(tmp);
		toggle_edit.setSelected(false);
		setSelectedFilter(filter.get(index-1).getFilterID(), filter.get(index-1));
		contr_filterFrame.updateErrorColor();
		getVorschauController().updatePreview();
	}

	@FXML
	void act_button_left(ActionEvent event) {
		for(int i=0;i<filter.size();i++) {
			if(filter.get(i).isSelected()) {
				System.out.println("is selected: "+i);
				if(1<i) {
					if(filter.get(i).isObjEdit()) {
						FilterButton tmp = filter.get(i);
						filter.remove(tmp);
						layout_filter.getChildren().remove(tmp.getComponent());
						filter.add(i-1, tmp);
						layout_filter.getChildren().add(i-1, tmp.getComponent());
					}
					else {
						FilterButton tmp = filter.get(i);
						filter.remove(tmp);
						layout_zusammenstellung.getChildren().remove(tmp.getComponent());
						layout_mini.getChildren().remove(tmp.getMiniComponent());
						filter.add(i-1, tmp);
						layout_zusammenstellung.getChildren().add(i-1, tmp.getComponent());
						layout_mini.getChildren().add(i-1, tmp.getMiniComponent());
					}
				}
				break;
			}
		}
	}

	@FXML
	void act_button_right(ActionEvent event) {
		for(int i=0;i<filter.size();i++) {
			if(filter.get(i).isSelected()) {
				System.out.println("is selected: "+i);
				if(filter.size()-1>i && i!=0) {
					if(filter.get(i).isObjEdit()) {
						FilterButton tmp = filter.get(i);
						filter.remove(tmp);
						layout_filter.getChildren().remove(tmp.getComponent());
						filter.add(i+1, tmp);
						layout_filter.getChildren().add(i+1, tmp.getComponent());
					}
					else {
						FilterButton tmp = filter.get(i);
						filter.remove(tmp);
						layout_zusammenstellung.getChildren().remove(tmp.getComponent());
						layout_mini.getChildren().remove(tmp.getMiniComponent());
						filter.add(i+1, tmp);
						layout_zusammenstellung.getChildren().add(i+1, tmp.getComponent());
						layout_mini.getChildren().add(i+1, tmp.getMiniComponent());
					}
				}
				break;
			}
		}
	}

	@FXML
	void act_button_left2(ActionEvent event) {
		FilterButton button = getSelectedFilter();
		if(button != null) {
			if(button.getController() instanceof contr_object) {
				contr_object obj = (contr_object)button.getController();
				obj.left();
			}
			else if(button.getController() instanceof contr_overlap) {
				contr_overlap obj = (contr_overlap)button.getController();
				obj.left();
			}
		}
	}

	@FXML
	void act_button_right2(ActionEvent event) {
		FilterButton button = getSelectedFilter();
		if(button != null) {
			if(button.getController() instanceof contr_object) {
				contr_object obj = (contr_object)button.getController();
				obj.right();
			}
			else if(button.getController() instanceof contr_overlap) {
				contr_overlap obj = (contr_overlap)button.getController();
				obj.right();
			}
		}
	}

	@FXML
	void act_toggle_edit(ActionEvent event) {
		//		scroll_zusam.setVisible(!toggle_edit.isSelected());
		scroll_zusam.setDisable(toggle_edit.isSelected());
		FilterButton tmp = getSelectedFilter();
		if(toggle_edit.isSelected()) {
			if(tmp.getController() instanceof contr_object)
				controll_auswahl.setController(cont_object);
			else
				controll_auswahl.setController(cont_overlap);

			//			setDisableSelection(true);
			button_delobj.setDisable(false);
			layout_buttons.setVisible(false);
			layout_buttons2.setVisible(true);
			layout_scrollcontent.setDisable(false);
			System.out.println("DISABLE -------------------------------------------------------------------------------------------------");

			//			FilterButton tmp = getSelectedFilter();
			Bildverarbeitung contr = tmp.getController();
			if(contr instanceof contr_object) {
				contr_hauptmenue.controll_auswahl.enableObjectMode(toggle_edit.isSelected());
				contr_object obj = (contr_object) contr;
				FilterButton obj_button = obj.getSelectedFilter();
				if(obj_button != null)
					setSelectedFilter(obj_button.getFilterID(), obj_button);
			}
			else if(contr instanceof contr_overlap) {
				contr_hauptmenue.controll_auswahl.enableOverlapModeMode(toggle_edit.isSelected());
				contr_overlap obj = (contr_overlap) contr;
				FilterButton obj_button = obj.getSelectedFilter();
				if(obj_button != null)
					setSelectedFilter(obj_button.getFilterID(), obj_button);
			}

			//			layout_scrollcontent.setStyle("-fx-background-color: white; -fx-border-color:  #d5c5da; -fx-border-width: 2");
		}
		else {
			contr_hauptmenue.controll_auswahl.enableOverlapModeMode(false);
			controll_auswahl.setController(this);
			button_delobj.setDisable(true);
			layout_buttons.setVisible(true);
			layout_buttons2.setVisible(false);
			layout_scrollcontent.setDisable(true);
			System.out.println("VISIBLE -------------------------------------------------------------------------------------------------");
			//			FilterButton tmp = getSelectedFilter();
			setSelectedFilter(tmp.getFilterID(), tmp);
			//			layout_scrollcontent.setStyle("-fx-background-color: #F8F8F8; -fx-border-color:  #d5c5da; -fx-border-width: 2");
		}
	}

	private void setSelectedFilter(int id, FilterButton filter) {
		System.out.println("setSelectedFilter!");
		filter.setSelected(true);

		if(filter.isObjEdit() || filter.getFilterID() == Bildverarbeitung.FILTER_OBJECT || filter.getFilterID() == Bildverarbeitung.FILTER_OVERLAP) {
			layout_obj1.setVisible(true);
			layout_obj2.setVisible(true);

			if(filter.getFilterID() == Bildverarbeitung.FILTER_OBJECT) {
				cont_object = (contr_object)filter.getController();
				cont_object.init(layout_filter, contr_hauptmenue.controll_auswahl, contr_filterFrame, getVorschauController(), pane_einstellungen, scroll_einstellungen, this, contr_hauptmenue.getVoreinstellungen());
			}
			else if(filter.getFilterID() == Bildverarbeitung.FILTER_OVERLAP) {
				cont_overlap = (contr_overlap)filter.getController();
				cont_overlap.init(layout_filter, contr_hauptmenue.controll_auswahl, contr_filterFrame, getVorschauController(), pane_einstellungen, scroll_einstellungen, this, contr_hauptmenue.getVoreinstellungen());
			}
		}
		else {
			layout_obj1.setVisible(false);
			layout_obj2.setVisible(false);
			toggle_edit.setSelected(false);
			cont_object = null;
			layout_filter.getChildren().clear();
		}

		AnchorPane pane = filter.getPane();
		pane_einstellungen.getChildren().clear();
		pane_einstellungen.getChildren().add(pane);
		scroll_einstellungen.prefWidthProperty().unbind();
		pane.prefWidthProperty().bind(scroll_einstellungen.widthProperty().subtract(24));
		if(getVorschauController().contr_settings.isOnlyOne())
			getVorschauController().updatePreview();
		testSecondFilters(id, filter);
	}

	private void clearSelection() {
		for(int i=0;i<filter.size();i++) {
			filter.get(i).setSelected(false);
		}
	}

	public void setDisableSelection(boolean b) {
		for(int i=0;i<filter.size();i++) {
			filter.get(i).setDisableSelection(b);
		}
	}

	private void addID(int id) {
		if(list_id.contains(id) == false)
			list_id.add(id);
	}

	private void removeID(int id) {
		if(list_id.contains(id) == true) {
			list_id.remove(list_id.indexOf(id));
			contr_hauptmenue.controll_auswahl.setDisableFilterList(list_id);
		}
	}

	private void testSecondFilters(int id, FilterButton filter) {
		if(id == Bildverarbeitung.FILTER_OBJECT || id == Bildverarbeitung.FILTER_OVERLAP || toggle_edit.isSelected() || filter.isObjEdit()) {
			layout_obj1.setVisible(true);
			layout_obj2.setVisible(true);
			scroll_filter.setPrefHeight(89);
			scroll_filter.setMinHeight(89);
			scroll_filter.setMaxHeight(89);
		}
		else {
			layout_obj1.setVisible(false);
			layout_obj2.setVisible(false);
			toggle_edit.setSelected(false);
		}
	}


	public void init(HBox box, Stage stage, boolean add_bg, contr_hauptmenue contr_hauptmenue, contr_filterFrame contr_filterFrame) {
		layout_buttons.setVisible(true);
		layout_buttons2.setVisible(false);
		button_delobj.setDisable(true);
		this.layout_mini = box;
		this.stage = stage;
		this.contr_hauptmenue = contr_hauptmenue;
		this.contr_filterFrame = contr_filterFrame;
		this.controll_auswahl = contr_hauptmenue.controll_auswahl;
		if(add_bg) {
			FilterButton filter = new FilterButton(Bildverarbeitung.FILTER_BACKGROUND, this.filter, true, contr_hauptmenue.getVoreinstellungen(), contr_filterFrame, contr_hauptmenue.control_vorschau, 85);
			addFilter(filter,Bildverarbeitung.FILTER_BACKGROUND, false);
		}

		toggle_edit.setSelected(false);
		scroll_zusam.setVisible(true);
		contr_hauptmenue.controll_auswahl.enableObjectMode(false);
		controll_auswahl.setController(this);
	}

	public contr_Vorschau_Frame getVorschauController() {
		return contr_hauptmenue.control_vorschau;
	}


	public xml getXMLData(int anz_iterationen, int num_obj_per_img) {
		xml root = new xml("filter");
		root.getElement().setAttribute("num", ""+anz_iterationen);
		root.getElement().setAttribute("num_obj_per_img", "1:"+num_obj_per_img);
		for(int i=0;i<filter.size();i++) {
			System.out.println("i: "+i+" "+filter.get(i).getFilterID());
			root.addNode(filter.get(i).getController().generateXML());
			addID(filter.get(i).getFilterID());
		}
		return root;
	}

	public xml getXMLDataVorschau(boolean allFilter, boolean random, int previewMin) {
		xml root = new xml("filter");
		root.getElement().setAttribute("num", "1");
		for(int i=0;i<filter.size();i++) {
			if((filter.get(i).isSelected() == true || filter.get(i).getFilterID()==Bildverarbeitung.FILTER_BACKGROUND) || allFilter) {
				if(filter.get(i).getController().getState()) {
					root.addNode(filter.get(i).getController().gererateXML_Preview(previewMin, random));
					addID(filter.get(i).getFilterID());
				}
				else {
					System.err.println("getState() == false!");
				}
			}
		}
		return root;
	}

	public void setPositions() {
		//		System.out.println("PSOISTIONS!");
		//		pane.setMaxWidth(slider_positions.POS_SLIDER_RIGHT);
		//		pane.setMinWidth(slider_positions.POS_SLIDER_RIGHT);
		//		pane.setPrefWidth(slider_positions.POS_SLIDER_RIGHT);
		//		pane.setMaxWidth(slider_positions.POS_SLIDER_RIGHT_3);
		//		pane.setMinWidth(slider_positions.POS_SLIDER_RIGHT_1);
	}

	public boolean hasError() {
		for(int i=0;i<filter.size();i++) {
			System.out.println("Check Filter: "+Bildverarbeitung.getName(filter.get(i).getFilterID())+" error: "+filter.get(i).hasError()+" sate: "+filter.get(i).getController().getState());
//			if(filter.get(i).hasError()==true)
//				return true;
			if(filter.get(i).getController().getState()==false)
				return true;
		}
		return false;
	}

	//	public void setWindow(AnchorPane pane2) {
	//		pane_einstellungen.getChildren().add(pane2);
	//	}
}
