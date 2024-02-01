package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.FluidMoveBehavior;

public class WaterParticle extends Particle{

    protected WaterParticle(Grid grid, int index) {
        super(Colors.WATER_COLOR, Colors.varyColor(Colors.WATER_COLOR),
                new Behavior[]{new FluidMoveBehavior(0.4, 16)},
                grid, index,
                0.6,
                false, false, true);
    }
}
