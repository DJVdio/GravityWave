package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.example.util.CountdownUtil;
import org.example.util.GravityWaveManager;
import org.example.util.ParticleSystem;

import java.util.List;

public class Player {
    public static final int PLAYER_SIZE = 30;
    private static final double BASE_GRAVITY = 0.15;
    private static final int BASE_JUMP_FORCE = 10;
    private final Image playerImage;
    private boolean isInverted = false;

    private double x;
    private double y;
    private double velocityY = 0;
    private double velocityX = 0;

    private boolean isJumping = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isOnGround = false;
    private int playerId;
    private boolean facingRight = true;

    private double shootInterval;
    private double bulletSpeed = 5;

    private CountdownUtil shootCountdown;
    private final GravityWaveManager gravityWaveManager;
    private final ParticleSystem particleSystem;

    public Player(double startX, double startY, int playerId, double shootInterval,
                  GravityWaveManager gravityWaveManager, ParticleSystem particleSystem) {
        this.x = startX;
        this.y = startY;
        this.playerId = playerId;
        this.shootInterval = shootInterval;
        this.shootCountdown = new CountdownUtil(shootInterval);
        this.shootCountdown.reset(true);
        this.gravityWaveManager = gravityWaveManager;
        this.particleSystem = particleSystem;
        String imagePath = "player" + playerId + ".png";
        playerImage = new Image(imagePath, PLAYER_SIZE, PLAYER_SIZE, true, true);
    }

    private double getActualGravity() {
        int wave = gravityWaveManager.getCurrentWave();
        isInverted = wave < 0;
        return Math.abs(wave) * BASE_GRAVITY * Integer.signum(wave);
    }

    private int getJumpDirection() {
        return gravityWaveManager.getCurrentWave() > 0 ? -1 : 1;
    }

    public void handleKeyPress(KeyCode code) {
        if (playerId == 1) {
            handlePlayer1Keys(code);
        } else if (playerId == 2) {
            handlePlayer2Keys(code);
        }
    }

    private void handlePlayer1Keys(KeyCode code) {
        if (code == KeyCode.A) {
            isMovingLeft = true;
            facingRight = false;
        } else if (code == KeyCode.D) {
            isMovingRight = true;
            facingRight = true;
        } else if (code == KeyCode.K && isOnGround) {
            jump();
        }
    }

    private void handlePlayer2Keys(KeyCode code) {
        if (code == KeyCode.LEFT) {
            isMovingLeft = true;
            facingRight = false;
        } else if (code == KeyCode.RIGHT) {
            isMovingRight = true;
            facingRight = true;
        } else if (code == KeyCode.NUMPAD2 && isOnGround) {
            jump();
        }
    }

    private void jump() {
        velocityY = BASE_JUMP_FORCE * getJumpDirection();
        isJumping = true;
        isOnGround = false;
        // 生成跳跃尘土粒子效果
        particleSystem.addEffect(
                x + PLAYER_SIZE / 2,
                y + PLAYER_SIZE,
                ParticleSystem.EffectType.JUMP_DUST
        );
        // 播放跳跃音效
        PlatformGame.playJumpSound();
    }

    public void handleKeyRelease(KeyCode code) {
        if (playerId == 1) {
            handlePlayer1KeyRelease(code);
        } else if (playerId == 2) {
            handlePlayer2KeyRelease(code);
        }
    }

    private void handlePlayer1KeyRelease(KeyCode code) {
        if (code == KeyCode.A) {
            isMovingLeft = false;
            if (!isMovingRight) {
                velocityX = 0;
            }
        } else if (code == KeyCode.D) {
            isMovingRight = false;
            if (!isMovingLeft) {
                velocityX = 0;
            }
        }
    }

    private void handlePlayer2KeyRelease(KeyCode code) {
        if (code == KeyCode.LEFT) {
            isMovingLeft = false;
            if (!isMovingRight) {
                velocityX = 0;
            }
        } else if (code == KeyCode.RIGHT) {
            isMovingRight = false;
            if (!isMovingLeft) {
                velocityX = 0;
            }
        }
    }

    public void update(List<Platform> platforms, Player otherPlayer, double elapsedTime) {
        double moveSpeed = Math.abs(gravityWaveManager.getCurrentWave()) == 1 ? 2.5 : 1.5;

        if (isMovingLeft) {
            velocityX = -moveSpeed;
            facingRight = false;
        } else if (isMovingRight) {
            velocityX = moveSpeed;
            facingRight = true;
        } else {
            velocityX = 0;
        }

        x += velocityX;
        x = Math.max(0, Math.min(x, PlatformGame.SCREEN_WIDTH - PLAYER_SIZE));

        double actualGravity = getActualGravity();
        velocityY += actualGravity;
        y += velocityY;

        isOnGround = false;
        handleCollisions(platforms, actualGravity);
        handleBoundaryCollision(actualGravity);

        shootCountdown.update(elapsedTime);
    }

    private void handleCollisions(List<Platform> platforms, double actualGravity) {
        for (Platform platform : platforms) {
            if (checkCollision(x, y, PLAYER_SIZE, PLAYER_SIZE,
                    platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight())) {

                boolean gravityPositive = actualGravity > 0;

                if (gravityPositive) {
                    if (velocityY > 0) {
                        y = platform.getY() - PLAYER_SIZE;
                        isOnGround = true;
                    } else {
                        y = platform.getY() + platform.getHeight();
                    }
                } else {
                    if (velocityY < 0) {
                        y = platform.getY() + platform.getHeight();
                        isOnGround = true;
                    } else {
                        y = platform.getY() - PLAYER_SIZE;
                    }
                }

                velocityY = 0;
                isJumping = false;
            }
        }
    }

    private void handleBoundaryCollision(double actualGravity) {
        if (actualGravity > 0) {
            if (y + PLAYER_SIZE > PlatformGame.SCREEN_HEIGHT) {
                y = PlatformGame.SCREEN_HEIGHT - PLAYER_SIZE;
                isOnGround = true;
                velocityY = 0;
            }
        } else {
            if (y < 0) {
                y = 0;
                isOnGround = true;
                velocityY = 0;
            }
        }
    }

    public Bullet shoot() {
        if (shootCountdown.isFinished()) {
            shootCountdown.reset();
            double bulletX = facingRight ? x + PLAYER_SIZE : x - Bullet.SIZE;
            double bulletY = y + (PLAYER_SIZE - Bullet.SIZE) / 2;
            return new Bullet(bulletX, bulletY, facingRight, bulletSpeed, playerId);
        }
        return null;
    }

    public void draw(GraphicsContext gc) {
        double imgX = x;
        double imgY = y;

        if (isInverted) {
            imgY = y + PLAYER_SIZE;
        }
        gc.save();
        if (!facingRight) {
            gc.translate(imgX + PLAYER_SIZE, imgY);
            gc.scale(-1, 1);
        } else {
            gc.translate(imgX, imgY);
        }

        if (isInverted) {
            gc.scale(1, -1);
        }
        gc.drawImage(playerImage, 0, 0);
        gc.restore();
        if (!shootCountdown.isFinished()) {
            gc.setFill(Color.WHITE);
            gc.fillText(shootCountdown.getFormattedRemainingTime(),
                    x + PLAYER_SIZE / 2 - 10, y - 10);
        }
    }

    private boolean checkCollision(double x1, double y1, double w1, double h1,
                                   double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setBulletSpeed(double bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public void setShootInterval(double shootInterval) {
        this.shootInterval = shootInterval;
        double remainingTime = shootCountdown.getRemainingTime();
        shootCountdown = new CountdownUtil(shootInterval);
        if (!shootCountdown.isFinished()) {
            shootCountdown.update(remainingTime);
        }
    }

    public double getShootInterval() {
        return this.shootInterval;
    }

    public double getBulletSpeed() {
        return this.bulletSpeed;
    }

    public double getRemainingTime() {
        return shootCountdown.getRemainingTime();
    }

    public void setRemainingTime(double remainingTime) {
        shootCountdown.setRemainingTime(remainingTime);
    }

    public int getPlayerId() {
        return playerId;
    }

    // 添加一个访问器，用于判断是否在地面上
    public boolean isOnGround() {
        return isOnGround;
    }
}
