package com.invaders99.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.invaders99.game.model.Bullet;
import com.invaders99.game.model.Enemy;
import com.invaders99.game.model.GameModel;
import com.invaders99.game.model.Player;
import com.invaders99.util.Assets;

public class GameRenderer {
    private static final Color PLAYER_COLOR = new Color(0f, 1f, 1f, 1f);
    private static final Color ENEMY_COLOR = new Color(1f, 0.2f, 0.5f, 1f);
    private static final Color BULLET_COLOR = new Color(1f, 1f, 0.4f, 1f);

    private final Texture background;
    private final BitmapFont font;
    private final Texture playerTex;
    private final Texture enemyTex;
    private final Texture bulletTex;

    public GameRenderer(Assets assets) {
        this.background = assets.getStarsBackground();
        this.font = assets.getDefaultFont();
        this.playerTex = Assets.createColorTexture(PLAYER_COLOR);
        this.enemyTex = Assets.createColorTexture(ENEMY_COLOR);
        this.bulletTex = Assets.createColorTexture(BULLET_COLOR);
    }

    public void render(GameModel model, SpriteBatch batch) {
        batch.begin();

        // Background
        batch.draw(background, 0, 0, GameModel.WORLD_WIDTH, GameModel.WORLD_HEIGHT);

        // Bullets
        for (Bullet b : model.bullets) {
            batch.draw(bulletTex, b.x - Bullet.WIDTH / 2f, b.y - Bullet.HEIGHT / 2f, Bullet.WIDTH, Bullet.HEIGHT);
        }

        // Enemies
        for (Enemy e : model.enemies) {
            batch.draw(enemyTex, e.x - Enemy.WIDTH / 2f, e.y - Enemy.HEIGHT / 2f, Enemy.WIDTH, Enemy.HEIGHT);
        }

        // Player
        Player p = model.player;
        batch.draw(playerTex, p.x - Player.WIDTH / 2f, p.y - Player.HEIGHT / 2f, Player.WIDTH, Player.HEIGHT);

        // Score
        font.getData().setScale(0.5f);
        font.draw(batch, "SCORE: " + model.score, 10f, GameModel.WORLD_HEIGHT - 10f);

        batch.end();
    }

    public void dispose() {
        playerTex.dispose();
        enemyTex.dispose();
        bulletTex.dispose();
    }
}
