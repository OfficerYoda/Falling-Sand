package de.officeryoda.fallingsand.particle.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.Particle;

public abstract class Behavior {

    public abstract void update(Particle particle, Grid grid, int direction);
}
