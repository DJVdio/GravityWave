package org.example.powerBall;

import javafx.scene.canvas.GraphicsContext;
import org.example.Player;

public abstract class PowerBall {
    protected double x;
    protected double y;
    protected double size;
    protected double spawnTime;
    protected double lifetime; // 生命周期（秒）

    public PowerBall(double x, double y, double size, double lifetime) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.lifetime = lifetime;
        this.spawnTime = System.nanoTime() / 1_000_000_000.0;
    }

    public abstract void update(double elapsedTime);

    public abstract void draw(GraphicsContext gc);

    public abstract void applyEffect(Player player);

    public boolean isExpired(double currentTime) {
        return currentTime - spawnTime > lifetime;
    }

    public boolean checkCollision(double playerX, double playerY, double playerSize) {
        return x < playerX + playerSize &&
                x + size > playerX &&
                y < playerY + playerSize &&
                y + size > playerY;
    }
}
