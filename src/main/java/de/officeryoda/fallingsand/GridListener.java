package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridListener extends MouseAdapter {

    private final Grid grid;
    private final int cellSize;
    private final int borderGap;
    private final JFrame jFrame;

    private final Timer timer;

    public GridListener(Grid grid, int cellSize, int borderGap, JFrame jFrame) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.borderGap = borderGap;
        this.jFrame = jFrame;

        timer = new Timer(Grid.updateInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call your method here
                spawn();
            }
        });
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

        x -= borderGap;
        x -= cellSize / 2;
        y -= borderGap;
        y -= 30; // top window bar

        if(x < 0 || x > grid.getWidth() * cellSize) return;
        if(y < 0 || y > grid.getHeight() * cellSize) return;

        grid.set(x / cellSize, y / cellSize, varyColor(Grid.SAND_COLOR));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Start the timer when the mouse is pressed
        timer.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Stop the timer when the mouse is released
        timer.stop();
    }
}
