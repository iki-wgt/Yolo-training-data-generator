package openCV;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class funktionen {

	public static Mat buffImg2Mat(BufferedImage image)  
	{  
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);  
		mat.put(0, 0, data);  

		return mat;  
	}  


	public static BufferedImage mat2BuffImg(Mat mat) {  
		BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);  
		WritableRaster raster = image.getRaster();  
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();  
		byte[] data = dataBuffer.getData();  
		mat.get(0, 0, data);  
		return image;  
	}  
	
	
	public static Image mat2Img(Mat mat) { 
		return SwingFXUtils.toFXImage(mat2BuffImg(mat), null);
	}
	
	public static Mat Img2Mat(Image image) {
		return buffImg2Mat(SwingFXUtils.fromFXImage(image, null));  
	}  
	
}



