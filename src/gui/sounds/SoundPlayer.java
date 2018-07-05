/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.sounds;

import java.net.URISyntaxException;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Adam Ostrozlik
 */
public class SoundPlayer {

    public static final SoundPlayer INSTANCE = new SoundPlayer();
    private boolean playEffects, muteAll, backgroundSound;
    private Media background, newGame, points, pop, correct;
    private MediaPlayer backgroundPlayer, effectsPlayer;

    private SoundPlayer() {
        try {
            muteAll = false;
            playEffects = backgroundSound = true;
            background = new Media(getClass().getResource("background").toURI().toString());
            newGame = new Media(getClass().getResource("newgame.mp3").toURI().toString());
            points = new Media(getClass().getResource("points.mp3").toURI().toString());
            pop = new Media(getClass().getResource("pop.mp3").toURI().toString());
            correct = new Media(getClass().getResource("correct.mp3").toURI().toString());
            if (background != null) {
                backgroundPlayer = new MediaPlayer(background);
                backgroundPlayer.setCycleCount(AudioClip.INDEFINITE);
                backgroundPlayer.play();
            }
        } catch (URISyntaxException ex) {
            System.out.println("Error loading sounds: " + ex.getLocalizedMessage());
        }
    }

    public void setBackgroundMusic(boolean play) {
        backgroundSound = play;
        if (muteAll) {
            return;
        }
        backgroundPlayer.setMute(!play);
    }

    public void setMuteAll(boolean yes_) {
        muteAll = yes_;
        if (backgroundSound) {
            backgroundPlayer.setMute(yes_);
        }
        playEffects = !yes_;
    }
    
    public void setPlayEffects(boolean play) {
        playEffects = play;
    }

    public void playEffect(EffectType type) {
        if (!playEffects || muteAll) {
            return;
        }

        switch (type) {
            case ROUND_BEGIN:
                playSound(newGame);
                break;
            case POINTS:
                playSound(points);
                break;
            case POP:
                playSound(pop);
                break;
            case CORRECT_ANSWER:
                playSound(correct);
                break;
        }
    }

    private void playSound(Media media) {
        effectsPlayer = new MediaPlayer(media);
        effectsPlayer.play();
    }

}
