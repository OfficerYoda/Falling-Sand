package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.interfaces.LimitedLifeExecutor;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.SmokeParticle;

import java.awt.*;

public class FlammableBehavior extends LimitedLifeBehavior {

    int fuel = (int) (10 + 100 * (Math.random()));

    public FlammableBehavior(int lifetime) {
        super(lifetime, new FlammableExecutor());
    }
}

class FlammableExecutor implements LimitedLifeExecutor {

    private final Color[] colors = {
            Color.decode("#541e1e"),
            Color.decode("#ff1f1f"),
            Color.decode("#ea5a00"),
            Color.decode("#ff6900"),
            Color.decode("#eecc09")
    };

    @Override
    public void onTick(LimitedLifeBehavior behavior, Particle particle) {
        double frequency = Math.sqrt((double) behavior.getLifetime() / behavior.getRemainingLife());
        double period = frequency * colors.length;
        double pct = behavior.getRemainingLife() / period;
        int colorIndex = (int) (Math.floor(pct) % colors.length);
        particle.setColor(colors[colorIndex]);
    }

    @Override
    public void onDeath(LimitedLifeBehavior behavior, Particle particle, Grid grid) {
        int index = particle.getIndex();
        SmokeParticle smoke = new SmokeParticle(grid, index);
        grid.set(index, smoke);
    }
}


