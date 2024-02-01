package de.officeryoda.fallingsand.particles.behavior.executors;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.behavior.LimitedLifeBehavior;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BurningSmokeLifeExecutor extends FlammableExecutor {

    @Override
    public void onTick(@NotNull LimitedLifeBehavior behavior, @NotNull Particle particle) {
        super.onTick(behavior, particle);

        Color color = particle.getColor();
        int alpha = (int) Math.floor(255.0 * behavior.getRemainingLife() / behavior.getLifetime());
        particle.setColor(changeAlpha(color, alpha));
    }

    @Override
    public void onDeath(LimitedLifeBehavior behavior, @NotNull Particle particle, Grid grid) {
        grid.clearIndex(particle.getIndex());
    }

    private Color changeAlpha(Color color, int alpha) {
        // Ensure alpha is within valid range (0 to 255)
        alpha = Math.max(0, Math.min(255, alpha));

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return new Color(red, green, blue, alpha);
    }
}
