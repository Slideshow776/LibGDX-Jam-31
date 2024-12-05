package no.sandramoen.libgdx31.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.actors.Element;
import no.sandramoen.libgdx31.actors.map.ImpassableTerrain;
import no.sandramoen.libgdx31.actors.map.TiledMapActor;
import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseActor;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;


public class LevelScreen extends BaseScreen {
    private TiledMap currentMap;

    private Array<ImpassableTerrain> impassables;
    private Element player;

    private TypingLabel topLabel;

    private TiledMapActor tilemap;

    public LevelScreen(TiledMap tiledMap) {
        currentMap = tiledMap;
        this.tilemap = new TiledMapActor(currentMap, mainStage);

        initializeActors();
        initializeGUI();
        mapCenterCamera();
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
            BaseGame.setActiveScreen(new LevelScreen(currentMap));
        else if (keycode == Keys.T)
            BaseGame.setActiveScreen(new LevelSelectScreen());
        else if (keycode == Keys.NUMPAD_0) {
            OrthographicCamera camera = (OrthographicCamera) mainStage.getCamera();
            camera.zoom += .1f;
        }
        return super.keyDown(keycode);
    }

    private void initializeActors() {
        impassables = new Array();
        loadActorsFromMap();
        // new Background(0, 0, mainStage);
    }

    private void loadActorsFromMap() {
        MapLoader mapLoader = new MapLoader(mainStage, tilemap, player, impassables);
        player = mapLoader.player;
    }

    private void mapCenterCamera() {
        new BaseActor(0, 0, mainStage).addAction(Actions.run(() -> {
            TiledMapActor.centerPositionCamera(mainStage);
            OrthographicCamera camera = (OrthographicCamera) mainStage.getCamera();
            camera.zoom = 1f;
        }));
    }

    private void initializeGUI() {
        topLabel = new TypingLabel("{SLOWER}G A M E   O V E R !", AssetLoader.getLabelStyle("Play-Bold59white"));
        topLabel.setAlignment(Align.top);

        uiTable.defaults().padTop(Gdx.graphics.getHeight() * .02f);
        uiTable.add(topLabel).height(topLabel.getPrefHeight() * 1.5f).expandY().top().row();
        // uiTable.setDebug(true);
    }
}
