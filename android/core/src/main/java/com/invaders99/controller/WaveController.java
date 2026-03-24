package com.invaders99.controller;

import com.badlogic.gdx.math.MathUtils;
import com.invaders99.model.Bullet;
import com.invaders99.model.Enemy;
import com.invaders99.model.Game;

public class WaveController {
    private static final float SPAWN_INTERVAL = 2.0f;
    private static final float ENEMY_SHOOT_INTERVAL = 1.5f;

    private float spawnTimer;
    private boolean spawnLeft = true;
    private float enemyShootTimer;

    public void update(float delta, Game model) {
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
            model.enemies.add(new Enemy(x, Game.WORLD_HEIGHT + Enemy.HEIGHT));
        }

        enemyShootTimer += delta;
        if (enemyShootTimer >= ENEMY_SHOOT_INTERVAL && model.enemies.size > 0) {
            enemyShootTimer = 0;
            Enemy shooter = model.enemies.random();
            model.enemyBullets.add(new Bullet(shooter.x, shooter.y - Enemy.HEIGHT / 2f, true));
        }
    }
}
