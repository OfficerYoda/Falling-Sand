package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a grid for a falling sand simulation.
 */
public class Grid {

    public static final Color SAND_COLOR = Color.decode("#dcb159");
    public static final int SAND_COLOR_RGB = SAND_COLOR.getRGB();
    public static final int CURSOR_RADIUS = 3;

    public static int updatesPerSecond = 100;
    public static int updateInterval = (int) (1f / updatesPerSecond * 1000); // time in ms

    private final int width;
    private final int height;
    private final int gridSize;
    private ArrayList<Integer> grid;
    private GridDrawer gridDrawer;
    private int[] cursorIndices = new int[0];

    /**
     * Constructs a Grid with the specified width and height.
     *
     * @param width  The width of the grid.
     * @param height The height of the grid.
     */
    public Grid(int width, int height, int cellSize) {
        this.width = width - width % cellSize; // mod to get rid of any extra
        this.height = height - height % cellSize; // mod to get rid of any extra
        this.grid = new ArrayList<>(Collections.nCopies(width * height, 0));
        this.gridSize = grid.size();

        // Use CountDownLatch for synchronization
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            gridDrawer = new GridDrawer(this, cellSize);
            latch.countDown(); // Signal that initialization is complete
        });

        try {
            latch.await(); // Wait until initialization is complete to guarantee that gridDrawer is initialized
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        // start the update loop
        Timer updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, updateInterval);
    }

    private void update() {
        // backward to not double apply gravity to a particle
        for(int i = this.gridSize - this.width - 1; i >= 0; i--) {
//            for(int i = 0; i < this.gridSize - this.width - 1; i++) {

            int x = i % width;
            int y = i / width;

            // Get the indices of the pixels directly below
            int dir = Math.random() < 0.5 ? 1 : -1;

            int below = i + width;
            int belowA = below - dir;
            int belowB = below + dir;

            // If there are no pixels below, including diagonals, move it accordingly.
            if(isEmpty(below)) {
                swap(i, below);
            } else if(isEmpty(belowA)) {
                swap(i, belowA);
            } else if(isEmpty(belowB)) {
                swap(i, belowB);
            }
        }

        gridDrawer.repaintGrid();
    }

    /**
     * Clears the grid, resetting all values to the default.
     */
    public void clear() {
        this.grid = new ArrayList<>(Collections.nCopies(width * height, 0));
    }

    /**
     * Sets the color of a specific particle at the given coordinates.
     *
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param color The color to set for the particle.
     */
    public void set(int x, int y, int color) {
        this.set(x + y * this.width, color);
    }

    /**
     * Sets the color of a specific particle at the given index.
     *
     * @param index The index.
     * @param color The color to set for the particle.
     */
    public void set(int index, int color) {
        this.grid.set(index, color);
    }

    /**
     * Gets the value at a specific grid position.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The value at the specified grid position.
     */
    public int get(int x, int y) {
        return this.grid.get(x + y * width);
    }


    /**
     * Swaps the positions of two particles (or empty spaces) in the grid.
     *
     * @param indexA The index of the first particle.
     * @param indexB The index of the second particle.
     */
    public void swap(int indexA, int indexB) {
        int temp = this.grid.get(indexA);
        this.grid.set(indexA, this.grid.get(indexB));
        this.grid.set(indexB, temp);
    }

    /**
     * Checks if a specific space in the grid is empty.
     *
     * @param index The index of the particle.
     * @return True if the space is empty, false otherwise.
     */
    public boolean isEmpty(int index) {
        if(index >= gridSize) return false;
        return this.grid.get(index) == 0;
    }

    /**
     * Checks if a specific space in the grid is empty.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the space is empty, false otherwise.
     */
    public boolean isEmpty(int x, int y) {
        return isEmpty(x + y * width);
    }

    /**
     * Gets the width of the grid.
     *
     * @return The width of the grid.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the grid.
     *
     * @return The height of the grid.
     */
    public int getHeight() {
        return height;
    }

    public int[] getCursorIndices() {
        return this.cursorIndices;
    }

    public void setCursorIndices(int[] cursorIndices) {
        this.cursorIndices = cursorIndices;
    }
}
