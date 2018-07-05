/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkDiscovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import messages.ClientMessages;
import messages.ServerMessages;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class ClientDiscovery extends Thread {

    private final Set<AvailableServer> servers;
    private String ipAddress;

    public ClientDiscovery() {
        servers = new HashSet<>();
        setDaemon(true);
        setName("ClientDiscovery");
        start();
    }
    /**
     * Discovery for specific server IP address
     * @param ip IP of server
     */
    public ClientDiscovery(String ip){
        servers = new HashSet<>();
        ipAddress = ip;
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] sendData = ClientMessages.CLIENT_LOOKINGFOR_SERVER.toString().getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length,
                    InetAddress.getByName(ipAddress != null && !ipAddress.isEmpty() ? ipAddress : "255.255.255.255"),   // monocast or broadcast
                    Server.SERVER_PORT);
            socket.send(sendPacket);

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if (i.isLoopback() ||!i.isUp()) {
                    continue;
                }
                for (InterfaceAddress ia : i.getInterfaceAddresses()) {
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Server.SERVER_PORT);
                    try {
                        socket.send(sendPacket);
                    } catch (IOException ex) {
                        System.out.println("Iterface not available" + i);
                    }
                }
            }

            byte[] received = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(received, received.length);
            synchronized (socket) {
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData(), "UTF-8").trim();
                String[] parts = message.split(",");
                if (parts[0].equals(ServerMessages.SERVER_GREETINGS_CLIENT.toString())) {
                    servers.add(new AvailableServer(parts[1], receivePacket, Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        }
    }

    public Set<AvailableServer> getServers() {
        return servers;
    }
}
