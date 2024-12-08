package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdx31.utils.BaseActor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Shape extends BaseActor {

    public enum Type {RED, YELLOW, BLUE}
    private ShapeDrawer shapeDrawer;
    private int gridX, gridY;

    // Constructor
    public Shape(float x, float y, Stage stage, ShapeDrawer shapeDrawer, Type type, float cellSize) {
        super(x, y, stage);
        this.shapeDrawer = shapeDrawer;
        loadImage("whitePixel");
        getColor().a = 0.25f;
        setSize(cellSize, cellSize); // Set default size

        //setDebug(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        shapeDrawer.setColor(Color.RED); // Color for the shape (Red for testing)
        float radius = getWidth() / 2f; // Radius for circle (based on the width)

        // Draw the filled circle
        shapeDrawer.filledCircle(getX() + radius, getY() + radius, radius);
        super.draw(batch, parentAlpha);
    }

    // Set the grid position to help track where the shape is
    public void setGridPosition(int x, int y) {
        this.gridX = x;
        this.gridY = y;
    }
}
