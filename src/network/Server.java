/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import messages.ClientMessages;
import messages.IMessage;
import messages.ServerMessages;
import networkDiscovery.ServerDiscovery;
import threads.ClientThread;
import threads.GameThread;
import threads.ServerListening;
import threads.ServerResponds;

/**
 *
 * @author Adam Ostrozlik
 */
public class Server {

    private final ServerDiscovery discovery;
    private final ServerListening listeningClients;
    private GameThread game;
    private boolean stopped;
    private final ArrayList<ClientThread> clients;
    public static final int SERVER_PORT = 63333;

    public Server(String serverName) throws IOException {
        stopped = false;
        int INITIAL_CAPACITY_OF_CLIENTS = 5;
        clients = new ArrayList<>(INITIAL_CAPACITY_OF_CLIENTS);
        listeningClients = new ServerListening(this);
        discovery = new ServerDiscovery(serverName, this);
    }

    public void addClient(Socket client) {
        try {
            clients.add(new ClientThread(client, this, clients.size()));
        } catch (IOException ex) {
            System.out.println("Cannot add client: " + client.toString());
        }
    }

    public void respondTo(String line, ClientThread source) {
        new ServerResponds(this, line, source);
    }

    public void clientClosed(ClientThread disconnects) {
        sendToAllExceptOfClient(ClientMessages.CLIENT_DISCONNECTS,
                String.valueOf(disconnects.getID()), disconnects);
        switch (clients.size()) {
            case 1:
                if (!gameRunning()) {
                    break;
                }
                sendToAllExceptOfClient(ServerMessages.YOU_ARE_ALONE, "", disconnects);
                break;
        }
        checkIfOnTurn(disconnects);

        disconnects.sendMessage(ServerMessages.YOU_CAN_GO_OFF.toString());
        disconnects.interrupt();
        clients.remove(disconnects);
    }

    public synchronized void sendToAll(IMessage controlMessage, String addons) {
        for (int i = 0; i < clients.size(); ++i) {
            clients.get(i).sendMessage(controlMessage + "," + addons);
        }
    }

    public synchronized void sendToAll(String message) {
        for (int i = 0; i < clients.size(); ++i) {
            clients.get(i).sendMessage(message);
        }
    }

    public synchronized void sendToAllExceptOfClient(IMessage controlMessage, String addons, ClientThread noSend) {
        for (int i = 0; i < clients.size(); ++i) {
            if (clients.get(i) == noSend) {
                continue;
            }
            clients.get(i).sendMessage(controlMessage + "," + addons);
        }
    }

    public synchronized void sendToAllExceptOfClient(String message, ClientThread noSend) {
        for (int i = 0; i < clients.size(); ++i) {
            if (clients.get(i) == noSend) {
                continue;
            }
            clients.get(i).sendMessage(message);
        }
    }

    public int getNumberOfClients() {
        return clients.size();
    }

    public ArrayList<ClientThread> getClients() {
        return clients;
    }

    public void startGame() {
        stopListening();
        game = new GameThread(this);
    }

    public boolean gameRunning() {
        return game != null && game.isRunning();
    }

    private void stopListening() {
        if (discovery != null) {
            discovery.interrupt();
        }
        if (listeningClients != null) {
            listeningClients.interrupt();
        }
    }

    public void playerWordIs(String word) {
        synchronized (game) {
            game.setWord(word);
            game.notifyAll();
        }
    }

    public void stopServer() {
        stopped = true;
        gameEnds();
        stopListening();
        if (game != null) {
            game.stopThread();
        }
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).interrupt();
        }
        clients.clear();
    }

    public synchronized void removeClient(Integer id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == id) {
                clientClosed(clients.get(i));
                break;
            }
        }
    }

    public void checkMessageFrom(String word, ClientThread source) {
        if (gameRunning()) {
            if (!game.compareWordFromClient(word, source)) {
                sendToAll(ClientMessages.MESSAGE, source.getID() + "," + word);
            } else {
                sendToAll(ClientMessages.MESSAGE, source.getID() + "," + "**********");
            }
        } else {
            sendToAll(ClientMessages.MESSAGE, source.getID() + "," + word);
        }
    }

    public void gameEnds() {
        sendToAll(ServerMessages.GAME_ENDS, "");
    }

    public boolean isClosed() {
        return stopped;
    }

    private void checkIfOnTurn(ClientThread disconnects) {
        if (gameRunning()) {
            if (disconnects == game.getOnTurn()) {
                game.interrupt();
            }
        }
    }
}
