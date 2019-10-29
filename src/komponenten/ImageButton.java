package komponenten;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import Data.data_class;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ImageButton extends VBox{

	boolean selected = false;
	ImageView imageView = null;
	boolean b_dragg = false;
	boolean b_dragg_save = false;
	data_class data = null;
	data_class data_from_file = null;
	File file;
	Label label = new Label("");
	
	
	public ImageButton(File file) {
		init(file, null);
	}

	public ImageButton(File file, data_class data) {
		init(file, data);
	}
	
	public byte[] getByte() {
		System.out.println("start getByte");
		System.out.println(imageView.getImage() == null);
		System.out.println("image sizes: "+imageView.getImage().getWidth()+ " h: "+imageView.getImage().getHeight()+" (105, 115)");
		byte [] b = null;
		try {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "png", byteOutput);
		b  = byteOutput.toByteArray();
		byteOutput.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return b;
	}

	private void init(File file, data_class data) {
		this.file = file;
		this.setMinSize(105, 115);
		this.setMaxSize(105, 115);
		this.setPrefSize(105, 115);
		FileInputStream input = null;

		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(4.0);
		dropShadow.setOffsetX(2.0);
		dropShadow.setOffsetY(2.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));  

		try {	
			input = new FileInputStream(file.getAbsolutePath());
			Image img = new Image(input, 90, 90, true, false);
			input.close();
			
//			Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
//			if(readers.hasNext()) {
//				ImageReader reader = readers.next(); 
//				reader.setInput(input);
//				IIOImage image = reader.readAll(0, null);
//
//				try {
//					int index = Integer.parseInt(getTextEntry(image.getMetadata(), "class"));
//					String name = getTextEntry(image.getMetadata(), "classname");
//					data_from_file = new data_class(index, name, 0);
//					System.out.println("Es hat funktioniert!: "+data_from_file);
//				}catch(Exception e) {
//					data_from_file = null;
//				}
//			}

			VBox box = new VBox();
			//			this.setStyle("-fx-border-color: #00AEAD");
			imageView = new ImageView(img);
			imageView.setPreserveRatio(true);
//			imageView.setFitHeight(90);
//			imageView.setFitWidth(90);
			//			box.setStyle("-fx-background-color: white");
			box.getChildren().add(imageView);
			box.setMinSize(90, 90);
			box.setMaxSize(90, 90);
			box.setPrefSize(90, 90);
			this.setEffect(dropShadow);
			VBox.setMargin(box, new Insets(0,2,0,2));
			this.getChildren().add(box);
			label.setText(file.getName());
			this.getChildren().add(label);
			this.setAlignment(Pos.CENTER);
			input.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				input.close();
			} catch (Exception e2) {
			}
		}

		this.setStyle("-fx-background-color: white;-fx-border-color: white");
		setData(data);
	}

//	public void save(File out) throws IIOInvalidTreeException {
//		if(data != null) {
//
//			ImageInputStream input = null;
//			ImageOutputStream output = null;
//			try {
//				input = ImageIO.createImageInputStream(file);
//				output = ImageIO.createImageOutputStream(out);
//
//				Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
//				ImageReader reader = readers.next(); // TODO: Validate that there are readers
//
//				reader.setInput(input);
//				IIOImage image = reader.readAll(0, null);
//
//				addTextEntry(image.getMetadata(), "class", ""+data.getIndex());
//				addTextEntry(image.getMetadata(), "classname", data.getName());
//
//				ImageWriter writer = ImageIO.getImageWriter(reader); // TODO: Validate that there are writers
//				writer.setOutput(output);
//				writer.write(image);
//
//				input.close();
//				output.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				try {
//					input.close();
//					output.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//	}


//	private static void addTextEntry(final IIOMetadata metadata, final String key, final String value) throws IIOInvalidTreeException {
//		IIOMetadataNode textEntry = new IIOMetadataNode("TextEntry");
//		textEntry.setAttribute("keyword", key);
//		textEntry.setAttribute("value", value);
//
//		IIOMetadataNode text = new IIOMetadataNode("Text");
//		text.appendChild(textEntry);
//
//		IIOMetadataNode root = new IIOMetadataNode(IIOMetadataFormatImpl.standardMetadataFormatName);
//		root.appendChild(text);
//
//		metadata.mergeTree(IIOMetadataFormatImpl.standardMetadataFormatName, root);
//	}
//
//	private static String getTextEntry(final IIOMetadata metadata, final String key) {
//		IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
//		NodeList entries = root.getElementsByTagName("TextEntry");
//
//		for (int i = 0; i < entries.getLength(); i++) {
//			IIOMetadataNode node = (IIOMetadataNode) entries.item(i);
//			if (node.getAttribute("keyword").equals(key)) {
//				return node.getAttribute("value");
//			}
//		}
//
//		return null;
//	}


	public void setData(data_class data) {
		this.data = data;

		if(data == null) {
			label.setText(file.getName());
		}
		else {
			label.setText(""+data.getIndex()+" "+data.getName());
		}
	}
	
	public void refreshLabelData() {
		if(data == null) {
			label.setText(file.getName());
		}
		else {
			label.setText(""+data.getIndex()+" "+data.getName());
		}
	}

	public data_class getData() {
		return data;
	}

	public void hover(boolean b) {
		if(selected==false) {
			if(b)
				setStyle("-fx-background-color: #E5F3FF;-fx-border-color: white");
			else
				setStyle("-fx-background-color: white;-fx-border-color: white");
		}
	}

	public void enableDrag(boolean b) {
		this.b_dragg = b;
		b_dragg_save = selected;
	}


	public void toggleSelection(boolean isinSelectionFild) {

		if(isinSelectionFild) {
			if(b_dragg_save) {
				setSelected(false);
			}
			else {
				setSelected(true);
			}
		}
		else {
			setSelected(b_dragg_save);
		}
	}

	public void toggleSelection() {
		if(selected)
			setSelected(false);
		else
			setSelected(true);
	}

	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean b) {
		if(b==false) {
			this.setStyle("-fx-background-color: white;-fx-border-color: white");
		}
		else {
			this.setStyle("-fx-background-color: #CCE8FF;-fx-border-color: #99D1FF");
		}
		selected = b;
	}



	public ImageView getView() {
		return imageView;
	}

	public void refresh() {
		if(data == null) {
			label.setText(file.getName());
		}
		else {
			label.setText(""+data.getIndex()+" "+data.getName());
		}
	}

	public File getFile() {
		return file;
	}

}
