package org.example.powerBall;

import javafx.scene.canvas.GraphicsContext;
import org.example.Player;

public class ShootingSpeedUpBall extends PowerBall {
    private static final double SIZE = 20;
    private static final double LIFETIME = 10.0; // 强化球存在时长（秒）

    public ShootingSpeedUpBall(double x, double y) {
        super(x, y, SIZE, LIFETIME);
    }

    @Override
    public void update(double elapsedTime) {
        // 这里可以添加强化球的动画或其他更新逻辑
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.fillOval(x, y, size, size);
    }

    @Override
    public void applyEffect(Player player) {
        double remainTime = player.getRemainingTime();
        // 增加子弹速度
        player.setBulletSpeed(player.getBulletSpeed() + 1);
        // 减少射击间隔 10%
        player.setShootInterval(player.getShootInterval() * 0.9);
        player.setRemainingTime(remainTime);
    }
}
