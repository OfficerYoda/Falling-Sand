package de.officeryoda.fallingsand;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class GridDrawer extends JFrame {

    public final static int TITLE_BAR_HEIGHT = 40;

    private final GridPanel gridPanel;
    private final int cellSize;

    public GridDrawer(Grid grid, int cellSize) {
        this.cellSize = cellSize;

        setTitle("Falling Sand");
        setSize(grid.getWidth() * cellSize + 16, // idk why but it was always 16px to narrow
                grid.getHeight() * cellSize + TITLE_BAR_HEIGHT - 1); // -1 to prevent one pixel row at the bottom
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gridPanel = new GridPanel(grid, cellSize);
        add(gridPanel);

        GridListener gridListener = new GridListener(grid, cellSize, this);
        addMouseListener(gridListener);
        addMouseWheelListener(gridListener);

        setInvisibleCursor();

        setLocationRelativeTo(null); // center the frame on the screen
        setVisible(true);
    }

    private void setInvisibleCursor() {
        // Create a transparent cursor image
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "InvisibleCursor");

        // Set the cursor for the JFrame
        setCursor(invisibleCursor);
    }

    public void repaintGrid() {
        gridPanel.repaint();
    }

    public void repaintGrid(Rectangle rect) {
        gridPanel.setBoundsRect(rect);
//        gridPanel.repaint();
        gridPanel.repaint(rect.x * cellSize, rect.y * cellSize, rect.width * cellSize, rect.height * cellSize);
    }
}

class GridPanel extends JPanel {

    private final Grid grid;
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;

    private Rectangle boundsRect;

    public GridPanel(Grid grid, int cellSize) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.gridWidth = grid.getWidth();
        this.gridHeight = grid.getHeight();

        this.boundsRect = new Rectangle(0, 0, getWidth(), getHeight());

        setBackground(Colors.BACKGROUND_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//        g.setColor(Colors.varyColor(Colors.BACKGROUND_COLOR));
//        g.fillRect(0, 0, getWidth(), getHeight());
//        paintParticles(g);
        paintParticleImage(g);
        if(GridListener.paintCursor) {
            paintCursorNew(g);
//            paintCursor(g);
        }
//        paintGrid(g);
//        paintFps(g);

        Grid.DRAW_FINISHED_LATCH.countDown();
    }

    private void paintParticleImage(Graphics g) {
        int[] colors = Arrays.stream(grid.getGrid()).mapToInt(particle -> particle.getColor().getRGB()).toArray();
        BufferedImage image = new BufferedImage(gridWidth, gridHeight, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, gridWidth, gridHeight, colors, 0, gridWidth);

        BufferedImage scaledImage = scaleImage(image, cellSize);

        // Draw the scaled image onto the panel
        g.drawImage(scaledImage, 0, 0, this);
    }

    private void paintGrid(Graphics g) {
        final int minX = 0;
        final int maxX = gridWidth * cellSize;
        final int minY = 0;
        final int maxY = gridHeight * cellSize;

        // Draw horizontal lines
        for(int y = 0; y <= gridHeight; y++) {
            g.drawLine(
                    minX, y * cellSize,
                    maxX, y * cellSize);
        }

        // Draw vertical lines
        for(int x = 0; x <= gridWidth; x++) {
            g.drawLine(
                    x * cellSize, minY,
                    x * cellSize, maxY);
        }
    }

    private void paintParticles(Graphics g) {
        if(grid.isCleared()) {
            clearPixels(g);
        } else if(!grid.getModifiedIndices().isEmpty()) {
            paintPixels(g);
        }
    }

    private void paintPixels(Graphics g) {
        int maxX = boundsRect.x + boundsRect.width;
        int maxY = boundsRect.y + boundsRect.height;

        for(int x = boundsRect.x; x < maxX; x++) {
            for(int y = boundsRect.y; y < maxY; y++) {
                int index = x + y * gridWidth;

                Color color = grid.get(index).getColor();
                paintPixel(x, y, color, g);
            }
        }
    }

    private void clearPixels(Graphics g) {
        g.setColor(Colors.BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void paintCursor(Graphics g) {
        List<Integer> cursorIndices = Arrays.stream(Arrays.stream(grid.getCursorIndices()).boxed().toArray(Integer[]::new)).toList();
        Set<Integer> cursorIndicesSet = new HashSet<>(cursorIndices);
        Color[] cursorColors = grid.getCursorColors();

        int maxX = boundsRect.x + boundsRect.width;
        int maxY = boundsRect.y + boundsRect.height;

        for(int x = boundsRect.x; x < maxX; x++) {
            for(int y = boundsRect.y; y < maxY; y++) {
                int index = x + y * gridWidth;
                int cursorIdx = cursorIndicesSet.contains(index) ? cursorIndices.indexOf(index) : -1;

                Color color = cursorIdx == -1 ? grid.get(index).getColor() : cursorColors[cursorIdx];
                paintPixel(x, y, color, g);
            }
        }
    }

    // new cursor implementation start

    private void paintCursorNew(Graphics g) {
        Integer[] cursorIndices = Arrays.stream(grid.getCursorIndices()).boxed().toArray(Integer[]::new);
        Color[] cursorColors = grid.getCursorColors();
        Map<Integer, Integer> indexMap = getIndexMap(cursorIndices);

        Rectangle rect = GridUtility.calculateBoundingRect(cursorIndices, gridWidth);

        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        int[] imgColors = new int[rect.width * rect.height];

        for(int x = 0; x < rect.width; x++) {
            for(int y = 0; y < rect.height; y++) {
                int index = (x + rect.x) + (y + rect.y) * gridWidth;
                int cursorIndex = indexMap.getOrDefault(index, -1);
                Color color = cursorIndex == -1 ? grid.get(index).getColor() : cursorColors[cursorIndex];

                imgColors[x + y * rect.width] = color.getRGB();
            }
        }

        image.setRGB(0, 0, rect.width, rect.height, imgColors, 0, rect.width);
        image = scaleImage(image, cellSize);

        g.drawImage(image, rect.x * cellSize, rect.y * cellSize, this);
    }

    // new cursor implementation end

    private void paintPixel(int x, int y, Color color, Graphics g) {
        g.setColor(color);
        g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }

    private void paintFps(Graphics g) {
        long deltaTime = (System.currentTimeMillis() - grid.getLastUpdate());

        // Ensure that the last update time is not the same as the current time (divide by zero)
        if(deltaTime == 0) return;

        int fps = (int) (1000 / deltaTime);

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        int fontSize = screenHeight / 30; // Adjust the divisor for the desired scaling

        Font font = new Font("Spline Sans", Font.PLAIN, fontSize);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + fps, 5, GridDrawer.TITLE_BAR_HEIGHT);
    }

    private @NotNull Map<Integer, Integer> getIndexMap(Integer[] array) {
        int gridSize = gridWidth * gridHeight;

        List<Integer> list = Arrays.stream(array).toList();
        Map<Integer, Integer> map = new HashMap<>();
        for(Integer entry : list) {
            map.put(entry, list.indexOf(entry));
        }
        return map;
    }

    @NotNull
    private BufferedImage scaleImage(BufferedImage image, int scaleFactor) {
        // Create an AffineTransform for scaling
        AffineTransform transform = new AffineTransform();
        transform.scale(scaleFactor, scaleFactor);

        // Apply the transformation to the image
        BufferedImage scaledImage = new BufferedImage(
                image.getWidth() * scaleFactor,
                image.getHeight() * scaleFactor,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, this);

        // Dispose the graphics context to free resources
        g2d.dispose();
        return scaledImage;
    }

    void setBoundsRect(Rectangle boundsRect) {
        this.boundsRect = boundsRect;
    }
}
