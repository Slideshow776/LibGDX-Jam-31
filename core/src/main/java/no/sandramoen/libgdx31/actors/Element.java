package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.libgdx31.actors.map.TiledMapActor;
import no.sandramoen.libgdx31.utils.BaseActor;


public class Element extends BaseActor {
    public boolean isDead;
    public enum Type {RED, YELLOW, BLUE}

    public Element(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("whitePixel");
        setSize(4, 4);
    }

    private void shakeCamera(float duration) {
        isShakyCam = true;
        new BaseActor(0f, 0f, getStage()).addAction(Actions.sequence(
                Actions.delay(duration),
                Actions.run(() -> {
                    isShakyCam = false;
                    TiledMapActor.centerPositionCamera(getStage());
                })
        ));
    }
}
