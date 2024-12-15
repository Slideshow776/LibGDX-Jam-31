package no.sandramoen.libgdx31.screens.gameplay;

import static no.sandramoen.libgdx31.utils.AssetLoader.heartbeatSound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.actors.Background;
import no.sandramoen.libgdx31.gui.BaseProgressBar;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;
import no.sandramoen.libgdx31.utils.GameUtils;


public class LevelScreen extends BaseScreen {


    public static int score;
    public static TypingLabel scoreLabel;
    public static TypingLabel messageLabel;

    private static BaseProgressBar healthBar;
    private static BaseProgressBar manaBar;
    private static Grid grid;
    private String direction;


    public LevelScreen(String direction) {
        this.direction = direction;

        GameUtils.stopAllMusic();
        AssetLoader.levelMusic.play();
        AssetLoader.levelMusic.setVolume(BaseGame.musicVolume);
        float songDuration = 85f;  // Replace with the actual duration of the music
        float randomStartPosition = MathUtils.random(0f, songDuration);
        AssetLoader.levelMusic.setPosition(randomStartPosition);
        AssetLoader.levelMusic.setLooping(true);

        initializeActors();
        initializeGUI();
    }


    @Override
    public void initialize() {
    }


    @Override
    public void update(float delta) {
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        else if (keycode == Keys.R)
            restart();
        else if (keycode == Keys.NUMPAD_0) {
            OrthographicCamera camera = (OrthographicCamera) mainStage.getCamera();
            camera.zoom += .1f;
        }

        return super.keyDown(keycode);
    }


    public static void looseMana(int amount) {
        manaBar.decrementPercentage(amount);
        AssetLoader.manaUseSound.play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0.0f);
        BaseGame.mana = manaBar.level;
    }


    public static void gainMana(int amount) {
        if (amount * 10 + manaBar.level > 100) {
            System.out.println("TODO: implement mana surge damage effects");
            healthBar.decrementPercentage(((amount * 10) + manaBar.level) - 100);
            looseHealth(((amount * 10) + manaBar.level) - 100);
            AssetLoader.manaSurgeSound.play(BaseGame.soundVolume);

            if (healthBar.level <= 0)
                messageLabel.setText("{COLOR=#389bc2}{WIND}The wizards magic ran wild!{ENDWIND}{CLEARCOLOR}\n{JOLT}They were forced to retreat!{ENDJOLT}\n{CROWD}Press '{RAINBOW}R{ENDRAINBOW}' to return.");
        }
        manaBar.incrementPercentage(amount * 10);
        BaseGame.mana = manaBar.level;
    }


    public static void looseHealth(int amount) {
        healthBar.decrementPercentage(amount);
        BaseGame.health = healthBar.level;
        if (healthBar.level >= 1) {
            // Calculate pitch based on the level, ranging from 0.8 to 1.2
            float pitch = 1.2f - ((healthBar.level - 10) / 100.0f) * (1.2f - 0.8f);  // Linearly map level to pitch between 0.8 and 1.2
            AssetLoader.healthLossSound.play(BaseGame.soundVolume, pitch, 0.0f);
        }

        if (healthBar.level != 0)
            return;

        grid.clearBoard();
        AssetLoader.gongSound.play(BaseGame.soundVolume);
        messageLabel.addAction(Actions.sequence(
            Actions.delay(1.0f),
            Actions.fadeIn(5.0f)
        ));
    }


    public static void gainHealth(int healthGained) {
        if (healthBar.level >= 100)
            return;

        healthBar.incrementPercentage(healthGained);

        // Calculate volume based on current health level, making it louder as health decreases
        float volume = BaseGame.soundVolume * 1.5f + (1.0f - (healthBar.level / 100.0f)) * 0.5f;
        // Play the heartbeat sound with a random pitch between 0.8 and 1.2
        heartbeatSound.play(volume, MathUtils.random(0.8f, 1.2f), 0.0f);
        BaseGame.health = healthBar.level;
    }


    private void initializeActors() {
        new Background(0, 0, mainStage, direction);

        int gridWidth = 7;  // Number of columns
        int gridHeight = 9; // Number of rows
        float spacing = 0.2f;  // Space between shapes

        // Create an Array for the margins: [left, right, top, bottom]
        float margin = spacing;
        Array<Float> margins = new Array<>();
        margins.add(margin);  // Left margin
        margins.add(margin);  // Right margin
        margins.add(margin);  // Top margin
        margins.add(-21.25f * margin);  // Bottom margin, magic number is a hack

        // Create the grid with specified margins and spacing
        grid = new Grid(gridWidth, gridHeight, mainStage, uiStage, shapeDrawer, spacing, margins, direction);
    }


    private void initializeGUI() {
        scoreLabel = new TypingLabel("0", AssetLoader.getLabelStyle("Play-Bold59white"));
        scoreLabel.setAlignment(Align.center);

        healthBar = new BaseProgressBar(0, 0, uiStage);
        healthBar.animationDuration = 1.0f;
        healthBar.incrementPercentage(BaseGame.health);
        healthBar.animationDuration = 0.25f;
        healthBar.setColor(Color.valueOf("780c72"));
        healthBar.setProgressBarColor(Color.valueOf("de3a68"));

        manaBar = new BaseProgressBar(0, 2, uiStage);
        manaBar.setProgress(100);
        manaBar.animationDuration = 1.0f;
        manaBar.decrementPercentage(100 - BaseGame.mana);
        manaBar.animationDuration = 0.25f;
        manaBar.setColor(Color.valueOf("2c1861"));
        manaBar.setProgressBarColor(Color.valueOf("69f7ff"));

        messageLabel = new TypingLabel("{JOLT}They barely made it back!{ENDJOLT}\n{CROWD}Press '{RAINBOW}R{ENDRAINBOW}' to return.", AssetLoader.getLabelStyle("Play-Bold59white"));
        messageLabel.getColor().a = 0.0f;
        messageLabel.setAlignment(Align.center);

        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add(scoreLabel)
            .height(scoreLabel.getPrefHeight() * 1.5f)
            .top()
            .align(Align.top)
            .row()
        ;

        uiTable.add(healthBar)
            .top()
            .row();

        uiTable.add(manaBar)
            .top()
            .expandY()
            .row();

        uiTable.add(messageLabel)
            .expandY()
            .top()
            .row();

        //uiTable.setDebug(true);
    }


    private void restart() {
        LevelScreen.score = 0;
        BaseGame.health = 100;
        BaseGame.mana = 0;
        BaseGame.setActiveScreen(new LevelScreen("up"));
    }
}
