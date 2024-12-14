package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.utils.compression.lzma.Base;

import no.sandramoen.libgdx31.actors.particles.RedKeyEffect;
import no.sandramoen.libgdx31.actors.particles.ShapeClickEffect;
import no.sandramoen.libgdx31.screens.gameplay.Grid;
import no.sandramoen.libgdx31.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseActor;
import no.sandramoen.libgdx31.utils.BaseGame;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Shape extends BaseActor {


    public static final float ANIMATION_REMOVAL_DELAY = 0.2f;

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
    public Type type;
    private Grid grid;
    private boolean clickable = true;


    public Shape(float x, float y, Stage stage, ShapeDrawer shapeDrawer, Type type, float cellSize, Grid grid) {
        super(x, y, stage);
        this.shapeDrawer = shapeDrawer;
        this.type = type;
        this.grid = grid;
        loadImage("shapes/circle/eyes");
        getColor().a = 0.0f;  // Ensure fully visible
        setSize(cellSize, cellSize); // Set the size of the shape

        // Set the origin to the center for scaling and rotation
        setOrigin(getWidth() / 2f, getHeight() / 2f);

        addListener(onShapeClicked());
        //setDebug(true);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Get center for drawing
        Vector2 center = getCenter();

        // Apply rotation and scaling
        shapeDrawer.setColor(type.getColor()); // Set the shape color

        // Scale and rotate based on current scale and rotation
        switch (type) {
            case CIRCLE:
                float radius = (getWidth() / 2f) * getScaleX(); // Use current scale for radius
                shapeDrawer.filledCircle(center.x, center.y, radius);
                break;
            case SQUARE:
                // Get rotated square vertices using the new method
                Vector2[] squareVertices = calculateRotatedSquare(center, getWidth(), getHeight(), getRotation());

                // Draw the rotated square using the vertices
                shapeDrawer.filledPolygon(new float[]{
                    squareVertices[0].x, squareVertices[0].y,
                    squareVertices[1].x, squareVertices[1].y,
                    squareVertices[2].x, squareVertices[2].y,
                    squareVertices[3].x, squareVertices[3].y
                });
                break;
            case TRIANGLE:
                Vector2[] triangleVertices = calculateRotatedTriangle(center, getWidth(), getHeight(), getScaleX(), getRotation());
                shapeDrawer.filledTriangle(
                    triangleVertices[0].x, triangleVertices[0].y,
                    triangleVertices[1].x, triangleVertices[1].y,
                    triangleVertices[2].x, triangleVertices[2].y
                );
                break;
            case STAR:
                float starRadius = (getWidth() / 2f) * getScaleX(); // Use scale for star
                shapeDrawer.filledPolygon(calculateDiamondStar(center, starRadius, getRotation()));
                break;
        }

        super.draw(batch, parentAlpha); // Call the parent draw method
    }


    public void setGridPosition(int x, int y) {
        this.gridX = x;
        this.gridY = y;
    }


    public Vector2 getGridPosition() {
        return new Vector2(gridX, gridY);
    }


    public void animatedRemove() {
        addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.0f, 0.0f, ANIMATION_REMOVAL_DELAY),
                    Actions.rotateBy(360f, ANIMATION_REMOVAL_DELAY)
                ),
                Actions.removeActor()
            )
        );
    }


    public void setClickable(boolean clickable) {
        this.clickable = clickable;

        if (clickable) {
            // Only add the listener if it hasn't been added already
            if (getListeners().isEmpty()) {
                addListener(onShapeClicked());
            }
        } else {
            removeListener(onShapeClicked());
        }
    }


    private Vector2 getCenter() {
        return new Vector2(
            getX() + getWidth() / 2f,  // X center
            getY() + getHeight() / 2f  // Y center
        );
    }


    private EventListener onShapeClicked() {
        return new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ShapeClickEffect clickEffect = new ShapeClickEffect();
                clickEffect.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
                clickEffect.setScale(0.0025f);

                //clickEffect.setColor(Color.valueOf("#" + type.getColor().toString()));
                System.out.println("#" + type.getColor().toString());
                clickEffect.setColor(Color.BLACK);

                getStage().addActor(clickEffect);
                clickEffect.start();
                if (!clickable)
                    return false;

                if (button == Input.Buttons.LEFT) {
                    grid.removeConnectedShapes(gridX, gridY, type);
                } else if (button == Input.Buttons.RIGHT && BaseGame.mana >= 10) {
                    LevelScreen.looseMana(10);
                    changeType();
                } else if (button == Input.Buttons.RIGHT && BaseGame.mana == 0) {
                    AssetLoader.manaEmptySound.play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0.0f);
                }
                return true;
            }
        };
    }


    public void changeType() {
        Type newType = null;
        switch (type) {
            case SQUARE:  // red
                newType = Type.TRIANGLE;  // blue
                break;
            case TRIANGLE:  // blue
                newType = Type.CIRCLE;  // green
                break;
            case CIRCLE:  // green
                newType = Type.STAR;  // yellow
                break;
            case STAR:  // yellow
                newType = Type.SQUARE;  // red
                break;
        }

        // Animate the size change (scale down to 0, then scale up to the new shape)
        Type finalNewType = newType;
        addAction(
            Actions.sequence(
                Actions.scaleTo(0.0f, 0.0f, 0.2f, Interpolation.fade), // Scale down to 0
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        type = finalNewType; // Change the type here
                        // Update the color and other properties specific to the new type if needed
                        setSize(getWidth(), getHeight()); // Reapply original size for the new shape type
                    }
                }),
                Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.fade) // Scale back up to normal size
            )
        );
    }


    private float[] calculateDiamondStar(Vector2 center, float radius, float rotation) {
        // Define a diamond-shaped star (4 points)
        float[] vertices = new float[8];
        float angleOffset = rotation;

        // Top point
        vertices[0] = center.x;
        vertices[1] = center.y + radius;

        // Right point
        vertices[2] = center.x + radius;
        vertices[3] = center.y;

        // Bottom point
        vertices[4] = center.x;
        vertices[5] = center.y - radius;

        // Left point
        vertices[6] = center.x - radius;
        vertices[7] = center.y;

        // Rotate each vertex around the center by the specified angle
        for (int i = 0; i < 4; i++) {
            float x = vertices[i * 2];
            float y = vertices[i * 2 + 1];

            // Apply rotation around the center point
            float angle = (float) Math.toRadians(angleOffset);
            float dx = x - center.x;
            float dy = y - center.y;

            vertices[i * 2] = center.x + (dx * (float) Math.cos(angle) - dy * (float) Math.sin(angle));
            vertices[i * 2 + 1] = center.y + (dx * (float) Math.sin(angle) + dy * (float) Math.cos(angle));
        }

        return vertices;
    }


    private Vector2[] calculateRotatedSquare(Vector2 center, float width, float height, float rotation) {
        // Apply scaling here
        float halfWidth = (width / 2f) * getScaleX();  // Apply scaling
        float halfHeight = (height / 2f) * getScaleY(); // Apply scaling

        // Define the original corners of the square
        Vector2[] vertices = new Vector2[4];

        vertices[0] = new Vector2(center.x - halfWidth, center.y - halfHeight); // Top-left
        vertices[1] = new Vector2(center.x + halfWidth, center.y - halfHeight); // Top-right
        vertices[2] = new Vector2(center.x + halfWidth, center.y + halfHeight); // Bottom-right
        vertices[3] = new Vector2(center.x - halfWidth, center.y + halfHeight); // Bottom-left

        // Apply rotation to each vertex around the center of the square
        for (int i = 0; i < 4; i++) {
            Vector2 vertex = vertices[i];
            float dx = vertex.x - center.x;
            float dy = vertex.y - center.y;
            float angle = (float) Math.toRadians(rotation);
            float rotatedX = center.x + (dx * (float) Math.cos(angle) - dy * (float) Math.sin(angle));
            float rotatedY = center.y + (dx * (float) Math.sin(angle) + dy * (float) Math.cos(angle));
            vertex.set(rotatedX, rotatedY);
        }

        return vertices;
    }


    private Vector2[] calculateRotatedTriangle(Vector2 center, float width, float height, float scale, float rotation) {
        float halfWidth = width / 2f * scale;
        float halfHeight = height / 2f * scale;

        // Original triangle points
        Vector2[] vertices = new Vector2[]{
            new Vector2(center.x, center.y + halfHeight), // Top
            new Vector2(center.x - halfWidth, center.y - halfHeight), // Bottom-left
            new Vector2(center.x + halfWidth, center.y - halfHeight)  // Bottom-right
        };

        // Rotate points around center
        for (Vector2 vertex : vertices) {
            float dx = vertex.x - center.x;
            float dy = vertex.y - center.y;
            float angle = (float) Math.atan2(dy, dx) + (float) Math.toRadians(rotation);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            vertex.set(
                center.x + distance * (float) Math.cos(angle),
                center.y + distance * (float) Math.sin(angle)
            );
        }

        return vertices;
    }
}
