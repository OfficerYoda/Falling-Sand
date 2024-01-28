package de.officeryoda.fallingsand;

import de.officeryoda.fallingsand.particle.Empty;
import de.officeryoda.fallingsand.particle.Particle;
import de.officeryoda.fallingsand.particle.ParticleFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Grid {

    public static CountDownLatch DRAW_FINISHED_LATCH = new CountDownLatch(1);

    public static int CURSOR_RADIUS = 8;

    public static int UPDATES_PER_SECOND = 50;
    public static int UPDATE_INTERVAL = (int) (1f / UPDATES_PER_SECOND * 1000); // time in ms

    private final int width;
    private final int height;
    private final int gridSize;
    private final Particle[] grid;

    private final Set<Integer> modifiedIndices;
    private boolean cleared; // if we cleared all pixels last update
    private Rectangle lastUpdateRect;
    private GridDrawer gridDrawer;
    private GridListener gridListener;
    private int[] cursorIndices = new int[0];
    private Color[] cursorColors = new Color[0];
    private long lastUpdate = System.currentTimeMillis();

    public Grid(int windowWidth, int windowHeight, int cellSize) {
        windowWidth -= windowWidth % cellSize;
        windowHeight -= windowHeight % cellSize;

        this.width = windowWidth / cellSize;
        this.height = windowHeight / cellSize;
        this.grid = new Particle[width * height];
        this.clear(); // fill grid with 'Empty' Particle
        this.gridSize = grid.length;

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
        for(int row = height - 1; row >= 0; row--) {
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
            rect = GridUtility.calculateBoundingRect(cursorIndices, width);
        } else {
            // add cursorIndices to also draw them
            List<Integer> intList = Arrays.asList(Arrays.stream(cursorIndices).boxed().toArray(Integer[]::new));
            modifiedIndices.addAll(intList);

            rect = GridUtility.calculateBoundingRect(modifiedIndices, width);
        }

        DRAW_FINISHED_LATCH = new CountDownLatch(1);
        // to prevent leaving stray pixels
        Rectangle totalRect = GridUtility.calculateBoundingRect(rect, lastUpdateRect);
        gridDrawer.repaintGrid(totalRect);
        lastUpdateRect = rect;

        try {
            DRAW_FINISHED_LATCH.await();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        Empty empty = new Empty();
        Arrays.fill(grid, empty);
        cleared = true;
    }

    public void set(int index, Particle particle) {
//        if(index >= gridSize) return;
        this.grid[index] = particle;

        this.modifiedIndices.add(index);
    }

    public Particle get(int index) {
        return this.grid[index];
    }

    public void swap(int indexA, int indexB) {
        if(this.grid[indexA].isEmpty() && this.grid[indexB].isEmpty()) return;

        Particle temp = this.grid[indexA];
        this.set(indexA, this.grid[indexB]);
        this.set(indexB, temp);
    }

    public boolean isEmpty(int index) {
        if(index >= gridSize) return false;
        return this.grid[index].isEmpty();
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

    /// Getters and Setters

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getCursorIndices() {
        return this.cursorIndices;
    }

    public void setCursorIndices(int @NotNull [] cursorIndices) {
        this.cursorIndices = cursorIndices;

        if(cursorIndices.length <= cursorColors.length) return;

        updateCursorColors();
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

    public Particle[] getGrid() {
        return grid;
    }
}
