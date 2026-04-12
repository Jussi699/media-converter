package converter;

import Model.Converter.ConverterImage;
import Model.Converter.DetermineType;
import Model.Logger.ErrorLogger;
import Model.WorkWithFiles.ClassSelect;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;

public class Controller {
    private static final int SUCCESS_MESSAGE_DURATION_SECONDS = 5;

    private static File image;
    private static File outputPath;
    private static String typeImage;
    private static int sizeIcoImage;
    private final PauseTransition hideSuccessMessageTimer =
            new PauseTransition(Duration.seconds(SUCCESS_MESSAGE_DURATION_SECONDS));

    @FXML
    private Label LabelSelectImageName;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private Pane mainPane;

    @FXML
    private Button btnSelectPhotoFile;

    @FXML
    private Label LabelConvertPhoto;

    @FXML
    private ToggleButton btnToPNG;

    @FXML
    private ToggleButton btnToJPEG;

    @FXML
    private Button btnSubmitConvert;

    @FXML
    private ProgressBar ProgressBarCompleteConvert;

    @FXML
    private Button btnChoiceDirForSaveImage;

    @FXML
    private ComboBox<String> ComboBoxIcoSize;

    @FXML
    private Label LabelSuccessConvert;

    @FXML
    public void initialize() {
        assert AnchorPane != null : "fx:id=\"AnchorPane\" was not injected!";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected!";
        assert btnSelectPhotoFile != null : "fx:id=\"btnSelectPhotoFile\" was not injected!";
        assert LabelConvertPhoto != null : "fx:id=\"LabelConvertPhoto\" was not injected!";
        assert btnToPNG != null : "fx:id=\"btnToPNG\" was not injected!";
        assert btnToJPEG != null : "fx:id=\"btnToJPEG\" was not injected!";
        assert btnSubmitConvert != null : "fx:id=\"btnSubmitConvert\" was not injected!";
        assert ProgressBarCompleteConvert != null : "fx:id=\"ProgressBarCompleteConvert\" was not injected!";
        assert btnChoiceDirForSaveImage != null : "fx:id=\"btnChoiceDirForSaveImage\" was not injected!";
        assert LabelSelectImageName != null : "fx:id=\"LabelSelectImageName\" was not injected!";
        assert ComboBoxIcoSize != null : "fx:id=\"ComboBoxIcoSize\" was not injected!";
        assert LabelSuccessConvert != null : "fx:id=\"LabelSuccessConvert\" was not injected!";

        outputPath = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
        LabelSuccessConvert.setVisible(false);
        LabelSuccessConvert.setManaged(false);
        LabelSuccessConvert.setText("");

        hideSuccessMessageTimer.setOnFinished(_ -> {
            LabelSuccessConvert.setVisible(false);
            LabelSuccessConvert.setManaged(false);
            LabelSuccessConvert.setText("");
        });

        ComboBoxIcoSize.getItems().addAll("16", "32", "64", "128");
        ComboBoxIcoSize.setDisable(false);
        ComboBoxIcoSize.setValue("to ICO");

        ComboBoxIcoSize.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("to ICO");
                    setStyle(
                            "-fx-background-color: LightGrey;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-alignment: center;" +
                                    "-fx-text-fill: black;"
                    );
                } else {
                    setText(item);
                    setStyle(
                            "-fx-background-color: #32CD32;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-border-radius: 10;" +
                                    "-fx-alignment: center;" +
                                    "-fx-text-fill: black;"
                    );
                }
            }
        });

        ComboBoxIcoSize.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setGraphic(null);
                    setStyle("-fx-alignment: center; -fx-text-fill: black;");
                }
            }
        });
    }

    @FXML
    private void ActionBtnToPNG(MouseEvent event) {
        btnToJPEG.setSelected(false);
        typeImage = "png";
        ComboBoxIcoSize.setDisable(false);
        ComboBoxIcoSize.setValue("to ICO");
    }

    @FXML
    private void ActionBtnToJPEG(MouseEvent event) {
        btnToPNG.setSelected(false);
        typeImage = "jpeg";
        ComboBoxIcoSize.setDisable(false);
        ComboBoxIcoSize.setValue("to ICO");
    }

    @FXML
    private void onChoiceIcoSize() {
        String selected = ComboBoxIcoSize.getValue();

        if (selected == null || selected.equals("to ICO")) {
            return;
        }

        sizeIcoImage = Integer.parseInt(selected);
        typeImage = "ico";
        btnToPNG.setSelected(false);
        btnToJPEG.setSelected(false);
    }

    @FXML
    public void ActionBtnSelectFile(MouseEvent mouseEvent) {
        ClassSelect selectImageFile = new ClassSelect();
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        image = selectImageFile.choiceFile(mouseEvent, stage);

        if (image != null) {
            LabelSelectImageName.setText(image.getName());
        }
    }

    @FXML
    public void btnChoiceDirForSaveImage(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        File selectedPath = ClassSelect.setPathForSave(mouseEvent, stage);
        if (selectedPath != null) {
            outputPath = selectedPath;
        }
    }

    private String getFileExtension(File file) {
        if (file == null) {
            return "";
        }

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String normalizeFormat(String format) {
        if (format == null) {
            return "";
        }

        String normalizedFormat = format.toLowerCase();
        if ("jpg".equals(normalizedFormat)) {
            return "jpeg";
        }

        return normalizedFormat;
    }

    private String getSourceImageFormat(File file) {
        try {
            return normalizeFormat(DetermineType.determineType(file));
        } catch (Exception e) {
            return normalizeFormat(getFileExtension(file));
        }
    }

    @FXML
    public void SubmitConvertAndDownload(MouseEvent event) {
        try {
            if (image == null || outputPath == null) {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Select image first.");
                return;
            }

            if (typeImage == null) {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Select photo format (PNG/JPEG/ICO).");
                return;
            }

            hideSuccessMessage();

            long startTime = System.currentTimeMillis();

            String inputExtension = getSourceImageFormat(image);
            String targetFormat = normalizeFormat(typeImage);

            if (inputExtension.equals(targetFormat)) {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                        "You cannot convert an image to the same format.");
                return;
            }

            if ("ico".equals(targetFormat)) {
                if (ComboBoxIcoSize.getValue() == null) {
                    ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Select ICO size.");
                    return;
                }

                ConverterImage.convertToIco(image, outputPath, sizeIcoImage);

            } else {
                if ("ico".equals(inputExtension)) {
                    ConverterImage.convertFromIco(image, outputPath, targetFormat);
                } else {
                    ConverterImage.convert(image, outputPath, targetFormat);
                }
            }

            File convertedFile = findLatestConvertedFile(outputPath, targetFormat, startTime);

            if (convertedFile != null && convertedFile.exists() && convertedFile.isFile() && convertedFile.length() > 0) {
                showSuccessMessage("Image converted successfully!");
            } else {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning",
                        "Conversion finished, but saved file was not found.");
            }

        } catch (IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Invalid image or format selected.");
            ErrorLogger.logError(104, "IllegalArgumentException during conversion", e);

        } catch (Exception e) {
            ErrorLogger.alertDialog(Alert.AlertType.ERROR, "Error", "Unexpected Error", "Something went wrong: " + e.getMessage());
            ErrorLogger.logError(999, "Unexpected error in SubmitConvertAndDownload", e);
        }
    }

    private File findLatestConvertedFile(File directory, String extension, long startTime) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles((dir, name) ->
                name.toLowerCase().endsWith("." + extension.toLowerCase()));

        if (files == null || files.length == 0) {
            return null;
        }

        File latestFile = null;

        for (File file : files) {
            if (file.isFile() && file.lastModified() >= startTime) {
                if (latestFile == null || file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }
        }

        return latestFile;
    }

    private void showSuccessMessage(String message) {
        LabelSuccessConvert.setText(message);
        LabelSuccessConvert.setManaged(true);
        LabelSuccessConvert.setVisible(true);
        hideSuccessMessageTimer.stop();
        hideSuccessMessageTimer.playFromStart();
    }

    private void hideSuccessMessage() {
        hideSuccessMessageTimer.stop();
        LabelSuccessConvert.setVisible(false);
        LabelSuccessConvert.setManaged(false);
        LabelSuccessConvert.setText("");
    }
}
