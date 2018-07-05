package gui;

import dialogs.ChooseWordDialog;
import gui.sounds.EffectType;
import gui.sounds.SoundPlayer;
import javafx.application.Platform;
import messages.ClientMessages;
import messages.ServerMessages;
import network.Client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Adam Ostrozlik
 */
public class ClientGuiAdapter {

    private final Client client;
    private final GuiController controller;

    public ClientGuiAdapter(Client client, GuiController controller) {
        this.client = client;
        this.controller = controller;
    }

    public void processMessage(String message) {
        String[] parts = message.split(",");
        if (parts[0].equals(ServerMessages.YOUR_WORDS_ARE.toString())) {
            Platform.runLater(() -> {
                String word = ChooseWordDialog.INSTANCE.show(new String[]{parts[1], parts[2], parts[3]});
                client.sendMessage(ClientMessages.MY_WORD_IS, word);
            });
        } else if (parts[0].equals(ServerMessages.ROUND_TIME.toString())) {
            controller.setupProgressTime(Integer.parseInt(parts[1]));
        } else if (parts[0].equals(ServerMessages.CLIENT_CONNECTS.toString())) {
            controller.addNewClient(parts[1], Integer.parseInt(parts[2]));
        } else if (parts[0].equals(ServerMessages.CLIENT_ONTURN.toString())) {
            controller.setDrawing(false);
            controller.startProgress(Integer.parseInt(parts[1]));
        } else if (parts[0].equals(ServerMessages.CLIENT_YOU_ARE_ON_TURN.toString())) {
            controller.setDrawing(true);
            controller.startProgress(client.getClientId());
        } else if (parts[0].equals(ServerMessages.PROVIDE_ID.toString())) {
            client.setClientId(Integer.parseInt(parts[1]));
            controller.addNewClient(client.getName(), client.getClientId());
        } else if (parts[0].equals(ClientMessages.CLIENT_DISCONNECTS.toString())) {
            System.out.println("REMOVING: " + message);
            controller.removeClient(Integer.parseInt(parts[1]));
        } else if (parts[0].equals(ServerMessages.GAME_ENDS.toString())) {
            Platform.runLater(() -> controller.gameEnds());
        } else if (parts[0].equals(ClientMessages.DRAWING.toString())) {
            Tool tool = Tool.getFromString(parts[1]);
            double width = Double.valueOf(parts[2]);
            String color = parts[3];
            double x1 = Double.valueOf(parts[4]);
            double y1 = Double.valueOf(parts[5]);
            double x2 = Double.valueOf(parts[6]);
            double y2 = Double.valueOf(parts[7]);
            controller.anotherPlayerDrawing(tool, width, color, x1, y1, x2, y2);
        } else if (parts[0].equals(ClientMessages.MESSAGE.toString())) {
            controller.setMessage(Integer.parseInt(parts[1]), parts[2]);
        } else if (parts[0].equals(ServerMessages.CLIENTS_INFO.toString())) {
            for (int i = 1; i < parts.length - 1; i+=2) {
                String name = parts[i];
                int id = Integer.parseInt(parts[i + 1]);
                controller.addNewClient(name, id);
            }
        } else if (parts[0].equals(ClientMessages.ERASE_ALL.toString())) {
            controller.eraseAll(false);
        } else if (parts[0].equals(ServerMessages.ADD_POINTS.toString())) {
            int id = Integer.parseInt(parts[1]);
            int points = Integer.parseInt(parts[2]);
            controller.addPointsToClient(id, points);
            if (id == client.getClientId()) {
                controller.setMessageFieldDisabled(true);
                SoundPlayer.INSTANCE.playEffect(EffectType.POINTS);
            }
        } else if (parts[0].equals(ServerMessages.NEW_ROUND.toString())){
            controller.setMessageFieldDisabled(true);
            controller.resetDrawingCoordinates();
            controller.setRoundInfo(parts[1]);
            SoundPlayer.INSTANCE.playEffect(EffectType.ROUND_BEGIN);
        } else if (parts[0].equals(ServerMessages.YOU_CAN_GO_OFF.toString())){
            client.closeResources(false);
        } else if (parts[0].equals(ServerMessages.YOU_ARE_ALONE.toString())){
            controller.askForContinue();
        }
    }
    
    public GuiController getController() {
        return controller;
    }

}
