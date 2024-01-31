package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;

public abstract class Behavior {

    public abstract void update(Particle particle, Grid grid, int direction);

}
