package no.sandramoen.libgdx31.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.gui.BaseProgressBar;
import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;


public class LevelScreen extends BaseScreen {


    public static int score;
    public static TypingLabel topLabel;

    private static BaseProgressBar healthBar;


    public LevelScreen() {
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
        else if (keycode == Keys.T)
            healthBar.incrementPercentage(10);
        else if (keycode == Keys.Y)
            healthBar.decrementPercentage(10);

        return super.keyDown(keycode);
    }


    public static void looseHealth() {
        healthBar.decrementPercentage(10);
    }


    private void initializeActors() {
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
        Grid grid = new Grid(gridWidth, gridHeight, mainStage, shapeDrawer, spacing, margins);
    }


    private void initializeGUI() {
        topLabel = new TypingLabel("0", AssetLoader.getLabelStyle("Play-Bold59white"));
        topLabel.setAlignment(Align.center);

        healthBar = new BaseProgressBar(0, 0, uiStage);
        healthBar.incrementPercentage(100);
        healthBar.setProgressBarColor(Color.valueOf("de3a68"));

        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add(topLabel)
            .height(topLabel.getPrefHeight() * 1.5f)
            //.expandY()
            .top()
            .row()
        ;

        uiTable.add(healthBar)
            .expandY()
            .top()
            .row();

        //uiTable.setDebug(true);
    }


    private void restart() {
        BaseGame.setActiveScreen(new LevelScreen());
        LevelScreen.score = 0;
    }
}
