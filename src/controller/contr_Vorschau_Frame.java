package controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.jdom2.Element;

import application.xml;
import controller.Filtereinstellungen.Bildverarbeitung;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class contr_Vorschau_Frame {

	@FXML private ImageView img3;
	@FXML private ImageView img2;
	@FXML private ImageView img1;
	@FXML private Label label1;
	@FXML private Label label2;
	@FXML private Label label3;
	@FXML private ProgressIndicator prog1;
	@FXML private ProgressIndicator prog2;
	@FXML private ProgressIndicator prog3;
	@FXML private Button button_generate;

	contr_Voreinstellungen contr_voreinstellungen = null;
	contr_hauptmenue contr_haupt = null;
	public contr_Vorschau_Settings contr_settings = null;

	Bildverarbeitung filter = null;
	Stage stage_setting = null;

	boolean queue = false;

	Thread thread = null;
	Process process = null;

	public final static String PATH_IMAGEGEN = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "imageGen";

	File file_workspace = new File(PATH_IMAGEGEN);
	File file_pixel = new File(PATH_IMAGEGEN + File.separator + "pixel");
	public static final File file_output = new File(PATH_IMAGEGEN + File.separator + "output");
	public static final File file_background = new File(PATH_IMAGEGEN + File.separator + "background");
	public static final File file_object = new File(PATH_IMAGEGEN + File.separator + "object");
	File file_std_background = new File(PATH_IMAGEGEN + File.separator + "std_background");
	File file_std_object = new File(PATH_IMAGEGEN + File.separator + "std_object");

	public static final File file_background_N = new File(PATH_IMAGEGEN + File.separator + "background" + File.separator + "back.jpg");
	public static final File file_object_N = new File(PATH_IMAGEGEN + File.separator + "object" + File.separator + "obj.png");
	public static final File file_class_N = new File(PATH_IMAGEGEN + File.separator + "object" + File.separator + "class.xml");

	File file_pixel_N = new File(PATH_IMAGEGEN + File.separator + "pixel" + File.separator + "pixel.png");
	File file_std_background_N = new File(PATH_IMAGEGEN + File.separator + "std_background" + File.separator + "back.jpg");
	File file_std_object_N = new File(PATH_IMAGEGEN + File.separator + "std_object" + File.separator + "obj.png");
	File file_std_class_N = new File(PATH_IMAGEGEN + File.separator + "std_object" + File.separator + "class.xml");


	private void saveClassXML(File file_out_class, String imageName) {
		xml root = new xml("root");

		//Add Classes ----------------------------------
		Element elem = new Element("class");
		elem.setAttribute("class", "0");
		elem.setAttribute("name", "object");
		root.addNode(elem);

		//Add File-Data --------------------------------
		elem = new Element("picture");
		elem.setText(imageName);
		elem.setAttribute("class", "0");
		elem.setAttribute("name","object");
		root.addNode(elem);

		xml.saveXML(root.getElement(), file_out_class);
	}


	public void initImgPath() {
		try {
			if(file_workspace.exists() == false) {
				file_workspace.mkdirs();
			}
			if(file_output.exists() == false) {
				file_output.mkdirs();
			}
			if(file_background.exists() == false) {
				file_background.mkdirs();
			}
			if(file_object.exists() == false) {
				file_object.mkdirs();
			}
			if(file_pixel.exists() == false) {
				file_pixel.mkdirs();
			}
			if(file_std_background.exists() == false) {
				file_std_background.mkdirs();
			}
			if(file_std_object.exists() == false) {
				file_std_object.mkdirs();
			}
			if(file_std_class_N.exists() == false) {
				saveClassXML(file_std_class_N, "obj.png");
			}
			if(file_class_N.exists() == false) {
				saveClassXML(file_class_N, "obj.png");
			}

			//extract images;
			if(file_pixel_N.exists()==false) {
				String url = "/pixel.png";
				Image image = new Image(getClass().getResourceAsStream(url));
				BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
				try {
					ImageIO.write(bImage, "png", file_pixel_N);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			if(file_std_background_N.exists()==false) {
				String url = "/back.jpg";
				Image image = new Image(getClass().getResourceAsStream(url));
				BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
				try {
					ImageIO.write(bImage, "jpg", file_std_background_N);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			if(file_std_object_N.exists()==false) {
				String url = "/obj.png";
				Image image = new Image(getClass().getResourceAsStream(url));
				BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
				try {
					ImageIO.write(bImage, "png", file_std_object_N);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	/*
	private boolean CopyRandomBackgroundAndObj() {
		boolean error = false;
		//zuerst Ordnerinhalte löschen:
		File[] del1 = file_background.listFiles();
		for(int i=del1.length-1;i>=0;i--) {
			del1[i].delete();
		}
		File[] del2 = file_object.listFiles();
		for(int i=del2.length-1;i>=0;i--) {
			del2[i].delete();
		}

		try {
			//Kopieren
			File quelle = new File(contr_voreinstellungen.getBackgroundPath());
			if(quelle.exists()) {
				if(quelle.isDirectory()) {
					File[] list = quelle.listFiles();
					int max = list.length -1;
					System.out.println("quelle: "+quelle);

					Random r = new Random();
					int index = r.nextInt((max - 0) + 1) + 0;
					System.out.println("choos random integer (back) from: 0 - "+max+" ergebnis: "+index+" file: ");
					System.out.println(list[index]);

					if(list[index].exists()) {
						if(list[index].isFile()) {
							try {
								Files.copy(list[index].toPath(), new File(file_background.getAbsolutePath() + File.separator + list[index].getName()).toPath());
							}catch(Exception e) {
								e.printStackTrace();
								error = true;
							}
						}
						else error = true;
					}
					else error = true;
				}
				else error = true;
			}
			else error = true;

			//Object:
			quelle = new File(contr_voreinstellungen.getObjPath());
			if(quelle.exists()) {
				if(quelle.isDirectory()) {
					File[] list = quelle.listFiles();
					int max = list.length -1;
					System.out.println("quelle: "+quelle);

					Random r = new Random();
					int index = r.nextInt((max - 0) + 1) + 0;
					System.out.println("choos random integer (obj) from: 0 - "+max+" ergebnis: "+index+" file: ");
					System.out.println(list[index]);

					if(list[index].exists()) {
						if(list[index].isFile()) {
							try {
								Files.copy(list[index].toPath(), new File(file_object.getAbsolutePath() + File.separator + list[index].getName()).toPath());
							}catch(Exception e) {
								e.printStackTrace();
								error = true;
							}
						}
						else error = true;
					}
					else error = true;
				}
				else error = true;
			}
			else error = true;

		}catch(Exception e) {
			e.printStackTrace();
			error = true;
		}
		return error;
	}
	 */

	public void init(contr_Voreinstellungen contr_voreinstellungen, contr_hauptmenue contr_haupt) {
		this.contr_voreinstellungen = contr_voreinstellungen;
		this.contr_haupt = contr_haupt;
		contr_settings.init(contr_voreinstellungen, this, stage_setting);
		initImgPath();
		//		CopyRandomBackgroundAndObj();
		updateIndicator(true, 1);
		updateIndicator(true, 2);
		updateIndicator(true, 3);
	}

	public void setData(Bildverarbeitung filter) {
		this.filter = filter;
	}

	private String pathWrapper(String path) {
		if(path.endsWith("\\")==false && path.endsWith("/")==false)
			path+="/";
		path = path.replace("\\",	 "/");
		return path;
	}

	public void generatePreviewXML() {
		queue = false;
		xml root = new xml("Daten");
		xml voreinstell = new xml("general");

		if(contr_settings.isStd_obj_path())
			voreinstell.addData("obj_path", pathWrapper(file_std_object.getAbsolutePath()));
		else
			voreinstell.addData("obj_path", pathWrapper(file_object.getAbsolutePath()));
		if(contr_settings.isStd_bg_path())
			voreinstell.addData("bg_path", pathWrapper(file_std_background.getAbsolutePath()));
		else
			voreinstell.addData("bg_path", pathWrapper(file_background.getAbsolutePath()));
		voreinstell.addData("output_path", pathWrapper(file_output.getAbsolutePath()));
		voreinstell.addData("output_height", 180);		
		voreinstell.addData("output_width", 180);

		root.addNode(voreinstell);
		try {
			boolean allLayer = !contr_settings.isOnlyOne();
			boolean random = contr_settings.isRandomSelected();
			root.addNode(contr_haupt.getSelectedFrame().getXMLCodeVorschau(allLayer, random, Bildverarbeitung.PREVIEW_MIN));
			root.addNode(contr_haupt.getSelectedFrame().getXMLCodeVorschau(allLayer, random, Bildverarbeitung.PREVIEW_AVG));
			root.addNode(contr_haupt.getSelectedFrame().getXMLCodeVorschau(allLayer, random, Bildverarbeitung.PREVIEW_MAX));
			xml.saveXML(root.getElement(), new File("preview.xml"));
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static String addSlasch_end(String s) {
		if(s.endsWith("/"))
			return s;
		else
			return (s += "/");
	}

	private void updateIndicator(boolean isLoading, int view_index) {
		boolean showLabel = !contr_settings.isRandomSelected();

		if(view_index == 1) {
			prog1.setVisible(isLoading);
			if(showLabel)
				label1.setVisible(isLoading);
			else
				label1.setVisible(false);
			if(isLoading)
				img1.setOpacity(0.3);
			else
				img1.setOpacity(1);
		}
		if(view_index == 2) {
			prog2.setVisible(isLoading);
			if(showLabel)
				label2.setVisible(isLoading);
			else
				label2.setVisible(false);
			if(isLoading)
				img2.setOpacity(0.3);
			else
				img2.setOpacity(1);
		}
		if(view_index == 3) {
			prog3.setVisible(isLoading);
			if(showLabel)
				label3.setVisible(isLoading);
			else
				label3.setVisible(false);
			if(isLoading)
				img3.setOpacity(0.3);
			else
				img3.setOpacity(1);
		}
	}


	@SuppressWarnings("deprecation")
	public void genImages() {

		updateIndicator(true, 1);
		updateIndicator(true, 2);
		updateIndicator(true, 3);


		if(thread!=null) {
			if(process.isAlive())
				process.destroy();
			if(thread.isAlive())
				thread.stop();
			while(thread.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		thread = new Thread() {
			public void run() {
				try {		
					String[] cmd = new String[4];
					cmd[0] = "python3";
					cmd[1] = "Python/main.py";
					cmd[2] = "preview.xml";
					cmd[3] = "-ny";
					Runtime rt = Runtime.getRuntime();
					try {
						process = rt.exec(cmd);
					} catch (IOException e) {
						System.out.println("Can't find program 'python3', try 'python'");
						cmd[0] = "python";
						try {
							process = rt.exec(cmd);
						} catch (IOException exc) {
							exc.printStackTrace();
							Alert alert2 = new Alert(AlertType.ERROR);
							alert2.setTitle("Error");
							alert2.setHeaderText("Error while starting the Python script");
							alert2.showAndWait();
						}
					}
					

					BufferedReader bfr_error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String line = "";
					System.out.println("\nOUTPUT FROM PYTHON-PROGRAM (PREVIEW):");
					int number = 0;
					while((line = bfr_error.readLine()) != null) {
//					while(process.isAlive() || ((line = bfr_error.readLine()) != null)) {
						
//					while(true) {
//						System.out.println("line: "+line);
						if(line.contains("chain number ")) {
							int index = line.indexOf("chain number ");
							try {
								number = Integer.parseInt(line.substring(index+13).replace(":", ""));
								//								System.out.println("number index: "+number);
								Thread.sleep(0, 1);

								if(number==2) {
									Platform.runLater(new Runnable() {
										@Override public void run() {
											File file = new File(file_output.getAbsolutePath() + File.separator + "1.jpg");
											if(file.exists()) {
												Image image = new Image(file.toURI().toString());
												img1.setImage(image);
												updateIndicator(false, 1);
											}
										}
									});
								}
								else if(number==3) {
									Platform.runLater(new Runnable() {
										@Override public void run() {
											File file = new File(file_output.getAbsolutePath() + File.separator + "2.jpg");
											if(file.exists()) {
												Image image = new Image(file.toURI().toString());
												img2.setImage(image);
												updateIndicator(false, 2);
											}
										}
									});
								}
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
//						System.out.println("läuft "+(line.isEmpty()));
						//						System.err.println(line);
					}

					Platform.runLater(new Runnable() {
						@Override public void run() {
							File file = new File(file_output.getAbsolutePath() + File.separator + "3.jpg");
							System.out.println("Vorschau fertig: exist: "+file.exists());
							if(file.exists()) {
								Image image = new Image(file.toURI().toString());
								img3.setImage(image);
								updateIndicator(false, 3);
							}
						}
					});

					//					if(queue == true) {
					//						generatePreviewXML();
					//						genImages();
					//					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		};

		thread.start();
	}



	@FXML 
	void initialize(){
		button_generate.managedProperty().bind(button_generate.visibleProperty());

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Vorschau_Settings.fxml"));
			AnchorPane pane_progress = (AnchorPane)loader.load();
			contr_settings = loader.<contr_Vorschau_Settings>getController();
			stage_setting = new Stage();
			stage_setting.setScene(new Scene(pane_progress,523,617));
			stage_setting.setTitle("Preview settings");
			stage_setting.setResizable(false);
			stage_setting.initModality(Modality.APPLICATION_MODAL);
			String url = "/background.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			stage_setting.getIcons().add(image); 
		}catch(Exception e) {
			e.printStackTrace();
		}


		//		button_generate.setVisible(false);
		button_generate.setDisable(true);
	}

	
	@FXML
	void act_button_einstellungen(ActionEvent event) {
		stage_setting.show();
	}
	

	public void updatePreview() {
		if(queue == false) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(thread!=null && process!=null) {
				if(process.isAlive()) {
					System.out.println("QUEUE ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo true");
					queue = true;
					waitThread();
				}
				else {
					generatePreviewXML();
					genImages();
				}
			}
			else {
				generatePreviewXML();
				genImages();
			}
		}
	}

	
	Thread thread_wait = null;
	public void waitThread() {

		if(thread_wait!=null)
			if(thread_wait.isAlive())
				thread_wait.interrupt();


		thread_wait = new Thread() {
			@Override
			public void run() {
				System.out.println("START WAIT THREAD ------------------------------------------------------- ");
				try {
					if(process != null) {
						while(process.isAlive()) {
							System.out.println("isAlive ###########################################");
							Thread.sleep(1000);
						}
						generatePreviewXML();
						genImages();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread_wait.start();
	}

	@FXML
	void act_button_generate(ActionEvent event) {
		updatePreview();
	}

	public void setButtonVisible(boolean selected) {
		button_generate.setDisable(!selected);
	}

	public xml getXMLCode() {
		return contr_settings.getXMLCode();
	}

	public void setXMLData(Element root) {
		contr_settings.setXMLData(root);
	}

	public void clear() {
		contr_settings.clear();

	}

}
