package model.utility;

import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.logger.ErrorLogger;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import java.io.File;
import java.util.List;

public class Util {
    public static File setPathForSave(Stage stage, File currentDirectory) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory for saving");
        File initialDirectory = resolveInitialDirectory(currentDirectory);
        if (initialDirectory != null) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }
        return directoryChooser.showDialog(stage);
    }

    public static File resolveInitialDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            return directory;
        }
        return null;
    }

    public static void showProgressBar(ProgressBar bar, PauseTransition timer) {
        bar.setProgress(1.0);
        timer.playFromStart();
    }

    public static Stage getStage(Control control) {
        return (Stage) control.getScene().getWindow();
    }

    public static MultimediaInfo getMetadata(File file) {
        if (file == null || !file.exists()) return null;
        try {
            return new MultimediaObject(file).getInfo();
        } catch (Exception e) {
            ErrorLogger.log(111, ErrorLogger.Level.ERROR, "Failed to get metadata", e);
            return null;
        }
    }

    public static boolean isSupportedMediaFile(File file, List<String> list) {
        String fileName = file.getName().toLowerCase();

        for (String ext : list) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


}
