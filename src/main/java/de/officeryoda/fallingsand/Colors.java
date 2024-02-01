package de.officeryoda.fallingsand;

import java.awt.*;

/**
 * The Colors class defines a set of static colors commonly used in the Falling Sand simulation.
 * It also provides methods for color variation based on hue, saturation, and lightness adjustments.
 */
public class Colors {

    /**
     * The background color used in the Falling Sand simulation.
     */
    public static final Color BACKGROUND_COLOR = Color.decode("#010409");

    /**
     * The color representing sand particles in the simulation.
     */
    public static final Color SAND_COLOR = Color.decode("#dcb159");

    /**
     * The color representing wood particles in the simulation.
     */
    public static final Color WOOD_COLOR = Color.decode("#46281d");

    /**
     * The color representing smoke particles in the simulation.
     */
    public static final Color SMOKE_COLOR = Color.decode("#4C4A4D");

    /**
     * The color representing fire particles in the simulation.
     */
    public static final Color FIRE_COLOR = Color.decode("#e34f0f");

    /**
     * The color representing water particles in the simulation.
     */
    public static final Color WATER_COLOR = Color.decode("#3556db");

    /**
     * Varies the given color based on saturation and lightness adjustments.
     *
     * @param colorInt         The RGB representation of the color to be varied.
     * @param satVary          The amount of saturation variation to apply.
     * @param lightUpperBound  The upper bound for lightness variation.
     * @param lightLowerBound  The lower bound for lightness variation.
     * @return                 The RGB value of the varied color.
     */
    public static int varyColor(int colorInt, double satVary, double lightUpperBound, double lightLowerBound) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((colorInt >> 16) & 0xFF, (colorInt >> 8) & 0xFF, colorInt & 0xFF, hsb);

        float hue = hsb[0];
        float saturation = (float) (hsb[1] + Math.random() * satVary);
        float lightness = (float) (hsb[2] + (Math.random() * lightUpperBound + lightLowerBound));

        return Color.HSBtoRGB(hue, saturation, lightness);
    }

    /**
     * Varies the given color based on saturation variation with default lightness bounds.
     *
     * @param colorInt         The RGB representation of the color to be varied.
     * @param satVary          The amount of saturation variation to apply.
     * @return                 The RGB value of the varied color.
     */
    public static int varyColor(int colorInt, double satVary) {
        return varyColor(colorInt, satVary, 0.2, -0.1);
    }

    /**
     * Varies the given color based on a default saturation variation and lightness bounds.
     *
     * @param colorInt         The RGB representation of the color to be varied.
     * @return                 The RGB value of the varied color.
     */
    public static int varyColor(int colorInt) {
        return varyColor(colorInt, -0.2);
    }

    /**
     * Varies the given color object based on saturation and lightness adjustments.
     *
     * @param color            The Color object to be varied.
     * @param satVary          The amount of saturation variation to apply.
     * @param lightUpperBound  The upper bound for lightness variation.
     * @param lightLowerBound  The lower bound for lightness variation.
     * @return                 A new Color object representing the varied color.
     */
    public static Color varyColor(Color color, double satVary, double lightUpperBound, double lightLowerBound) {
        return new Color(varyColor(color.getRGB(), satVary, lightUpperBound, lightLowerBound));
    }

    /**
     * Varies the given color object based on saturation variation with default lightness bounds.
     *
     * @param color            The Color object to be varied.
     * @param satVary          The amount of saturation variation to apply.
     * @return                 A new Color object representing the varied color.
     */
    public static Color varyColor(Color color, double satVary) {
        return new Color(varyColor(color.getRGB(), satVary));
    }

    /**
     * Varies the given color object based on default saturation variation and lightness bounds.
     *
     * @param color            The Color object to be varied.
     * @return                 A new Color object representing the varied color.
     */
    public static Color varyColor(Color color) {
        return new Color(varyColor(color.getRGB()));
    }
}
