package Model.Converter;

import Model.Logger.ErrorLogger;
import javafx.scene.control.Alert;
import net.ifok.image.image4j.codec.ico.ICOEncoder;
import net.ifok.image.image4j.codec.ico.ICODecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ConverterImage {
    private static final Random random = new Random();

    public static void convert(File image, File pathForSave, String typeFile) {
        try {
            BufferedImage bufImage = ImageIO.read(image);
            if (bufImage == null) {
                throw new IOException("Unable to read image");
            }

            String outputFormat = normalizeOutputFormat(typeFile);
            BufferedImage preparedImage = prepareImageForFormat(bufImage, outputFormat);
            String nameFile = image.getName().substring(0, image.getName().lastIndexOf(".")) + "_converted_" + random.nextInt() + "." + typeFile;
            String outputPath = pathForSave.getAbsolutePath() + File.separator + nameFile;
            File outputImage = new File(outputPath);

            boolean written = ImageIO.write(preparedImage, outputFormat, outputImage);
            if (!written) {
                throw new IOException("Unsupported output format: " + typeFile);
            }
        } catch (IOException | IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "You have not selected an image.");
            ErrorLogger.logError(101, "Argument is null, maybe not selected image! | " +
                    "In class: " + DetermineType.class.getName() +
                    "In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        }
    }

    public static void convertToIco(File image, File pathForSave, int size) {
        try {
            BufferedImage bufImage = ImageIO.read(image);
            if (bufImage == null) {
                throw new IOException("Unable to read image");
            }

            if (size <= 0) {
                throw new IllegalArgumentException("Size must be greater than 0");
            }

            BufferedImage resized = resizeImage(bufImage, size, size);

            String nameFile = image.getName().substring(0, image.getName().lastIndexOf(".")) + "_converted_" + random.nextInt() +".ico";
            String outputPath = pathForSave.getAbsolutePath() + File.separator + nameFile;
            File outputImage = new File(outputPath);

            ICOEncoder.write(resized, outputImage);

        } catch (IOException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                    "Maybe you have not selected an image.");
            ErrorLogger.logError(105,
                    "ICO convert error | In class: " + ConverterImage.class.getName() +
                            " In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        } catch (IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                    "Wrong ico size.");
            ErrorLogger.logError(106,
                    "ICO convert error | In class: " + ConverterImage.class.getName() +
                            " In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        }
    }

    public static void convertFromIco(File image, File pathForSave, String typeFile) {
        try {
            List<BufferedImage> images = ICODecoder.read(image);
            if (images == null || images.isEmpty()) {
                throw new IOException("ICO file does not contain images");
            }

            String outputFormat = normalizeOutputFormat(typeFile);
            BufferedImage bestImage = prepareImageForFormat(getBestImage(images), outputFormat);

            String nameFile = image.getName().substring(0, image.getName().lastIndexOf(".")) + "_converted_" + random.nextInt() + "." + typeFile;
            String outputPath = pathForSave.getAbsolutePath() + File.separator + nameFile;
            File outputImage = new File(outputPath);

            boolean written = ImageIO.write(bestImage, outputFormat, outputImage);
            if (!written) {
                throw new IOException("Unsupported output format: " + typeFile);
            }

        } catch (IOException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                    "Maybe ICO file was not selected or cannot be converted.");
            ErrorLogger.logError(105,
                    "ICO decode/convert error | In class: " + ConverterImage.class.getName() +
                            " In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        } catch (IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                    "Wrong arguments for ICO conversion.");
            ErrorLogger.logError(106,
                    "ICO decode/convert error | In class: " + ConverterImage.class.getName() +
                            " In Method: " + ErrorLogger.getCurrentMethodName(), e);
            return;
        }
    }

    private static String normalizeOutputFormat(String typeFile) {
        if (typeFile == null) {
            throw new IllegalArgumentException("Output format is null");
        }

        return "jpeg".equalsIgnoreCase(typeFile) ? "jpg" : typeFile.toLowerCase();
    }

    private static BufferedImage prepareImageForFormat(BufferedImage source, String format) {
        if (!"jpg".equals(format) && !"jpeg".equals(format)) {
            return source;
        }

        BufferedImage rgbImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgbImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();
        return rgbImage;
    }

    private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();

        return resized;
    }

    private static BufferedImage getBestImage(List<BufferedImage> images) {
        BufferedImage best = images.getFirst();

        for (BufferedImage img : images) {
            if (img.getWidth() * img.getHeight() > best.getWidth() * best.getHeight()) {
                best = img;
            }
        }

        return best;
    }
}

