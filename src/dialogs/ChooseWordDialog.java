/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Adam Ostrozlik
 */
public class ChooseWordDialog {

    public static final ChooseWordDialog INSTANCE;
    private final Dialog choices;
    private final ComboBox<String> box;

    static {
        INSTANCE = new ChooseWordDialog();
    }

    private ChooseWordDialog() {
        choices = new Dialog();
        choices.setTitle("Výběr možností");
        choices.setHeaderText("Zvol si jednu možnost");
        box = new ComboBox<>();
        
        BorderPane bp = new BorderPane(box);
        choices.getDialogPane().setContent(bp);
        choices.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    public String show(String[] data) {
        box.getItems().clear();
        box.getItems().addAll(data);

        box.getSelectionModel().select(0);
        
        choices.showAndWait();
        return box.getSelectionModel().getSelectedItem();
    }
}
