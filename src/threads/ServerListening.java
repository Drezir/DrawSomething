/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ServerListening extends Thread {

    private final ServerSocket socket;
    private boolean listening;
    private final Server server;

    public ServerListening(Server server) throws IOException {
        socket = new ServerSocket(Server.SERVER_PORT);
        this.server = server;
        listening = true;
        setDaemon(true);
        setName("ServerListening");
        start();
    }

    @Override
    public void run() {
        while (listening) {
            synchronized (socket) {
                try {
                    Socket s = socket.accept();
                    if (!listening) break;
                    server.addClient(s);
                } catch (IOException ex) {
                    System.out.println("ServerSocket could not establish connection");
                }
            }
        }
    }

    @Override
    public void interrupt() {
        listening = false;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing server socket");
            }
        }
    }

}
