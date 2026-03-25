package com.invaders99.controller.state;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.invaders99.controller.WaveController;
import com.invaders99.model.Bullet;
import com.invaders99.model.Enemy;
import com.invaders99.model.Game;
import com.invaders99.model.Player;
import com.invaders99.service.AudioService;
import com.invaders99.util.Assets;

public class GameController extends InputAdapter {
    private static final float SHOOT_INTERVAL = 0.3f;
    private static final float INVINCIBLE_DURATION = 1.5f;

    private final Game model;
    private final Viewport viewport;
    private final Assets assets;
    private final WaveController waveController;
    private final Vector3 touchPoint = new Vector3();

    private float shootTimer;
    private float invincibleTimer;

    public GameController(Game model, Viewport viewport, Assets assets, WaveController waveController) {
        this.model = model;
        this.viewport = viewport;
        this.assets = assets;
        this.waveController = waveController;
    }

    public void update(float delta) {
        if (model.isGameOver()) return;

        updateInvincibility(delta);
        autoShoot(delta);
        waveController.update(delta, model);
        moveBullets(delta);
        moveEnemies(delta);
        checkCollisions();
        cleanupOffscreen();
    }

    private void updateInvincibility(float delta) {
        if (model.invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0) {
                model.invincible = false;
            }
        }
    }

    private void autoShoot(float delta) {
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer -= SHOOT_INTERVAL;
            model.bullets.add(new Bullet(model.player.x, model.player.y + Player.HEIGHT / 2f));
        }
    }

    private void moveBullets(float delta) {
        for (Bullet b : model.bullets) {
            b.y += Bullet.SPEED * delta;
        }
        for (Bullet b : model.enemyBullets) {
            b.y -= Bullet.ENEMY_SPEED * delta;
        }
    }

    private void moveEnemies(float delta) {
        for (Enemy e : model.enemies) {
            e.y -= Enemy.SPEED * delta;
        }
    }

    private void checkCollisions() {
        // Player bullet vs enemy
        for (int i = model.bullets.size - 1; i >= 0; i--) {
            Bullet b = model.bullets.get(i);
            for (int j = model.enemies.size - 1; j >= 0; j--) {
                Enemy e = model.enemies.get(j);
                if (b.getBounds().overlaps(e.getBounds())) {
                    model.bullets.removeIndex(i);
                    model.enemies.removeIndex(j);
                    model.score++;
                    AudioService.getInstance().playSound(assets.getLaserSound());
                    break;
                }
            }
        }

        // Player-enemy collision
        if (!model.invincible) {
            for (int i = model.enemies.size - 1; i >= 0; i--) {
                Enemy e = model.enemies.get(i);
                if (model.player.getBounds().overlaps(e.getBounds())) {
                    model.enemies.removeIndex(i);
                    hitPlayer();
                    break;
                }
            }
        }

        // Enemy bullet vs player
        if (!model.invincible) {
            for (int i = model.enemyBullets.size - 1; i >= 0; i--) {
                Bullet b = model.enemyBullets.get(i);
                if (b.getBounds().overlaps(model.player.getBounds())) {
                    model.enemyBullets.removeIndex(i);
                    hitPlayer();
                    break;
                }
            }
        }
    }

    private void cleanupOffscreen() {
        for (int i = model.bullets.size - 1; i >= 0; i--) {
            if (model.bullets.get(i).y > Game.WORLD_HEIGHT + 20f) {
                model.bullets.removeIndex(i);
            }
        }
        for (int i = model.enemyBullets.size - 1; i >= 0; i--) {
            if (model.enemyBullets.get(i).y < -20f) {
                model.enemyBullets.removeIndex(i);
            }
        }
        for (int i = model.enemies.size - 1; i >= 0; i--) {
            if (model.enemies.get(i).y < -Enemy.HEIGHT) {
                model.enemies.removeIndex(i);
                model.lives--;
            }
        }
    }

    private void hitPlayer() {
        model.lives--;
        model.invincible = true;
        invincibleTimer = INVINCIBLE_DURATION;
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
        model.player.x = MathUtils.clamp(touchPoint.x, Player.WIDTH / 2f, Game.WORLD_WIDTH - Player.WIDTH / 2f);
    }
}
