package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a grid for a falling sand simulation.
 */
public class Grid {

    public static int SAND_COLOR = Color.decode("#dcb159").getRGB();

    private final int width;
    private final int height;
    private ArrayList<Integer> grid;
    private GridDrawer gridDrawer;

    /**
     * Constructs a Grid with the specified width and height.
     *
     * @param width  The width of the grid.
     * @param height The height of the grid.
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new ArrayList<>(Collections.nCopies(width * height, 0));

        // Use CountDownLatch for synchronization
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            gridDrawer = new GridDrawer(this, 10, 50);
            latch.countDown(); // Signal that initialization is complete
        });

        try {
            latch.await(); // Wait until initialization is complete to guarantee that gridDrawer is initialized
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
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
        int index = x + y * this.width;
        if(index >= grid.size()) return;

        this.grid.set(index, color);
        this.gridDrawer.repaintGrid();
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
}
