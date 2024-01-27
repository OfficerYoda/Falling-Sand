package de.officeryoda.fallingsand.particle;

import java.awt.*;

public abstract class Particle {

    protected Color color;
    protected boolean isEmpty;

    public Particle(Color color, boolean isEmpty) {
        this.color = varyColor(color);
        this.isEmpty = isEmpty;
    }

    private Color varyColor(Color color) {
        int colorInt = color.getRGB();
        float[] hsb = new float[3];
        Color.RGBtoHSB((colorInt >> 16) & 0xFF, (colorInt >> 8) & 0xFF, colorInt & 0xFF, hsb);

        float hue = hsb[0];
        float saturation = (float) (hsb[1] + Math.random() * -0.2);
        float lightness = (float) (hsb[2] + (Math.random() * 0.2 - 0.1));

        colorInt = Color.HSBtoRGB(hue, saturation, lightness);
        return new Color(colorInt);
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }
}