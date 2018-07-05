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
public enum ServerMessages implements IMessage{
    
    SERVER_GREETINGS_CLIENT("WELCOME"),
    YOU_ARE_ALONE("YOUAREALONE"),
    CLIENT_CONNECTS("CLIENTON"),
    PROVIDE_ID("PROVIDEID"),
    CLIENT_ONTURN("ONTURN"),
    NEW_ROUND("NEWROUND"),
    ADD_POINTS("ADDPOINTS"),
    CLIENT_YOU_ARE_ON_TURN("YOUPLAY"),
    GAME_ENDS("GAMEENDS"),
    YOUR_WORDS_ARE("CHOOSEWORDS"),
    CLIENTS_INFO("CLIENTSINFO"),
    ROUND_TIME("ROUNDTIME"), 
    YOU_CAN_GO_OFF("YOUCANGOOFF");
    
    private final String msg;

    private ServerMessages(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
    
    
}
