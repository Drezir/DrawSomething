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
public class ServerDialog {
    
    private final TextInputDialog dialog;
    public static final ServerDialog INSTANCE;
    
    static{
        INSTANCE = new ServerDialog();
    }
    
    private ServerDialog(){
        dialog = new TextInputDialog();
        dialog.setTitle("Nastaveni serveru");
        dialog.setHeaderText("Zadejde nazev serveru");
    }
    
    public String show() throws DialogException {
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String text = result.get().trim();
            if (!text.isEmpty()) return text;
            throw new DialogException("bad name of server", true);
        }
        throw new DialogException("bad name of server", false);
    }
    
}
