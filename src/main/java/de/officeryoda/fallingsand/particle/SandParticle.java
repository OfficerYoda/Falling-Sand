package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;
import de.officeryoda.fallingsand.particle.behavior.FallBehavior;

public class SandParticle extends Particle {

    public SandParticle(Grid grid, int index) {
        super(Colors.SAND_COLOR, Colors.varyColor(Colors.SAND_COLOR),false,
                new Behavior[]{new FallBehavior(0.4, 16)},
                grid, index);
    }
}
