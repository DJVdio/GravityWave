package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.powerBall.PowerBall;
import org.example.powerBall.ShootingSpeedUpBall;
import org.example.util.GravityWaveManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PlatformGame extends Application {
    static final int SCREEN_WIDTH = 1600;
    static final int SCREEN_HEIGHT = 900;

    private Player player1;
    private Player player2;
    private List<Bullet> bullets = new ArrayList<>();
    private Platform platform = new Platform();
    private List<Platform> platforms = platform.getPlatforms();
    private boolean gameOver = false;
    private String winnerText = "";
    private GravityWaveManager gravityWaveManager = new GravityWaveManager();
    private List<PowerBall> powerBalls = new ArrayList<>();
    private final Random random = new Random();
    private double lastPowerBallSpawn = 0;

    // 新增 Replay 按钮
    private Button replayButton;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // 初始化 Replay 按钮
        replayButton = new Button("Replay");
        replayButton.setFont(new Font("Arial", 24));
        replayButton.setLayoutX(SCREEN_WIDTH / 2 - 60);
        replayButton.setLayoutY(SCREEN_HEIGHT / 2 + 100);
        replayButton.setVisible(false);
        replayButton.setOnAction(e -> resetGame());
        root.getChildren().add(replayButton);

        // 初始化玩家
        double groundY = SCREEN_HEIGHT - 20 - Player.PLAYER_SIZE;
        player1 = new Player(100, groundY, 1, 1.5, gravityWaveManager);
        player2 = new Player(400, 100, 2, 1.5, gravityWaveManager);

        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (gameOver) return;
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.D || e.getCode() == KeyCode.K) {
                player1.handleKeyPress(e.getCode());
            } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.NUMPAD2) {
                player2.handleKeyPress(e.getCode());
            } else if (e.getCode() == KeyCode.J) {
                Bullet bullet = player1.shoot();
                if (bullet != null) bullets.add(bullet);
            } else if (e.getCode() == KeyCode.NUMPAD1) {
                Bullet bullet = player2.shoot();
                if (bullet != null) bullets.add(bullet);
            }
        });

        scene.setOnKeyReleased(e -> {
            if (gameOver) return;
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.D || e.getCode() == KeyCode.K) {
                player1.handleKeyRelease(e.getCode());
            } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.NUMPAD2) {
                player2.handleKeyRelease(e.getCode());
            }
        });

        new AnimationTimer() {
            private long lastUpdateTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (!gameOver) {
                    double elapsedTime = (now - lastUpdateTime) / 1_000_000_000.0;
                    lastUpdateTime = now;

                    gravityWaveManager.update(elapsedTime);
                    player1.update(platforms, player2, elapsedTime);
                    player2.update(platforms, player1, elapsedTime);
                    updateBullets();
                    checkBulletCollision();
                    updatePowerBalls(elapsedTime);
                    checkPowerBallCollision();
                    spawnPowerBall(elapsedTime);
                }
                draw(gc);
            }
        }.start();

        primaryStage.setTitle("Platform Game with Power-Ups");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (bullet.isOutOfBounds()) {
                iterator.remove();
            }
        }
    }

    private void checkBulletCollision() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (bullet.hits(player1)) {
                gameOver(2);
                iterator.remove();
                break;
            } else if (bullet.hits(player2)) {
                gameOver(1);
                iterator.remove();
                break;
            }
        }
    }

    private void spawnPowerBall(double elapsedTime) {
        lastPowerBallSpawn += elapsedTime;
        if (lastPowerBallSpawn >= 4.0) {
            generatePowerBall();
            lastPowerBallSpawn = 0;
        }
    }

    private void generatePowerBall() {
        double x = random.nextDouble() * (SCREEN_WIDTH - 50);
        double y = random.nextDouble() * (SCREEN_HEIGHT - 50);
        PowerBall powerBall = new ShootingSpeedUpBall(x, y);
        powerBalls.add(powerBall);
    }

    private void updatePowerBalls(double elapsedTime) {
        Iterator<PowerBall> iterator = powerBalls.iterator();
        while (iterator.hasNext()) {
            PowerBall powerBall = iterator.next();
            powerBall.update(elapsedTime);
            if (powerBall.isExpired(System.nanoTime() / 1_000_000_000.0)) {
                iterator.remove();
            }
        }
    }

    private void checkPowerBallCollision() {
        Iterator<PowerBall> iterator = powerBalls.iterator();
        while (iterator.hasNext()) {
            PowerBall powerBall = iterator.next();
            if (powerBall.checkCollision(player1.getX(), player1.getY(), Player.PLAYER_SIZE)) {
                powerBall.applyEffect(player1);
                iterator.remove();
            } else if (powerBall.checkCollision(player2.getX(), player2.getY(), Player.PLAYER_SIZE)) {
                powerBall.applyEffect(player2);
                iterator.remove();
            }
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(gravityWaveManager.getWaveColor());
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (!gameOver) {
            player1.draw(gc);
            player2.draw(gc);

            gc.setFill(Color.WHITE);
            for (Platform platform : platforms) {
                gc.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
            }

            for (Bullet bullet : bullets) {
                bullet.draw(gc);
            }

            for (PowerBall powerBall : powerBalls) {
                powerBall.draw(gc);
            }

            drawGravityWaveInfo(gc);
        }

        if (gameOver) {
            gc.setFont(new Font("Arial", 60));
            gc.setFill(Color.WHITE);
            gc.fillText("Game Over", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 - 50);

            gc.setFont(new Font("Arial", 40));
            gc.fillText(winnerText, SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 50);
        }
    }

    private void drawGravityWaveInfo(GraphicsContext gc) {
        gc.setFont(new Font("Arial", 24));
        gc.setFill(Color.YELLOW);
        String waveName = gravityWaveManager.getCurrentWaveName();
        String waveStatus = "CURRENT: " + waveName;
        String nextChange = "NEXT CHANGE: " + String.format("%.1f", gravityWaveManager.getRemainingTime());
        gc.fillText(waveStatus, 20, 30);
        gc.fillText(nextChange, 20, 60);
    }

    public void gameOver(int winnerId) {
        gameOver = true;
        winnerText = "Player " + winnerId + " Wins!";
        replayButton.setVisible(true); // 显示 Replay 按钮
    }

    // 重置游戏状态
    private void resetGame() {
        gameOver = false;
        winnerText = "";
        replayButton.setVisible(false);

        // 重置玩家位置和属性
        double groundY = SCREEN_HEIGHT - 20 - Player.PLAYER_SIZE;
        player1 = new Player(100, groundY, 1, 1.5, gravityWaveManager);
        player2 = new Player(400, 100, 2, 1.5, gravityWaveManager);

        // 清空子弹和强化球
        bullets.clear();
        powerBalls.clear();
        lastPowerBallSpawn = 0;

        // 重置重力波管理器
        gravityWaveManager.reset();
    }

    public static void main(String[] args) {
        launch(args);
    }
}