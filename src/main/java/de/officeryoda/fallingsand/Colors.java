package de.officeryoda.fallingsand;

import java.awt.*;

public class Colors {

    public static final Color SAND_COLOR = Color.decode("#dcb159");
    public static final int SAND_COLOR_RGB = SAND_COLOR.getRGB();

    public static int varyColor(int colorInt) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((colorInt >> 16) & 0xFF, (colorInt >> 8) & 0xFF, colorInt & 0xFF, hsb);

        float hue = hsb[0];
        float saturation = (float) (hsb[1] + Math.random() * -0.2);
        float lightness = (float) (hsb[2] + (Math.random() * 0.2 - 0.1));

        return Color.HSBtoRGB(hue, saturation, lightness);
    }

    public static Color varyColor(Color color) {
        return new Color(varyColor(color.getRGB()));
    }
}
