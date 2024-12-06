package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx31.utils.BaseActor;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Element extends BaseActor {


    public boolean isDead;
    public enum Type {RED, YELLOW, BLUE}
    private ShapeDrawer shapeDrawer;


    public Element(float x, float y, Stage stage, ShapeDrawer shapeDrawer) {
        super(x, y, stage);
        this.shapeDrawer = shapeDrawer;
        loadImage("whitePixel");
        setSize(1, 1);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        shapeDrawer.setColor(Color.RED);
        shapeDrawer.filledCircle(getX(), getY(), getWidth());
        super.draw(batch, parentAlpha);
    }
}
