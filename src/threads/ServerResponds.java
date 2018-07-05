/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import messages.ClientMessages;
import messages.ServerMessages;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ServerResponds extends Thread {

    private final Server server;
    private final String message;
    private final ClientThread source;

    public ServerResponds(Server server, String message, ClientThread source) {
        this.server = server;
        this.message = message;
        this.source = source;
        setDaemon(true);
        setName("ServerRespondss");
        start();
    }

    @Override
    public void run() {
        if (message == null) return;
        String[] parts = message.split(",");
        if (parts[0].equals(ClientMessages.CLIENT_PROVIDES_NAME.toString())) {
            source.setClientName(parts[1]);
            server.sendToAllExceptOfClient(ServerMessages.CLIENT_CONNECTS, 
                    parts[1] + "," + source.getID(), source);
            sendConnectedClients();
        } else if (parts[0].equals(ClientMessages.MY_WORD_IS.toString())) {
            server.playerWordIs(parts[1]);
        } else if (parts[0].equals(ClientMessages.DRAWING.toString())){
            server.sendToAllExceptOfClient(message, source);
        } else if (parts[0].equals(ClientMessages.MESSAGE.toString())){
            server.checkMessageFrom(parts[1], source);
        } else if (parts[0].equals(ClientMessages.CLIENT_DISCONNECTS.toString())){
            server.removeClient(Integer.valueOf(parts[1]));
        } else if (parts[0].equals(ClientMessages.ERASE_ALL.toString())){
            server.sendToAllExceptOfClient(message, source);
        }
    }

    private void sendConnectedClients() {
        StringBuilder sb = new StringBuilder(server.getNumberOfClients() * 50);
        server.getClients().stream()
                .filter(client -> client != source)
                .forEach(client -> {
                    sb.append(client.getClientName());
                    sb.append(",");
                    sb.append(client.getID());
                    sb.append(",");
                });
        source.sendMessage(ServerMessages.CLIENTS_INFO.toString() + "," + sb.toString());
    }

}
