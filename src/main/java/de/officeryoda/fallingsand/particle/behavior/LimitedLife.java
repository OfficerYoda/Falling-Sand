package de.officeryoda.fallingsand.particle.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.interfaces.LimitedLifeExecutor;
import de.officeryoda.fallingsand.interfaces.TriConsumer;
import de.officeryoda.fallingsand.particle.Particle;

import java.util.function.BiConsumer;

public class LimitedLife extends Behavior {

    private final int lifetime;
    private final BiConsumer<LimitedLife, Particle> onTick;
    private final TriConsumer<LimitedLife, Particle, Grid> onDeath;
    private int remainingLife;

    public LimitedLife(int lifetime, LimitedLifeExecutor lifeExecutor) {
        this.lifetime = lifetime;
        this.remainingLife = lifetime;
        if (lifeExecutor != null) {
            this.onTick = lifeExecutor::onTick;
            this.onDeath = lifeExecutor::onDeath;
        } else {
            this.onTick = (limitedLife, particle) -> {};
            this.onDeath = (limitedLife, particle, grid) -> {};
        }
    }

    @Override
    public void update(Particle particle, Grid grid, int direction) {
        if(remainingLife <= 0) {
            onDeath.accept(this, particle, grid);
        } else {
            remainingLife--;
        }

        onTick.accept(this, particle);
        grid.onModified(particle.getIndex());
    }

    public int getLifetime() {
        return this.lifetime;
    }

    public int getRemainingLife() {
        return this.remainingLife;
    }
}

