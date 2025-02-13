package org.example.powerBall;

import javafx.scene.canvas.GraphicsContext;
import org.example.Player;

public class ShootingSpeedUpBall extends PowerBall {
    private static final double SIZE = 20; // 绿球的大小
    private static final double LIFETIME = 10.0; // 绿球的存活时间

    public ShootingSpeedUpBall(double x, double y) {
        super(x, y, SIZE, LIFETIME);
    }

    @Override
    public void update(double elapsedTime) {
        // 绿球不需要特殊更新逻辑
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GREEN); // 绘制绿色球
        gc.fillOval(x, y, size, size);
    }

    @Override
    public void applyEffect(Player currentPlayer, Player otherPlayer) {
        // 不需要使用 otherPlayer 参数
        double remainTime = currentPlayer.getRemainingTime();
        currentPlayer.setBulletSpeed(currentPlayer.getBulletSpeed() + 1); // 增加子弹速度
        currentPlayer.setShootInterval(currentPlayer.getShootInterval() * 0.9); // 减少射击间隔
        currentPlayer.setRemainingTime(remainTime); // 保持剩余时间
    }
}