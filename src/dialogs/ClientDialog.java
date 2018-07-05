/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import java.util.Optional;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author Adam Ostrozlik
 */
public class ClientDialog {
    
    private final TextInputDialog dialog;
    public static final ClientDialog INSTANCE;
    
    static{
        INSTANCE = new ClientDialog();
    }
    
    private ClientDialog(){
        dialog = new TextInputDialog();
        dialog.setTitle("Nastaveni klienta");
        dialog.setHeaderText("Zadejde jmeno klienta");
    }
    
    public String show() throws DialogException {
        dialog.setContentText("");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String text = result.get().trim();
            if (!text.isEmpty()) return text;
            throw new DialogException("bad name of client", true);
        }
        throw new DialogException("bad name of client", false);
    }
    
}
