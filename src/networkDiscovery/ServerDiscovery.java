/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkDiscovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import messages.ClientMessages;
import messages.ServerMessages;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ServerDiscovery extends Thread {

    private boolean discovering;
    private final String serverName;
    private final Server server;
    private DatagramSocket socket;

    public ServerDiscovery(String serverName, Server server) {
        this.serverName = serverName;
        this.server = server;
        discovering = true;
        setDaemon(true);
        setName("ServerDiscovery");
        start();
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(Server.SERVER_PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            byte[] received = new byte[15000];
            while (discovering) {
                synchronized (socket) {
                    DatagramPacket packet = new DatagramPacket(received, received.length);
                    socket.receive(packet);
                    if (interrupted()) {
                        return;
                    }
                    String message = new String(packet.getData()).trim();
                    if (message.equals(ClientMessages.CLIENT_LOOKINGFOR_SERVER.toString())) {
                        String strResponse = ServerMessages.SERVER_GREETINGS_CLIENT.toString() + ","
                                + serverName + "," + server.getNumberOfClients();
                        byte[] response = strResponse.getBytes();
                        DatagramPacket sendPacket
                                = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error with server discovery: " + ex.getLocalizedMessage());
        } finally {
            closeResources();
        }
    }

    @Override
    public void interrupt() {
        discovering = false;
        super.interrupt();
        closeResources();
    }

    private void closeResources() {
        if (socket != null) {
            socket.close();
        }
    }

}
