package com.invaders99.game.controller;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.invaders99.game.model.Bullet;
import com.invaders99.game.model.Enemy;
import com.invaders99.game.model.GameModel;
import com.invaders99.game.model.Player;

public class GameController extends InputAdapter {
    private static final float SHOOT_INTERVAL = 0.3f;
    private static final float SPAWN_INTERVAL = 2.0f;

    private final GameModel model;
    private final Viewport viewport;
    private final Vector3 touchPoint = new Vector3();

    private float shootTimer;
    private float spawnTimer;
    private boolean spawnLeft = true;

    public GameController(GameModel model, Viewport viewport) {
        this.model = model;
        this.viewport = viewport;
    }

    public void update(float delta) {
        // Auto-shoot
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer -= SHOOT_INTERVAL;
            model.bullets.add(new Bullet(model.player.x, model.player.y + Player.HEIGHT / 2f));
        }

        // Spawn enemies
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL;
            float x;
            if (spawnLeft) {
                x = MathUtils.random(30f, 150f);
            } else {
                x = MathUtils.random(210f, 330f);
            }
            spawnLeft = !spawnLeft;
            model.enemies.add(new Enemy(x, GameModel.WORLD_HEIGHT + Enemy.HEIGHT));
        }

        // Move bullets
        for (Bullet b : model.bullets) {
            b.y += Bullet.SPEED * delta;
        }

        // Move enemies
        for (Enemy e : model.enemies) {
            e.y -= Enemy.SPEED * delta;
        }

        // Collisions
        for (int i = model.bullets.size - 1; i >= 0; i--) {
            Bullet b = model.bullets.get(i);
            for (int j = model.enemies.size - 1; j >= 0; j--) {
                Enemy e = model.enemies.get(j);
                if (b.getBounds().overlaps(e.getBounds())) {
                    model.bullets.removeIndex(i);
                    model.enemies.removeIndex(j);
                    model.score++;
                    break;
                }
            }
        }

        // Remove off-screen bullets
        for (int i = model.bullets.size - 1; i >= 0; i--) {
            if (model.bullets.get(i).y > GameModel.WORLD_HEIGHT + 20f) {
                model.bullets.removeIndex(i);
            }
        }

        // Remove off-screen enemies
        for (int i = model.enemies.size - 1; i >= 0; i--) {
            if (model.enemies.get(i).y < -Enemy.HEIGHT) {
                model.enemies.removeIndex(i);
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        movePlayerTo(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        movePlayerTo(screenX, screenY);
        return true;
    }

    private void movePlayerTo(int screenX, int screenY) {
        touchPoint.set(screenX, screenY, 0);
        viewport.unproject(touchPoint);
        model.player.x = MathUtils.clamp(touchPoint.x, Player.WIDTH / 2f, GameModel.WORLD_WIDTH - Player.WIDTH / 2f);
    }
}
