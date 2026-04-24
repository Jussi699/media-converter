package converter;

import javafx.animation.PauseTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.compressorImage.Compressor;
import model.compressorImage.CompressionResult;
import model.converterImage.UsefulMethods;
import model.logger.ErrorLogger;
import model.select.SelectFile;
import model.utility.DetermineType;
import model.utility.Item;
import viewHelp.Alerts;
import viewHelp.ComboBoxes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

import static model.utility.Util.getStage;
import static model.utility.Util.setPathForSave;
import static viewHelp.Message.*;
import static model.converterImage.UsefulMethods.readPreviewImage;

public class CompressorImageViewController {
    private static final int SUCCESS_MESSAGE_DURATION_SECONDS = 5;
    private static final String DEFAULT_FILE_TEXT = "Selected file: none";
    private static final double PREVIEW_PADDING = 5.0;

    private float scale;
    private float quality;
    private File image;
    private File outputPath;
    private File compressedImage;
    private String typeImage;

    private final PauseTransition hideSuccessMessageTimer =
            new PauseTransition(Duration.seconds(SUCCESS_MESSAGE_DURATION_SECONDS));

    @FXML private ComboBox<Item> comboBoxOutputQuality;
    @FXML private ComboBox<Item> comboBoxScaleImage;
    @FXML private Button btnSelectPhotoFile;
    @FXML private Button btnChoiceDirForSaveImage;
    @FXML private Label labelSuccessConvert;
    @FXML private Label labelSelectImageName;
    @FXML private ScrollPane scrollPanePhotoOriginal;
    @FXML private ScrollPane scrollPanePhotoCompressed;
    @FXML private StackPane imageContainerOriginal;
    @FXML private StackPane imageContainerCompressed;
    @FXML private ImageView imageViewPhotoOriginal;
    @FXML private ImageView imageViewPhotoCompressed;

    @FXML
    public void initialize() {
        comboBoxOutputQuality.setValue(new Item(-1, "Quality"));
        comboBoxScaleImage.setValue(new Item(-1, "Scale"));

        outputPath = Paths.get(System.getProperty("user.home"), "Desktop").toFile();

        setupClearMessageTimer(labelSuccessConvert, hideSuccessMessageTimer);

        labelSuccessConvert.setVisible(false);
        labelSuccessConvert.setText("Compression status");
        labelSelectImageName.setText(DEFAULT_FILE_TEXT);

        scrollPanePhotoOriginal.getStyleClass().add("scroll-pane-image");
        scrollPanePhotoCompressed.getStyleClass().add("scroll-pane-image");

        configurePreview(scrollPanePhotoOriginal, imageContainerOriginal, imageViewPhotoOriginal);
        configurePreview(scrollPanePhotoCompressed, imageContainerCompressed, imageViewPhotoCompressed);

        ComboBoxes.setupComboBox(comboBoxOutputQuality, Item::title);
        ComboBoxes.setupComboBox(comboBoxScaleImage, Item::title);

        isPressedReset();

        comboBoxOutputQuality.getItems().addAll(
                new Item(-1f, "Quality"),
                new Item(1.0f, "100%"), new Item(0.9f, "90%"),
                new Item(0.85f, "85%"), new Item(0.75f, "75%"),
                new Item(0.6f, "60%"), new Item(0.5f, "50%"),
                new Item(0.25f, "25%"), new Item(0.15f, "15%"),
                new Item(0.10f, "10%"), new Item(0.05f, "5%")
        );

        comboBoxScaleImage.getItems().addAll(
                new Item(-1f, "Scale"),
                new Item(1.0f, "100%"), new Item(0.9f, "90%"),
                new Item(0.85f, "85%"), new Item(0.75f, "75%"),
                new Item(0.6f, "60%"), new Item(0.5f, "50%"),
                new Item(0.25f, "25%"), new Item(0.15f, "15%"),
                new Item(0.10f, "10%"), new Item(0.05f, "5%")
        );
    }
    
    public void ActionBtnSelectFile() {
        SelectFile selectImageFile = new SelectFile();
        Stage stage = (Stage) btnSelectPhotoFile.getScene().getWindow();
        image = selectImageFile.choiceFile(stage,
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp",
                        "*.tiff", "*.tif", "*.bmp", "*.ppm", "*.pgm", "*.pam", "*.jpe", "*.svg"),
                "Choice image"
        );

        if (image == null) return;

        ErrorLogger.info("User selected file (image): " + image.getAbsolutePath());
        typeImage = DetermineType.determineFormat(image);
        labelSelectImageName.setText("Select image: " + image.getName());
        loadPreview(image, imageViewPhotoOriginal, scrollPanePhotoOriginal, imageContainerOriginal);
        clearCompressedPreview();
    }

    public void btnChoiceDirForSaveImage() {
        Stage stage = getStage(btnChoiceDirForSaveImage);
        File selectedPath = setPathForSave(stage, outputPath);
        if (selectedPath != null) {
            outputPath = selectedPath;
        }
    }

    public void submitCompressAndDownload() {
        if (image == null || outputPath == null) {
            Alerts.alertDialog(Alert.AlertType.WARNING, "Warning", "File missing!", "Select image first.");
            return;
        }

        if (typeImage == null) {
            Alerts.alertDialog(Alert.AlertType.WARNING, "Warning", "File missing!", "Select photo format (PNG/JPEG/ICO/WEBP).");
            return;
        }

        UsefulMethods usefulMethods = new UsefulMethods();
        String targetFormat = usefulMethods.normalizeFormat(typeImage);
        boolean isSvg = "svg".equalsIgnoreCase(targetFormat);
        boolean qualityRequired = isQualityRequired(targetFormat);

        if (!isSvg && scale == -1) {
            Alerts.alertDialog(Alert.AlertType.WARNING, "Setting missing", "Missing settings", "First select Scale!");
            return;
        }

        if (qualityRequired && quality == -1) {
            Alerts.alertDialog(Alert.AlertType.WARNING, "Setting missing", "Missing settings", "First select Quality!");
            return;
        }

        try {
            hideSuccessMessage(labelSuccessConvert, hideSuccessMessageTimer);
            CompressionResult compressionResult;

            if ("svg".equals(targetFormat)) {
                compressionResult = Compressor.compressToSvgz(image, outputPath);
            } else {
                compressionResult = Compressor.compressorStandardImage(image, outputPath, scale, quality);
            }

            if (compressionResult == null) {
                showErrorMessage(labelSuccessConvert, "So close, yet no success", hideSuccessMessageTimer);
                ErrorLogger.warn("Compressed image has null! " + getClass().getName());
                return;
            }

            compressedImage = compressionResult.outputFile();
            if (!compressionResult.sizeReduced()) {
                compressedImage = null;
                String warningMessage = String.format(Locale.US,
                        "Compression skipped: file would not shrink (%s -> %s)",
                        formatBytes(compressionResult.originalSizeBytes()),
                        formatBytes(compressionResult.compressedSizeBytes()));
                showErrorMessage(labelSuccessConvert, warningMessage, hideSuccessMessageTimer);
                Alerts.alertDialog(Alert.AlertType.INFORMATION, "Information", "Compression skipped",
                        "The compressed file would be larger than the original, so it was not kept.");
                clearCompressedPreview();
                ErrorLogger.info("Compression skipped because output is larger or equal to source: " + image.getName());
                return;
            }

            if (compressedImage.exists() && compressedImage.isFile() && compressedImage.length() > 0) {
                loadPreview(compressedImage, imageViewPhotoCompressed, scrollPanePhotoCompressed, imageContainerCompressed);
                showSuccessText(labelSuccessConvert, buildSuccessMessage(compressionResult), hideSuccessMessageTimer);
                ErrorLogger.info("Compression completed: " + compressedImage.getName());
            } else {
                ErrorLogger.warn("Compression finished but file not found or empty: " + compressedImage.getName());
                showErrorMessage(labelSuccessConvert, "Compression Failed: File missing", hideSuccessMessageTimer);
                Alerts.alertDialog(Alert.AlertType.WARNING, "Warning", "Missing File",
                        "Compression finished, but saved file was not found.");
            }

        } catch (IllegalArgumentException e) {
            ErrorLogger.log(118, ErrorLogger.Level.WARN, "Invalid parameters for compression", e);
            showErrorMessage(labelSuccessConvert, "Error: Invalid parameters", hideSuccessMessageTimer);
            Alerts.alertDialog(Alert.AlertType.WARNING, "Warning", "Invalid Parameters", e.getMessage());
        } catch (IOException e) {
            ErrorLogger.log(105, ErrorLogger.Level.ERROR, "IO Error during compression", e);
            showErrorMessage(labelSuccessConvert, "Error: " + e.getMessage(), hideSuccessMessageTimer);
            Alerts.alertDialog(Alert.AlertType.ERROR, "Error", "Compression Failed", e.getMessage());
        } catch (Exception e) {
            ErrorLogger.log(1001, ErrorLogger.Level.ERROR, "Unexpected error during conversion", e);
            showErrorMessage(labelSuccessConvert, "System Error: " + e.getMessage(), hideSuccessMessageTimer);
            Alerts.alertDialog(Alert.AlertType.ERROR, "Error", "System Error", "Something went wrong: " + e.getMessage());
        }
    }

    public void isPressedReset() {
        comboBoxOutputQuality.setValue(new Item(-1, "Quality"));
        comboBoxScaleImage.setValue(new Item(-1, "Scale"));

        image = null;
        compressedImage = null;
        typeImage = null;
        quality = -1;
        scale = -1;

        labelSelectImageName.setText(DEFAULT_FILE_TEXT);
        clearPreview(imageViewPhotoOriginal, imageContainerOriginal);
        clearCompressedPreview();
        hideSuccessMessage(labelSuccessConvert, hideSuccessMessageTimer);
    }

    public void onChoiceScaleImage() {
        Item selectedItem = comboBoxScaleImage.getValue();
        scale = (selectedItem != null) ? selectedItem.id() : -1;
        ErrorLogger.info("User select scale: " + scale);
    }

    public void onChoiceOutputQuality() {
        Item selectedItem = comboBoxOutputQuality.getValue();
        quality = (selectedItem != null) ? selectedItem.id() : -1;
        ErrorLogger.info("User select quality: " + quality);
    }

    private String buildSuccessMessage(CompressionResult result) {
        return String.format(Locale.US,
                "Compressed to %s | saved %.1f%% (%s -> %s)",
                result.format().toUpperCase(Locale.ROOT),
                result.savedPercent(),
                formatBytes(result.originalSizeBytes()),
                formatBytes(result.compressedSizeBytes()));
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format(Locale.US, "%.1f KB", bytes / 1024.0);
        }
        return String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private boolean isQualityRequired(String format) {
        return "jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format) || "webp".equalsIgnoreCase(format);
    }

    private void configurePreview(ScrollPane scrollPane, StackPane container, ImageView imageView) {
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        container.setManaged(true);

        scrollPane.viewportBoundsProperty().addListener((_, _, _) ->
                updatePreviewSize(scrollPane, container, imageView));
        imageView.imageProperty().addListener((_, _, _) ->
                updatePreviewSize(scrollPane, container, imageView));
    }

    private void loadPreview(File file, ImageView imageView, ScrollPane scrollPane, StackPane container) {
        try {
            BufferedImage bufferedImage = readPreviewImage(file);
            if (bufferedImage == null) {
                clearPreview(imageView, container);
                return;
            }

            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(fxImage);
            updatePreviewSize(scrollPane, container, imageView);
        } catch (IOException e) {
            ErrorLogger.log(107, ErrorLogger.Level.ERROR, "IO | File error while loading preview", e);
            clearPreview(imageView, container);
        }
    }

    private void updatePreviewSize(ScrollPane scrollPane, StackPane container, ImageView imageView) {
        if (imageView.getImage() == null) {
            return;
        }

        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return;
        }

        double fitWidth = Math.max(100, viewportWidth - PREVIEW_PADDING);
        double fitHeight = Math.max(100, viewportHeight - PREVIEW_PADDING);

        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);

        container.setMinWidth(fitWidth);
        container.setMinHeight(fitHeight);
        container.setPrefWidth(fitWidth);
        container.setPrefHeight(fitHeight);
    }

    private void clearCompressedPreview() {
        clearPreview(imageViewPhotoCompressed, imageContainerCompressed);
    }

    private void clearPreview(ImageView imageView, StackPane container) {
        imageView.setImage(null);
        container.setPrefWidth(250.0);
        container.setPrefHeight(180.0);
        container.setMinWidth(250.0);
        container.setMinHeight(180.0);
    }
}
