/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import network.Client;

/**
 *
 * @author Adam Ostrozlik
 */
public class MessagesProcesor extends Thread {

    private final ConcurrentLinkedQueue<String> messages;
    private boolean listening;
    private final Client client;
    private boolean messageProvided;

    public MessagesProcesor(Client client) {
        setDaemon(true);
        listening = true;
        messageProvided = false;
        this.client = client;
        messages = new ConcurrentLinkedQueue<>();
        setName("MessageProcessor");
        start();
    }

    public void add(String message) {
        if (message == null) {
            return;
        }
        messages.add(message);
        messageProvided = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void run() {
        while (listening) {
            while (!messages.isEmpty()) {
                process();
            }
            messageProvided = false;
            synchronized (this) {
                try {
                    while (!messageProvided) {
                        this.wait();
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Cannot wait for message inMessagesProcesor");
                }
            }
        }
    }

    @Override
    public void interrupt() {
        listening = false;
        super.interrupt();
        messages.clear();
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void process() {
        client.processMessage(messages.poll());
    }

}
