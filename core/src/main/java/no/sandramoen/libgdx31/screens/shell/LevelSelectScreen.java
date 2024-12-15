package no.sandramoen.libgdx31.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import no.sandramoen.libgdx31.utils.BaseScreen;
import no.sandramoen.libgdx31.utils.GameUtils;


public class LevelSelectScreen extends BaseScreen {
    private boolean isLeaving = false;


    @Override
    public void initialize() {
        GameUtils.stopAllMusic();
        AssetLoader.levelSelectMusic.play();
        AssetLoader.levelSelectMusic.setVolume(BaseGame.musicVolume);
        float songDuration = 95f;  // Replace with the actual duration of the music
        float randomStartPosition = MathUtils.random(0f, songDuration);
        AssetLoader.levelSelectMusic.setPosition(randomStartPosition);
        AssetLoader.levelSelectMusic.setLooping(true);

        uiTable.defaults()
            .width(Gdx.graphics.getWidth() * .15f)
            .height(Gdx.graphics.getHeight() * .075f)
            .spaceTop(Gdx.graphics.getHeight() * .01f);

        TypingLabel label = new TypingLabel("{CROWD}Where did they go next?", AssetLoader.getLabelStyle("Play-Bold59white"));
        label.setAlignment(Align.center);

        uiTable.add(label)
            .colspan(2)
            .growX()
            .padBottom(Gdx.graphics.getHeight() * 0.1f)
            .row();

        float directionWidth = 0.35f;
        float directionHeight = directionWidth / 2.0f;

        Array<String> directions = new Array();
        directions.add("up", "left", "right", "down");
        directions.shuffle();

        uiTable.add(directionButton(directions.pop(), 0))
            .colspan(2)
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .row();

        uiTable.add(directionButton(directions.pop(), 1))
            //.padRight(Gdx.graphics.getWidth() * 0.2f)
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight);

        uiTable.add(directionButton(directions.pop(), 2))
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .row();

        uiTable.add(directionButton(directions.pop(), 3))
            .width(Gdx.graphics.getWidth() * directionWidth)
            .height(Gdx.graphics.getHeight() * directionHeight)
            .colspan(2);

        //uiTable.setDebug(true);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }


    private Image directionButton(String direction, int temp) {
        Image image = new Image(AssetLoader.textureAtlas.findRegion("GUI/" + direction));
        float originX = image.getWidth() * 0.41f;
        float originY = image.getHeight() * 0.29f;
        image.setOrigin(originX, originY);
        if (temp == 0) {
            image.setRotation(0);
        }else if (temp == 1) {
            image.setRotation(90);
        }else if (temp == 2) {
            image.setRotation(-90);
        }else if (temp == 3)
            image.setRotation(180);
        image.getColor().a = 0.8f;
        image.addListener(
            new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    isLeaving = true;
                    float duration = 0.1f;
                    image.addAction(Actions.sequence(
                        Actions.alpha(1.0f, duration, Interpolation.sine),
                        Actions.scaleTo(0.8f, 0.8f, duration),
                        Actions.scaleTo(1.2f, 1.2f, duration),
                        Actions.run(() -> BaseGame.setActiveScreen(new LevelScreen(direction)))
                    ));
                    return true;
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    if (isLeaving)
                        return;
                    float duration = 1.0f;
                    image.getColor().a = 1.0f;
                    image.addAction(Actions.parallel(
                        Actions.alpha(1.0f, duration, Interpolation.sine),
                        Actions.scaleTo(1.2f, 1.2f, duration, Interpolation.sine)
                    ));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    if (isLeaving)
                        return;
                    float duration = 1.0f;
                    image.getColor().a = 1.0f;
                    image.addAction(Actions.parallel(
                        Actions.alpha(0.8f, duration, Interpolation.sine),
                        Actions.scaleTo(1.0f, 1.0f, duration, Interpolation.sine)
                    ));
                }
            });
        return image;
    }
}
