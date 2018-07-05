/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.util.ArrayList;
import messages.ServerMessages;
import network.Server;

/**
 *
 * @author Adam Ostrozlik
 */
public class GameThread extends Thread {

    private final Server server;
    private String word;
    private final WordsLoader wordsLoader;
    private int[] points;
    private int pointsIndex, correctCount;
    private boolean running;
    private ClientThread onTurn;

    public GameThread(Server server) {
        this.server = server;
        wordsLoader = new WordsLoader();
        running = true;
        setDaemon(true);
        setName("GameThread");
        start();
    }

    public ClientThread getOnTurn() {
        return onTurn;
    }

    @Override
    public void run() {
        final int ROUNDS = 5;
        final int ROUND_TIME = 60;  // seconds
        int round = 0;
        ArrayList<ClientThread> clients = server.getClients();
        server.sendToAll(ServerMessages.ROUND_TIME, String.valueOf(ROUND_TIME));
        while (round < ROUNDS && running && clients.size() > 0) {
            try {
                for (int i = 0; i < clients.size(); ++i) {
                    onTurn = clients.get(i);
                    points = new int[]{3, 2, 1};
                    pointsIndex = correctCount = 0;
                    server.sendToAll(ServerMessages.NEW_ROUND, (round + 1) + " / " + ROUNDS);
                    clients.get(i).sendMessage(ServerMessages.YOUR_WORDS_ARE.toString() + "," + getWordsString());
                    waitForChosingWord();
                    server.sendToAllExceptOfClient(ServerMessages.CLIENT_ONTURN,
                            String.valueOf(clients.get(i).getID()), clients.get(i));
                    clients.get(i).sendMessage(ServerMessages.CLIENT_YOU_ARE_ON_TURN.toString());
                    try {
                        sleep(ROUND_TIME * 1000);
                    } catch (InterruptedException ex) {
                        System.out.println("Game sleep interrupted");
                    } finally {
                        ++round;
                    }
                }
            } catch (IndexOutOfBoundsException ex) {
                System.out.println("Clients are empty");
            }
        }
    }

    private void gameEnds() {
        server.gameEnds();
    }

    public void stopThread() {
        gameEnds();
        running = false;
        this.interrupt();
    }

    private String getWordsString() {
        try {
            wordsLoader.join();
        } catch (InterruptedException ex) {
            System.out.println("Error wating for words to be loaded: " + ex.getLocalizedMessage());
        }
        final int WORDS_TO_CHOOSE_FROM = 3;
        ArrayList<String> words = wordsLoader.pickRandomWords(WORDS_TO_CHOOSE_FROM);
        StringBuilder sb = new StringBuilder(words.size() + WORDS_TO_CHOOSE_FROM);
        words.stream()
                .forEach(word -> {
                    sb.append(word);
                    sb.append(",");
                });
        return sb.toString();
    }

    private void waitForChosingWord() {
        synchronized (this) {
            word = "";
            try {
                while (word.isEmpty()) {
                    wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("Cannot wait for client word: " + ex.getLocalizedMessage());
                gameEnds();
            }
        }
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean compareWordFromClient(String word, ClientThread source) {
        System.out.println("Compare: " + word + " and " + this.word);
        if (this.word.equals(word)) {
            int sum = points[pointsIndex];
            if (pointsIndex < points.length - 1) {
                ++pointsIndex;
            }
            server.sendToAll(ServerMessages.ADD_POINTS, source.getID() + "," + String.valueOf(sum));
            ++correctCount;
            if (correctCount == server.getNumberOfClients() - 1) // -1 because player cannot guess while drawing
            {
                this.interrupt();
            }
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return running;
    }
}
