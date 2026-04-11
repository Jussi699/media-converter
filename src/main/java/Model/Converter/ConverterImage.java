package Model.Converter;

import Model.Logger.ErrorLogger;
import javafx.scene.control.Alert;
import net.ifok.image.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConverterImage {
    public static void convert(File image, File pathForSave, String typeFile) {
        try {
            String type = DetermineType.determineType(image);

            BufferedImage bufImage = ImageIO.read(image);
            if (bufImage == null) {
                throw new IOException("Unable to read image");
            }

            String nameFile = image.getName().substring(0, image.getName().indexOf(".")) + "_converted.";
            String outputPath = pathForSave.getAbsolutePath() + File.separator + nameFile + typeFile;
            File outputImage = new File(outputPath);

            ImageIO.write(bufImage, typeFile, outputImage);
        } catch (IOException  | IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "You have not selected an image.");
            ErrorLogger.logError(101, "Argument is null, maybe not selected image! | " +
                    "In class: " + DetermineType.class.getName() +
                    "In Method: " + ErrorLogger.getCurrentMethodName() , e);
            return;
        }
    }

    public static void convertToIco(File image, File pathForSave, String typeFile) {
        try {
            BufferedImage bufImage = ImageIO.read(image);
            String nameFile = image.getName().substring(0, image.getName().indexOf(".")) + "_converted.";
            String outputPath = pathForSave.getAbsolutePath() + File.separator + nameFile + typeFile;
            File outputImage = new File(outputPath);

            ICOEncoder.write(bufImage, outputImage);
        } catch (IOException | IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "You have not selected an image.");
            ErrorLogger.logError(104, "ICO convert error | In class: " + ConverterImage.class.getName() + " In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        }
    }
}
