/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkDiscovery;

import java.net.DatagramPacket;

/**
 *
 * @author Adam Ostrozlik
 */
public class AvailableServer {
    private final String serverName;
    private final DatagramPacket packet;
    private final int players;

    public AvailableServer(String serverName, DatagramPacket packet, int players) {
        this.serverName = serverName;
        this.packet = packet;
        this.players = players;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPlayers() {
        return players;
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(serverName);
        sb.append(" (");
        sb.append(packet.getSocketAddress());
        sb.append(")\tClients: ");
        sb.append(players);
        return sb.toString();
    }
    
    
}
