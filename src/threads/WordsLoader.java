/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Adam Ostrozlik
 */
public class WordsLoader extends Thread {

    private ArrayList<String> words;

    public WordsLoader() {
        setDaemon(true);
        setName("WordsLoader");
        start();
    }

    @Override
    public void run() {
        try {
            StringBuilder sb;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResource("/threads/words.txt").openStream()))) {
                sb = new StringBuilder(10000);
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            String w = sb.toString();
            this.words = new ArrayList(Arrays.asList(w.split(",")));
        } catch (IOException ex) {
            System.out.println("Cannot read text file: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    public ArrayList<String> pickRandomWords(int count) {
        ArrayList<String> chosen = new ArrayList(count);
        Random rnd = new Random();
        while (chosen.size() < count) {
            int index = rnd.nextInt(words.size());
            String word = words.get(index);
            if (!chosen.contains(word)) {
                chosen.add(word);
            }
        }
        return chosen;
    }

}
