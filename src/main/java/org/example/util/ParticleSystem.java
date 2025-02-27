package org.example.util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private List<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    public void addEffect(double x, double y, EffectType type) {
        switch (type) {
            case BULLET_TRAIL:
                for (int i = 0; i < 3; i++) {
                    double angle = random.nextDouble() * Math.PI * 2;
                    double speed = random.nextDouble() * 0.5;
                    particles.add(new Particle(
                            x, y,
                            Math.cos(angle) * speed,
                            Math.sin(angle) * speed,
                            0.3,
                            Color.rgb(255, 200, 0, 0.7),
                            2
                    ));
                }
                break;
            case JUMP_DUST:
                for (int i = 0; i < 10; i++) {
                    double angle = random.nextDouble() * Math.PI * 2;
                    double speed = random.nextDouble() * 2;
                    particles.add(new Particle(
                            x, y,
                            Math.cos(angle) * speed,
                            Math.sin(angle) * speed,
                            0.7,
                            Color.gray(0.5, 0.7),
                            3
                    ));
                }
                break;
        }
    }

    public void updateAndDraw(GraphicsContext gc, double elapsedTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            if (!p.update(elapsedTime)) {
                iterator.remove();
            } else {
                p.draw(gc);
            }
        }
    }

    public enum EffectType {
        BULLET_TRAIL, JUMP_DUST
    }
}
