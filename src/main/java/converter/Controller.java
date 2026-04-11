package converter;

import Model.Converter.ConverterImage;
import Model.Logger.ErrorLogger;
import Model.WorkWithFiles.ClassSelect;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class Controller {
    private static File image;
    private static File outputPath ;
    private static String typeImage;
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
    private ToggleButton btnToICO;

    @FXML
    private Button btnSubmitConvert;

    @FXML
    private ProgressBar ProgressBarCompleteConvert;

    @FXML
    private Button btnChoiceDirForSaveImage;

    @FXML
    public void initialize() {
        assert AnchorPane != null : "fx:id=\"AnchorPane\" was not injected!";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected!";
        assert btnSelectPhotoFile != null : "fx:id=\"btnSelectPhotoFile\" was not injected!";
        assert LabelConvertPhoto != null : "fx:id=\"LabelConvertPhoto\" was not injected!";
        assert btnToPNG != null : "fx:id=\"btnToPNG\" was not injected!";
        assert btnToJPEG != null : "fx:id=\"btnToJPEG\" was not injected!";
        assert btnToICO != null : "fx:id=\"btnToICO\" was not injected!";
        assert btnSubmitConvert != null : "fx:id=\"btnSubmitConvert\" was not injected!";
        assert ProgressBarCompleteConvert != null : "fx:id=\"ProgressBarCompleteConvert\" was not injected!";
        assert btnChoiceDirForSaveImage != null : "fx:id=\"btnChoiceDirForSaveImage\" was not injected!";
        assert LabelSelectImageName != null : "fx:id=\"LabelSelectImageName\" was not injected!";

        outputPath = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
    }

    @FXML
    private void ActionBtnToPNG(MouseEvent event) {
        btnToICO.setSelected(false);
        btnToJPEG.setSelected(false);
        typeImage = "png";
    }

    @FXML
    private void ActionBtnToICO(MouseEvent event) {
        btnToJPEG.setSelected(false);
        btnToPNG.setSelected(false);
        typeImage = "ico";
    }

    @FXML
    private void ActionBtnToJPEG(MouseEvent event) {
        btnToICO.setSelected(false);
        btnToPNG.setSelected(false);
        typeImage = "jpeg";
    }

    @FXML
    public void ActionBtnSelectFile(MouseEvent mouseEvent) {
        ClassSelect selectImageFile = new ClassSelect();
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        image = selectImageFile.choiceFile(mouseEvent, stage);
        LabelSelectImageName.setText(image.getName());

    }

    public void btnChoiceDirForSaveImage(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        outputPath  = ClassSelect.setPathForSave(mouseEvent, stage);
    }


    public void SubmitConvertAndDownload(MouseEvent event) {
        try {
            if (image == null || outputPath == null) {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Select image and save directory first.");
                return;
            }

            if (!btnToICO.isSelected() && typeImage == null) {
                ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Select photo format (PNG/JPEG/ICO).");
                return;
            }

            if (btnToICO.isSelected()) {
                ConverterImage.convertToIco(image, outputPath, typeImage);
            } else {
                ConverterImage.convert(image, outputPath, typeImage);
            }
        } catch (IllegalArgumentException e) {
            ErrorLogger.alertDialog(Alert.AlertType.WARNING, "Warning", "Warning", "Invalid image or format selected.");
            ErrorLogger.logError(102, "IllegalArgumentException during conversion", e);

        } catch (Exception e) {
            ErrorLogger.alertDialog(Alert.AlertType.ERROR, "Error", "Unexpected Error", "Something went wrong: " + e.getMessage());
            ErrorLogger.logError(999, "Unexpected error in SubmitConvertAndDownload", e);
        }
    }
}
