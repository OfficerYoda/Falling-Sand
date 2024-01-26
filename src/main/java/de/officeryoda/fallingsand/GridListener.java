package de.officeryoda.fallingsand;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GridListener extends MouseMotionAdapter {

    private final Grid grid;
    private final int cellSize;
    private final int borderGap;

    public GridListener(Grid grid, int cellSize, int borderGap) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.borderGap = borderGap;
    }

    public static int varyColor(int color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsb);

        float hue = hsb[0];
        float saturation = (float) (hsb[1] + Math.random() * -0.2);
        float lightness = (float) (hsb[2] + (Math.random() * 0.2 - 0.1));

        System.out.printf("hsl: " + hsb[0] + ", " + hsb[1] + ", " + hsb[2] + "%n");
        return Color.HSBtoRGB(hue, saturation, lightness);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Handle mouse drag here
        int x = e.getX();
        int y = e.getY();

        x -= borderGap;
        x -= cellSize / 2;
        y -= borderGap;
        y -= 30; // top window bar

        if(x < 0 || x > grid.getWidth() * cellSize) return;
        if(y < 0 || y > grid.getHeight() * cellSize) return;

        int color = varyColor(Grid.SAND_COLOR);
        System.out.println(color);
        grid.set(x / cellSize, y / cellSize, color);
    }
}
