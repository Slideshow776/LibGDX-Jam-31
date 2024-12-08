package no.sandramoen.libgdx31.screens.gameplay;

import no.sandramoen.libgdx31.actors.Shape;
import no.sandramoen.libgdx31.utils.BaseGame;
import space.earlygrey.shapedrawer.ShapeDrawer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Grid {

    private int width, height;
    private Array<Array<Shape>> grid;
    private float spacing; // Space between shapes
    private Array<Float> margins; // Array for margins (left, right, top, bottom)

    // Constructor with margin array
    public Grid(int width, int height, Stage stage, ShapeDrawer shapeDrawer, float spacing, Array<Float> margins) {
        this.width = width;
        this.height = height;
        this.spacing = spacing;
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
                Shape shape = new Shape(posX, posY, stage, shapeDrawer, randomType, cellSize);

                // Set grid position to help track where the shape is
                shape.setGridPosition(x, y);
                column.add(shape); // Add the shape to the column
            }
            grid.add(column); // Add the column of shapes to the grid
        }
    }
}
