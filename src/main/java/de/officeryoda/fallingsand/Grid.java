package de.officeryoda.fallingsand;

import de.officeryoda.fallingsand.particle.Empty;
import de.officeryoda.fallingsand.particle.Particle;
import de.officeryoda.fallingsand.particle.ParticleFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Represents a grid for a falling sand simulation.
 */
public class Grid {

    public static CountDownLatch DRAW_FINISHED_LATCH = new CountDownLatch(1);

    public static int CURSOR_RADIUS = 8;

    public static int UPDATES_PER_SECOND = 50;
    public static int UPDATE_INTERVAL = (int) (1f / UPDATES_PER_SECOND * 1000); // time in ms

    private final int width;
    private final int height;
    private final int gridSize;
    private final int cellSize;
    private final Particle[] grid;

    private final Set<Integer> modifiedIndices;
    private boolean cleared; // if we cleared all pixels last update
    private Rectangle lastUpdateRect;
    private GridDrawer gridDrawer;
    private GridListener gridListener;
    private int[] cursorIndices = new int[0];
    private Color[] cursorColors = new Color[0];
    private long lastUpdate = System.currentTimeMillis();

    /**
     * Constructs a Grid with the specified width and height.
     *
     * @param windowWidth  The width of the window displaying grid.
     * @param windowHeight The height of the window displaying  grid.
     */
    public Grid(int windowWidth, int windowHeight, int cellSize) {
        windowWidth -= windowWidth % cellSize;
        windowHeight -= windowHeight % cellSize;

        this.width = windowWidth / cellSize;
        this.height = windowHeight / cellSize;
        this.grid = new Particle[width * height];
        this.clear(); // fill grid with 'Empty' Particle
        this.gridSize = grid.length;
        this.cellSize = cellSize;

        this.modifiedIndices = new HashSet<>();
        this.cleared = false;
        this.lastUpdateRect = new Rectangle();

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
        }, 0, UPDATE_INTERVAL);
    }

    public static void setCursorRadius(int radius) {
        Grid.CURSOR_RADIUS = Math.max(1, radius);
    }

    private void update() {
        cleared = false;
        modifiedIndices.clear();

        gridListener.spawn();

        updateParticles();
        repaintGrid();

        lastUpdate = System.currentTimeMillis();
    }

    private void updateParticles() {
        // backward to not double apply gravity to a particle
        for(int row = height - 2; row >= 0; row--) {
            int rowOffset = row * this.width;
            boolean leftToRight = Math.random() > 0.5;
            for(int col = 0; col < this.width; col++) {
                // Go from right to left or left to right depending on our random value
                int columnOffset = leftToRight ? col : -col - 1 + this.width;
                int index = rowOffset + columnOffset;

                Particle particle = grid[index];
                particle.updateVelocity();
                for(int j = 0; j < particle.getUpdateCount(); j++) {
                    int newIdx = updatePixel(index);

                    // stop the particle if the newIndex bottom or bottomR exceeds the boundary (gridSize - width - 1)
                    if(newIdx == index || newIdx > gridSize - width) {
                        particle.resetVelocity();
                        break;
                    } else {
                        index = newIdx;
                    }
                }
            }
        }
    }

    private int updatePixel(int i) {
        if(isEmpty(i)) return i;

        // Get the indices of the pixels directly below
        int below = i + width;
        int belowA = below - 1;
        int belowB = below + 1;
        int column = i % this.width;

        // If there are no pixels below, including diagonals, move it accordingly.
        System.out.println("i: " + i);
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

    private void repaintGrid() {
        Rectangle rect;
        if(cleared) {
            gridDrawer.repaintGrid();
            return;
        } else if(modifiedIndices.isEmpty()) {
            // draw cursor
            rect = calculateBoundingBox(Arrays.asList(Arrays.stream(cursorIndices).boxed().toArray(Integer[]::new)));
        } else {
            // add cursorIndices to also draw them
            List<Integer> intList = Arrays.asList(Arrays.stream(cursorIndices).boxed().toArray(Integer[]::new));
            modifiedIndices.addAll(intList);

            rect = calculateBoundingBox(modifiedIndices);
        }

        DRAW_FINISHED_LATCH = new CountDownLatch(1);
        // to prevent leaving stray pixels
        Rectangle totalRect = getBoundingRectangle(rect, lastUpdateRect);
        gridDrawer.repaintGrid(totalRect);
        lastUpdateRect = rect;

        try {
            DRAW_FINISHED_LATCH.await();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Rectangle calculateBoundingBox(Collection<Integer> modifiedIndices) {
        // Calculate bounding box in a single pass for x and y coordinates
        IntSummaryStatistics statsX = modifiedIndices.stream().mapToInt(i -> i % width).summaryStatistics();
        IntSummaryStatistics statsY = modifiedIndices.stream().mapToInt(i -> i / width).summaryStatistics();

        int minX = statsX.getMin() * cellSize;
        int maxX = statsX.getMax() * cellSize;
        int minY = statsY.getMin() * cellSize;
        int maxY = statsY.getMax() * cellSize;

        // Create and return the bounding box
        return new Rectangle(minX, minY, maxX - minX + cellSize, maxY - minY + cellSize);
    }

    public Rectangle getBoundingRectangle(Rectangle rect1, Rectangle rect2) {
        // Calculate the coordinates of the top-left corner
        int x = Math.min(rect1.x, rect2.x);
        int y = Math.min(rect1.y, rect2.y);

        // Calculate the dimensions of the bounding rectangle
        int width = Math.max(rect1.x + rect1.width, rect2.x + rect2.width) - x;
        int height = Math.max(rect1.y + rect1.height, rect2.y + rect2.height) - y;

        // Create and return the bounding rectangle
        return new Rectangle(x, y, width, height);
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
//        if(index >= gridSize) return;
        this.grid[index] = particle;

        this.modifiedIndices.add(index);
    }

    public Particle get(int index) {
        if(index >= gridSize) return ParticleFactory.createParticle(0);
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

    public synchronized Set<Integer> getModifiedIndices() {
        return this.modifiedIndices;
    }

    public void setGridListener(GridListener gridListener) {
        this.gridListener = gridListener;
    }
}
