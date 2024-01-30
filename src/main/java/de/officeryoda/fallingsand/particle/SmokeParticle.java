package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.interfaces.LimitedLifeExecutor;
import de.officeryoda.fallingsand.particle.behavior.Behavior;
import de.officeryoda.fallingsand.particle.behavior.GasMovesBehavior;
import de.officeryoda.fallingsand.particle.behavior.LimitedLife;

import java.awt.*;

public class SmokeParticle extends Particle {

    public SmokeParticle(Grid grid, int index) {
        super(Colors.SMOKE_COLOR, Colors.varyColor(Colors.SMOKE_COLOR, -0.05, 0.05, -0.05), new Behavior[]{
                new GasMovesBehavior(-0.05, 0.25),
                new LimitedLife(
                // Each particle has 400 - 800 life (random)
                (int) (400 + 400 * (Math.random())), new SmokeLifeExecutor())
        }, grid, index, false, true);
    }
}

class SmokeLifeExecutor implements LimitedLifeExecutor {

    @Override
    public void onTick(LimitedLife behavior, Particle particle) {
        Color color = particle.getColor();
        int alpha = (int) Math.floor(255.0 * behavior.getRemainingLife() / behavior.getLifetime());
        particle.setColor(changeAlpha(color, alpha));
    }

    @Override
    public void onDeath(LimitedLife behavior, Particle particle, Grid grid) {
        grid.clearIndex(particle.index);
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
