package org.example;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Platform {
    private double x, y, width, height;

    private static List<Platform> basicPlatforms = new ArrayList<>();
    private static List<Platform> generalPlatforms = new ArrayList<>();

    public Platform() {
        basicPlatforms.add(new Platform(0, PlatformGame.SCREEN_HEIGHT - 20, PlatformGame.SCREEN_WIDTH, 10));
        basicPlatforms.add(new Platform(0, 0, PlatformGame.SCREEN_WIDTH, 10));
        generalPlatforms.add(new Platform(200, 750, 300, 20));
        generalPlatforms.add(new Platform(1000, 750, 300, 20));
        generalPlatforms.add(new Platform(400, 600, 800, 20));
        generalPlatforms.add(new Platform(600, 450, 300, 20));
        generalPlatforms.add(new Platform(900, 300, 100, 20));
        generalPlatforms.add(new Platform(1100, 450, 300, 20));
        generalPlatforms.add(new Platform(1300, 100, 150, 20));
        generalPlatforms.add(new Platform(300, 100, 150, 20));
        generalPlatforms.add(new Platform(700, 100, 50, 20));
        generalPlatforms.add(new Platform(400, 200, 800, 20));
    }

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public List<Platform> getPlatforms() {
        List<Platform> platforms = new ArrayList<>();
        platforms.addAll(basicPlatforms);
        platforms.addAll(generalPlatforms);
        return platforms;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    static String imagePath = "platform.png";
    static Image platformImage = new Image(imagePath);
    public static Image getPlatformImage() {
        return platformImage;
    }
}