package no.sandramoen.libgdx31.screens.gameplay;

import no.sandramoen.libgdx31.actors.Shape;
import no.sandramoen.libgdx31.utils.BaseGame;
import space.earlygrey.shapedrawer.ShapeDrawer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Grid {

    private int width, height;
    private Array<Array<Shape>> grid;
    private float shapeSpacing; // Space between shapes
    private Array<Float> margins; // Array for margins (left, right, top, bottom)
    private Stage stage;


    public Grid(int width, int height, Stage stage, ShapeDrawer shapeDrawer, float spacing, Array<Float> margins) {
        this.width = width;
        this.height = height;
        this.stage = stage;
        this.shapeSpacing = spacing;
        this.margins = margins;
        this.grid = new Array<>(width);

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
        float cellWidth = (availableWidth - (spacing * (width - 1))) / width;  // Available width minus spacing between columns
        float cellHeight = (availableHeight - (spacing * (height - 1))) / height; // Available height minus spacing between rows
        float cellSize = Math.min(cellWidth, cellHeight); // Ensure cells are square by taking the minimum size

        // Calculate offsets to center the grid in the available space
        float offsetX = marginLeft + (availableWidth - (width * cellSize + (spacing * (width - 1)))) / 2f;
        float offsetY = marginBottom + (availableHeight - (height * cellSize + (spacing * (height - 1)))) / 2f;

        // Initialize the grid with shapes
        for (int x = 0; x < width; x++) {
            Array<Shape> column = new Array<>(height);
            for (int y = 0; y < height; y++) {
                // Calculate the shape position within each grid cell, considering spacing and margins
                float posX = offsetX + x * (cellSize + spacing); // Account for spacing
                float posY = offsetY + y * (cellSize + spacing); // Account for spacing

                // Randomly assign a shape type (CIRCLE, SQUARE, TRIANGLE, STAR)
                Shape.Type randomType = Shape.Type.values()[(int) (Math.random() * Shape.Type.values().length)];
                Shape shape = new Shape(posX, posY, stage, shapeDrawer, randomType, cellSize, this);

                // Set grid position to help track where the shape is
                shape.setGridPosition(x, y);
                column.add(shape); // Add the shape to the column
            }
            grid.add(column); // Add the column of shapes to the grid
        }
    }


    public void removeConnectedShapes(int x, int y, Shape.Type shapeType) {
        // Use a queue to perform breadth-first search (BFS) to find all connected shapes
        Array<Vector2> toVisit = new Array<>();
        Array<Vector2> visited = new Array<>();
        Array<Shape> shapesToRemove = new Array<>();  // List to store shapes for removal
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
            if (currentX > 0) { toVisit.add(new Vector2(currentX - 1, currentY)); distances.add(currentDistance + 1); } // Left
            if (currentX < width - 1) { toVisit.add(new Vector2(currentX + 1, currentY)); distances.add(currentDistance + 1); } // Right
            if (currentY > 0) { toVisit.add(new Vector2(currentX, currentY - 1)); distances.add(currentDistance + 1); } // Down
            if (currentY < height - 1) { toVisit.add(new Vector2(currentX, currentY + 1)); distances.add(currentDistance + 1); } // Up
        }

        // Now, we will process each group of shapes based on their distance from the clicked shape
        float delay = 0.1f;  // Initial delay for shapes at distance 1
        float delayIncrement = 0.05f;  // Delay increment to speed up as we move further away from the clicked shape

        // Start with the clicked shape
        Shape clickedShape = grid.get(x).get(y);
        if (clickedShape != null && clickedShape.type == shapeType) {
            clickedShape.addAction(Actions.run(() -> clickedShape.animatedRemove())); // Remove clicked shape immediately
        }

        // Process shapes grouped by distance sequentially
        for (float distance : distanceGroups.keySet()) {
            Array<Shape> shapesAtDistance = distanceGroups.get(distance);

            // For shapes at the same distance, remove them one by one, with the delay increasing slightly
            for (Shape shape : shapesAtDistance) {
                shape.addAction(Actions.delay(delay, Actions.run(() -> shape.animatedRemove())));
            }

            // After processing shapes at this distance, increase the delay for the next group
            delay += delayIncrement;
        }
    }

}

