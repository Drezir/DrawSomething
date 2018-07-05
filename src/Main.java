
import gui.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Adam Ostrozlik
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        String path = "gui/gui.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
        primaryStage.show();
        ((GuiController)loader.getController()).Initialize();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
