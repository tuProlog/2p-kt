package it.unibo.tuprolog.ui.gui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TuPrologFx extends Application {
    private static final int width = 700;


    @Override
    public void start(Stage stage) {
        InformationProvider ip = new InformationProvider();
        stage.setTitle("TuProlog");
        stage.getIcons().add(new Image(TuPrologFx.class.getResourceAsStream("2prologsticker.png")));
        SolverController myController = new ControllerImpl();
        TuPrologPane tuPrologPane = new TuPrologPane(myController);
        Scene scene = new Scene(tuPrologPane, width, ip.getWindowDefaultSize().getSecond());
        scene.getStylesheets().add(TuPrologFx.class.getResource("java-keywords-light.css").toExternalForm());
        scene.getStylesheets().add(TuPrologFx.class.getResource("light-code-area.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        //maingra
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
        super.stop();
    }
}

