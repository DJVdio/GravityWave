//package GD1.powerBall;
//
//import GD1.Player;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//
//public class SuperBulletBall extends PowerBall {
//    private static final double SIZE = 20;
//    private static final double LIFETIME = 10.0; // 强化球存在时长（秒）
//
//    public SuperBulletBall(double x, double y) {
//        super(x, y, SIZE, LIFETIME);
//    }
//
//    @Override
//    public void update(double elapsedTime) {
//        // 这里可以添加强化球的动画或其他更新逻辑
//    }
//
//    @Override
//    public void draw(GraphicsContext gc) {
//        gc.setFill(Color.BLUE);
//        gc.fillOval(x, y, size, size);
//    }
//
//    @Override
//    public void applyEffect(Player player) {
//
//    }
//
//}
