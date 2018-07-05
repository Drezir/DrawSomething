/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dialogs.AvailableServersDialog;
import dialogs.ClientDialog;
import dialogs.DialogException;
import dialogs.ErrorDialog;
import dialogs.QuestionDialog;
import dialogs.ServerDialog;
import gui.sounds.EffectType;
import gui.sounds.SoundPlayer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import messages.ClientMessages;
import network.Client;
import network.Server;
import networkDiscovery.AvailableServer;
import threads.ServerStatusChecker;

/**
 * FXML Controller class
 *
 * @author Adam Ostrozlik
 */
enum Tool {
    PEN("pen"), RUBBER("rubber");

    static Tool getFromString(String part) {
        if (part.toLowerCase(Locale.UK).equals("pen")) {
            return PEN;
        } else {
            return RUBBER;
        }
    }

    Tool(String str) {
        this.str = str;
    }
    private final String str;

    @Override
    public String toString() {
        return str;
    }

}

public class GuiController {

    private Server server;
    private Client client;
    private ImageView penIcon, rubberIcon, crossIcon, leftLogo;
    private Tool tool;
    private boolean drawing;
    private CanvasDrawer canvasDrawer;
    private HashMap<Integer, UserSpace> clients;
    private Node multiplayerNode;
    private Thread waiting;
    private ImageCursor penCursor, rubberCursor, crossCursor;

    private FlowPane PlayersPane;

    @FXML
    private Canvas Canvas;
    @FXML
    private ToggleButton penButton;
    @FXML
    private ToggleButton rubberButton;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ToggleGroup toolGroup;
    @FXML
    private Slider WidthSlider;
    @FXML
    private Menu serverMenu;
    @FXML
    private TextField messageText;
    @FXML
    private Menu clientMenu;
    @FXML
    private Text TextRounds;
    @FXML
    private BorderPane MainPane;
    @FXML
    private CheckMenuItem menuSoundsEnable;
    @FXML
    private CheckMenuItem menuBackgroundMusic;
    @FXML
    private CheckMenuItem menuSoundEffects;
    @FXML
    private ScrollPane ScrollPane;

    private void drawing(MouseEvent event) {
        if (!drawing) {
            return;
        }
        canvasDrawer.drawLine(event, tool);
        String send = tool.toString() + "," + canvasDrawer.getData();
        if (client != null) {
            client.sendMessage(ClientMessages.DRAWING, send);
        }
    }

    public void Initialize() {
        multiplayerNode = MainPane.getLeft();
        drawing = true;
        tool = Tool.PEN;
        clients = new HashMap<>();
        canvasDrawer = new CanvasDrawer(Canvas, colorPicker, WidthSlider);
        try {
            leftLogo = new ImageView(getClass().getResource("images/drawsomething.png").toURI().toURL().toString());
            penIcon = new ImageView(getClass().getResource("images/pen.png").toURI().toURL().toString());
            rubberIcon = new ImageView(getClass().getResource("images/rubber.png").toURI().toURL().toString());
            crossIcon = new ImageView(getClass().getResource("images/cross.png").toURI().toURL().toString());
            penCursor = new ImageCursor(penIcon.getImage());
            rubberCursor = new ImageCursor(rubberIcon.getImage());
            crossCursor = new ImageCursor(crossIcon.getImage());
            leftLogo.setFitWidth(((FlowPane) multiplayerNode).getWidth());
            leftLogo.setFitHeight(((FlowPane) multiplayerNode).getHeight());
            penIcon.setFitHeight(penButton.getHeight());
            penIcon.setFitWidth(penButton.getWidth());
            rubberIcon.setFitHeight(rubberButton.getHeight());
            rubberIcon.setFitWidth(rubberButton.getWidth());
            penButton.setGraphic(penIcon);
            rubberButton.setGraphic(rubberIcon);
            Canvas.setOnMouseMoved(event -> {
                if (!drawing) {
                    Canvas.setCursor(crossCursor);
                    return;
                }
                if (tool == Tool.PEN) {
                    Canvas.setCursor(penCursor);
                } else {
                    Canvas.setCursor(rubberCursor);
                }
            });

            Canvas.setOnMousePressed(event -> {
                drawing(event);
                canvasDrawer.resetCoordinates();
            });

            Canvas.setOnMouseDragged(event -> {
                drawing(event);
            });
            Canvas.setOnMouseReleased(event -> {
                if (!drawing) {
                    return;
                }
                canvasDrawer.releaseMouse(event);
            });
            messageText.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    sendMessage();
                }
            });

            PlayersPane = new FlowPane(Orientation.VERTICAL);
            PlayersPane.setAlignment(Pos.TOP_CENTER);
            PlayersPane.setColumnHalignment(HPos.CENTER);
            ScrollPane.setContent(PlayersPane);
            SoundPlayer.INSTANCE.setBackgroundMusic(true);
            switchPanes(false);
            canvasDrawer.initialState();
            colorPicker.setValue(Color.BLACK);
        } catch (URISyntaxException | MalformedURLException ex) {
            ErrorDialog.INSTANCE.show("Could not load program: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    private void switchPanes(boolean multiplayer) {
        if (multiplayer) {
            MainPane.setLeft(multiplayerNode);
        } else {
            MainPane.setLeft(leftLogo);
        }
    }

    public void addNewClient(String name, int id) {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user.fxml"));
            Parent node = null;
            try {
                node = loader.load();
            } catch (IOException ex) {
                System.out.println("Cannot add client box: " + ex.getLocalizedMessage());
            }
            PlayersPane.getChildren().add(node);
            UserController controller = loader.getController();
            controller.setName(name);
            clients.put(id, new UserSpace(controller, node));
        });
    }

    @FXML
    private void onHostGame(ActionEvent event) {
        if (server != null && !server.isClosed()) {
            ErrorDialog.INSTANCE.show("Server již běží");
            return;
        }
        try {
            String serverName = ServerDialog.INSTANCE.show();
            String clientName = ClientDialog.INSTANCE.show();
            server = new Server(serverName);
            client = new Client(clientName, server, this);
            serverMenu.setVisible(true);
            clientMenu.setVisible(true);
            switchPanes(true);
        } catch (DialogException ex) {
            ex.showErrorDialog();
        } catch (IOException ex) {
            ErrorDialog.INSTANCE.show("Server nemohl by vytvoren: " + ex.getMessage());
        }
    }

    @FXML
    private void onJoinGame(ActionEvent event) {
        if (client != null && !client.isClosed()) {
            ErrorDialog.INSTANCE.show("Klient již běží");
            return;
        }
        try {
            String clientName = ClientDialog.INSTANCE.show();
            AvailableServer chosenServer = AvailableServersDialog.INSTANCE.show();
            client = new Client(clientName, chosenServer, this);
            clientMenu.setVisible(true);
            switchPanes(true);
        } catch (DialogException ex) {
            ex.showErrorDialog();
        } catch (IOException ex) {
            ErrorDialog.INSTANCE.show("Klient nemohl by vytvoren: " + ex.getMessage());
        }
    }

    @FXML
    private void onEndGame(ActionEvent event) {
        waitForServerApproval(true);
        switchPanes(false);
    }

    @FXML
    private void onSoundsEnable(ActionEvent event) {
        boolean allowed = menuSoundsEnable.isSelected();
        SoundPlayer.INSTANCE.setMuteAll(!allowed);
    }

    @FXML
    private void onMusicEnable(ActionEvent event) {
        boolean value = menuBackgroundMusic.isSelected();
        SoundPlayer.INSTANCE.setBackgroundMusic(value);
    }

    @FXML
    private void onEfectsEnable(ActionEvent event) {
        SoundPlayer.INSTANCE.setPlayEffects(menuSoundEffects.isSelected());
    }

    @FXML
    private void onPenClick(ActionEvent event) {
        tool = Tool.PEN;
    }

    @FXML
    private void onRubberClick(ActionEvent event) {
        tool = Tool.RUBBER;
    }

    @FXML
    private void onDeleteAll(ActionEvent event) {
        eraseAll(true);
    }

    @FXML
    private void onStartGame(ActionEvent event) {
        if (server != null && !server.gameRunning()) {
            server.startGame();
            new ServerStatusChecker(this, server);
        }
    }

    void setupProgressTime(int progress) {
        clients.entrySet().stream().forEach((entry) -> {
            entry.getValue().controller.setProgressTime(progress);
        });
    }

    void startProgress(int playerId) {
        resetAllProgresses();
        canvasDrawer.initialState();// start progress = new round
        if (clients.containsKey(playerId)) {
            clients.get(playerId).controller.startProgress();
        }
    }

    void setDrawing(boolean drawing) {
        setMessageFieldDisabled(drawing);
        this.drawing = drawing;
    }

    void setMessage(int playerId, String message) {
        if (clients.containsKey(playerId)) {
            SoundPlayer.INSTANCE.playEffect(EffectType.POP);
            clients.get(playerId).controller.setMessage(message);
        }
    }

    void removeClient(int playerId) {
        Platform.runLater(() -> {
            if (clients.containsKey(playerId)) {
                PlayersPane.getChildren().remove(clients.get(playerId).node);
                clients.remove(playerId);
            }
        });
    }

    void gameEnds() {
        waitForServerApproval(true);
        switchPanes(false);
    }

    void anotherPlayerDrawing(Tool tool, double width, String color, double x1, double y1, double x2, double y2) {
        canvasDrawer.drawLine(tool, width, color, x1, y1, x2, y2);
    }

    public void eraseAll(boolean notifyClients) {
        canvasDrawer.initialState();
        if (client != null && notifyClients) {
            client.sendMessage(ClientMessages.ERASE_ALL, "");
        }
    }

    void addPointsToClient(int id, int points) {
        if (clients.containsKey(id)) {
            clients.get(id).controller.addPoints(points);
        }
    }

    @FXML
    private void onSendMessage(ActionEvent event) {
        sendMessage();
    }

    private void sendMessage() {
        if (messageText.getText().isEmpty()) {
            return;
        }
        if (client != null) {
            client.sendMessage(ClientMessages.MESSAGE, messageText.getText());
        }
        messageText.clear();
    }

    @FXML
    private void onClientDisconnects(ActionEvent event) {
        waitForServerApproval(false);
    }

    private void disconnectClient() {
        if (client != null) {
            client.close();
            clients.entrySet().stream().forEach((entry) -> {
                entry.getValue().controller.cancelAll();
            });
            PlayersPane.getChildren().clear();
            clientMenu.setVisible(false);
            clients.clear();
            setDrawing(true);
            switchPanes(false);
        }
    }

    @FXML
    private void onEndApp(ActionEvent event) {
        disconnectClient();
        closeServer();
        System.exit(0);
    }

    public void closeServer() {
        if (server != null) {
            Platform.runLater(() -> serverMenu.setVisible(false));
            server.stopServer();
        }
    }

    void setMessageFieldDisabled(boolean disable) {
        messageText.setDisable(disable);
    }

    private void resetAllProgresses() {
        clients.entrySet().stream().forEach((entry) -> {
            entry.getValue().controller.cancelAll();
        });
    }

    void setRoundInfo(String info) {
        TextRounds.setText(info);
    }

    public void stopWaitingForServerApproval() {
        if (waiting != null) {
            waiting.interrupt();
        }
    }

    void askForContinue() {
        Platform.runLater(() -> {
            boolean cont = QuestionDialog.INSTANCE.show(
                    "Jste sám ve hře. Přejete si pokračovat? ");
            if (cont) {
                setDrawing(true);
            } else {
                waitForServerApproval(false);
            }
        });
    }

    private void waitForServerApproval(boolean closeServer) {
        final int WAIT_DURATION = 3000;
        final Node node = MainPane.getCenter();
        MainPane.setCenter(new ProgressIndicator());
        waiting = new Thread() {
            @Override
            public void run() {
                try {
                    Platform.runLater(() -> disconnectClient());
                    sleep(WAIT_DURATION);
                    closeObjects();
                } catch (InterruptedException ex) {
                    System.out.println("Waiting for closing resources interrupted");
                } finally {
                    Platform.runLater(() -> {
                        MainPane.setCenter(node);
                    });
                    closeObjects();
                }
            }

            void closeObjects() {
                if (client != null) {
                    client.closeResources(true);
                }
                if (server != null && closeServer) {
                    closeServer();
                }
                Platform.runLater(() -> {
                    MainPane.setCenter(node);
                });
            }
        };
        waiting.setName("WaitingThread");
        waiting.start();
    }

    void resetDrawingCoordinates() {
        canvasDrawer.resetCoordinates();
    }

    private class UserSpace {

        UserController controller;
        Node node;

        public UserSpace(UserController controller, Node node) {
            this.controller = controller;
            this.node = node;
        }

    }
}
