package org.example;

import javafx.scene.canvas.GraphicsContext;

public class Bullet {
    public static final int SIZE = 10;

    private double x;
    private double y;
    private double velocityX;

    public Bullet(double x, double y, boolean facingRight, double speed) {
        this.x = x;
        this.y = y;
        this.velocityX = facingRight ? speed : -speed;
    }

    public void update() {
        x += velocityX;
    }

    public boolean isOutOfBounds() {
        return x < -SIZE || x > PlatformGame.SCREEN_WIDTH + SIZE;
    }

    public boolean hits(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();
        double playerSize = Player.PLAYER_SIZE;

        return x < playerX + playerSize &&
                x + SIZE > playerX &&
                y < playerY + playerSize &&
                y + SIZE > playerY;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.YELLOW);
        gc.fillOval(x, y, SIZE, SIZE);
    }
}
