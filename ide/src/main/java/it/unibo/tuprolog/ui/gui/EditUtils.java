package it.unibo.tuprolog.ui.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;

import java.util.Map;

public class EditUtils {

    public EditUtils() {
    }

    public void undoHandler(ActionEvent e, TabPane tabPaneTheory, Map<Tab, CodeArea> openTabs) {
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                openTabs.get(b).undo();

            }
        }
    }


    public void redoHandler(ActionEvent e, TabPane tabPaneTheory, Map<Tab, CodeArea> openTabs) {
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                openTabs.get(b).redo();

            }
        }
    }

    public void copyHandler(ActionEvent e, TabPane tabPaneTheory, Map<Tab, CodeArea> openTabs) {
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                openTabs.get(b).copy();

            }
        }
    }

    public void pasteHandler(ActionEvent e, TabPane tabPaneTheory, Map<Tab, CodeArea> openTabs) {
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                openTabs.get(b).paste();

            }
        }
    }

    public void cutHandler(ActionEvent e, TabPane tabPaneTheory, Map<Tab, CodeArea> openTabs) {
        for (Tab b : tabPaneTheory.getTabs()) {
            if (b.isSelected()) {
                openTabs.get(b).cut();

            }
        }
    }
}
