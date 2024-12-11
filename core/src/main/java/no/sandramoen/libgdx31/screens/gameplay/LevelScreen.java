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
    public void initialize() {}


    @Override
    public void update(float delta) {}


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


    public static void looseHealth() {
        healthBar.decrementPercentage(10);
        if (healthBar.level >= 10) {
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
    }



    private void initializeActors() {
        new Background(0, 0, mainStage, new Vector3(.06f, .06f, .06f));

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
        healthBar.incrementPercentage(100);
        healthBar.setProgressBarColor(Color.valueOf("de3a68"));

        messageLabel = new TypingLabel("{CROWD}press '{RAINBOW}R{ENDRAINBOW}' to reincarnate", AssetLoader.getLabelStyle("Play-Bold59white"));
        messageLabel.getColor().a = 0.0f;
        messageLabel.setAlignment(Align.center);

        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add(scoreLabel)
            .height(scoreLabel.getPrefHeight() * 1.5f)
            //.expandY()
            .top()
            .align(Align.top)
            .row()
        ;

        uiTable.add(healthBar)
            .expandY()
            .top()
            .row();

        uiTable.add(messageLabel)
            .expandY()
            .top()
            .row();

        //uiTable.setDebug(true);
    }


    private void restart() {
        BaseGame.setActiveScreen(new LevelScreen("up"));
        LevelScreen.score = 0;
    }
}
