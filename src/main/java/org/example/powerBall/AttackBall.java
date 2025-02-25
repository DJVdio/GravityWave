package org.example.powerBall;

import javafx.scene.canvas.GraphicsContext;
import org.example.Bullet;
import org.example.Player;
import org.example.PlatformGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttackBall extends PowerBall {
    private static final double SIZE = 20; // 红球的大小
    private static final double LIFETIME = 8.0; // 红球的存活时间
    private static final Random random = new Random();

    public AttackBall(double x, double y) {
        super(x, y, SIZE, LIFETIME);
    }

    @Override
    public void update(double elapsedTime) {
        // 红球不需要特殊更新逻辑
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.RED); // 绘制红色球
        gc.fillOval(x, y, size, size);
    }

    @Override
    public void applyEffect(Player currentPlayer, Player otherPlayer) {
        spawnAttackBullets(currentPlayer, otherPlayer); // 生成子弹
    }

    private void spawnAttackBullets(Player shooter, Player target) {
        List<Bullet> bullets = new ArrayList<>();
        int attackerId = shooter.getPlayerId();
        double bulletSpeed = 2.0;

        for (int i = 0; i < 8; i++) {
            double startX, startY;
            int edge = random.nextInt(4);
            switch (edge) {
                case 0: // 左边缘外
                    startX = -Bullet.SIZE;
                    startY = random.nextDouble() * PlatformGame.SCREEN_HEIGHT;
                    break;
                case 1: // 右边缘外
                    startX = PlatformGame.SCREEN_WIDTH + Bullet.SIZE;
                    startY = random.nextDouble() * PlatformGame.SCREEN_HEIGHT;
                    break;
                case 2: // 上边缘外
                    startX = random.nextDouble() * PlatformGame.SCREEN_WIDTH;
                    startY = -Bullet.SIZE;
                    break;
                default: // 下边缘外
                    startX = random.nextDouble() * PlatformGame.SCREEN_WIDTH;
                    startY = PlatformGame.SCREEN_HEIGHT + Bullet.SIZE;
                    break;
            }

            double targetX, targetY;
            if (i == 7) { // 第八颗子弹追踪目标玩家
                targetX = target.getX() + Player.PLAYER_SIZE / 2;
                targetY = target.getY() + Player.PLAYER_SIZE / 2;
            } else { // 其他子弹随机目标
                targetX = random.nextDouble() * PlatformGame.SCREEN_WIDTH;
                targetY = random.nextDouble() * PlatformGame.SCREEN_HEIGHT;
            }

            double angle = Math.atan2(targetY - startY, targetX - startX);

            Bullet bullet = new Bullet(
                    startX,
                    startY,
                    true, // 方向参数不再重要
                    bulletSpeed,
                    attackerId,
                    javafx.scene.paint.Color.RED // 红色子弹
            );

            bullet.setVelocity(
                    Math.cos(angle) * bulletSpeed,
                    Math.sin(angle) * bulletSpeed
            );

            bullets.add(bullet);
        }

        PlatformGame.addBullets(bullets);
    }
}
