package de.officeryoda.fallingsand.grid;

import de.officeryoda.fallingsand.particles.ParticleFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GridListener extends MouseAdapter implements MouseWheelListener, KeyListener {

    private final Grid grid;
    private final int cellSize;
    private final JFrame jFrame;
    private boolean leftPressed;
    private boolean middlePressed;
    private boolean rightPressed;

    public GridListener(Grid grid, int cellSize, JFrame jFrame) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.jFrame = jFrame;
        this.leftPressed = false;

        grid.setGridListener(this);
    }

    public void spawn() {
        int[] cursorIndices = setCursorIndices();

        if(rightPressed) {
            grid.clear();
            rightPressed = false;
        }

        if(!leftPressed) return;
//        grid.set(cursorIndices[0], ParticleFactory.createParticle());
        double spawnChance = ParticleFactory.createDummyParticle().getBrushSpawnChance();

        for(int index : cursorIndices) {
            if(index < 0) continue;
            if(Math.random() < spawnChance) {
                grid.set(index, ParticleFactory.createParticle(grid, index));
            }
        }
    }

    private int[] setCursorIndices() {
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
            return new int[0];
        }

        int cursorIndex = x / cellSize + y / cellSize * grid.getWidth();
        int[] cursorIndices = getIndicesInRadius(cursorIndex, Grid.CURSOR_RADIUS);
        grid.setCursorIndices(cursorIndices);

        return cursorIndices;
    }

    private int[] getIndicesInRadius(int centerIndex, int radius) {
        int width = grid.getWidth();
        int centerX = centerIndex % width;
        int centerY = centerIndex / width;
        int sqrRadius = radius * radius;

        List<Integer> indices = new ArrayList<>();

        for(int extX = -radius; extX <= radius; extX++) {
            for(int extY = -radius; extY <= radius; extY++) {
                int newX = (centerX + extX + width) % width; // Ensure no wrap-around for X
                int newY = centerY + extY;

                int sqrDst = sqrDistance(centerX, centerY, newX, newY);
                if(sqrDst < sqrRadius) {
                    int index = newX + newY * width;
                    indices.add(index);
                }
            }
        }

        int gridSize = grid.getWidth() * grid.getHeight();
        return indices.stream()
                .mapToInt(Integer::intValue)
                .filter(value -> 0 <= value && value < gridSize)
                .toArray();
    }

    private int sqrDistance(int x1, int y1, int x2, int y2) {
        int deltaX = x2 - x1;
        int deltaY = y2 - y1;
        return deltaX * deltaX + deltaY * deltaY;
    }

    private void changeCursorSize(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation(); // -1 = up; 1 = down
        Grid.setCursorRadius(Grid.CURSOR_RADIUS - wheelRotation);
    }

    private void nextParticle() {
        ParticleFactory.nextParticle();
        grid.updateCursorColors();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                leftPressed = true;
                break;
            case MouseEvent.BUTTON2:
                middlePressed = true;
                break;
            case MouseEvent.BUTTON3:
                rightPressed = true;
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                leftPressed = false;
                break;
            case MouseEvent.BUTTON2:
                middlePressed = false;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if(e.getButton() == MouseEvent.BUTTON2) {
            nextParticle();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        setCursorIndices();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        grid.setCursorIndices(new int[0]);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        if(middlePressed) {
            nextParticle();
        } else {
            changeCursorSize(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_P) {
            grid.setPaused(!grid.isPaused());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
