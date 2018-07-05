/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

/**
 *
 * @author Adam Ostrozlik
 */
public enum ClientMessages implements IMessage {
    
    CLIENT_LOOKINGFOR_SERVER("HELLO"),   // message to a server to get know each other
    MY_WORD_IS("MYWORDIS"),
    DRAWING("DRAWING"),
    MESSAGE("MESSAGE"),
    CLIENT_DISCONNECTS("DISCONNECTING"),
    ERASE_ALL("ERASEALL"),
    CLIENT_PROVIDES_NAME("MYNAMEIS");
    
    
    private final String msg;
    
    private ClientMessages(String msg) {
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return msg;
    }
    
}
