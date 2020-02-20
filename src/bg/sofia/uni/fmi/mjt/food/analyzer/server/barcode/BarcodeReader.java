package bg.sofia.uni.fmi.mjt.food.analyzer.server.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class BarcodeReader {

	private static String decodeBarcode(File barcodeImage) throws IOException {
		
		BufferedImage bufferedImage = ImageIO.read(barcodeImage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no barcode in the image");
			return null;
		}
	}

	public static String getBarcode(String path) {
		String decodedText = null;
		try {
			File file = new File(path);
			decodedText = decodeBarcode(file);
		} catch (IOException e) {
			System.out.println("Could not decode barcode, IOException :: " + e.getMessage());
		}
		return decodedText;
	}

}
