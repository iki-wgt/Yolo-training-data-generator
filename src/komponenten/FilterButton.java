package komponenten;

import java.util.ArrayList;

import application.xml;
import controller.contr_Voreinstellungen;
import controller.contr_Vorschau_Frame;
import controller.contr_filterFrame;
import controller.Filtereinstellungen.Bildverarbeitung;
import controller.Filtereinstellungen.contr_background;
import controller.Filtereinstellungen.contr_blur;
import controller.Filtereinstellungen.contr_brightnes;
import controller.Filtereinstellungen.contr_clip;
import controller.Filtereinstellungen.contr_contrast;
import controller.Filtereinstellungen.contr_gamma;
import controller.Filtereinstellungen.contr_hue;
import controller.Filtereinstellungen.contr_noise;
import controller.Filtereinstellungen.contr_overlap;
import controller.Filtereinstellungen.contr_overlay;
import controller.Filtereinstellungen.contr_rotate;
import controller.Filtereinstellungen.contr_saturation;
import controller.Filtereinstellungen.contr_scale;
import controller.Filtereinstellungen.contr_translate;
import controller.Filtereinstellungen.contr_value;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FilterButton {
	Label label = new Label();
	VBox box = new VBox();
	Button button = new Button("");
	ImageView view_mini = null;
	VBox box_mini = new VBox();
	boolean selected = true;
	boolean selection_desabled = false;
	boolean obedit = false;
	ArrayList<FilterButton> filter_array = null;
	int FilterID = -1;
	AnchorPane pane = null;
	Bildverarbeitung controller = null;
	boolean b_error = false;
	ImageView view_button = null;
	int width = 85;
	Bildverarbeitung parent = null;

	contr_Voreinstellungen paths = null;
	contr_filterFrame contr_filterframe = null;
	contr_Vorschau_Frame contr_vorschau = null;

	public FilterButton(int FilterID, ArrayList<FilterButton> filter_array, boolean initEinstellungen, contr_Voreinstellungen paths, contr_filterFrame contr_filterframe, contr_Vorschau_Frame contr_vorschau, int width) {
		this.FilterID = FilterID;
		this.paths = paths;
		this.contr_filterframe = contr_filterframe;
		this.contr_vorschau = contr_vorschau;
		this.width = width;
		label.setText(Bildverarbeitung.getName(FilterID));
//		button.getStyleClass().add(Bildverarbeitung.getImageName(FilterID));
		button.setPrefSize(width, width);
		button.setMinSize(width, width);
		button.setMaxSize(width, width);
		box.getChildren().add(label);
		box.getChildren().add(button);
		box.setStyle("-fx-background-color: #88D1D2");
		this.filter_array = filter_array;
		String url = "/"+Bildverarbeitung.getFileName(FilterID);
		System.out.println("url "+url);
		System.out.println(getClass().getResource("/"+Bildverarbeitung.getFileName(FilterID)));
		Image image = new Image(getClass().getResourceAsStream(url));
		
		view_mini = new ImageView(image);
		view_mini.setPreserveRatio(true);
		view_mini.setFitHeight(83);
		box_mini.setStyle("-fx-border-color: darkgray; -fx-border-width: 1; -fx-background-color: white");
		box_mini.getChildren().add(view_mini);
		
		view_button = new ImageView(image);
		view_button.setPreserveRatio(true);
		button.setGraphic(view_button);
		view_button.setFitWidth(width);

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(selected == false) {
					clearSelection();
					selected = true;
					box.setStyle("-fx-background-color: #88D1D2");
				}
			}
		});
		

		if(initEinstellungen == true) {
			initButton(FilterID);
		}
	}

	private void initButton(int filterID) {
		try {
			if(filterID == Bildverarbeitung.FILTER_BACKGROUND) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/background.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_background>getController();
				controller.init(paths, this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_TRANSLATE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/translate.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_translate>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_CLIP) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/clip.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_clip>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_ROTATE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/rotate.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_rotate>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_SCALE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/scale.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_scale>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_NOISE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/noise.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_noise>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_HUE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/hue.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_hue>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_BRIGHTNESS) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/brightnes.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_brightnes>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_OVERLAY) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/overlay.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_overlay>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
				controller.setError(true);
			}
			else if(filterID == Bildverarbeitung.FILTER_OVERLAP) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/overlap.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_overlap>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
				controller.setError(true);
			}
			else if(filterID == Bildverarbeitung.FILTER_BLUR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/blur.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_blur>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_CONTRAST) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/contrast.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_contrast>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_SATURATION) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/saturation.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_saturation>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_GAMMA) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/gamma.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_gamma>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_VALUE) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/value.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_value>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else if(filterID == Bildverarbeitung.FILTER_OBJECT) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/object.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_value>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
				controller.setError(true);
			}
			else if(filterID == Bildverarbeitung.FILTER_RESOLUTION) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Filtereinstellungen/resolution.fxml"));
				pane = (AnchorPane)loader.load();
				controller = loader.<contr_scale>getController();
				controller.init(this, contr_filterframe,contr_vorschau);
			}
			else {
				System.err.println("Filter in FilterButton nicht gefunden!");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public AnchorPane getPane() {
		return pane;
	}

	public Bildverarbeitung getController() {
		return controller;
	}

	private void clearSelection() {
		for(int i=0;i<filter_array.size();i++) {
			filter_array.get(i).setSelected(false);
		}
	}

	public void setSelected(boolean b) {
		if(b==true) {
			box.setStyle("-fx-background-color: #88D1D2");
		}
		else {
			box.setStyle("-fx-background-color: transparent");
		}
		selected = b;
	}
	
	public void setDisableSelection(boolean b) {
		selection_desabled = b;
		if(b==true) {
			if(selected == true) {
				box.setStyle("-fx-background-color: #A6A6A6");
			}
			else {
				box.setStyle("-fx-background-color: transparent");
			}
		}
		else {
			if(selected == true) {
				box.setStyle("-fx-background-color: #88D1D2");
			}
			else {
				box.setStyle("-fx-background-color: transparent");
			}
		}
	}

	public boolean isSelected() {
		return selected;
	}
	
	public boolean isSelectionDisabled() {
		return selection_desabled;
	}
	
	public void setObjEdit(boolean b) {
		this.obedit = b;
		getController().setObjectMode(b);
	}
	
	public boolean isObjEdit() {
		return obedit;
	}
	
	public VBox getComponent() {
//		this.width = 100;
//		view_button.setFitWidth(width);
//		button.setPrefSize(width, width);
//		button.setMinSize(width, width);
//		button.setMaxSize(width, width);
		return box;
	}
	
//	public VBox getComponent(int width) {
//		this.width = width;
//		setErrorColor(b_error);
//		view_button.setFitWidth(width);
//		button.setPrefSize(width, width);
//		button.setMinSize(width, width);
//		button.setMaxSize(width, width);
//		return box;
//	}
	
	public Node getMiniComponent() {
		return box_mini;
	}

	public Button getButton() {
		return button;
	}

	public int getFilterID() {
		return FilterID;
	}

	public xml getXML() {
		//		if(FilterID == Bildverarbeitung.FILTER_BLOOM))

		return null;
	}
	
	public void setParent(Bildverarbeitung parent) {
		this.parent = parent;
	}
	
	public void setErrorColor(boolean b) {
		System.out.println("setErrorColor width: "+width+" bw: "+button.getWidth());
		b_error = b;
		ColorAdjust monochrome = new ColorAdjust();
		monochrome.setSaturation(-1.0);
		Blend blush = new Blend(BlendMode.MULTIPLY,monochrome,new ColorInput(0,0,83,83,Color.RED));
		Blend blush2 = new Blend(BlendMode.MULTIPLY,monochrome,new ColorInput(0,0,width,width,Color.RED));
	
		if(b == true) {
			view_mini.setEffect(blush);
			button.setEffect(blush2);
			System.out.println("SET EFEKT");
		}
		else {
			System.out.println("REMOVE EFEKT");
			view_mini.setEffect(null);
			button.setEffect(null);
		}
		
		if(parent != null)
			parent.setError(b);
	}

	public boolean hasError() {
		return b_error;
	}

	public void setWidth(int width) {
		this.width = width;
		view_button.setFitWidth(width);
		button.setPrefSize(width, width);
		button.setMinSize(width, width);
		button.setMaxSize(width, width);
	}

	public int getWidth() {
		return width;
	}

	public Bildverarbeitung getParent() {
		return parent;
	}

}
