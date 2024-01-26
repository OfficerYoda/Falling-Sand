package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class GridListener extends MouseAdapter {

    private final Grid grid;
    private final int cellSize;
    private final JFrame jFrame;
    private final Timer timer;

    private boolean isLeftPressed;

    public GridListener(Grid grid, int cellSize, JFrame jFrame) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.jFrame = jFrame;
        this.isLeftPressed = false;

        this.timer = new Timer(Grid.updateInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call your method here
                spawn();
            }
        });

        this.timer.start();
    }

    public static int varyColor(int color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsb);

        float hue = hsb[0];
        float saturation = (float) (hsb[1] + Math.random() * -0.2);
        float lightness = (float) (hsb[2] + (Math.random() * 0.2 - 0.1));

        return Color.HSBtoRGB(hue, saturation, lightness);
    }

    private void spawn() {
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePos, jFrame);

        int x = mousePos.x;
        int y = mousePos.y;

        x -= cellSize / 2 + cellSize;
        y += cellSize / 2 + cellSize;
        y -= GridDrawer.TITLE_BAR_HEIGHT;

        if(x < 0 || x > grid.getWidth() * cellSize ||
                y < 0 || y > grid.getHeight() * cellSize) {
            grid.setCursorIndices(new int[0]);
            return;
        }

        int cursorIndex = x / cellSize + y / cellSize * grid.getWidth();
        int[] cursorIndices = getIndicesInSquare(cursorIndex, Grid.CURSOR_RADIUS);
        Arrays.stream(cursorIndices).forEach(System.out::println);
        grid.setCursorIndices(cursorIndices);

        if(!isLeftPressed) return;
//        grid.set(cursorIndices[0], Grid.SAND_COLOR_RGB);
        for(int index : cursorIndices) {
            if(Math.random() < 0.5) {
                grid.set(index, varyColor(Grid.SAND_COLOR_RGB));
            }
        }
    }

    private int[] getIndicesInRadius(int centerIndex, int radius) {
        int width = grid.getWidth();
        int centerX = centerIndex % width;
        int centerY = centerIndex / width;
        int sqrRadius = radius * radius;

        java.util.List<Integer> indices = new ArrayList<>();

        for(int extX = -radius; extX <= radius; extX++) {
            for(int extY = -radius; extY <= radius; extY++) {
                int newX = centerX + extX;
                int newY = centerY + extY;

                int sqrDst = sqrDistance(centerX, centerY, newX, newY);
                if(sqrDst <= sqrRadius) {
                    int index = newX + newY * width;
                    indices.add(index);
                }
            }
        }

        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] getIndicesInSquare(int centerIndex, int radius) {
        int width = grid.getWidth();
        int centerX = centerIndex % width;
        int centerY = centerIndex / width;
        int sqrRadius = radius * radius;

        java.util.List<Integer> indices = new ArrayList<>();

        for(int extX = -radius; extX <= radius; extX++) {
            for(int extY = -radius; extY <= radius; extY++) {
                int newX = centerX + extX;
                int newY = centerY + extY;

                int index = newX + newY * width;
                indices.add(index);
            }
        }

        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private int sqrDistance(int x1, int y1, int x2, int y2) {
        int deltaX = x2 - x1;
        int deltaY = y2 - y1;
        return deltaX * deltaX + deltaY * deltaY;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isLeftPressed = true;
//        timer.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isLeftPressed = false;
//        timer.stop();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        grid.setCursorIndices(new int[0]);
    }
}
