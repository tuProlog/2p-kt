package it.unibo.tuprolog.ui.gui;


import it.unibo.tuprolog.solve.Solution;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.*;
import java.util.*;

public class TuPrologPane extends BorderPane {

    private final static int defaultInsets = 5;


    public static final String file = "File";
    public static final String edit = "Edit";
    public static final String window = "Window";
    public static final String help = "Help";

    public static final String newfile = "New file";
    public static final String open = "Open";
    public static final String save = "Save";
    public static final String saveas = "Save as";
    public static final String rename = "Rename";
    public static final String libraries = "Libraries";

    public static final String undo = "Undo";
    public static final String redo = "Redo";
    public static final String copy = "Copy";
    public static final String cut = "Cut";
    public static final String paste = "Paste";

    public static final String darktheme = "Dark theme";
    public static final String lighttheme = "Light theme";
    public static final String fontsize = "Font size";

    public static final String about = "About";

    public static final String insetquery = "Insert query";
    public static final String timeout = "Timeout";
    public static final String solve = "Solve";

    public static final String solution = "Solution";
    public static final String bindings = "Bindings";
    //public static final String input = "Input";
    public static final String output = "Output";
    public static final String exception = "Exception";
    public static final String next = "Next";
    public static final String stop = "Stop";
    //public static final String send = "Send";

    public static final String untitled = "Untitled";


    private final SolverController myController;
    private static Map<Tab, File> openFiles = new HashMap<>();
    private static Map<Tab, CodeArea> openTabs = new HashMap<>();
    private int untitledIndex = 0;
    private static ArrayList<String> darkcodearea = new ArrayList<>();
    private static ArrayList<String> lightcodearea = new ArrayList<>();
    private SyntaxColoring syntaxColoring;

    private static int fsize = 12;
    private Iterator<Solution> sol;
    private static String style = "";
    private static TabPane tabPaneTheory;
    private static TextField queryField;
    private static TextField timeoutField;
    private static TextArea solutionText;
    private static TextArea bindingsText;
    //private TextArea inputText;
    private static TextArea outputText;
    private static TextArea exceptionText;

    private HelpUtils helpUtils = new HelpUtils();
    private EditUtils editUtils = new EditUtils();
    private WindowUtils windowUtils = new WindowUtils();
    //private Button sendInput;

    /*private FileChooser fileChooser;*/
    /*private MenuBar menubar; private Menu file; private Menu edit; private Menu window; private MenuItem apri;  private MenuItem salva; private MenuItem salvaConNome;
    private MenuItem rinomina; private MenuItem undo; private MenuItem redo;
    private MenuItem copy; private MenuItem paste; private MenuItem cut; private MenuItem darktheme; private MenuItem lighttheme;  private MenuItem nuovoFile;*/
    /* private Tab solution; private Tab bindings; private Tab input; private Tab output; private Tab exception;*/
    /* private Button next; private Button stop; private Button solve;*/


    public TuPrologPane(SolverController myController) {
        this.myController = myController;
        initGUI();
    }

    private void initGUI() {
        setTop(makeTop());
        setCenter(makeCenter());
        setBottom(makeBottom());
    }

    private Node makeTop() {
        HBox hbox = new HBox();

        MenuBar menuBar = new MenuBar();
        Menu file = new Menu(TuPrologPane.file);
        Menu edit = new Menu(TuPrologPane.edit);
        Menu window = new Menu(TuPrologPane.window);
        Menu help = new Menu(TuPrologPane.help);


        MenuItem nuovoFile = new MenuItem(TuPrologPane.newfile);
        MenuItem apri = new MenuItem(TuPrologPane.open);
        MenuItem salva = new MenuItem(TuPrologPane.save);
        MenuItem salvaConNome = new MenuItem(TuPrologPane.saveas);
        MenuItem rinomina = new MenuItem(TuPrologPane.rename);
        MenuItem librerie = new MenuItem(TuPrologPane.libraries);
        //nuovoFile.setStyle("-fx-pref-width: 80;");

        nuovoFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        apri.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        salva.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        rinomina.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));

        file.getItems().add(nuovoFile);
        file.getItems().add(apri);
        file.getItems().add(new SeparatorMenuItem());
        file.getItems().add(salva);
        file.getItems().add(salvaConNome);
        file.getItems().add(rinomina);
        file.getItems().add(new SeparatorMenuItem());
        file.getItems().add(librerie);

        nuovoFile.setOnAction(this::newFileHandler);
        apri.setOnAction(this::openFileHandler);
        salva.setOnAction(this::saveFileHandler);
        salvaConNome.setOnAction(this::saveAsHandler);
        rinomina.setOnAction(this::renameFileHandler);
        librerie.setOnAction(this::librariesHandler);

        MenuItem undo = new MenuItem(TuPrologPane.undo);
        MenuItem redo = new MenuItem(TuPrologPane.redo);
        MenuItem copy = new MenuItem(TuPrologPane.copy);
        MenuItem cut = new MenuItem(TuPrologPane.cut);
        MenuItem paste = new MenuItem(TuPrologPane.paste);

        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));

        undo.setOnAction(e -> editUtils.undoHandler(e, tabPaneTheory, openTabs));
        redo.setOnAction(e -> editUtils.redoHandler(e, tabPaneTheory, openTabs));
        copy.setOnAction(e -> editUtils.copyHandler(e, tabPaneTheory, openTabs));
        paste.setOnAction(e -> editUtils.pasteHandler(e, tabPaneTheory, openTabs));
        cut.setOnAction(e -> editUtils.cutHandler(e, tabPaneTheory, openTabs));

        edit.getItems().add(undo);
        edit.getItems().add(redo);
        edit.getItems().add(new SeparatorMenuItem());
        edit.getItems().add(copy);
        edit.getItems().add(cut);
        edit.getItems().add(paste);

        MenuItem darktheme = new MenuItem(TuPrologPane.darktheme);
        MenuItem lighttheme = new MenuItem(TuPrologPane.lighttheme);
        MenuItem fontsize = new MenuItem(TuPrologPane.fontsize);

        darktheme.setOnAction(e -> windowUtils.changeToDarkColorHandler(e, this.getScene(), this, darkcodearea, openTabs, style, syntaxColoring, solutionText, bindingsText, outputText, exceptionText, queryField, timeoutField));
        lighttheme.setOnAction(e -> windowUtils.changeToLightColorHandler(e, this.getScene(), this, lightcodearea, openTabs, style, syntaxColoring, solutionText, bindingsText, outputText, exceptionText, queryField, timeoutField));
        fontsize.setOnAction(e -> windowUtils.fontSizeHandler(e, fsize, openTabs));

        window.getItems().add(darktheme);
        window.getItems().add(lighttheme);
        window.getItems().add(new SeparatorMenuItem());
        window.getItems().add(fontsize);

        MenuItem about = new MenuItem(TuPrologPane.about);
        about.setOnAction(e -> helpUtils.aboutHandler(e, style));
        help.getItems().add(about);

        menuBar.getMenus().add(file);
        menuBar.getMenus().add(edit);
        menuBar.getMenus().add(window);
        menuBar.getMenus().add(help);

        for (Menu m : menuBar.getMenus()) {
            for (MenuItem mi : m.getItems()) {
                mi.setStyle("-fx-pref-width: 100;");
            }
        }


        hbox.getChildren().add(menuBar);
        return hbox;
    }

    private Node makeCenter() {
        VBox vbox = new VBox();
        tabPaneTheory = new TabPane();

        ContextMenu cm = new ContextMenu();
        cm.setPrefWidth(100);

        MenuItem copy = new MenuItem(TuPrologPane.copy);
        MenuItem cut = new MenuItem(TuPrologPane.cut);
        MenuItem paste = new MenuItem(TuPrologPane.paste);
        MenuItem undo = new MenuItem(TuPrologPane.undo);
        MenuItem redo = new MenuItem(TuPrologPane.redo);

        copy.setOnAction(e -> editUtils.copyHandler(e, tabPaneTheory, openTabs));
        paste.setOnAction(e -> editUtils.pasteHandler(e, tabPaneTheory, openTabs));
        cut.setOnAction(e -> editUtils.cutHandler(e, tabPaneTheory, openTabs));
        undo.setOnAction(e -> editUtils.undoHandler(e, tabPaneTheory, openTabs));
        redo.setOnAction(e -> editUtils.redoHandler(e, tabPaneTheory, openTabs));

        cm.getItems().addAll(copy, cut, paste, undo, redo);
        tabPaneTheory.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> {
                    if (e.getButton() == MouseButton.SECONDARY)
                        cm.show(tabPaneTheory, e.getScreenX(), e.getScreenY());

                });
        tabPaneTheory.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> {
                    if (e.getButton() == MouseButton.PRIMARY)
                        cm.hide();
                });
        for (MenuItem mi : cm.getItems()) {
            mi.setStyle("-fx-pref-width: 100;");
        }
        for (CodeArea c : openTabs.values()) {
            c.setContextMenu(cm);
        }

        vbox.getChildren().add(tabPaneTheory);
        return vbox;
    }

    private Node makeBottom() {
        VBox vbox = new VBox();
        HBox hboxQuery = new HBox();
        HBox hboxTimeout = new HBox();
        HBox hboxButtons = new HBox();

        Label q = new Label(TuPrologPane.insetquery);

        queryField = new TextField();
        queryField.setCache(true);
        queryField.setPrefHeight(20);
        queryField.setPrefWidth(400);
        queryField.setFont(Font.font("Courier new"));

        Label t = new Label(TuPrologPane.timeout);
        timeoutField = new TextField();
        timeoutField.setPrefWidth(80);
        timeoutField.setFont(Font.font("Courier New"));

        Button solve = new Button(TuPrologPane.solve);
        solve.setOnAction(this::solveQueryHandler);

        hboxQuery.setPadding(new Insets(defaultInsets, defaultInsets, defaultInsets, defaultInsets));
        hboxQuery.setSpacing(10);
        hboxQuery.getChildren().addAll(q, queryField, t, timeoutField, solve);


        TabPane tabPaneQuery = new TabPane();
        Tab solution = new Tab(TuPrologPane.solution);
        solution.setClosable(false);
        Tab bindings = new Tab(TuPrologPane.bindings);
        bindings.setClosable(false);
        Tab output = new Tab(TuPrologPane.output);
        output.setClosable(false);
        //Tab input = new Tab(TuPrologPane.input);
        //input.setClosable(false);
        Tab exception = new Tab(TuPrologPane.exception);
        exception.setClosable(false);

        solutionText = new TextArea();
        bindingsText = new TextArea();
        outputText = new TextArea();
        //inputText = new TextArea();
        exceptionText = new TextArea();

        final Font curierNew = Font.font("Courier New");

        solutionText.setFont(curierNew);
        bindingsText.setFont(curierNew);
        outputText.setFont(curierNew);
        //inputText.setFont(curierNew);
        exceptionText.setFont(curierNew);

        solutionText.setEditable(false);
        bindingsText.setEditable(false);
        outputText.setEditable(false);
        exceptionText.setEditable(false);

        solution.setContent(solutionText);
        bindings.setContent(bindingsText);
        output.setContent(outputText);
        //input.setContent(inputText);
        exception.setContent(exceptionText);

        tabPaneQuery.getTabs().add(solution);
        tabPaneQuery.getTabs().add(bindings);
        tabPaneQuery.getTabs().add(output);
        //tabPaneQuery.getTabs().add(input);
        tabPaneQuery.getTabs().add(exception);

        myController.addExceptionListener(x -> exceptionText.setText(exceptionText.getText() + "\n" + x.getMessage()));
        myController.addOutputListener(x -> outputText.setText(outputText.getText() + "\n" + x));

        Button next = new Button(TuPrologPane.next);
        Button stop = new Button(TuPrologPane.stop);
        //sendInput = new Button(TuPrologPane.send);
        //sendInput.setVisible(false);
        /*
        input.setOnSelectionChanged(x -> {
            if (input.isSelected()) {
                sendInput.setVisible(true);
            } else {
                sendInput.setVisible(false);
            }
        });
        */
        next.setOnAction(this::nextSolutionHandler);
        stop.setOnAction(this::stopSolutionHandler);
        //sendInput.setOnAction(this::sendInputHandler);
        hboxButtons.setPadding(new Insets(defaultInsets, defaultInsets, defaultInsets, defaultInsets));
        hboxButtons.setSpacing(10);
        hboxButtons.getChildren().add(next);
        hboxButtons.getChildren().add(stop);
        //hboxButtons.getChildren().add(sendInput);

        vbox.getChildren().add(hboxQuery);
        vbox.getChildren().add(hboxTimeout);
        vbox.getChildren().add(tabPaneQuery);
        vbox.getChildren().add(hboxButtons);
        return vbox;
    }

    private void solveQueryHandler(ActionEvent e) {

        String query = queryField.getText();
        int timeout;

        saveBeforeSolve();
        if (!timeoutField.getText().isEmpty()) {
            if (timeoutField.getText().matches("[0-9]*")) {
                timeout = Integer.parseInt(timeoutField.getText());
                sol = myController.solveQuery(query, new HashSet<>(openFiles.values()), Optional.of(timeout)).iterator();
            } else {
                timeoutField.setText("");
                sol = myController.solveQuery(query, new HashSet<>(openFiles.values()), Optional.empty()).iterator();
            }
        } else {
            sol = myController.solveQuery(query, new HashSet<>(openFiles.values()), Optional.empty()).iterator();
        }

        if (sol.hasNext()) {
            Solution s = sol.next();
            solutionText.setText(myController.printSolution(s));
            bindingsText.setText(myController.printBinding(s));
        } else {
            queryField.setText("");
            solutionText.setText("");
            bindingsText.setText("");
            timeoutField.setText("");
        }
    }

    private void saveBeforeSolve() {
        try {
            for (Tab b : openFiles.keySet()) {
                if (b.isSelected() && openTabs.get(b) != null) {
                    PrintWriter pw = new PrintWriter(openFiles.get(b));
                    pw.append(openTabs.get(b).getText());
                    pw.flush();
                    pw.close();
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void nextSolutionHandler(ActionEvent e) {
        if (sol != null && sol.hasNext()) {
            Solution s = sol.next();
            solutionText.setText(myController.printSolution(s));
            bindingsText.setText(myController.printBinding(s));
        } else {
            queryField.setText("");
            solutionText.setText("");
            bindingsText.setText("");
            exceptionText.setText("");
            outputText.setText("");
        }
    }

    private void stopSolutionHandler(ActionEvent e) {
        queryField.setText("");
        solutionText.setText("");
        bindingsText.setText("");
        exceptionText.setText("");
        outputText.setText("");
    }

    /*
    private void sendInputHandler(ActionEvent e) {


    }*/
    public void newFileHandler(ActionEvent e) {

        Tab untitled = new Tab(TuPrologPane.untitled + untitledIndex);

        CodeArea text = new CodeArea();
        untitledIndex++;
        text.setParagraphGraphicFactory(LineNumberFactory.get(text));
        text.setStyle("-fx-font-size: " + fsize + ";");
        syntaxColoring = new SyntaxColoring(text);
        untitled.setContent(text);

        text.setLineHighlighterOn(true);
        text.setPrefHeight(1000);
        untitled.setStyle(style);
        if (!style.isEmpty()) {
            text.setLineHighlighterFill(Color.valueOf("#303030"));
            text.setBackground(new Background(new BackgroundFill(Color.valueOf("#202020"), CornerRadii.EMPTY, Insets.EMPTY)));
            text.setStyle(0, text.getLength(), darkcodearea);
        } else {
            text.setLineHighlighterFill(Paint.valueOf("#E0E0E0"));
            text.setStyle(0, text.getLength(), lightcodearea);
        }
        openTabs.put(untitled, text);
        tabPaneTheory.getTabs().add(untitled);

    }

    public void openFileHandler(ActionEvent e) {
        Stage stage = (Stage) this.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PROLOG files (*.pl) or TXT files (*.txt)", "*.txt", "*.pl");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            for (File file : files) {
                openFileShowOnTab(file);
            }
        }

    }

    private void openFileShowOnTab(File file) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader filereader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = filereader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            Tab newFile = new Tab(file.getName());

            CodeArea textNewFile = new CodeArea(sb.toString());
            syntaxColoring = new SyntaxColoring(textNewFile);
            textNewFile.setStyle("-fx-font-size: " + fsize + ";");
            textNewFile.setParagraphGraphicFactory(LineNumberFactory.get(textNewFile));
            newFile.setContent(textNewFile);


            if (!style.isEmpty()) {
                textNewFile.setLineHighlighterFill(Color.valueOf("#303030"));
                textNewFile.setBackground(new Background(new BackgroundFill(Color.valueOf("#202020"), CornerRadii.EMPTY, Insets.EMPTY)));
                textNewFile.setStyle(0, textNewFile.getLength(), darkcodearea);
            } else {
                textNewFile.setLineHighlighterFill(Paint.valueOf("#E0E0E0"));
                textNewFile.setStyle(0, textNewFile.getLength(), lightcodearea);
            }

            textNewFile.setLineHighlighterOn(true);
            textNewFile.setPrefHeight(1000);
            newFile.setStyle(style);
            openFiles.put(newFile, file);
            openTabs.put(newFile, textNewFile);
            tabPaneTheory.getTabs().add(newFile);
            SingleSelectionModel<Tab> selectionModel = tabPaneTheory.getSelectionModel();
            selectionModel.select(newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveFileHandler(ActionEvent e) {
        try {
            for (Tab b : tabPaneTheory.getTabs()) {
                if (b.isSelected()) {
                    if (b.getText().startsWith("Untitled")) {
                        FileChooser fileChooser = new FileChooser();
                        Stage stage = (Stage) this.getScene().getWindow();
                        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PROLOG files (*.pl) or TXT files (*.txt)", "*.txt", "*.pl");
                        fileChooser.getExtensionFilters().add(extFilter);
                        File file = fileChooser.showSaveDialog(stage);
                        if (file != null) {
                            PrintWriter pw = new PrintWriter(file);
                            pw.append(openTabs.get(b).getText());
                            pw.flush();
                            pw.close();
                            b.setText(file.getName());
                            openFiles.put(b, file);
                        }
                    } else {
                        PrintWriter pw = new PrintWriter(openFiles.get(b));
                        pw.append(openTabs.get(b).getText());
                        pw.flush();
                        pw.close();
                    }

                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void saveAsHandler(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.getScene().getWindow();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PROLOG files (*.pl) or TXT files (*.txt)", "*.txt", "*.pl");
        fileChooser.getExtensionFilters().add(extFilter);
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                fileChooser.setInitialFileName(openFiles.get(b).getName());
                //fileChooser.setInitialDirectory(openFiles.get(b));
            }
        }
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveTextToFile(file);
        }
    }

    private void saveTextToFile(File file) {
        try {
            for (Tab b : tabPaneTheory.getTabs()) {
                if (b.isSelected()) {
                    PrintWriter pw = new PrintWriter(file);
                    pw.append(openTabs.get(b).getText());
                    pw.flush();
                    pw.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void renameFileHandler(ActionEvent e) {
        String fileName = "";
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                fileName = b.getText();
            }
        }
        if (!fileName.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog(fileName);
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(TuPrologPane.class.getResourceAsStream("2prologsticker.png")));
            dialog.getDialogPane().setStyle(style);
            dialog.setTitle("Rename file");
            dialog.setHeaderText("Change name");
            dialog.setContentText("Please enter the new file name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                fileName = result.get();
                for (Tab b : tabPaneTheory.getTabs()) {
                    renameFile(b, fileName);
                }
            }
        }
    }

    private void renameFile(Tab b, String fileName) {
        if (b.isSelected()) {
            StringTokenizer stk = new StringTokenizer(openFiles.get(b).getAbsolutePath(), File.separator);
            String last;
            StringBuilder path = new StringBuilder();
            while (stk.hasMoreTokens()) {
                last = stk.nextToken();
                if (stk.hasMoreTokens()) path.append(last).append(File.separator);
            }
            path.append(fileName);
            File newFileName = new File(path.toString());
            boolean l = openFiles.get(b).renameTo(newFileName);
            if (l) {
                b.setText(fileName);
                openFiles.replace(b, newFileName);
            }
        }
    }

    public void librariesHandler(ActionEvent e) {

    }



}