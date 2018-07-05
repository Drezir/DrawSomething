/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import gui.ClientGuiAdapter;
import gui.GuiController;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import messages.ClientMessages;
import networkDiscovery.AvailableServer;
import threads.ClientCommunication;

/**
 *
 * @author Adam Ostrozlik
 */
public class Client {

    private final String name;
    private boolean stopped;
    private final ClientCommunication communication;
    private final ClientGuiAdapter adapter;
    private int clientId;

    public Client(String name, AvailableServer server, GuiController controller) throws IOException {
        this.name = name;
        stopped = false;
        adapter = new ClientGuiAdapter(this, controller);
        Socket socket = new Socket(server.getPacket().getAddress(), server.getPacket().getPort());
        communication = new ClientCommunication(socket, this);
    }

    public Client(String name, Server server, GuiController controller) throws IOException {
        this.name = name;
        stopped = false;
        adapter = new ClientGuiAdapter(this, controller);
        Socket socket = new Socket(InetAddress.getLocalHost().getHostAddress(), Server.SERVER_PORT);
        communication = new ClientCommunication(socket, this);
    }

    public String getName() {
        return name;
    }

    public void processMessage(String message) {
        adapter.processMessage(message);
    }

    public void sendMessage(ClientMessages control, String addons) {
        communication.sendMessage(control, addons);
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void close() {
        sendMessage(ClientMessages.CLIENT_DISCONNECTS, String.valueOf(clientId));
    }

    public void closeResources(boolean forceClose) {
        stopped = true;
        if (!forceClose) {
            adapter.getController().stopWaitingForServerApproval();
        } else if (communication != null) {
            communication.interrupt();
        }
    }

    public boolean isClosed() {
        return stopped;
    }

}
