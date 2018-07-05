/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Adam Ostrozlik
 */
public class QuestionDialog {
    
    public static final QuestionDialog INSTANCE = new QuestionDialog();;
    private final Dialog d;
    private final Label label;
    
    private QuestionDialog(){
        d = new Dialog();
        d.setTitle("Vyřešte problém");
        d.setHeaderText("Zvolte jednu možnost");
        label = new Label();
        BorderPane bp = new BorderPane(label);
        d.getDialogPane().setContent(bp);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
    }
    
    public boolean show(String text){
        label.setText(text);
        Optional<ButtonType> result = d.showAndWait();
        return result.get() == ButtonType.YES;
    }
}
