package com.invaders99.view;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.invaders99.model.Bullet;
import com.invaders99.model.Enemy;
import com.invaders99.model.Game;
import com.invaders99.model.Player;
import com.invaders99.util.Assets;

public class GameRenderer {
    private static final Color PLAYER_COLOR = new Color(0f, 1f, 1f, 1f);
    private static final Color ENEMY_COLOR = new Color(1f, 0.2f, 0.5f, 1f);
    private static final Color BULLET_COLOR = new Color(1f, 1f, 0.4f, 1f);
    private static final Color ENEMY_BULLET_COLOR = new Color(1f, 0.4f, 0.1f, 1f);

    private final Texture background;
    private final BitmapFont font;
    private final Texture playerTex;
    private final Texture enemyTex;
    private final Texture bulletTex;
    private final Texture enemyBulletTex;

    public GameRenderer(Assets assets) {
        this.background = assets.getStarsBackground();
        this.font = assets.getDefaultFont();
        this.playerTex = Assets.createColorTexture(PLAYER_COLOR);
        this.enemyTex = Assets.createColorTexture(ENEMY_COLOR);
        this.bulletTex = Assets.createColorTexture(BULLET_COLOR);
        this.enemyBulletTex = Assets.createColorTexture(ENEMY_BULLET_COLOR);
    }

    public void render(Game model, SpriteBatch batch, Viewport viewport) {
        batch.begin();

        Camera cam = viewport.getCamera();
        float camW = cam.viewportWidth;
        float camH = cam.viewportHeight;
        batch.draw(background, cam.position.x - camW / 2f, cam.position.y - camH / 2f, camW, camH);

        for (Bullet b : model.bullets) {
            batch.draw(bulletTex, b.x - Bullet.WIDTH / 2f, b.y - Bullet.HEIGHT / 2f, Bullet.WIDTH, Bullet.HEIGHT);
        }

        for (Bullet b : model.enemyBullets) {
            batch.draw(enemyBulletTex, b.x - Bullet.WIDTH / 2f, b.y - Bullet.HEIGHT / 2f, Bullet.WIDTH, Bullet.HEIGHT);
        }

        for (Enemy e : model.enemies) {
            batch.draw(enemyTex, e.x - Enemy.WIDTH / 2f, e.y - Enemy.HEIGHT / 2f, Enemy.WIDTH, Enemy.HEIGHT);
        }

        Player p = model.player;
        if (model.invincible) {
            batch.setColor(1f, 1f, 1f, 0.4f);
        }
        batch.draw(playerTex, p.x - Player.WIDTH / 2f, p.y - Player.HEIGHT / 2f, Player.WIDTH, Player.HEIGHT);
        if (model.invincible) {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        font.getData().setScale(0.5f);
        font.draw(batch, "SCORE: " + model.score, 10f, Game.WORLD_HEIGHT - 10f);
        font.draw(batch, "LIVES: " + model.lives, 10f, Game.WORLD_HEIGHT - 30f);
        font.getData().setScale(1f); // reset scale

        batch.end();
    }

    public void dispose() {
        playerTex.dispose();
        enemyTex.dispose();
        bulletTex.dispose();
        enemyBulletTex.dispose();
    }
}
