package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.FlammableBehavior;

public class WoodParticle extends Particle {

    public WoodParticle(Grid grid, int index) {
        super(Colors.WOOD_COLOR, Colors.varyColor(Colors.WOOD_COLOR),
                new Behavior[]{new FlammableBehavior((int) (200 + 100 * Math.random()), false, 0.007)},
                grid, index, 1.0);
    }
}
