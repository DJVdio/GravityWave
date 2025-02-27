package org.example.powerBall;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.Player;

public class SuperBulletBall extends PowerBall {
    private static final double SIZE = 20;
    private static final double LIFETIME = 10.0;
    private static final double EFFECT_DURATION = 10.0; // 效果持续时间

    public SuperBulletBall(double x, double y) {
        super(x, y, SIZE, LIFETIME);
    }

    @Override
    public void update(double elapsedTime) {
        // 不需要特殊更新逻辑
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, size, size);
    }

    @Override
    public void applyEffect(Player currentPlayer, Player otherPlayer) {
        currentPlayer.activateSuperBullet(EFFECT_DURATION);
    }
}