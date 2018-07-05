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
import java.net.Socket;
import java.nio.charset.Charset;
import messages.ServerMessages;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ClientThread extends Thread {

    private final Socket socket;
    private final PrintWriter out;
    private final Server server;
    private final BufferedReader in;
    private String clientName;
    private final int id;
    private boolean alive;

    public ClientThread(Socket socket, Server server, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        this.server = server;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        sendId();
        setDaemon(true);
        setName("ClientThread");
        start();
    }

    private void sendId() {
        out.println(ServerMessages.PROVIDE_ID + "," + id);
    }

    @Override
    public void run() {
        alive = true;
        while (alive) {
            try {
                String line = in.readLine();
                if (!alive) {
                    break;
                }
                server.respondTo(line, this);
            } catch (IOException ex) {
                System.out.println("Client connection lost: " + clientName);
                notifyServer();
                closeResources();
            }
        }
    }

    @Override
    public void interrupt() {
        alive = false;
        super.interrupt();
        closeResources();
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private void closeResources() {
        out.close();
        try {
            in.close();
        } catch (IOException ex) {
            System.out.println("Error closing input stream: " + clientName);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error closing socket: " + clientName);
        }
    }

    private void notifyServer() {
        server.clientClosed(this);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public int getID() {
        return id;
    }

}
