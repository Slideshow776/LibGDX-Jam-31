package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.libgdx31.utils.BaseActor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Shape extends BaseActor {

    public enum Type {
        CIRCLE(Color.valueOf("7fd66f")),    // green
        SQUARE(Color.valueOf("de3a68")),    // red
        TRIANGLE(Color.valueOf("69f7ff")),  // blue
        STAR(Color.valueOf("ffe785"));      // yellow

        private final Color color;

        Type(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private ShapeDrawer shapeDrawer;
    private int gridX, gridY;
    private Type type;

    // Outline color (white)
    private final Color outlineColor = Color.WHITE;
    private final float outlineScale = 1.075f;

    // Constructor
    public Shape(float x, float y, Stage stage, ShapeDrawer shapeDrawer, Type type, float cellSize) {
        super(x, y, stage);
        this.shapeDrawer = shapeDrawer;
        this.type = type;
        loadImage("whitePixel");
        getColor().a = 0.0f;  // Set transparency (optional)
        setSize(cellSize, cellSize); // Set the size of the shape

        // Set the origin to the center
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Get the color from the shape type
        Color shapeColor = type.getColor();  // Get color based on the shape type
        shapeDrawer.setColor(outlineColor);  // Set outline color to white

        // Get center position for drawing
        Vector2 center = getCenter();

        // Draw the outline first (scaled up to simulate a thicker border)
        switch (type) {
            case CIRCLE:
                shapeDrawer.filledCircle(center.x, center.y, getWidth() / 2f * outlineScale); // Fill the original circle
                break;
            case SQUARE:
                shapeDrawer.filledRectangle(center.x - getWidth() / 2f * outlineScale, center.y - getHeight() / 2f * outlineScale, getWidth() * outlineScale, getHeight() * outlineScale); // Fill the square
                break;
            case TRIANGLE:
                shapeDrawer.filledTriangle(
                    center.x, center.y - getHeight() / 2f * outlineScale,  // Top point
                    center.x - getWidth() / 2f * outlineScale, center.y + getHeight() / 2f * outlineScale, // Left bottom
                    center.x + getWidth() / 2f * outlineScale, center.y + getHeight() / 2f * outlineScale  // Right bottom
                );
                break;
            case STAR:
                shapeDrawer.filledPolygon(new float[] {
                    center.x, center.y + getHeight() / 2f * outlineScale, // Top point
                    center.x - getWidth() / 4f * outlineScale, center.y + getHeight() / 4f * outlineScale, // Left top
                    center.x - getWidth() / 2f * outlineScale, center.y, // Left bottom
                    center.x - getWidth() / 4f * outlineScale, center.y - getHeight() / 4f * outlineScale, // Left middle
                    center.x, center.y - getHeight() / 2f * outlineScale, // Bottom point
                    center.x + getWidth() / 4f * outlineScale, center.y - getHeight() / 4f * outlineScale, // Right middle
                    center.x + getWidth() / 2f * outlineScale, center.y, // Right bottom
                    center.x + getWidth() / 4f * outlineScale, center.y + getHeight() / 4f * outlineScale  // Right top
                });
                break;
        }

        // Now fill the shape with the inner color (draw the actual shape on top)
        shapeDrawer.setColor(shapeColor);  // Set the fill color
        switch (type) {
            case CIRCLE:
                shapeDrawer.filledCircle(center.x, center.y, getWidth() / 2f); // Fill the original circle
                break;
            case SQUARE:
                shapeDrawer.filledRectangle(center.x - getWidth() / 2f, center.y - getHeight() / 2f, getWidth(), getHeight()); // Fill the square
                break;
            case TRIANGLE:
                shapeDrawer.filledTriangle(
                    center.x, center.y - getHeight() / 2f,  // Top point
                    center.x - getWidth() / 2f, center.y + getHeight() / 2f, // Left bottom
                    center.x + getWidth() / 2f, center.y + getHeight() / 2f  // Right bottom
                );
                break;
            case STAR:
                shapeDrawer.filledPolygon(new float[] {
                    center.x, center.y + getHeight() / 2f, // Top point
                    center.x - getWidth() / 4f, center.y + getHeight() / 4f, // Left top
                    center.x - getWidth() / 2f, center.y, // Left bottom
                    center.x - getWidth() / 4f, center.y - getHeight() / 4f, // Left middle
                    center.x, center.y - getHeight() / 2f, // Bottom point
                    center.x + getWidth() / 4f, center.y - getHeight() / 4f, // Right middle
                    center.x + getWidth() / 2f, center.y, // Right bottom
                    center.x + getWidth() / 4f, center.y + getHeight() / 4f  // Right top
                });
                break;
        }

        super.draw(batch, parentAlpha); // Call the parent draw method to apply transformations etc.
    }

    // Set the grid position to help track where the shape is
    public void setGridPosition(int x, int y) {
        this.gridX = x;
        this.gridY = y;
    }

    private Vector2 getCenter() {
        return new Vector2(
            getX() + getWidth() / 2f,  // X center
            getY() + getHeight() / 2f  // Y center
        );
    }
}
