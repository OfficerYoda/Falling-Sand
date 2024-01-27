package de.officeryoda.fallingsand;

import de.officeryoda.fallingsand.particle.Sand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GridListener extends MouseAdapter implements MouseWheelListener {

    private final Grid grid;
    private final int cellSize;
    private final JFrame jFrame;

    private boolean isLeftPressed;

    public GridListener(Grid grid, int cellSize, JFrame jFrame) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.jFrame = jFrame;
        this.isLeftPressed = false;

        // Call your method here
        Timer timer = new Timer(Grid.updateInterval, e -> spawn());

        timer.start();
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
        int[] cursorIndices = getIndicesInRadius(cursorIndex, Grid.CURSOR_RADIUS);
        grid.setCursorIndices(cursorIndices);

        if(!isLeftPressed) return;
//        grid.set(cursorIndices[0], Grid.SAND_COLOR_RGB);
        for(int index : cursorIndices) {
            if(index < 0) continue;
            if(Math.random() < 0.5) {
                grid.set(index, new Sand());
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
        if(e.getButton() == MouseEvent.BUTTON1) {
            isLeftPressed = true;
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            grid.clear();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            isLeftPressed = false;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        grid.setCursorIndices(new int[0]);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        int wheelRotation = e.getWheelRotation(); // -1 = up; 1 = down
        Grid.setCursorRadius(Grid.CURSOR_RADIUS - wheelRotation);
    }
}
