/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import messages.ClientMessages;
import network.Client;

/**
 *
 * @author Adam Ostrozlik
 */
public class ClientCommunication extends Thread {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Client client;
    private boolean running;
    private final MessagesProcesor messagesProcesor;

    public ClientCommunication(Socket socket, Client client) throws UnsupportedEncodingException, IOException {
        this.socket = socket;
        this.client = client;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        messagesProcesor = new MessagesProcesor(client);
        sendNameToServer();
        running = true;
        setDaemon(true);
        setName("Clientcommunication");
        start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                String line = in.readLine();
                if (!running) {
                    break;
                }
                messagesProcesor.add(line);
            } catch (IOException ex) {
                System.out.println("Error reading message from server: " + ex.getLocalizedMessage());
                running = false;
            }
        }
        closeResources();
    }

    @Override
    public void interrupt() {
        running = false;
        closeResources();
        super.interrupt();
    }

    private void sendNameToServer() {
        sendMessage(ClientMessages.CLIENT_PROVIDES_NAME, client.getName());
    }

    public void sendMessage(ClientMessages message, String addons) {
        out.println(message.toString() + "," + addons);
    }

    private void closeResources() {
        out.close();
        try {
            in.close();
        } catch (IOException ex) {
            System.out.println("Error closing clients input stream");
        }
        if (messagesProcesor != null) {
            messagesProcesor.interrupt();
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error closing clients output stream");
        }
    }

}
