package com.invaders99.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.invaders99.model.Bullet;
import com.invaders99.model.Enemy;
import com.invaders99.model.Game;
import com.invaders99.model.Player;
import com.invaders99.util.Assets;

public class GameRenderer {
    private final Texture background;
    private final BitmapFont font;

    private final Texture playerSheet;
    private final TextureRegion playerFrame1;
    private final TextureRegion playerFrame2;

    private final Texture enemySheet;
    private final TextureRegion[] enemyFrames;

    private final Texture bulletSheet;
    private final TextureRegion bulletFrame1;
    private final TextureRegion bulletFrame2;

    private final Texture enemyBulletSheet;
    private final TextureRegion enemyBulletFrame1;
    private final TextureRegion enemyBulletFrame2;

    private float animTime = 0f;

    public GameRenderer(Assets assets) {
        this.background = assets.getStarsBackground();
        this.font = assets.getDefaultFont();

        this.playerSheet = new Texture("alien.png");
        this.playerSheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] playerFrames = TextureRegion.split(playerSheet, 32, 32);
        this.playerFrame1 = playerFrames[0][0];
        this.playerFrame2 = playerFrames.length > 1 ? playerFrames[1][0] : playerFrames[0][0];

        this.enemySheet = new Texture("enemy.png"); // rename to your actual filename
        this.enemySheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] enemyGrid = TextureRegion.split(enemySheet, 32, 32);
        this.enemyFrames = new TextureRegion[] {
            enemyGrid[0][0],
            enemyGrid[0][1],
            enemyGrid[1][0],
            enemyGrid[1][1]
        };

        this.bulletSheet = new Texture("bullet.png");
        this.bulletSheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] bulletFrames = TextureRegion.split(bulletSheet, 32, 32);
        this.bulletFrame1 = bulletFrames[0][0];
        this.bulletFrame2 = bulletFrames.length > 1 ? bulletFrames[1][0] : bulletFrames[0][0];

        this.enemyBulletSheet = new Texture("enemy_bullet.png");
        this.enemyBulletSheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] enemyBulletFrames = TextureRegion.split(enemyBulletSheet, 32, 32);
        this.enemyBulletFrame1 = enemyBulletFrames[0][0];
        this.enemyBulletFrame2 = enemyBulletFrames.length > 1 ? enemyBulletFrames[1][0] : enemyBulletFrames[0][0];
    }

    public void render(Game model, SpriteBatch batch, Viewport viewport) {
        animTime += Gdx.graphics.getDeltaTime();

        batch.begin();

        Camera cam = viewport.getCamera();
        float camW = cam.viewportWidth;
        float camH = cam.viewportHeight;

        batch.draw(
            background,
            cam.position.x - camW / 2f,
            cam.position.y - camH / 2f,
            camW,
            camH
        );

        TextureRegion currentBulletFrame =
            ((int) (animTime * 10f) % 2 == 0) ? bulletFrame1 : bulletFrame2;

        for (Bullet b : model.bullets) {
            batch.draw(
                currentBulletFrame,
                b.x - Bullet.WIDTH / 2f,
                b.y - Bullet.HEIGHT / 2f,
                Bullet.WIDTH,
                Bullet.HEIGHT
            );
        }

        TextureRegion currentEnemyBulletFrame =
            ((int) (animTime * 10f) % 2 == 0) ? enemyBulletFrame1 : enemyBulletFrame2;

        for (Bullet b : model.enemyBullets) {
            batch.draw(
                currentEnemyBulletFrame,
                b.x - Bullet.WIDTH / 2f,
                b.y - Bullet.HEIGHT / 2f,
                Bullet.WIDTH,
                Bullet.HEIGHT
            );
        }

        TextureRegion currentEnemyFrame = enemyFrames[(int) (animTime * 8f) % enemyFrames.length];

        for (Enemy e : model.enemies) {
            batch.draw(
                currentEnemyFrame,
                e.x - Enemy.WIDTH / 2f,
                e.y - Enemy.HEIGHT / 2f,
                Enemy.WIDTH,
                Enemy.HEIGHT
            );
        }

        Player p = model.player;
        TextureRegion currentPlayerFrame =
            ((int) (animTime * 6f) % 2 == 0) ? playerFrame1 : playerFrame2;

        if (model.invincible) {
            batch.setColor(1f, 1f, 1f, 0.4f);
        }

        batch.draw(
            currentPlayerFrame,
            p.x - Player.WIDTH / 2f,
            p.y - Player.HEIGHT / 2f,
            Player.WIDTH,
            Player.HEIGHT
        );

        if (model.invincible) {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        font.getData().setScale(0.5f);
        font.draw(batch, "SCORE: " + model.score, 10f, Game.WORLD_HEIGHT - 10f);
        font.draw(batch, "LIVES: " + model.lives, 10f, Game.WORLD_HEIGHT - 30f);

        batch.end();
    }

    public void dispose() {
        playerSheet.dispose();
        enemySheet.dispose();
        bulletSheet.dispose();
        enemyBulletSheet.dispose();
    }
}
