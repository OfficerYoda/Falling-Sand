package de.officeryoda.fallingsand.grid;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.particles.EmptyParticle;
import de.officeryoda.fallingsand.particles.OutOfBoundsParticle;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.ParticleFactory;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Grid {

    public static final int UPDATES_PER_SECOND = 50;
    public static final int UPDATE_INTERVAL = (int) (1f / UPDATES_PER_SECOND * 1000); // time in ms
    private static final int TIMEOUT_TIME_MS = 100;

    public static CountDownLatch DRAW_FINISHED_LATCH = new CountDownLatch(1);
    public static int CURSOR_RADIUS = 5;

    @Getter
    private final int width;
    @Getter
    private final int height;
    private final int gridSize;
    @Getter
    private final Particle[] grid;

    private final Set<Integer> modifiedIndices;
    @Getter
    private boolean cleared; // if we cleared all pixels last update
    private Rectangle lastUpdateRect;
    private GridDrawer gridDrawer;
    @Setter
    private GridListener gridListener;
    @Getter
    private int[] cursorIndices = new int[0];
    @Getter
    private Color[] cursorColors = new Color[0];
    @Getter
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

        // A pass for positive y direction velocities,
        // and negative
        for(int pass = -1; pass <= 1; pass += 2) {
            updateParticles(pass);
        }
        repaintGrid();

        lastUpdate = System.currentTimeMillis();
    }

    private void updateParticles(int direction) {
        // backward to not double apply gravity to a particle
        for(int row = this.height - 1; row >= 0; row--) {
            var rowOffset = row * this.width;
            var leftToRight = Math.random() > 0.5;
            for(var i = 0; i < this.width; i++) {
                // Go from right to left or left to right depending on our random value
                int columnOffset = leftToRight ? i : -i + this.width - 1;
                int index = rowOffset + columnOffset;

                index = this.modifyIndexHook(index, direction);
                Particle particle = this.grid[index];

                particle.update(direction);
            }
        }
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
            Rectangle cursorBounds = GridUtility.calculateBoundingRect(cursorIndices, width);
            rect = GridUtility.calculateBoundingRect(modifiedIndices, width);
            rect = GridUtility.calculateBoundingRect(cursorBounds, rect);
        }

        DRAW_FINISHED_LATCH = new CountDownLatch(1);

        // to prevent leaving stray pixels
        Rectangle totalRect = GridUtility.calculateBoundingRect(rect, lastUpdateRect);
        gridDrawer.repaintGrid(totalRect);
        lastUpdateRect = rect;

        try {
            DRAW_FINISHED_LATCH.await(TIMEOUT_TIME_MS, TimeUnit.MILLISECONDS);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int modifyIndexHook(int index, int direction) {
        if(direction == -1) {
            return this.grid.length - index - 1;
        }
        return index;
    }

    public void clear() {
        Arrays.setAll(grid, index -> new EmptyParticle(this, index));
        cleared = true;
    }

    public void set(int index, Particle particle) {
//        if(index >= gridSize) return;
        this.grid[index] = particle;

        this.modifiedIndices.add(index);
    }

    public Particle get(int index) {
        if(index < 0 || gridSize <= index) return new OutOfBoundsParticle();
        return this.grid[index];
    }

    public void swap(int indexA, int indexB) {
        if(this.grid[indexA].isEmpty() && this.grid[indexB].isEmpty()) return;
        Particle temp = this.grid[indexA];
        this.grid[indexA] = this.grid[indexB];
        this.setIndex(this.grid[indexB], indexA);
        this.setIndex(temp, indexB);
    }

    private void setIndex(Particle particle, int index) {
        this.grid[index] = particle;
        particle.setIndex(index);
        this.modifiedIndices.add(index);
    }

    public boolean isEmpty(int index) {
        if(index >= gridSize) return false;
        return this.grid[index].isEmpty();
    }

    public void updateCursorColors() {
        cursorColors = new Color[cursorIndices.length];
        if(cursorIndices.length == 0) return; // needs testing

        Particle particle = ParticleFactory.createDummyParticle();
        Color baseColor = particle.getBaseColor();
        for(int i = 0; i < cursorIndices.length; i++) {
            Color color;
            if(particle.isEmpty()) { // Empty particle
                color = baseColor;
            } else if(particle.isAiry()) { // Smoke Particle
                color = Colors.varyColor(baseColor, -0.05, 0.05, -0.05);
            } else {
                color = Colors.varyColor(baseColor);
            }

            cursorColors[i] = color;
        }
    }

    public void clearIndex(int index) {
        this.set(index, new EmptyParticle(this, index));
    }

    public void onModified(int index) {
        this.modifiedIndices.add(index);
    }

    public void setCursorIndices(int @NotNull [] cursorIndices) {
        this.cursorIndices = cursorIndices;

        if(cursorIndices.length <= cursorColors.length) return;

        updateCursorColors();
    }
}
