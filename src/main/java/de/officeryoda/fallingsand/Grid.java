package de.officeryoda.fallingsand;

import de.officeryoda.fallingsand.particle.Empty;
import de.officeryoda.fallingsand.particle.Particle;
import de.officeryoda.fallingsand.particle.ParticleFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a grid for a falling sand simulation.
 */
public class Grid {

    public static int CURSOR_RADIUS = 3;

    public static int updatesPerSecond = 120;
    public static int updateInterval = (int) (1f / updatesPerSecond * 1000); // time in ms

    private final int width;
    private final int height;
    private final int gridSize;
    private final Particle[] grid;

    private final Set<Integer> modifiedIndices;
    private boolean cleared; // if we cleared all pixels last update

    private GridDrawer gridDrawer;

    private int[] cursorIndices = new int[0];
    private Color[] cursorColors = new Color[0];

    private long lastUpdate = System.currentTimeMillis();

    /**
     * Constructs a Grid with the specified width and height.
     *
     * @param width  The width of the grid.
     * @param height The height of the grid.
     */
    public Grid(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.grid = new Particle[width * height];
        this.clear(); // fill grid with 'Empty' Particle
        this.gridSize = grid.length;

        this.modifiedIndices = new HashSet<>();
        this.cleared = false;

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

    public static void setCursorRadius(int radius) {
        Grid.CURSOR_RADIUS = Math.max(1, radius);
    }

    private void update() {
        cleared = false;
        modifiedIndices.clear();

        // backward to not double apply gravity to a particle
        for(int row = height - 1; row >= 0; row--) {
            int rowOffset = row * this.width;
            boolean leftToRight = Math.random() > 0.5;
            for(int i = 0; i < this.width; i++) {
                // Go from right to left or left to right depending on our random value
                int columnOffset = leftToRight ? i : -i - 1 + this.width;
                int index = rowOffset + columnOffset;

                Particle particle = grid[index];
                particle.updateVelocity();
                for(int j = 0; j < particle.getUpdateCount(); j++) {
                    int newIdx = updatePixel(index);

                    if(newIdx == index) {
                        particle.resetVelocity();
                        break;
                    } else {
                        index = newIdx;
                    }
                }
            }
        }

//        if(cleared) {
//            gridDrawer.repaintGrid();
//        } else if(!modifiedIndices.isEmpty()) {
//            IntSummaryStatistics statsX = modifiedIndices.stream()
//                    .mapToInt(i -> i % width)
//                    .summaryStatistics();
//            IntSummaryStatistics statsY = modifiedIndices.stream().
//                    mapToInt(i -> i / width).
//                    summaryStatistics();
//
//            int minX = statsX.getMin();
//            int maxX = statsX.getMax();
//            int minY = statsY.getMin();
//            int maxY = statsY.getMax();
//
//            int area = (maxX - minX) * (maxY - minY);
//            int gridArea = width * height;
//
//            System.out.println("saved: " + (area / gridArea) * 100 + "%");
//
//            gridDrawer.repaintGrid(minX, minY, maxX - minX, maxY - minY);
//        }
        gridDrawer.repaintGrid();

        lastUpdate = System.currentTimeMillis();
    }

    private int updatePixel(int i) {
        if(isEmpty(i)) return i;

        // Get the indices of the pixels directly below
        int below = i + width;
        int belowA = below - 1;
        int belowB = below + 1;
        int column = i % this.width;

        // If there are no pixels below, including diagonals, move it accordingly.
        if(isEmpty(below)) {
            swap(i, below);
            return below;
        } else if(this.isEmpty(belowA) && belowA % this.width < column) { // Check to make sure belowLeft didn't wrap to the next line
            this.swap(i, belowA);
            return belowA;
        } else if(this.isEmpty(belowB) && belowB % this.width > column) { // Check to make sure belowRight didn't wrap to the next line
            this.swap(i, belowB);
            return belowB;
        }

        return i;
    }

    /**
     * Clears the grid, resetting all values to the default.
     */
    public void clear() {
        Empty empty = new Empty();
        Arrays.fill(grid, empty);
        cleared = true;
    }

    /**
     * Sets the color of a specific particle at the given index.
     *
     * @param index    The index.
     * @param particle The Particle.
     */
    public void set(int index, Particle particle) {
        if(index >= gridSize) return;
        this.grid[index] = particle;
        this.modifiedIndices.add(index);
    }

    public Particle get(int index) {
        return this.grid[index];
    }

    /**
     * Swaps the positions of two particles (or empty spaces) in the grid.
     *
     * @param indexA The index of the first particle.
     * @param indexB The index of the second particle.
     */
    public void swap(int indexA, int indexB) {
        if(this.grid[indexA].isEmpty() && this.grid[indexB].isEmpty()) return;

        Particle temp = this.grid[indexA];
        this.set(indexA, this.grid[indexB]);
        this.set(indexB, temp);
    }

    /**
     * Checks if a specific space in the grid is empty.
     *
     * @param index The index of the particle.
     * @return True if the space is empty, false otherwise.
     */
    public boolean isEmpty(int index) {
        if(index >= gridSize) return false;
        return this.grid[index].isEmpty();
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

        if(cursorIndices.length <= cursorColors.length) return;

        updateCursorColors();
    }

    public void updateCursorColors() {
        cursorColors = new Color[cursorIndices.length];
        if(cursorIndices.length == 0) return; // needs testing

        Particle particle = ParticleFactory.createParticle();
        Color color = particle.getBaseColor();
        for(int i = 0; i < cursorIndices.length; i++) {
            cursorColors[i] = particle.isEmpty() ? color : Colors.varyColor(color);
        }
    }

    public Color[] getCursorColors() {
        return this.cursorColors;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public boolean isCleared() {
        return this.cleared;
    }

    public Set<Integer> getModifiedIndices() {
        return this.modifiedIndices;
    }
}
