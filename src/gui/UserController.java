/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Adam Ostrozlik
 */
public class UserController implements Initializable {

    @FXML
    private TextField UserName;
    @FXML
    private TextField UserPoints;
    @FXML
    private ProgressIndicator UserProgress;
    @FXML
    private TextField playerMessage;

    public void setName(String name) {
        UserName.setText(name);
    }

    private Thread displayMessage;
    private final int DISPLAY_TIME = 5;   // seconds

    public void setMessage(String message) {
        playerMessage.setText("");
        if (displayMessage != null) {
            displayMessage.interrupt();
        }
        displayMessage = new Thread() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    playerMessage.setText(message);
                });
                try {
                    sleep(1000 * DISPLAY_TIME);
                } catch (InterruptedException ex) {
                    System.out.println("Displaying message interrupted");
                }
                Platform.runLater(() -> {
                    playerMessage.clear();
                });
            }

            @Override
            public void interrupt() {
                super.interrupt();
                Platform.runLater(() -> {
                    playerMessage.setText("");
                });
            }
        };
        displayMessage.setDaemon(true);
        displayMessage.setName("DisplayMessageThread");
        displayMessage.start();
    }

    private Thread progress;
    private int progressTime;

    public void startProgress() {
        if (progress != null) {
            progress.interrupt();
        }
        UserProgress.setVisible(true);
        progress = new Thread() {
            @Override
            public void run() {
                int current = 0;
                while (current < progressTime && !isInterrupted()) {
                    final int currentFinal = current;
                    try {
                        Platform.runLater(() -> {
                            UserProgress.setProgress((double) currentFinal / progressTime);
                        });
                        sleep(1000);    // 1 second sleep
                        ++current;
                    } catch (InterruptedException ex) {
                        System.out.println("Cannot sleep for 1 second in progress thread");
                        break;
                    }
                }
                Platform.runLater(() -> UserProgress.setVisible(false));
            }

            @Override
            public void interrupt() {
                super.interrupt();
                Platform.runLater(() -> {
                    UserProgress.setProgress(0);
                });
            }

        };
        progress.setDaemon(true);
        progress.setName("ProgressIndicator");
        progress.start();
    }

    void setProgressTime(int progress) {
        progressTime = progress;
    }

    void cancelAll() {
        if (progress != null) {
            progress.interrupt();
        }
        if (displayMessage != null) {
            displayMessage.interrupt();
        }
    }

    void addPoints(int points) {
        int sum = points + Integer.parseInt(UserPoints.getText());
        UserPoints.setText(String.valueOf(sum));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserPoints.setStyle("-fx-text-fill: #0000FF");
    }
}
