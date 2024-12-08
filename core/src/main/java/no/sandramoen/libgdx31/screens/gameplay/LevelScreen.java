package no.sandramoen.libgdx31.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;


public class LevelScreen extends BaseScreen {


    private TypingLabel topLabel;


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
            BaseGame.setActiveScreen(new LevelScreen());
        else if (keycode == Keys.T)
            BaseGame.setActiveScreen(new LevelSelectScreen());
        else if (keycode == Keys.NUMPAD_0) {
            OrthographicCamera camera = (OrthographicCamera) mainStage.getCamera();
            camera.zoom += .1f;
        }
        return super.keyDown(keycode);
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
        topLabel = new TypingLabel("{SLOWER}G A M E   O V E R !", AssetLoader.getLabelStyle("Play-Bold59white"));
        topLabel.setAlignment(Align.top);

        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(topLabel).height(topLabel.getPrefHeight() * 1.5f).expandY().top().row();
        // uiTable.setDebug(true);
    }
}
