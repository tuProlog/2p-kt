package it.unibo.tuprolog.ui.gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WindowUtils {
    public WindowUtils() {
    }

    public void changeToDarkColorHandler(ActionEvent e, Scene scene, Pane pane,
                                         ArrayList<String> darkcodearea, Map<Tab, CodeArea> openTabs,
                                         String style, SyntaxColoring syntaxColoring, TextArea solutionText,
                                         TextArea bindingsText, TextArea outputText, TextArea exceptionText,
                                         TextField queryField, TextField timeoutField) {
        scene.getStylesheets().remove(TuPrologPane.class.getResource("java-keywords-light.css").toExternalForm());
        scene.getStylesheets().add(TuPrologPane.class.getResource("java-keywords-dark.css").toExternalForm());
        scene.getStylesheets().remove(TuPrologPane.class.getResource("light-code-area.css").toExternalForm());
        scene.getStylesheets().add(TuPrologPane.class.getResource("dark-code-area.css").toExternalForm());
        darkcodearea.add("text");
        darkcodearea.add("caret");
        darkcodearea.add("lineno");
        for (CodeArea c : openTabs.values()) {
            //c.setStyle(0,c.getLength(), Collections.singleton("-fx-fill: white !important;"));
            c.setBackground(new Background(new BackgroundFill(Color.valueOf("#202020"), CornerRadii.EMPTY, Insets.EMPTY)));
            c.setLineHighlighterFill(Color.valueOf("#303030"));
            //c.setStyle( "-fx-fill: #FFFFFF;");
            c.setStyle(0, c.getLength(), darkcodearea);
            syntaxColoring = new SyntaxColoring(c);
        }
        pane.setStyle("-fx-base:black;");
        style = "-fx-base:black;";
        solutionText.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        bindingsText.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        outputText.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        //inputText.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        exceptionText.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        queryField.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
        timeoutField.setStyle("-fx-text-fill: white; -fx-control-inner-background:#202020;");
    }

    public void changeToLightColorHandler(ActionEvent actionEvent, Scene scene, Pane pane,
                                          ArrayList<String> lightcodearea, Map<Tab, CodeArea> openTabs,
                                          String style, SyntaxColoring syntaxColoring, TextArea solutionText,
                                          TextArea bindingsText, TextArea outputText, TextArea exceptionText,
                                          TextField queryField, TextField timeoutField) {
        scene.getStylesheets().remove(TuPrologPane.class.getResource("java-keywords-dark.css").toExternalForm());
        scene.getStylesheets().add(TuPrologPane.class.getResource("java-keywords-light.css").toExternalForm());

        scene.getStylesheets().remove(TuPrologPane.class.getResource("dark-code-area.css").toExternalForm());
        scene.getStylesheets().add(TuPrologPane.class.getResource("light-code-area.css").toExternalForm());
        pane.setStyle("");
        style = "";
        lightcodearea.add("text");
        lightcodearea.add("caret");
        lightcodearea.add("lineno");
        for (CodeArea c : openTabs.values()) {
            c.setBackground(new Background(new BackgroundFill(Color.valueOf("#F0F0F0"), CornerRadii.EMPTY, Insets.EMPTY)));
            c.setStyle(0, c.getLength(), lightcodearea);
            c.setLineHighlighterFill(Color.valueOf("#E0E0E0"));
            syntaxColoring = new SyntaxColoring(c);
        }
        for (Tab b : openTabs.keySet()) {
            b.setStyle("");
        }
        solutionText.setStyle("-fx-text-fill: black;");
        bindingsText.setStyle("-fx-text-fill: black;");
        outputText.setStyle("-fx-text-fill: black;");
        //inputText.setStyle("-fx-text-fill: black;");
        exceptionText.setStyle("-fx-text-fill: black;");
        queryField.setStyle("-fx-text-fill: black;");
        timeoutField.setStyle("-fx-text-fill: black;");
    }

    public void fontSizeHandler(ActionEvent e, int fsize, Map<Tab, CodeArea> openTabs) {
        List<String> choices = new ArrayList<>();
        choices.add("Small");
        choices.add("Medium");
        choices.add("Large");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Small", choices);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(TuPrologPane.class.getResourceAsStream("2prologsticker.png")));
        dialog.setTitle("Font size");
        dialog.setHeaderText("Font size chooser");
        dialog.setContentText("Choose font size for text areas:");
        if (fsize == 12) dialog.setSelectedItem("Small");
        if (fsize == 14) dialog.setSelectedItem("Medium");
        if (fsize == 16) dialog.setSelectedItem("Large");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals("Small")) {
                for (CodeArea c : openTabs.values()) {
                    c.setStyle("-fx-font-size: 12;");
                }
                fsize = 12;
            }
            if (result.get().equals("Medium")) {
                for (CodeArea c : openTabs.values()) {
                    c.setStyle("-fx-font-size: 14;");
                }
                fsize = 14;
            }
            if (result.get().equals("Large")) {
                for (CodeArea c : openTabs.values()) {
                    c.setStyle("-fx-font-size: 16;");
                }
                fsize = 16;
            }

        }

    }
}
