package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.powerBall.*;
import org.example.util.GravityWaveManager;
import org.example.util.ParticleSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PlatformGame extends Application {
    public static final int SCREEN_WIDTH = 1600;
    public static final int SCREEN_HEIGHT = 900;

    private Player player1;
    private Player player2;
    private static List<Bullet> bullets = new ArrayList<>();
    private Platform platform = new Platform();
    private List<Platform> platforms = platform.getPlatforms();
    private boolean gameOver = false;
    private String winnerText = "";
    private GravityWaveManager gravityWaveManager = new GravityWaveManager();
    private List<PowerBall> powerBalls = new ArrayList<>();
    private final Random random = new Random();
    private double lastPowerBallSpawn = 0;
    private Image platformImage = platform.getPlatformImage();

    // 粒子系统
    private ParticleSystem particleSystem = new ParticleSystem();

    // 音效（设为静态，便于在其他类中调用）
    private static MediaPlayer shootSound;
    private static MediaPlayer jumpSound;
    private static MediaPlayer bgmPlayer;

    // Replay 按钮
    private Button replayButton;

    // 介绍页面相关
    private Scene introScene;
    private boolean introShown = false;

    @Override
    public void start(Stage primaryStage) {
        createIntroScene(primaryStage); // 先创建介绍页面
        primaryStage.setScene(introScene);
        primaryStage.setTitle("Platform Game - Introduction");
        primaryStage.show();
    }

    // 创建介绍页面
    private void createIntroScene(Stage primaryStage) {
        Pane introRoot = new Pane();
        introScene = new Scene(introRoot, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 背景
        Rectangle bg = new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        bg.setFill(Color.DARKGRAY);

        // 玩家1信息
        VBox player1Box = createPlayerInfo(
                "player1.png",
                "Player 1 Controls",
                "A/D - Move Left/Right",
                "K - Jump",
                "J - Shoot"
        );
        player1Box.setLayoutX(50);
        player1Box.setLayoutY(50);

        // 玩家2信息
        VBox player2Box = createPlayerInfo(
                "player2_Reverse.png",
                "Player 2 Controls",
                "←/→ - Move Left/Right",
                "NumPad2 - Jump",
                "NumPad1 - Shoot"
        );
        player2Box.setLayoutX(SCREEN_WIDTH - 200);
        player2Box.setLayoutY(50);

        // 游戏规则
        Text rules = new Text(
                "Game Rules:\n" +
                        "- Shoot the opponent to win!\n" +
                        "- Collect Power Balls for special effects:\n" +
                        "  Red: Spawn attack bullets to kill your enemy\n" +
                        "  Green: Increase shooting speed and reduce shooting CD\n" +
                        "  Blue: You can shoot more bullets in one time\n" +
                        "- Gravity changes every 10 seconds"
        );
        rules.setFont(Font.font(24));
        rules.setFill(Color.WHITE);
        rules.setTextAlignment(TextAlignment.CENTER);
        rules.setWrappingWidth(SCREEN_WIDTH - 100);
        rules.setX(50);
        rules.setY(SCREEN_HEIGHT / 2 - 100);

        // 开始按钮
        Button startBtn = new Button("Start Game");
        startBtn.setFont(Font.font(30));
        startBtn.setPrefSize(400, 120);
        startBtn.setLayoutX(SCREEN_WIDTH / 2 - 200);
        startBtn.setLayoutY(SCREEN_HEIGHT - 150);
        startBtn.setOnAction(e -> {
            primaryStage.setScene(new Scene(new Pane())); // 临时场景
            initializeGame(primaryStage); // 初始化游戏
        });

        introRoot.getChildren().addAll(bg, player1Box, player2Box, rules, startBtn);
    }

    // 创建玩家信息面板的辅助方法
    private VBox createPlayerInfo(String imagePath, String title, String... controls) {
        ImageView playerImage = new ImageView(new Image(imagePath, 100, 100, true, true));
        Text titleText = new Text(title);
        titleText.setFont(Font.font(20));
        titleText.setFill(Color.WHITE);

        VBox controlsBox = new VBox(10);
        for (String control : controls) {
            Text t = new Text(control);
            t.setFont(Font.font(16));
            t.setFill(Color.WHITE);
            controlsBox.getChildren().add(t);
        }

        return new VBox(20, playerImage, titleText, controlsBox);
    }

    // 分离游戏初始化逻辑
    private void initializeGame(Stage stage) {
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
        player1 = new Player(100, groundY, 1, 1.5, gravityWaveManager, particleSystem);
        player2 = new Player(400, 100, 2, 1.5, gravityWaveManager, particleSystem);

        // 加载音效
        try {
            shootSound = new MediaPlayer(new Media(getClass().getResource("/sounds/shoot.wav").toString()));
            jumpSound = new MediaPlayer(new Media(getClass().getResource("/sounds/jump.wav").toString()));
            Media bgmMedia = new Media(getClass().getResource("/sounds/bgm.mp3").toString());
            bgmPlayer = new MediaPlayer(bgmMedia);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 循环播放
            bgmPlayer.setVolume(1);
            bgmPlayer.play();
        } catch (Exception e) {
            System.err.println("音效文件加载失败: " + e.getMessage());
        }

        Scene gameScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        gameScene.setOnKeyPressed(e -> {
            if (gameOver) return;
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.D) {
                player1.handleKeyPress(e.getCode());
            } else if (e.getCode() == KeyCode.K) {
                // 玩家 1 跳跃，并播放跳跃音效
                if (player1.isOnGround()) {
                    player1.handleKeyPress(e.getCode());
                    playJumpSound();
                }
            } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                player2.handleKeyPress(e.getCode());
            } else if (e.getCode() == KeyCode.NUMPAD2) {
                if (player2.isOnGround()) {
                    player2.handleKeyPress(e.getCode());
                    playJumpSound();
                }
            } else if (e.getCode() == KeyCode.J) {
                List<Bullet> bullets = player1.shoot(); // 需要修改shoot方法返回多个子弹
                if (bullets != null) {
                    PlatformGame.addBullets(bullets);
                    shootSound.stop();
                    shootSound.play();
                }
            } else if (e.getCode() == KeyCode.NUMPAD1) {
                List<Bullet> bullets = player2.shoot(); // 需要修改shoot方法返回多个子弹
                if (bullets != null) {
                    PlatformGame.addBullets(bullets);
                    shootSound.stop();
                    shootSound.play();
                }
            }
        });

        gameScene.setOnKeyReleased(e -> {
            if (gameOver) return;
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.D || e.getCode() == KeyCode.K) {
                player1.handleKeyRelease(e.getCode());
            } else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.NUMPAD2) {
                player2.handleKeyRelease(e.getCode());
            }
        });

        stage.setScene(gameScene);
        stage.setTitle("Platform Game with Power-Ups");

        // 原有动画定时器
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
                if (gameOver){
                    lastUpdateTime = now;
                }
                draw(gc);
            }
        }.start();
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            // 添加子弹尾迹效果
            particleSystem.addEffect(
                    bullet.getX() + Bullet.SIZE / 2,
                    bullet.getY() + Bullet.SIZE / 2,
                    ParticleSystem.EffectType.BULLET_TRAIL
            );
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
                powerBall.applyEffect(player1, player2);
                iterator.remove();
            } else if (powerBall.checkCollision(player2.getX(), player2.getY(), Player.PLAYER_SIZE)) {
                powerBall.applyEffect(player2, player1);
                iterator.remove();
            }
        }
    }

    private void draw(GraphicsContext gc) {
        double t = System.nanoTime() / 1_000_000_000.0;
        if(!gameOver){
            gc.setFill(gravityWaveManager.getWaveColor());
        }
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (!gameOver) {
            // 绘制粒子效果
            particleSystem.updateAndDraw(gc, 0.016); // 假设帧率为60FPS

            player1.draw(gc);
            player2.draw(gc);

//            gc.setFill(Color.WHITE);
            for (Platform platform : platforms) {
//                gc.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
                if (platformImage != null) {
                    // 使用图片原始的宽高作为平铺单元
                    ImagePattern pattern = new ImagePattern(
                            platformImage,
                            0, 0,
                            platformImage.getWidth(), platformImage.getHeight(),
                            false
                    );
                    gc.setFill(pattern);
                    gc.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
                }
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
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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
        replayButton.setVisible(true);
    }

    private void resetGame() {
        gameOver = false;
        winnerText = "";
        replayButton.setVisible(false);

        // 重置玩家位置和属性
        double groundY = SCREEN_HEIGHT - 20 - Player.PLAYER_SIZE;
        player1 = new Player(100, groundY, 1, 1.5, gravityWaveManager, particleSystem);
        player2 = new Player(400, 100, 2, 1.5, gravityWaveManager, particleSystem);

        // 清空子弹和强化球
        bullets.clear();
        powerBalls.clear();
        lastPowerBallSpawn = 0;

        // 重置重力波管理器
        gravityWaveManager.reset();
    }

    private void generatePowerBall() {
        double x = random.nextDouble() * (SCREEN_WIDTH - 50);
        double y = random.nextDouble() * (SCREEN_HEIGHT - 50);

        PowerBall powerBall;
        switch (random.nextInt(BallTypeEnum.values().length)) {
            case 0:
                powerBall = new AttackBall(x, y);
                break;
            case 1:
                powerBall = new ShootingSpeedUpBall(x, y);
                break;
            case 2:  // 新增蓝球类型
                powerBall = new SuperBulletBall(x, y);
                break;
            default:
                throw new IllegalStateException("Unexpected value");
        }

        powerBalls.add(powerBall);
    }

    public static void addBullets(List<Bullet> newBullets) {
        bullets.addAll(newBullets);
    }

    // 静态方法，用于播放跳跃音效
    public static void playJumpSound() {
        if (jumpSound != null) {
            jumpSound.stop();
            jumpSound.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
