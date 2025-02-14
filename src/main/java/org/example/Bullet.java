package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    public static final int SIZE = 10; // 子弹大小

    private double x; // 子弹的 x 坐标
    private double y; // 子弹的 y 坐标
    private double velocityX; // 子弹的 x 轴速度
    private double velocityY; // 子弹的 y 轴速度
    private int shooterId; // 发射者 ID
    private Color color; // 子弹颜色

    /**
     * 构造函数（6 参数）
     */
    public Bullet(double x, double y, boolean facingRight, double speed,
                  int shooterId, Color color) {
        this.x = x;
        this.y = y;
        this.velocityX = facingRight ? speed : -speed;
        this.velocityY = 0;
        this.shooterId = shooterId;
        this.color = color;
    }

    /**
     * 构造函数（5 参数，兼容旧代码）
     */
    public Bullet(double x, double y, boolean facingRight, double speed, int shooterId) {
        this(x, y, facingRight, speed, shooterId, Color.YELLOW);
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public boolean isOutOfBounds() {
        return x < -SIZE || x > PlatformGame.SCREEN_WIDTH + SIZE ||
                y < -SIZE || y > PlatformGame.SCREEN_HEIGHT + SIZE;
    }

    public boolean hits(Player player) {
        return shooterId != player.getPlayerId() &&
                x < player.getX() + Player.PLAYER_SIZE &&
                x + SIZE > player.getX() &&
                y < player.getY() + Player.PLAYER_SIZE &&
                y + SIZE > player.getY();
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x, y, SIZE, SIZE);
    }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public int getShooterId() {
        return shooterId;
    }

    public void setShooterId(int shooterId) {
        this.shooterId = shooterId;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
}
