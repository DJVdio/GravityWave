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
    private static final Random random = new Random(); // 随机数生成器

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
        int attackerId = shooter.getPlayerId(); // 获取发射者 ID
        double bulletSpeed = shooter.getBulletSpeed() / 2.0; // 子弹速度为玩家速度的一半

        for (int i = 0; i < 8; i++) {
            // 生成屏幕外随机位置
            double startX, startY;
            int edge = random.nextInt(4); // 随机选择屏幕边缘
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

            // 确定目标位置
            double targetX, targetY;
            if (i == 7) { // 第八颗子弹追踪目标玩家
                targetX = target.getX() + Player.PLAYER_SIZE / 2;
                targetY = target.getY() + Player.PLAYER_SIZE / 2;
            } else { // 其他子弹随机目标
                targetX = random.nextDouble() * PlatformGame.SCREEN_WIDTH;
                targetY = random.nextDouble() * PlatformGame.SCREEN_HEIGHT;
            }

            // 计算射击角度
            double angle = Math.atan2(targetY - startY, targetX - startX);

            // 创建红色子弹
            Bullet bullet = new Bullet(
                    startX,
                    startY,
                    true, // 方向参数不再重要
                    bulletSpeed,
                    attackerId,
                    javafx.scene.paint.Color.RED // 设置红色
            );

            // 设置二维速度
            bullet.setVelocity(
                    Math.cos(angle) * bulletSpeed,
                    Math.sin(angle) * bulletSpeed
            );

            bullets.add(bullet);
        }

        PlatformGame.addBullets(bullets); // 将子弹添加到游戏
    }
}