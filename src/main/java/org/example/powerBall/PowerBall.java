package org.example.powerBall;

import org.example.Player;

public abstract class PowerBall {
    protected double x; // 球的 x 坐标
    protected double y; // 球的 y 坐标
    protected double size; // 球的大小
    protected double spawnTime; // 生成时间
    protected double lifetime; // 球的存活时间

    public PowerBall(double x, double y, double size, double lifetime) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.lifetime = lifetime;
        this.spawnTime = System.nanoTime() / 1_000_000_000.0; // 记录生成时间
    }

    /**
     * 更新球的逻辑
     * @param elapsedTime 经过的时间
     */
    public abstract void update(double elapsedTime);

    /**
     * 绘制球
     * @param gc 画布上下文
     */
    public abstract void draw(javafx.scene.canvas.GraphicsContext gc);

    /**
     * 应用效果
     * @param currentPlayer 当前玩家（捡到球的玩家）
     * @param otherPlayer 另一名玩家
     */
    public abstract void applyEffect(Player currentPlayer, Player otherPlayer);

    /**
     * 检查球是否过期
     * @param currentTime 当前时间
     * @return 如果过期返回 true，否则返回 false
     */
    public boolean isExpired(double currentTime) {
        return currentTime - spawnTime > lifetime;
    }

    /**
     * 检查球是否与玩家碰撞
     * @param playerX 玩家的 x 坐标
     * @param playerY 玩家的 y 坐标
     * @param playerSize 玩家的大小
     * @return 如果碰撞返回 true，否则返回 false
     */
    public boolean checkCollision(double playerX, double playerY, double playerSize) {
        return x < playerX + playerSize &&
                x + size > playerX &&
                y < playerY + playerSize &&
                y + size > playerY;
    }
}