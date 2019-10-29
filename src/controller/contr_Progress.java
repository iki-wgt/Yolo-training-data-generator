package controller;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class contr_Progress {
	@FXML private ImageView img;
	@FXML private Label txt_number;
	@FXML private Label txt_size;
	@FXML private Label headline;
	@FXML private Label please_wait;
	@FXML private ProgressBar progress;
	@FXML private Label txt_time;
	@FXML private Button button_abr;
	@FXML private HBox label_time;

	Stage stage = null;
	Process process = null;
	contr_hauptmenue controll = null;
	Thread thread = null;
	long time_start = 0;

	//SCENE-BUILDER Funktionen ////////////////////////////////////////
	@FXML 
	void initialize(){
		progress.setProgress(0.0);
		txt_number.setText("0");
		txt_size.setText("0");
		progress.setAccessibleText("0");
		txt_time.setText("---");
		label_time.setVisible(false);

		try {
			String url = "/wait.png";
			Image image = new Image(getClass().getResourceAsStream(url));
			img.setImage(image);
			img.setPreserveRatio(true);
			img.setFitHeight(146);
		}catch(Exception e) {
			e.printStackTrace();
		};
	}

	@FXML
	void act_button_open(ActionEvent event) {
		File file = new File (controll.getVoreinstellungen().getOutputPath());
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void act_button_abr(ActionEvent event) {
		process.destroy();
		while(process.isAlive()) {
			try {
				Thread.sleep(10);
				System.out.println("is Alive!!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stage.close();
	}

	public void setNumber(int num) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				txt_number.setText(""+num);
			}
		});
	}

	private void calcEstimate(int index, int maxIndex) {
		long time_end = System.currentTimeMillis();
		double dif = time_end - time_start;
		int tmp = (int)(dif / (index-1));
		int time_total = (int)((maxIndex - (index-1)) * tmp);
		Duration diff = new Duration(time_total);
		System.out.println("calcEstimate: index: "+index+" maxIndex: "+maxIndex+" dif: "+dif);
		
		String hms = String.format("%d:%02d:%02d", 
                (int)diff.toHours(),
                (int)diff.toMinutes(), 
                (int)diff.toSeconds()%60);
		
		label_time.setVisible(true);
		
		Platform.runLater(new Runnable() {
			@Override public void run() {
				txt_time.setText(""+hms);
			}
		});
	}

	//Eigene Funktionen ///////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
	public void init(Stage stage, contr_hauptmenue controll) {
		//		this.process = process;
		this.stage = stage;
		this.controll = controll;

		int size = controll.getSumme();
		txt_size.setText(""+size);
		headline.setText("Images get generated");
		button_abr.setText("  Abort  ");
		label_time.setVisible(false);
		please_wait.setVisible(false);




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
				String[] cmd = new String[5];
				cmd[0] = "python3";
				cmd[1] = "Python/main.py";
				cmd[2] = "data.xml";
				cmd[3] = "-yv";
				cmd[4] = ""+controll.getVoreinstellungen().getYoloVersion();
				Runtime rt = Runtime.getRuntime();
				try {
					process = rt.exec(cmd);
				} catch (IOException e) {
					cmd[0] = "python";
					System.out.println("Can't find program 'python3', try 'python'");
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
				try {
					//					BufferedReader bfr_std = new BufferedReader(new InputStreamReader(process.getInputStream()));
					BufferedReader bfr_error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

					String line = "";
					//					while((line = bfr_std.readLine()) != null) {
					//						System.out.println(line);
					//					}
					System.out.println("\nOUTPUT FROM PYHTON-FILE:");
					int number = 0;
					while((line = bfr_error.readLine()) != null) {
						System.err.println(line);
						if(line.contains("Process filter chain number ")) {
							int index = line.indexOf("chain number ");
							try {
								number = Integer.parseInt(line.substring(index+13).replace(":", ""));
								if(number==1)
									time_start = System.currentTimeMillis();
								setNumber(number);
								Thread.sleep(0, 1);
								progress.setProgress(number/(double)size);
								if(number%7 == 0) {
									System.out.println("AUFRAUF");
									calcEstimate(number, size);
								}
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
					}

					Platform.runLater(new Runnable() {
						@Override public void run() {
							button_abr.setText("  Close  ");
							headline.setText("Images successfully generated!");
							label_time.setVisible(false);
							please_wait.setVisible(false);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Error");
					alert2.setHeaderText("Error while starting the Python script");
					alert2.showAndWait();
				}
			};
		};

		thread.start();

	}

}
