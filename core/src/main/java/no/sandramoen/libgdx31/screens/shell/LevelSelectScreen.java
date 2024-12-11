package no.sandramoen.libgdx31.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;
import no.sandramoen.libgdx31.utils.GameUtils;


public class LevelSelectScreen extends BaseScreen {


    @Override
    public void initialize() {
        GameUtils.stopAllMusic();
        AssetLoader.levelSelectMusic.play();
        AssetLoader.levelSelectMusic.setVolume(BaseGame.musicVolume);
        float songDuration = 95f;  // Replace with the actual duration of the music
        float randomStartPosition = MathUtils.random(0f, songDuration);
        AssetLoader.levelSelectMusic.setPosition(randomStartPosition);
        AssetLoader.levelSelectMusic.setLooping(true);

        AssetLoader.openDoorSound.play(BaseGame.soundVolume);

        uiTable.defaults()
            .width(Gdx.graphics.getWidth() * .15f)
            .height(Gdx.graphics.getHeight() * .075f)
            .spaceTop(Gdx.graphics.getHeight() * .01f);

        TypingLabel label = new TypingLabel("{CROWD}deeper and ever deeper...", AssetLoader.getLabelStyle("Play-Bold59white"));
        label.setAlignment(Align.center);

        uiTable.add(label)
            .colspan(2)
            .growX()
            .padBottom(Gdx.graphics.getHeight() * 0.1f)
            .row();

        float directionWidth = 0.25f;
        float directionHeight = 0.125f;

        uiTable.add(directionButton("up"))
            .colspan(2)
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .row();

        uiTable.add(directionButton("left"))
            .padRight(Gdx.graphics.getWidth() * 0.2f)
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight);

        uiTable.add(directionButton("right"))
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .row();

        uiTable.add(directionButton("down"))
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .colspan(2);

        // uiTable.setDebug(true);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }


    private Image directionButton(String direction) {
        Image image = new Image(AssetLoader.textureAtlas.findRegion("GUI/" + direction));
        image.getColor().a = 0.8f;
        image.addListener(
            new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    BaseGame.setActiveScreen(new LevelScreen(direction));
                    return true;
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    image.getColor().a = 1.0f;
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    image.getColor().a = 0.8f;
                }
            });
        return image;
    }
}
