package it.unibo.tuprolog.ui.gui;

import it.unibo.tuprolog.Info;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelpUtils {
    private static final String TUPROLOGVERSION = Info.VERSION;

    public HelpUtils() {
    }

    public void aboutHandler(ActionEvent e, String style) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(TuPrologPane.class.getResourceAsStream("2prologsticker.png")));
        alert.getDialogPane().setStyle(style);
        alert.setTitle("About");
        alert.setHeaderText("TuProlog current version is:");
        alert.setContentText(TUPROLOGVERSION);
        alert.showAndWait();
    }
}
