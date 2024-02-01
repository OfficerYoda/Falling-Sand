package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.MoveBehavior;

public class SandParticle extends Particle {

    public SandParticle(Grid grid, int index) {
        super(Colors.SAND_COLOR, Colors.varyColor(Colors.SAND_COLOR),
                new Behavior[]{new MoveBehavior(0.4, 16)},
                grid, index, 0.5);
    }
}
