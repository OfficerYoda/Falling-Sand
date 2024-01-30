package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

public class EmptyParticle extends Particle {
    public EmptyParticle(Grid grid, int index) {
        super(Colors.BACKGROUND_COLOR, Colors.BACKGROUND_COLOR,
                true, new Behavior[]{},
                grid, index);
    }
}
