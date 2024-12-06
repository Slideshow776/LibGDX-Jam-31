package no.sandramoen.libgdx31.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.actors.Element;
import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;


public class LevelScreen extends BaseScreen {


    private Element player;
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
        player = new Element(3, 7, mainStage, shapeDrawer);
    }


    private void initializeGUI() {
        topLabel = new TypingLabel("{SLOWER}G A M E   O V E R !", AssetLoader.getLabelStyle("Play-Bold59white"));
        topLabel.setAlignment(Align.top);

        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(topLabel).height(topLabel.getPrefHeight() * 1.5f).expandY().top().row();
        // uiTable.setDebug(true);
    }
}
