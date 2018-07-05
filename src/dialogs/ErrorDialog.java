/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Adam Ostrozlik
 */
public class ErrorDialog {

    public static final ErrorDialog INSTANCE;
    private final Alert DIALOG;

    static {
        INSTANCE = new ErrorDialog();
    }

    private ErrorDialog() {
        DIALOG = new Alert(AlertType.ERROR);
        DIALOG.setTitle("Chyba");
    }

    public void show(String text) {
        DIALOG.setContentText(text);
        DIALOG.showAndWait();
    }

}
