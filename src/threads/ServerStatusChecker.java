/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import gui.GuiController;
import javafx.application.Platform;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ServerStatusChecker extends Thread {

    private final GuiController controller;
    private final Server server;

    public ServerStatusChecker(GuiController controller, Server server) {
        this.controller = controller;
        this.server = server;
        setDaemon(true);
        setName("ServerStatusChecker");
        start();
    }

    @Override
    public void run() {
        final int CHECK_FREQUENCY = 3000;
        while (server.gameRunning()) {
            if (server.getNumberOfClients() == 0) {
                Platform.runLater(() -> {
                    controller.closeServer();
                });
                break;
            }
            try {
                sleep(CHECK_FREQUENCY);
            } catch (InterruptedException ex) {
                System.out.println("Checking server interrupted");
                break;
            }
        }
    }
}
