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
     * @param x 子弹的初始 x 坐标
     * @param y 子弹的初始 y 坐标
     * @param facingRight 子弹是否朝右
     * @param speed 子弹的速度
     * @param shooterId 发射者 ID
     * @param color 子弹颜色
     */
    public Bullet(double x, double y, boolean facingRight, double speed,
                  int shooterId, Color color) {
        this.x = x;
        this.y = y;
        this.velocityX = facingRight ? speed : -speed; // 根据方向设置速度
        this.velocityY = 0; // 初始 y 轴速度为 0
        this.shooterId = shooterId; // 设置发射者 ID
        this.color = color; // 设置子弹颜色
    }

    /**
     * 构造函数（5 参数，兼容旧代码）
     * @param x 子弹的初始 x 坐标
     * @param y 子弹的初始 y 坐标
     * @param facingRight 子弹是否朝右
     * @param speed 子弹的速度
     * @param shooterId 发射者 ID
     */
    public Bullet(double x, double y, boolean facingRight, double speed, int shooterId) {
        this(x, y, facingRight, speed, shooterId, Color.YELLOW); // 默认颜色为黄色
    }

    /**
     * 更新子弹位置
     */
    public void update() {
        x += velocityX; // 更新 x 坐标
        y += velocityY; // 更新 y 坐标
    }

    /**
     * 检查子弹是否超出屏幕边界
     * @return 如果子弹超出边界返回 true，否则返回 false
     */
    public boolean isOutOfBounds() {
        return x < -SIZE || x > PlatformGame.SCREEN_WIDTH + SIZE ||
                y < -SIZE || y > PlatformGame.SCREEN_HEIGHT + SIZE;
    }

    /**
     * 检查子弹是否击中玩家
     * @param player 玩家对象
     * @return 如果击中返回 true，否则返回 false
     */
    public boolean hits(Player player) {
        return shooterId != player.getPlayerId() && // 不能击中自己
                x < player.getX() + Player.PLAYER_SIZE &&
                x + SIZE > player.getX() &&
                y < player.getY() + Player.PLAYER_SIZE &&
                y + SIZE > player.getY();
    }

    /**
     * 绘制子弹
     * @param gc 画布上下文
     */
    public void draw(GraphicsContext gc) {
        gc.setFill(color); // 使用动态颜色
        gc.fillOval(x, y, SIZE, SIZE); // 绘制圆形子弹
    }

    /**
     * 设置子弹速度
     * @param vx x 轴速度
     * @param vy y 轴速度
     */
    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    /**
     * 获取发射者 ID
     * @return 发射者 ID
     */
    public int getShooterId() {
        return shooterId;
    }

    /**
     * 设置发射者 ID
     * @param shooterId 发射者 ID
     */
    public void setShooterId(int shooterId) {
        this.shooterId = shooterId;
    }

    /**
     * 获取子弹颜色
     * @return 子弹颜色
     */
    public Color getColor() {
        return color;
    }

    /**
     * 设置子弹颜色
     * @param color 子弹颜色
     */
    public void setColor(Color color) {
        this.color = color;
    }
}