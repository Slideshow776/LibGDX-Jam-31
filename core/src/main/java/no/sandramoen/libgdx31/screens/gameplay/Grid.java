package no.sandramoen.libgdx31.screens.gameplay;

import no.sandramoen.libgdx31.actors.Shape;
import no.sandramoen.libgdx31.screens.shell.LevelSelectScreen;
import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseGame;
import space.earlygrey.shapedrawer.ShapeDrawer;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import java.util.HashMap;


public class Grid {


    private int width, height;
    private Array<Array<Shape>> grid;
    private float spacing;
    private Stage mainStage;
    private Stage uiStage;

    public Grid(int width, int height, Stage mainStage, Stage uiStage, ShapeDrawer shapeDrawer, float spacing, Array<Float> margins, String direction) {
        this.width = width;
        this.height = height;
        this.mainStage = mainStage;
        this.uiStage = uiStage;
        this.spacing = spacing;

        grid = new Array<>(width);

        // Ensure the margins array has 4 elements (left, right, top, bottom)
        if (margins.size != 4) {
            throw new IllegalArgumentException("Margins array must contain exactly 4 elements: left, right, top, bottom.");
        }

        // Extract individual margin values from the array
        float marginLeft = margins.get(0);
        float marginRight = margins.get(1);
        float marginTop = margins.get(2);
        float marginBottom = margins.get(3);

        // Calculate the available space for the grid inside the margins
        float availableWidth = BaseGame.WORLD_WIDTH - (marginLeft + marginRight);
        float availableHeight = BaseGame.WORLD_HEIGHT - (marginTop + marginBottom);

        // Calculate cell size considering spacing and margins
        float cellWidth = (availableWidth - (spacing * (width - 1))) / width;
        float cellHeight = (availableHeight - (spacing * (height - 1))) / height;
        float cellSize = Math.min(cellWidth, cellHeight);

        // Calculate offsets to center the grid in the available space
        float offsetX = marginLeft + (availableWidth - (width * cellSize + (spacing * (width - 1)))) / 2f;
        float offsetY = marginBottom + (availableHeight - (height * cellSize + (spacing * (height - 1)))) / 2f;

        // Initialize the grid with shapes
        float baseDelay = 0.1f; // Delay factor for row stagger
        for (int x = 0; x < width; x++) {
            Array<Shape> column = new Array<>(height);

            for (int y = 0; y < height; y++) {

                // Calculate the shape position within each grid cell, considering spacing and margins
                float posX = offsetX + x * (cellSize + spacing);
                float posY = offsetY + y * (cellSize + spacing);

                // Determine shape type based on direction
                Shape.Type shapeType;
                double randomValue = Math.random(); // Random value between 0 and 1

                switch (direction.toLowerCase()) {
                    case "up":
                        shapeType = (randomValue < 0.334) ? Shape.Type.SQUARE : getRandomTypeExcluding(Shape.Type.SQUARE);
                        break;
                    case "left":
                        shapeType = (randomValue < 0.334) ? Shape.Type.STAR : getRandomTypeExcluding(Shape.Type.STAR);
                        break;
                    case "right":
                        shapeType = (randomValue < 0.334) ? Shape.Type.CIRCLE : getRandomTypeExcluding(Shape.Type.CIRCLE);
                        break;
                    case "down":
                        shapeType = (randomValue < 0.334) ? Shape.Type.TRIANGLE : getRandomTypeExcluding(Shape.Type.TRIANGLE);
                        break;
                    default:
                        shapeType = Shape.Type.values()[(int) (Math.random() * Shape.Type.values().length)];
                }

                // Create and configure the shape
                Shape shape = new Shape(posX, posY, mainStage, shapeDrawer, shapeType, cellSize, this);

                // Set grid position to help track where the shape is
                shape.setGridPosition(x, y);
                column.add(shape);

                // Stagger animation for each row
                float delay = baseDelay * y;
                shape.setScale(0f);
                shape.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.scaleTo(1f, 1f, 0.2f, Interpolation.sineOut)
                ));
                mainStage.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> AssetLoader.swordSounds.get(MathUtils.random(0, 13)).play(BaseGame.soundVolume * 0.5f, 0.4f + (1.25f * delay), 0.0f))
                ));
            }
            grid.add(column);
        }
    }

    // Helper method to get a random shape type excluding a specific type
    private Shape.Type getRandomTypeExcluding(Shape.Type excludedType) {
        Shape.Type[] types = Shape.Type.values();
        Shape.Type randomType;
        do {
            randomType = types[(int) (Math.random() * types.length)];
        } while (randomType == excludedType);
        return randomType;
    }



    public void removeConnectedShapes(int x, int y, Shape.Type shapeType) {
        int levelScore = 0;
        float scoreModifier = 1.0005f;
        int triggeredShapes = 0;
        int numSquaresTriggered = 0;

        disableAllShapeClicks();

        // Use a queue to perform breadth-first search (BFS) to find all connected shapes
        Array<Vector2> toVisit = new Array<>();
        Array<Vector2> visited = new Array<>();
        Array<Float> distances = new Array<>();  // Store the distance of shapes from the clicked shape
        HashMap<Float, Array<Shape>> distanceGroups = new HashMap<>(); // Group shapes by distance

        // Start with the clicked shape
        toVisit.add(new Vector2(x, y));
        distances.add(0f);  // The clicked shape has a distance of 0

        // Perform BFS to gather shapes connected to the clicked shape
        while (!toVisit.isEmpty()) {
            Vector2 current = toVisit.pop();
            int currentX = (int) current.x;
            int currentY = (int) current.y;

            // Check if already visited
            if (visited.contains(current, false)) continue;

            // Mark the current shape as visited
            visited.add(current);

            // Get the shape at the current position
            Shape shape = grid.get(currentX).get(currentY);
            if (shape == null || shape.type != shapeType) continue; // Skip if shape is null or type mismatch

            // Add shape to the removal list and group by distance
            float currentDistance = distances.get(distances.size - 1);
            if (!distanceGroups.containsKey(currentDistance)) {
                distanceGroups.put(currentDistance, new Array<>());
            }
            distanceGroups.get(currentDistance).add(shape);

            // Add neighbors to the queue with incremented distance (up, down, left, right)
            if (currentX > 0) {
                toVisit.add(new Vector2(currentX - 1, currentY));
                distances.add(currentDistance + 1);
            } // Left
            if (currentX < width - 1) {
                toVisit.add(new Vector2(currentX + 1, currentY));
                distances.add(currentDistance + 1);
            } // Right
            if (currentY > 0) {
                toVisit.add(new Vector2(currentX, currentY - 1));
                distances.add(currentDistance + 1);
            } // Down
            if (currentY < height - 1) {
                toVisit.add(new Vector2(currentX, currentY + 1));
                distances.add(currentDistance + 1);
            } // Up
        }

        // Now, we will process each group of shapes based on their distance from the clicked shape
        float delay = 0.1f;  // Initial delay for shapes at distance 1
        float delayIncrement = 0.1f;  // Delay increment to speed up as we move further away from the clicked shape

        // Start with the clicked shape
        Shape clickedShape = grid.get(x).get(y);
        clickedShape.animatedRemove(); // Remove clicked shape immediately
        grid.get(x).set(y, null);  // Mark the grid position as null immediately

        // Process shapes grouped by distance sequentially
        for (float distance : distanceGroups.keySet()) {

            // calculate score
            levelScore += (1 * scoreModifier);
            scoreModifier = 1.1f + (distance * 0.05f); // Modifier grows based on distance
            Array<Shape> shapesAtDistance = distanceGroups.get(distance);

            // For shapes at the same distance, remove them one by one, with the delay increasing slightly
            for (Shape shape : shapesAtDistance) {
                float finalDelay = delay;
                triggeredShapes++;
                shape.addAction(Actions.delay(delay, Actions.run(() -> {
                    AssetLoader.swordSounds.get(MathUtils.random(0, 13)).play(BaseGame.soundVolume, 0.3f + (finalDelay * 0.8f), 0.0f);
                    //AssetLoader.bubbleSound.play(BaseGame.soundVolume, 0.8f + (finalDelay), 0.0f);
                    shape.animatedRemove();
                })));

                // feature TODO: label that shows the score you get.
                /*TypingLabel label = new TypingLabel("0", AssetLoader.getLabelStyle("Play-Bold20white"));
                label.scaleBy(0.05f);
                label.setPosition(shape.getX(), shape.getY());
                mainStage.addActor(label);*/

                // Immediately set the grid position to null as the shape is marked for removal
                Vector2 shapePosition = shape.getGridPosition();
                grid.get((int) shapePosition.x).set((int) shapePosition.y, null);
                if (shape.type == Shape.Type.SQUARE)
                    numSquaresTriggered++;
            }

            // After processing shapes at this distance, increase the delay for the next group
            delay += delayIncrement;
        }

        mainStage.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.run(() -> applyGravity())
        ));

        // set score
        LevelScreen.score += levelScore;
        LevelScreen.scoreLabel.setText("" + LevelScreen.score);
        LevelScreen.scoreLabel.restart();
        LevelScreen.scoreLabel.invalidateHierarchy();

        if (triggeredShapes == 1)
            LevelScreen.looseHealth();

        if (numSquaresTriggered >= 12)
            LevelScreen.gainHealth(30);
        else if (numSquaresTriggered >= 8)
            LevelScreen.gainHealth(20);
        else if (numSquaresTriggered >= 4)
            LevelScreen.gainHealth(10);

        if (!areAnyShapesLeft()) {
            clearBoard();
            mainStage.addAction(Actions.sequence(
                Actions.delay(2.0f),
                Actions.run(() -> BaseGame.setActiveScreen(new LevelSelectScreen()))
            ));
        }
    }


    public void applyGravity() {
        float delay = 0.1f; // Initial delay for the first shape
        float bounceDelay = 0.5f;

        // Loop over each column (x coordinate)
        for (int x = 0; x < width; x++) {
            // Track the highest empty row in this column (start from the top)
            int nullRow = -1;  // Initially, no empty row (will set this later when we find a null space)

            // Iterate through the column from top to bottom
            for (int y = 0; y < height; y++) {
                Shape shape = grid.get(x).get(y);

                if (shape == null) {
                    // If this position is empty, track this row as the potential place for a falling shape
                    if (nullRow == -1) {
                        nullRow = y;
                    }
                } else {
                    // If we find a shape and there's an available space (nullRow), move the shape there
                    if (nullRow != -1 && nullRow != y) {
                        // Animate the shape falling to the nullRow position
                        float startY = shape.getY();  // Start position of the shape
                        // In applyGravity() method, update the endY calculation to account for spacing
                        float endY = shape.getY() + (nullRow - y) * (shape.getHeight() + spacing);  // Adjust for both cell height and spacing

                        shape.addAction(Actions.delay(delay, Actions.moveTo(shape.getX(), endY, bounceDelay, Interpolation.bounceOut)));  // Animate with delay

                        // Update the grid after the animation starts
                        grid.get(x).set(nullRow, shape);
                        grid.get(x).set(y, null);  // Set the original position to null

                        // Update the shape's grid position to reflect the new position
                        shape.setGridPosition(x, nullRow);

                        // Update nullRow to track the next available empty row below
                        nullRow++;

                        // Increment delay for next falling shape
                        delay += 0.01f; // Adjust this value for faster/slower falling
                    }
                }
            }
        }
        mainStage.addAction(Actions.sequence(
            Actions.delay((delay + bounceDelay) * 0.65f),
            Actions.run(() -> enableAllShapeClicks())
        ));
    }


    public void clearBoard() {
        // Disable clicks to prevent interaction during the clearing process
        disableAllShapeClicks();

        Array<Shape> allShapes = new Array<>();

        // Collect all the shapes in the grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Shape shape = grid.get(x).get(y);
                if (shape != null) {
                    allShapes.add(shape); // Add shape to list of shapes
                }
            }
        }

        // Shuffle the list of shapes to randomize the order
        allShapes.shuffle();

        float delay = 0.01f;  // Starting delay for the first shape
        float delayIncrement = 0.005f;  // Delay increment to gradually increase the time for each subsequent shape

        // Iterate through each shape in the shuffled list and apply the removal animation
        for (Shape shape : allShapes) {
            shape.addAction(Actions.sequence(
                Actions.delay(delay), // Add a random delay for each shape
                Actions.fadeOut(0.5f), // Fade out the shape
                Actions.scaleTo(0f, 0f, 0.5f, Interpolation.sineIn), // Shrink the shape to 0
                Actions.run(() -> {
                    // Remove the shape from the grid and stage
                    shape.animatedRemove();
                    // Set the grid position to null as the shape is removed
                    Vector2 shapePosition = shape.getGridPosition();
                    grid.get((int) shapePosition.x).set((int) shapePosition.y, null);
                })
            ));

            // Increase the delay for the next shape to create a cascading effect
            delay += delayIncrement;
        }

        // After all shapes have been removed, we can optionally reinitialize the grid or trigger a new round.
        mainStage.addAction(Actions.sequence(
            Actions.delay(delay),  // Wait for all animations to complete
            Actions.run(() -> {
                // Optionally: reinitialize the grid or trigger a new round here
                enableAllShapeClicks();
                // Optionally apply gravity or any other post-clear action
                applyGravity();
            })
        ));
    }


    public void disableAllShapeClicks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Shape shape = grid.get(x).get(y);
                if (shape != null) {
                    shape.setClickable(false); // Disable click for this shape
                }
            }
        }
    }


    public void enableAllShapeClicks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Shape shape = grid.get(x).get(y);
                if (shape != null) {
                    shape.setClickable(true); // Re-enable click for this shape
                }
            }
        }
    }


    public boolean areAnyShapesLeft() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Shape shape = grid.get(x).get(y);
                if (shape != null) {
                    return true; // If we find a shape, return true
                }
            }
        }
        return false; // If no shapes are found, return false
    }


    public void printBoard() {
        // Iterate over each row of the grid from top to bottom
        for (int y = height - 1; y >= 0; y--) {  // Start from the top (y = height - 1)
            StringBuilder row = new StringBuilder();

            for (int x = 0; x < width; x++) {
                Shape shape = grid.get(x).get(y);

                // Print 'X' for a cell with a shape, or a space for an empty cell
                if (shape != null) {
                    row.append("X ");
                } else {
                    row.append("  ");  // Empty cell (double space for visual alignment)
                }
            }
            // Print the constructed row
            System.out.println(row.toString());
        }
    }
}

