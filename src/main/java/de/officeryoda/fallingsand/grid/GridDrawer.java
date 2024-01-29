package de.officeryoda.fallingsand.grid;

import de.officeryoda.fallingsand.Colors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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

        this.boundsRect = new Rectangle(0, 0, gridWidth * cellSize, gridHeight * cellSize);

        setBackground(Colors.BACKGROUND_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//        paintParticles(g);
        paintParticleImage(g);
        paintCursor(g);
//        paintGrid(g);
//        paintFps(g);
        Grid.DRAW_FINISHED_LATCH.countDown();
    }

    private void paintParticleImage(Graphics g) {
        if(grid.isCleared()) {
            paintClearedGrid(g);
            return;
        }
        BufferedImage image = new BufferedImage(boundsRect.width, boundsRect.height, BufferedImage.TYPE_INT_RGB);
        int[] imgColors = new int[boundsRect.width * boundsRect.height + 1];

        for(int x = 0; x < boundsRect.width; x++) {
            for(int y = 0; y < boundsRect.height; y++) {
                int index = (x + boundsRect.x) + (y + boundsRect.y) * gridWidth;
                Color color = grid.get(index).getColor();

                imgColors[x + y * boundsRect.width] = color.getRGB();
            }
        }

        image.setRGB(0, 0, boundsRect.width, boundsRect.height, imgColors, 0, boundsRect.width);
        image = scaleImage(image, cellSize);

        g.drawImage(image, boundsRect.x * cellSize, boundsRect.y * cellSize, this);
    }

    private void paintClearedGrid(Graphics g) {
        g.setColor(Colors.BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void paintCursor(Graphics g) {
        int[] cursorIndices = grid.getCursorIndices();
        if(cursorIndices.length == 0) return;

        Color[] cursorColors = grid.getCursorColors();
        Map<Integer, Integer> indexMap = getIndexMap(cursorIndices);

        Rectangle rect = GridUtility.calculateBoundingRect(cursorIndices, gridWidth);

        BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
        int[] imgColors = new int[rect.width * rect.height];

        for(int x = 0; x < rect.width; x++) {
            for(int y = 0; y < rect.height; y++) {
                int index = (x + rect.x) + (y + rect.y) * gridWidth;
                int cursorIndex = indexMap.getOrDefault(index, -1);
                int color = (cursorIndex == -1 ? grid.get(index).getColor() : cursorColors[cursorIndex]).getRGB();

                imgColors[x + y * rect.width] = color;
            }
        }

        image.setRGB(0, 0, rect.width, rect.height, imgColors, 0, rect.width);
        image = scaleImage(image, cellSize);

        g.drawImage(image, rect.x * cellSize, rect.y * cellSize, this);
    }

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

    private @NotNull Map<Integer, Integer> getIndexMap(int[] array) {
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < array.length; i++) {
            map.put(array[i], i);
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

    public Rectangle adjustToGridDimensions(Rectangle rect) {
        int newWidth = Math.min(rect.width, gridWidth);
        int newHeight = Math.min(rect.height, gridHeight);

        return new Rectangle(rect.x, rect.y, newWidth, newHeight);
    }

    public void setBoundsRect(Rectangle boundsRect) {
        boundsRect = adjustToGridDimensions(boundsRect);
        this.boundsRect = boundsRect;
    }
}
