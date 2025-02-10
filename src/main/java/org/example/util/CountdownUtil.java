package org.example.util;

public class CountdownUtil {
    private double interval; // 倒计时总时长
    private double remainingTime; // 剩余时间

    public CountdownUtil(double interval) {
        this.interval = interval;
        this.remainingTime = interval;
    }

    /**
     * 更新倒计时
     *
     * @param elapsedTime 经过的时间（秒）
     */
    public void update(double elapsedTime) {
        if (remainingTime > 0) {
            remainingTime -= elapsedTime;
            if (remainingTime < 0) {
                remainingTime = 0;
            }
        }
    }

    /**
     * 重置倒计时
     *
     * @param immediate 如果为 true，则将剩余时间设置为 0
     */
    public void reset(boolean immediate) {
        if (immediate) {
            remainingTime = 0; // 立即重置为 0
        } else {
            remainingTime = interval; // 重置为完整间隔
        }
    }

    /**
     * 重置倒计时（默认重置为完整间隔）
     */
    public void reset() {
        reset(false);
    }

    /**
     * 检查倒计时是否结束
     *
     * @return 如果倒计时结束，返回 true；否则返回 false
     */
    public boolean isFinished() {
        return remainingTime <= 0;
    }

    /**
     * 获取剩余时间
     *
     * @return 剩余时间（秒）
     */
    public double getRemainingTime() {
        return remainingTime;
    }

    /**
     * 获取格式化后的剩余时间（保留 1 位小数）
     *
     * @return 格式化后的剩余时间字符串
     */
    public String getFormattedRemainingTime() {
        return String.format("%.1f", remainingTime);
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }
}