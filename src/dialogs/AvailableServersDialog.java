/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import networkDiscovery.AvailableServer;
import networkDiscovery.ClientDiscovery;

/**
 *
 * @author Adam Ostrozlik
 */
public class AvailableServersDialog {

    private final Dialog dialog;
    public static final AvailableServersDialog INSTANCE;
    private final ListView<AvailableServer> list;
    private final TextField ip;

    static {
        INSTANCE = new AvailableServersDialog();
    }

    private AvailableServersDialog() {
        dialog = new Dialog();
        dialog.setTitle("Dostupne servery");
        dialog.setHeaderText("Vyberte si dostupny server");

        BorderPane bp = new BorderPane();
        FlowPane fp = new FlowPane(Orientation.HORIZONTAL);
        fp.setAlignment(Pos.CENTER);
        fp.setColumnHalignment(HPos.CENTER);
        ip = new TextField();
        ip.setPromptText("Ip adresa");
        Button connectServer = new Button("Najít server");
        Button refresh = new Button("Aktualizovat list");
        list = new ListView();
        list.setMaxHeight(200);
        refresh.setOnAction(event -> {
            list.getItems().clear();
            ClientDiscovery discovery = new ClientDiscovery();
            discoveryWaitAndSet(discovery);
        });
        connectServer.setOnAction(event -> {
            if (isIpValid(ip.getText())) {
                list.getItems().clear();
                ClientDiscovery discovery = new ClientDiscovery(ip.getText());
                discoveryWaitAndSet(discovery);
            } else {
                ErrorDialog.INSTANCE.show("Ip adresa není validní");
            }
        });
        fp.getChildren().addAll(ip, connectServer);
        bp.setCenter(list);
        bp.setTop(refresh);
        bp.setBottom(fp);
        dialog.getDialogPane().setContent(bp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    private void discoveryWaitAndSet(ClientDiscovery discovery) {
        try {
            final int MILISECONDS_TO_WAIT = 5000;
            discovery.join(MILISECONDS_TO_WAIT);
        } catch (InterruptedException ex) {
            System.out.println("Problem occured while discovering servers");
        } finally {
            discovery.interrupt();
            list.getItems().addAll(FXCollections.observableSet(discovery.getServers()));
        }
    }

    public AvailableServer show() throws DialogException {
        list.getItems().clear();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (!list.getSelectionModel().isEmpty()) {
                return list.getSelectionModel().getSelectedItem();
            }
            throw new DialogException("no server have been chosen", true);
        }
        throw new DialogException("no server have been chosen", false);
    }

    public static boolean isIpValid(String ip) {
        Pattern p = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
        Matcher m = p.matcher(ip);
        return m.matches();
    }

}
