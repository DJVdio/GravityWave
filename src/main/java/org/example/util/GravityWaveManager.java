package org.example.util;

import javafx.scene.paint.Color;

import java.util.Random;

public class GravityWaveManager {
    private static final double WAVE_INTERVAL = 10.0; // 10秒变化一次
    private int currentWave = 1; // 开局时重力波级别一定是1
    private double waveTimer = 0;
    private final Random random = new Random();

    public GravityWaveManager() {
        // 确保开局时重力波级别为1
        currentWave = 1;
    }

    public void update(double elapsedTime) {
        waveTimer += elapsedTime;
        if (waveTimer >= WAVE_INTERVAL) {
            changeWave();
            waveTimer = 0;
        }
    }

    private void changeWave() {
        int[] possibleWaves = {1, 2, -1, -2};
        int newWave;
        do {
            newWave = possibleWaves[random.nextInt(possibleWaves.length)];
        } while (newWave == currentWave); // 确保不会连续出现相同的重力波
        currentWave = newWave;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public String getCurrentWaveName() {
        switch (currentWave) {
            case 1:
                return "Normal";
            case 2:
                return "Super Gravity";
            case -1:
                return "Inverted";
            case -2:
                return "Super Inverted";
            default:
                return "Unknown";
        }
    }

    public Color getWaveColor() {
        switch (currentWave) {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.DARKRED;
            case -1:
                return Color.LIGHTBLUE;
            case -2:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }

    public double getRemainingTime() {
        return WAVE_INTERVAL - waveTimer;
    }

    public void reset() {
        currentWave = 1;          // 重置为初始重力波
        waveTimer = 0;     // 重置倒计时
    }
}