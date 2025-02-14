package org.example.util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    private double x, y;       // 粒子坐标
    private double vx, vy;     // 速度分量
    private double life;       // 剩余寿命（秒）
    private Color color;       // 颜色
    private double size;       // 大小

    public Particle(double x, double y, double vx, double vy, double life, Color color, double size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.color = color;
        this.size = size;
    }

    public boolean update(double elapsedTime) {
        x += vx * elapsedTime * 60; // 补偿帧率
        y += vy * elapsedTime * 60;
        life -= elapsedTime;
        return life > 0;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x, y, size, size);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Color getColor() { return color; }
}
