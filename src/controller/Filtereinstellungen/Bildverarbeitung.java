package controller.Filtereinstellungen;

import java.io.File;
import java.io.InputStream;

import org.jdom2.Element;

import com.sun.javafx.scene.control.skin.Utils;

import application.xml;
import controller.contr_Voreinstellungen;
import controller.contr_Vorschau_Frame;
import controller.contr_filterFrame;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import komponenten.FilterButton;

@SuppressWarnings("restriction")
public abstract class Bildverarbeitung {

	public static int FILTER_BACKGROUND = 0;
	public static int FILTER_TRANSLATE = 1;
	public static int FILTER_CLIP = 2;
	public static int FILTER_ROTATE = 3;
	public static int FILTER_SCALE = 4;
	public static int FILTER_NOISE = 5;
	public static int FILTER_BRIGHTNESS = 7;
	public static int FILTER_OVERLAP = 9;
	public static int FILTER_HUE = 10;
	public static int FILTER_BLUR = 11;
	public static int FILTER_CONTRAST = 12;
	public static int FILTER_OVERLAY = 13;
	public static int FILTER_SATURATION = 14;
	public static int FILTER_GAMMA = 15;
	public static int FILTER_VALUE = 16;
	public static int FILTER_OBJECT = 17;
	public static int FILTER_RESOLUTION = 18;

	public static final int PREVIEW_MIN = 0;
	public static final int PREVIEW_MAX = 1;
	public static final int PREVIEW_AVG = 2;
	public static final int PREVIEW_NORMAL = 3;

	contr_Voreinstellungen paths = null;
	contr_filterFrame contr_filterframe = null;
	contr_Vorschau_Frame contr_vorschau = null;
	FilterButton filterb = null;
	boolean b_filter_mode = false;
	Bildverarbeitung parent = null;

	public void setParent(Bildverarbeitung parent) {
		this.parent = parent;
	}

	public Bildverarbeitung getParent() {
		return parent;
	}

	/**
	 * Funktion gibt den Namen der Bilddatei zurück, die auf der Festplatte gespeichert wurde
	 * @param filter
	 * @return
	 */
	public static String getFileName(int filter) {
		if(filter == FILTER_BACKGROUND)
			return "background.png";
		else if(filter == FILTER_TRANSLATE)
			return "translate.png";
		else if(filter == FILTER_CLIP)
			return "clip.png";
		else if(filter == FILTER_ROTATE)
			return "rotate.png";
		else if(filter == FILTER_SCALE)
			return "scale.png";
		else if(filter == FILTER_NOISE)
			return "noise.png";
		else if(filter == FILTER_BRIGHTNESS)
			return "brightness.png";
		else if(filter == FILTER_OVERLAP)
			return "overlap.png";
		else if(filter == FILTER_HUE)
			return "hue.png";
		else if(filter == FILTER_BLUR)
			return "blur.png";
		else if(filter == FILTER_CONTRAST)
			return "contrast.png";
		else if(filter == FILTER_OVERLAY)
			return "overlay.png";
		else if(filter == FILTER_SATURATION)
			return "saturation.png";
		else if(filter == FILTER_GAMMA)
			return "gamma.png";
		else if(filter == FILTER_VALUE)
			return "value.png";
		else if(filter == FILTER_OBJECT)
			return "object.png";
		else if(filter == FILTER_RESOLUTION)
			return "resolution.png";

		return null;
	}

	/**
	 * Funktion liefert den Namen, der in der GUI angezeigt wird
	 * @param FILTER
	 * @return
	 */
	public static String getName(int filter) {
		if(filter == FILTER_BACKGROUND)
			return "background";
		else if(filter == FILTER_TRANSLATE)
			return "translate";
		else if(filter == FILTER_CLIP)
			return "clip";
		else if(filter == FILTER_ROTATE)
			return "rotate";
		else if(filter == FILTER_SCALE)
			return "scale";
		else if(filter == FILTER_NOISE)
			return "noise";
		else if(filter == FILTER_BRIGHTNESS)
			return "brightness";
		else if(filter == FILTER_OVERLAP)
			return "overlap";
		else if(filter == FILTER_HUE)
			return "hue";
		else if(filter == FILTER_BLUR)
			return "blur";
		else if(filter == FILTER_CONTRAST)
			return "contrast";
		else if(filter == FILTER_OVERLAY)
			return "overlay";
		else if(filter == FILTER_SATURATION)
			return "saturation";
		else if(filter == FILTER_GAMMA)
			return "gamma";
		else if(filter == FILTER_VALUE)
			return "value";
		else if(filter == FILTER_OBJECT)
			return "object";
		else if(filter == FILTER_RESOLUTION)
			return "resolution";


		return null;
	}

	/**
	 * Funktion gibt den Namen zurück, der im Stylesheet hinterlegt wurde
	 * @param filter
	 * @return
	 */
	public static String getImageName(int filter) {
		if(filter == FILTER_BACKGROUND)
			return "img_background";
		else if(filter == FILTER_TRANSLATE)
			return "img_move";
		else if(filter == FILTER_CLIP)
			return "img_move_out";
		else if(filter == FILTER_ROTATE)
			return "img_rot";
		else if(filter == FILTER_SCALE)
			return "img_scale";
		else if(filter == FILTER_NOISE)
			return "img_nois";
		else if(filter == FILTER_BRIGHTNESS)
			return "img_helligkeit";
		else if(filter == FILTER_OVERLAP)
			return "img_deckung";
		else if(filter == FILTER_HUE)
			return "img_farbton";
		else if(filter == FILTER_BLUR)
			return "img_bloom";
		else if(filter == FILTER_CONTRAST)
			return "img_contrast";
		else if(filter == FILTER_OVERLAY)
			return "img_overlay";
		else if(filter == FILTER_SATURATION)
			return "img_saturation";
		else if(filter == FILTER_GAMMA)
			return "img_gamma";
		else if(filter == FILTER_VALUE)
			return "img_value";
		else if(filter == FILTER_OBJECT)
			return "img_object";
		else if(filter == FILTER_RESOLUTION)
			return "img_resolution";


		return null;
	}



	public static ImageView getImageView(int id, int width, int height) {

		InputStream is = null;
		try {
			System.out.println("load");
			is=Utils.class.getResourceAsStream("D:/00-Meine_Programme/10_Masterprojekt/Masterprojekt/Bilder/"+getFileName(id));
			System.out.println("end load");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Image image = new Image(is);
		ImageView view = new ImageView();
		view.setImage(image);
		view.setPreserveRatio(true);
		view.setFitHeight(height);
		view.setFitWidth(width);
		System.out.println("return");
		return view;
	}


	public abstract void loadXML(Element root);
	public abstract xml generateXML();
	public abstract xml gererateXML_Preview(int PREVIEW, boolean random);
	public abstract int getID();

	//Preview Funktionen
	public static void prev_SliderData(Slider slider_from, Slider slider_to, int PREVIEW, xml root, boolean caste_to_int, boolean random, boolean aloww_zero, boolean invert) {

		if(random == true) {
			prev_SliderData(slider_from, slider_to, root, caste_to_int);
		}
		else {
			if(caste_to_int) {
				int min = (int)slider_from.getValue();
				int max = (int)slider_to.getValue();
				if(aloww_zero == false) {
					if(min == 0)min = 1;
					if(max == 0)max = 1;
				}
				if(invert == true) {
					min = -1*min;
					max = -1*max;
				}
				double avg = ((max - min)/2.0) + min;
				int avgi = (int)avg;
				System.out.println("min: "+min+" avg: "+avg+" max: "+max);
				if(PREVIEW_MIN == PREVIEW)
					root.getElement().addContent(""+min+":"+min);
				else if(PREVIEW_MAX == PREVIEW)
					root.getElement().addContent(""+max+":"+max);
				else if(PREVIEW_AVG == PREVIEW)
					root.getElement().addContent(""+avgi+":"+avgi);
				else if(PREVIEW_NORMAL == PREVIEW)
					root.getElement().addContent(""+min+":"+max);
			}
			else {
				float min = (float)slider_from.getValue();
				float max = (float)slider_to.getValue();
				if(aloww_zero == false) {
					if(min == 0)min = 1;
					if(max == 0)max = 1;
				}
				if(invert == true) {
					min = -1*min;
					max = -1*max;
				}
				float avg = (float)(((max - min)/2.0) + min);
				System.out.println("min: "+min+" avg: "+avg+" max: "+max);
				if(PREVIEW_MIN == PREVIEW)
					root.getElement().addContent(""+min+":"+min);
				if(PREVIEW_MAX == PREVIEW)
					root.getElement().addContent(""+max+":"+max);
				if(PREVIEW_AVG == PREVIEW)
					root.getElement().addContent(""+avg+":"+avg);
				else if(PREVIEW_NORMAL == PREVIEW)
					root.getElement().addContent(""+min+":"+max);
			}
		}
	}
	
	public static void prev_SliderData(Slider slider_from, Slider slider_to, int PREVIEW, xml root, boolean caste_to_int, boolean random) {
		prev_SliderData(slider_from, slider_to, PREVIEW, root, caste_to_int, random, true, false);
	}

	public static void prev_SliderData(Slider slider_from, Slider slider_to, xml root, boolean caste_to_int) {
		if(caste_to_int) {
			int min = (int)slider_from.getValue();
			int max = (int)slider_to.getValue();
			root.getElement().addContent(""+min+":"+max);
		}
		else {
			float min = (float)slider_from.getValue();
			float max = (float)slider_to.getValue();
			root.getElement().addContent(""+min+":"+max);
		}
	}





	public static int getID(String name) {

		if(name.equals("background"))
			return FILTER_BACKGROUND;

		else if(name.equals("translate"))
			return FILTER_TRANSLATE;

		else if(name.equals("clip"))
			return FILTER_CLIP;

		else if(name.equals("rotate"))
			return FILTER_ROTATE;

		else if(name.equals("scale"))
			return FILTER_SCALE;

		else if(name.equals("noise"))
			return FILTER_NOISE;

		else if(name.equals("brightness"))
			return FILTER_BRIGHTNESS;

		else if(name.equals("overlap"))
			return FILTER_OVERLAP;

		else if(name.equals("hue"))
			return FILTER_HUE;

		else if(name.equals("blur"))
			return FILTER_BLUR;

		else if(name.equals("contrast"))
			return FILTER_CONTRAST;

		else if(name.equals("overlay"))
			return FILTER_OVERLAY;

		else if(name.equals("saturation"))
			return FILTER_SATURATION;

		else if(name.equals("gamma"))
			return FILTER_GAMMA;

		else if(name.equals("value"))
			return FILTER_VALUE;

		else if(name.equals("add_obj"))
			return FILTER_OBJECT;
		
		else if(name.equals("resolution"))
			return FILTER_RESOLUTION;


		return 0;
	}

	public void init(contr_Voreinstellungen paths, FilterButton filterb, contr_filterFrame contr_filterframe, contr_Vorschau_Frame contr_vorschau) {
		this.paths = paths;
		this.filterb = filterb;
		this.contr_filterframe = contr_filterframe;
		this.contr_vorschau = contr_vorschau;
	}

	public void init(FilterButton filterb, contr_filterFrame contr_filterframe, contr_Vorschau_Frame contr_vorschau) {
		this.filterb = filterb;
		this.contr_filterframe = contr_filterframe;
		this.contr_vorschau = contr_vorschau;
	}

	protected boolean checkPath(File file, boolean isDirectory) {
		boolean error = false;
		if(file == null) {
			setError(error);
			return true;
		}
		else if(file.exists() == false) {
			System.err.println("Folgendes Verzeichnis existiert nicht mehr: "+file.getAbsolutePath());
			error = true;
		}
		if(isDirectory && file.isFile()) {
			System.err.println("File ist eine Datei und kein Verzeichnis!");
			error = true;
		}

		//		if(filterb.getParent() != null) {
		//			System.out.println("PARENT IST NOT NULL!!!!!!!");
		//			if(filterb.getParent().getState() == true) {
		//				error = true;
		//			}
		//		}

		setError(error);

		return error;
	}

	public void setError(boolean b) {
		filterb.setErrorColor(b);
		System.out.println("SET ERROR: "+parent+" b: "+b);
		if(parent!=null && b == true) {
			parent.setError(true);
		}
		else if(parent!=null) {
			parent.setError(!parent.getState());
		}
		contr_filterframe.updateErrorColor();
	}

	//	protected void addPreviewListener(Slider slider) {
	//		slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
	//			@Override
	//			public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
	//				 updatePreview();
	//			}
	//		});
	//	}

	protected void bindSlider_from_to(Slider from, Slider to, TextField txt_from) {
		from.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0 && to.getValue() > new_val.doubleValue()) {
					to.setMin(new_val.intValue());
					txt_from.setText(""+new_val.intValue());
				}
			}
		});
	}

	protected void bindSlider_to_from(Slider to, Slider from, TextField txt_to) {
		to.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if(new_val.intValue()!=0  && from.getValue() < new_val.doubleValue()) {
					from.setMax(new_val.intValue());
					txt_to.setText(""+new_val.intValue());
				}
			}
		});
	}

	protected void bindSlider(Slider slider, TextField txt) {
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				txt.setText(""+new_val.intValue());
			}
		});
	}

	protected void addPreviewListener(Slider from, Slider to, Slider fix, RadioButton radio_fix) {
		from.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
				if (! isNowChanging) {
					if(!radio_fix.isSelected()) updatePreview();
				}
			}
		});
		to.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
				if (! isNowChanging) {
					if(!radio_fix.isSelected()) updatePreview();
				}
			}
		});
		fix.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
				if (! isNowChanging) {
					if(radio_fix.isSelected()) updatePreview();
				}
			}
		});
	}

	protected void addPreviewListener(RadioButton back, RadioButton obj, RadioButton both) {
		back.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent event) {updatePreview();}});
		obj.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent event) {updatePreview();}});
		both.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent event) {updatePreview();}});
	}

	public void updatePreview() {
		if(getState() == true) {
			contr_vorschau.updatePreview();
		}
	}

	public boolean getState() {
		return true;
	}

	public void setObjectMode(boolean b) {
		b_filter_mode = b;
	}

	public boolean isObjectMode() {
		return b_filter_mode;
	}

}
