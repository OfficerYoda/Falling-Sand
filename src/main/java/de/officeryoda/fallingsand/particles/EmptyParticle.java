package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;

public class EmptyParticle extends Particle {
    public EmptyParticle(Grid grid, int index) {
        super(Colors.BACKGROUND_COLOR, Colors.BACKGROUND_COLOR,
                new Behavior[]{},
                grid, index,
                true);
    }
}
