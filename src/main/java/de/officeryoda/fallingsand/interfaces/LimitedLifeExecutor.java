package de.officeryoda.fallingsand.interfaces;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.Particle;
import de.officeryoda.fallingsand.particle.behavior.LimitedLife;

public interface LimitedLifeExecutor {

    void onTick(LimitedLife behavior, Particle particle);
    void onDeath(LimitedLife behavior, Particle particle, Grid grid);

}
