package de.officeryoda.fallingsand.interfaces;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.behavior.LimitedLifeBehavior;

public interface LimitedLifeExecutor {

    void onTick(LimitedLifeBehavior behavior, Particle particle);
    void onDeath(LimitedLifeBehavior behavior, Particle particle, Grid grid);

}
