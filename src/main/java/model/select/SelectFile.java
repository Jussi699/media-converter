package model.select;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static model.utility.Util.*;

public class SelectFile extends AbstractSelectFile {
    private final FileChooser fileChooser = new FileChooser();

    @Override
    public File choiceFile(Stage stage, FileChooser.ExtensionFilter filter, String title) {
        fileChooser.setTitle(title);
        
        File initialDirectory = resolveInitialDirectory(getSavedInputPath());
        if (initialDirectory != null) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        fileChooser.getExtensionFilters().setAll(filter);

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            saveInputPath(selectedFile);
        }
        
        return selectedFile;
    }
}
